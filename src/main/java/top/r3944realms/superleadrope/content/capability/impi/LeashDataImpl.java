/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.content.capability.impi;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.LeashDataSyncPacket;
import top.r3944realms.superleadrope.util.capability.LeashDataAPI;
import top.r3944realms.superleadrope.util.capability.LeashStateAPI;
import top.r3944realms.superleadrope.util.riding.RindingLeash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 预期行为
 * <table border="1">
 *     <thead>
 *      <tr>
 *         <th>场景</th>
 *         <th>行为</th>
 *      </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <th>距离 ≤ maxDistance</th>
 *             <th>正常弹性拉力，重置 keepLeashTicks 为最大值</th>
 *         </tr>
 *         <tr>
 *             <th>maxDistance < distance ≤ 2*maxDistance</th>
 *             <th>增强拉力，并减少 keepLeashTicks（每Tick减1）</th>
 *         </tr>
 *         <tr>
 *             <th>distance > 2*maxDistance && keepLeashTicks > 0</th>
 *              <th>施加更强拉力并减少Tick</th>
 *         </tr>
 *         <tr>
 *             <th>distance > 2*maxDistance && keepLeashTicks == 0</th>
 *              <th>立即断裂</th>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class LeashDataImpl implements ILeashData {
    private final Entity entity;
    private boolean needsSync = false;
    private long lastSyncTime;
    private final Set<UUID> delayedHolders = new CopyOnWriteArraySet<>();
    private final Map<UUID, LeashInfo> leashHolders = new ConcurrentHashMap<>();
    // 引入解决 绳结不保存导致第二进入持有者不存在的问题
    private final Map<BlockPos, LeashInfo> leashKnots = new ConcurrentHashMap<>();

    public LeashDataImpl(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void markForSync() {
        if (!entity.level().isClientSide) {
            needsSync = true;
            immediateSync(); // 立即同步一次
        }
    }

    /**
     * 立即同步，无视时间间隔
     */
    @Override
    public void immediateSync() {
        syncNow();
    }

    /**
     * 定期调用，每 tick 或每几秒检测
     */
    @Override
    public void checkSync() {
        if (!needsSync || entity.level().isClientSide) return;

        long now = System.currentTimeMillis();
        // 每隔 2 秒同步一次
        if (now - lastSyncTime > 2000) {
            syncNow();
        }
    }

    /**
     * 内部统一同步方法，避免重复逻辑
     */
    private void syncNow() {
        CompoundTag currentData = serializeNBT();

        NetworkHandler.sendToPlayer(
                new LeashDataSyncPacket(entity.getId(), currentData),
                entity,
                PacketDistributor.TRACKING_ENTITY_AND_SELF
        );
        lastSyncTime = System.currentTimeMillis();
        needsSync = false;
    }

    @Override
    public boolean addLeash(Entity holder) {
        return addLeash(holder, CommonEventHandler.leashConfigManager.getMaxLeashLength());
    }

    @Override
    public boolean addLeash(Entity holder, String reserved) {
        return addLeash(holder, CommonEventHandler.leashConfigManager.getMaxLeashLength(), reserved);
    }

    // 添加拴绳（支持自定义最大长度）
    @Override
    public boolean addLeash(Entity holder, double maxDistance) {
        return addLeash(holder, maxDistance, CommonEventHandler.leashConfigManager.getElasticDistance(), 0, "");
    }

    // 添加拴绳（支持自定义最大长度和弹性距离）
    @Override
    public boolean addLeash(Entity holder, double maxDistance, double elasticDistance, int maxKeepLeashTicks) {
        return addLeash(holder, maxDistance, elasticDistance, maxKeepLeashTicks, "");
    }

    // 添加拴绳（支持自定义最大长度 + reserved 字段）
    @Override
    public boolean addLeash(Entity holder, double maxDistance, String reserved) {
        return addLeash(holder, maxDistance, CommonEventHandler.leashConfigManager.getElasticDistance(), 0, reserved);
    }

    // 添加拴绳（最终实现：支持最大长度、弹性距离、保持 Tick、reserved）
    @Override
    public boolean addLeash(Entity holder, double maxDistance,
                            double elasticDistance, int maxKeepLeashTicks, String reserved) {
        boolean isSuperKnot = holder instanceof SuperLeashKnotEntity;
        if ((!isSuperKnot && leashHolders.containsKey(holder.getUUID()))
                || (isSuperKnot && leashKnots.containsKey(((SuperLeashKnotEntity) holder).getPos()))) {
            return false;
        }

        if (!canBeLeashed()) {
            Optional<UUID> uuidOptional = occupyLeash();
            if (uuidOptional.isEmpty()) {
                return false;
            }
            removeLeash(uuidOptional.get());
        }

        LeashInfo info = LeashInfo.create(
                holder,
                reserved,
                maxDistance,
                elasticDistance,
                maxKeepLeashTicks,
                maxKeepLeashTicks
        );

        if (isSuperKnot) {
            leashKnots.put(((SuperLeashKnotEntity) holder).getPos(), info);
        } else {
            leashHolders.put(holder.getUUID(), info);
        }
        LeashStateAPI.Offset.setHolderFor(entity, holder);
        markForSync();
        return true;
    }

    // 使用已有的 LeashInfo 添加拴绳（直接走最终实现）
    @Override
    public void addLeash(Entity holder, LeashInfo leashInfo) {
        addLeash(holder,
                leashInfo.maxDistance(),
                leashInfo.elasticDistance(),
                leashInfo.maxKeepLeashTicks(),
                leashInfo.reserved()
        );
    }

    @Override
    public void addDelayedLeash(Player holderPlayer) {
        delayedHolders.add(holderPlayer.getUUID());
    }

    @Override
    public void removeDelayedLeash(UUID onceHolderUUID) {
        delayedHolders.remove(onceHolderUUID);
    }

    private <K> boolean updateLeashInfo(
            Map<K, LeashInfo> map,
            K key,
            Function<LeashInfo, LeashInfo> updater
    ) {
        LeashInfo old = map.get(key);
        if (old == null || old.holderIdOpt().isEmpty()) return false;

        LeashInfo updated = updater.apply(old);
        if (updated == null) return false;

        map.put(key, updated);
        markForSync();
        return true;
    }

    @Override
    public boolean setMaxDistance(Entity holder, double newMaxDistance) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), newMaxDistance) :
                setMaxDistance(holder.getUUID(), newMaxDistance);
    }

    @Override
    public boolean setMaxDistance(Entity holder, double newMaxDistance, int newMaxKeepLeashTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), newMaxDistance, newMaxKeepLeashTicks) :
                setMaxDistance(holder.getUUID(), newMaxDistance, newMaxKeepLeashTicks);
    }

    @Override
    public boolean setMaxDistance(Entity holder, double distance, int maxKeepTicks, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), distance, maxKeepTicks, reserved) :
                setMaxDistance(holder.getUUID(), distance, maxKeepTicks, reserved);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, double newMaxDistance) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                newMaxDistance,
                old.elasticDistance(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, double newMaxDistance, int newMaxKeepLeashTicks) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                newMaxDistance,
                old.elasticDistance(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, double distance, int maxKeepTicks, String reserved) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                reserved,
                distance,
                old.elasticDistance(),
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, double newMaxDistance) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                newMaxDistance,
                old.elasticDistance(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, double newMaxDistance, int newMaxKeepLeashTicks) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                newMaxDistance,
                old.elasticDistance(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, double distance, int maxKeepTicks, String reserved) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                reserved,
                distance,
                old.elasticDistance(),
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @Override
    public boolean setElasticDistance(Entity holder, double newElasticDistance) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistance(superLeashKnotEntity.getPos(), newElasticDistance) :
                setElasticDistance(holder.getUUID(), newElasticDistance);
    }

    // 动态修改弹性距离
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(UUID holderUUID, double newElasticDistance) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                old.maxDistance(),
                newElasticDistance,
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(BlockPos knotPos, double newElasticDistance) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                old.maxDistance(),
                newElasticDistance,
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @Override
    public boolean setElasticDistance(Entity holder, double newElasticDistance, int newMaxKeepLeashTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistance(superLeashKnotEntity.getPos(), newElasticDistance, newMaxKeepLeashTicks) :
                setElasticDistance(holder.getUUID(), newElasticDistance, newMaxKeepLeashTicks);
    }

    @Override
    public boolean setElasticDistance(Entity holder, double distance, int maxKeepTicks, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistance(superLeashKnotEntity.getPos(), distance, maxKeepTicks, reserved) :
                setElasticDistance(holder.getUUID(), distance, maxKeepTicks, reserved);
    }

    // 动态修改弹性距离
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(UUID holderUUID, double newElasticDistance, int newMaxKeepLeashTicks) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                old.maxDistance(),
                newElasticDistance,
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(UUID holderUUID, double distance, int maxKeepTicks, String reserved) {
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                reserved,
                old.maxDistance(),
                distance,
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(BlockPos knotPos, double newElasticDistance, int newMaxKeepLeashTicks) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.reserved(),
                old.maxDistance(),
                newElasticDistance,
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistance(BlockPos knotPos, double newElasticDistance, int newMaxKeepLeashTicks, String reserved) {
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                reserved,
                old.maxDistance(),
                newElasticDistance,
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    /**
     * 计算拴绳拉力（防抖动逻辑）
     */
    @Override
    public void applyLeashForces() {
        Vec3 combinedForce = Vec3.ZERO;
        Vec3 combinedDirection = Vec3.ZERO;
        int validLeashes = 0;

        // 计算所有拴绳的合力和平均方向
        for (Map.Entry<UUID, LeashInfo> entry : leashHolders.entrySet()) {
            Vec3 force = calculateLeashForceForUUID(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
                validLeashes++;
            }
        }

        for (Map.Entry<BlockPos, LeashInfo> entry : leashKnots.entrySet()) {
            Vec3 force = calculateLeashForceForBlockPos(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
                validLeashes++;
            }
        }

        boolean hasForce = !combinedForce.equals(Vec3.ZERO);
        Entity finalApplyEntity = RindingLeash.getFinalEntityForLeashIfForce(entity, hasForce);

        if (hasForce) {
            // 处理玩家和其他实体
            if (finalApplyEntity instanceof ServerPlayer player) {
                RindingLeash.applyForceToPlayer(player, combinedForce);
            } else {
                finalApplyEntity.setDeltaMovement(finalApplyEntity.getDeltaMovement().add(combinedForce));
                finalApplyEntity.hurtMarked = true;

                // 对生物使用合力方向进行移动（只有在能够移动时才执行）
                if (finalApplyEntity instanceof Mob mob && validLeashes > 0 && canMobMove(mob)) {
                    moveMobTowardsCombinedDirection(mob, combinedDirection, validLeashes, combinedForce.length());
                } else if (finalApplyEntity instanceof Mob mob) {
                    // 无法移动时停止导航
                    mob.getNavigation().stop();
                }
            }

            RindingLeash.protectAnimalMovement(finalApplyEntity, true);
        } else {
            RindingLeash.protectAnimalMovement(finalApplyEntity, false);

            // 没有力时也停止导航
            if (finalApplyEntity instanceof Mob mob) {
                mob.getNavigation().stop();
            }
        }
    }

    /**
     * 检查生物是否能够移动
     */
    private boolean canMobMove(Mob mob) {
        // 检查各种无法移动的情况
        return !mob.isNoGravity() &&            // 有重力才能移动
                !mob.isSleeping() &&            // 没有在睡觉
                !mob.isDeadOrDying() &&         // 没有死亡或濒死
                !mob.isFreezing() &&            // 没有被冻结
                mob.canUpdate() &&              // 可以更新
                mob.isEffectiveAi() &&          // AI有效
                mob.getDeltaMovement().lengthSqr() < 100.0; // 移动速度不是特别快（防止异常情况）
    }

    /**
     * 让生物朝着合力方向移动
     */
    private void moveMobTowardsCombinedDirection(Mob mob, Vec3 combinedDirection, int leashCount, double forceMagnitude) {
        if (combinedDirection.equals(Vec3.ZERO)) return;

        // 再次检查是否能够移动
        if (!canMobMove(mob)) {
            mob.getNavigation().stop();
            return;
        }

        // 计算平均方向
        Vec3 averageDirection = combinedDirection.scale(1.0 / leashCount).normalize();

        // 根据力的大小调整移动速度
        double speed = calculateMobSpeed(mob, forceMagnitude);

        // 计算目标位置（在合力方向上稍微超前一点）
        Vec3 targetPos = mob.position().add(averageDirection.scale(3.0)); // 3格距离

        // 检查目标位置是否可达
        if (isPositionReachable(mob, targetPos)) {
            // 设置移动目标
            mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, speed);

            // 设置生物朝向合力方向
            mob.getLookControl().setLookAt(targetPos);
        } else {
            // 位置不可达时停止导航
            mob.getNavigation().stop();
        }
    }

    /**
     * 检查位置是否可达
     */
    private boolean isPositionReachable(Mob mob, Vec3 targetPos) {
        // 简单的距离检查
        double distance = mob.position().distanceTo(targetPos);
        if (distance > 20.0) { // 距离太远
            return false;
        }

        // 检查是否有导航路径
        Path path = mob.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 0);
        return path != null && !path.isDone();
    }

    /**
     * 根据生物类型和力的大小计算移动速度
     */
    private double calculateMobSpeed(Mob mob, double forceMagnitude) {
        double baseSpeed = mob instanceof Llama ? 2.0 : 1.0;

        // 力越大，移动速度越快（但有上限）
        double forceFactor = Math.min(forceMagnitude * 0.5, 2.0); // 限制最大加速2倍

        return baseSpeed * (1.0 + forceFactor);
    }



    /**
     * 为UUID拴绳计算力
     */
    private Vec3 calculateLeashForceForUUID(Map.Entry<UUID, LeashInfo> entry) {
        UUID uuid = entry.getKey();
        Entity uuidHolder = ((ServerLevel) entity.level()).getEntity(uuid);
        if (uuidHolder != null) {
            return calculateLeashForce(uuidHolder, entry);
        } else {
            if (!delayedHolders.contains(uuid)) {
                SuperLeadRope.logger.warn("Could not apply leash forces for {}, because it is not found(it will be removed from list).", uuid);
                leashHolders.remove(uuid);
            }
            return null;
        }
    }

    /**
     * 为方块位置拴绳计算力
     */
    private Vec3 calculateLeashForceForBlockPos(Map.Entry<BlockPos, LeashInfo> entry) {
        SuperLeashKnotEntity orCreateKnot = SuperLeashKnotEntity.getOrCreateKnot(entity.level(), entry.getKey());
        return calculateLeashForce(orCreateKnot, entry);
    }

    /**
     * 计算单个拴绳的力
     */
    private Vec3 calculateLeashForce(Entity holder, Map.Entry<?, LeashInfo> entry) {
        Vec3 holderPos = holder.position().add(0, holder.getBbHeight() * 0.7, 0);
        LeashInfo info = entry.getValue();
        Vec3 entityPos = entity.position();
        double distance = holderPos.distanceTo(entityPos);
        double extremeSnapDist = info.maxDistance() * CommonEventHandler.leashConfigManager.getExtremeSnapFactor();

        // 1. 检查是否超出断裂距离
        if (distance > extremeSnapDist) {
            if (info.keepLeashTicks() > 0) {
                // 计算临界拉力
                Vec3 pullForce = calculateCriticalPullForce(holderPos, entityPos, distance, info);
                entry.setValue(info.decrementKeepTicks());
                return pullForce;
            }
            // 断裂
            removeLeash(holder);
            //TODO: 是不是应该考虑让断裂统一发出声音，还是就这样由断裂发出
            entity.level().playSound(null, holder.getOnPos(), SLPSoundEvents.LEAD_BREAK.get(), SoundSource.PLAYERS);
            return null;
        }

        // 2. 正常弹性拉力逻辑
        Vec3 pullForce = Vec3.ZERO;
        if (distance > info.elasticDistance()) {
            pullForce = calculatePullForce(holderPos, entityPos, distance, info);
        }

        // 3. 重置缓冲Tick
        if (distance <= info.maxDistance() && info.keepLeashTicks() < info.maxKeepLeashTicks()) {
            entry.setValue(info.resetKeepTicks());
        }

        return pullForce;
    }

    // 计算正常拉力（保持不变）
    @Contract("_, _, _, _ -> new")
    private @NotNull Vec3 calculatePullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance, @NotNull LeashInfo info) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();
        double pullStrength = 0.2;

        if (distance > info.maxDistance()) {
            double excessRatio = (distance - info.maxDistance()) / info.maxDistance();
            pullStrength += excessRatio * 0.8;
        }

        Vec3 pullForce = pullDirection.scale(
                (distance - info.elasticDistance()) * pullStrength * CommonEventHandler.leashConfigManager.getSpringDampening()
        );

        return new Vec3(
                pullForce.x * CommonEventHandler.leashConfigManager.getXElasticity(),
                pullForce.y * CommonEventHandler.leashConfigManager.getXElasticity(),
                pullForce.z * CommonEventHandler.leashConfigManager.getZElasticity()
        );
    }

    // 计算临界拉力（保持不变）
    private @NotNull Vec3 calculateCriticalPullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance, @NotNull LeashInfo info) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();
        double excessRatio = (distance - info.maxDistance()) / info.maxDistance();
        double pullStrength = 1.0 + excessRatio * 2.0;

        Vec3 pullForce = pullDirection.scale(
                (distance - info.elasticDistance()) * pullStrength * CommonEventHandler.leashConfigManager.getSpringDampening()
        );

        return new Vec3(
                pullForce.x * CommonEventHandler.leashConfigManager.getXElasticity(),
                pullForce.y * CommonEventHandler.leashConfigManager.getYElasticity(),
                pullForce.z * CommonEventHandler.leashConfigManager.getZElasticity()
        );
    }

    // 移除拴绳绑定
    @Override
    public boolean removeLeash(Entity holder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                removeLeash(superLeashKnotEntity.getPos()) :
                removeLeash(holder.getUUID());
    }
    @Override
    public boolean removeLeash(UUID holderUUID) {
        boolean removed = leashHolders.remove(holderUUID) != null;
        if (removed) {
            LeashStateAPI.Operations.detach(entity, holderUUID);
            markForSync();
        }
        return removed;
    }

    @Override
    public boolean removeLeash(BlockPos knotPos) {
        boolean removed = leashKnots.remove(knotPos) != null;
        if (removed) {
            LeashStateAPI.Operations.detach(entity, knotPos);
            markForSync();
        }
        return removed;
    }

    @Override
    public void removeAllLeashes() {
        leashHolders.clear();
        leashKnots.clear();
        LeashStateAPI.Offset.removeHolderAll(entity);
        markForSync();
    }

    @Override
    public void removeAllHolderLeashes() {
        leashHolders.clear();
        LeashStateAPI.Offset.removeAllHolderUUIDs(entity);
        markForSync();
    }

    @Override
    public void removeAllKnotLeashes() {
        leashKnots.clear();
        LeashStateAPI.Offset.removeAllHolderBlockPoses(entity);
        markForSync();
    }

    @Override
    public boolean transferLeash(Entity holder, Entity newHolder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                transferLeash(superLeashKnotEntity.getPos(), newHolder) :
                transferLeash(holder.getUUID(), newHolder);
    }

    @Override
    public boolean transferLeash(Entity holder, Entity newHolder, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                transferLeash(superLeashKnotEntity.getPos(), newHolder, reserved) :
                transferLeash(holder.getUUID(), newHolder, reserved);
    }

    // 将拴绳持有者转移到新实体(非拴绳结 -> 任意)
    @Override
    public boolean transferLeash(UUID oldHolderUUID, Entity newHolder) {
        LeashInfo info = leashHolders.remove(oldHolderUUID);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateAPI.Operations.transfer(entity, oldHolderUUID, newHolder);
        markForSync();
        return true;
    }
    @Override
    public boolean transferLeash(UUID oldHolderUUID, Entity newHolder, String reserved) {
        LeashInfo info = leashHolders.remove(oldHolderUUID);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, reserved);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, reserved);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateAPI.Operations.transfer(entity, oldHolderUUID, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean transferLeash(BlockPos knotPos, Entity newHolder) {
        LeashInfo info = leashKnots.remove(knotPos);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateAPI.Operations.transfer(entity, knotPos, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean transferLeash(BlockPos knotPos, Entity newHolder, String reserved) {
        LeashInfo info = leashKnots.remove(knotPos);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, reserved);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, reserved);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateAPI.Operations.transfer(entity, knotPos, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean hasLeash() {
        return !leashKnots.isEmpty() || !leashHolders.isEmpty();
    }

    @Override
    public boolean hasKnotLeash() {
        return !leashKnots.isEmpty();
    }

    @Override
    public boolean hasHolderLeash() {
        return !leashHolders.isEmpty();
    }

    //只能系在这些实体上，在这里，其它情况一律忽略
    //TODO: 标签支持控制
    public static boolean isLeashable(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof Boat || entity instanceof Minecart;
    }


    // 获取所有拴绳信息
    @Override
    public Collection<LeashInfo> getAllLeashes() {
        return Stream.concat(
                leashHolders.values().stream(),
                leashKnots.values().stream()
        ).collect(Collectors.toList());
    }

    @Override
    public boolean isLeashedBy(Entity holder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                isLeashedBy(superLeashKnotEntity.getPos()) :
                isLeashedBy(holder.getUUID());
    }

    // 检查是否被特定实体拴住
    @Override
    public boolean isLeashedBy(UUID holderUUID) {
        return leashHolders.containsKey(holderUUID);
    }

    @Override
    public boolean isLeashedBy(BlockPos knotPos) {
        return leashKnots.containsKey(knotPos);
    }

    @Override
    public boolean isInDelayedLeash(UUID holderUUID) {
        return delayedHolders.contains(holderUUID);
    }

    @Override
    public Optional<LeashInfo> getLeashInfo(Entity holder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                getLeashInfo(superLeashKnotEntity.getPos()) :
                getLeashInfo(holder.getUUID());
    }

    @Override
    public Optional<LeashInfo> getLeashInfo(UUID holderUUID) {
        return Optional.ofNullable(leashHolders.get(holderUUID));
    }

    @Override
    public Optional<LeashInfo> getLeashInfo(BlockPos knotPos) {
        return Optional.ofNullable(leashKnots.get(knotPos));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag holdersList = new ListTag();
        ListTag delayedHolderList = new ListTag();
        for (LeashInfo info : leashHolders.values()) {
            CompoundTag infoTag = generateCompoundTagFromUUIDLeashInfo(info);
            holdersList.add(infoTag);
        }
        for (LeashInfo info : leashKnots.values()) {
            CompoundTag infoTag = generateCompoundTagFromBlockPosLeashInfo(info);
            holdersList.add(infoTag);
        }
        for (UUID uuid : delayedHolders) {
            CompoundTag infoTag = generateCompoundTagFromUUID(uuid);
            delayedHolderList.add(infoTag);
        }
        tag.put("LeashHolders", holdersList);
        tag.put("DelayedHolders", delayedHolderList);
        return tag;
    }
    private static @NotNull CompoundTag generateCompoundTagFromUUID(@NotNull UUID uuid) {
        CompoundTag infoTag = new CompoundTag();
        infoTag.putUUID("DelayHolderUUID", uuid);
        return infoTag;
    }
    private static @NotNull CompoundTag generateCompoundTagFromUUIDLeashInfo(@NotNull LeashInfo info) {
        CompoundTag infoTag = new CompoundTag();
        if (info.holderUUIDOpt().isEmpty()) {
            throw new IllegalArgumentException("LeashInfo.holderUUIDOpt is empty");
        }
        infoTag.putUUID("HolderUUID", info.holderUUIDOpt().get());
        return getCommonCompoundTag(info, infoTag);
    }
    private static @NotNull CompoundTag generateCompoundTagFromBlockPosLeashInfo(@NotNull LeashInfo info) {
        CompoundTag infoTag = new CompoundTag();
        if (info.blockPosOpt().isEmpty()) {
            throw new IllegalArgumentException("LeashInfo.blockPos is empty");
        }
        BlockPos blockPos = info.blockPosOpt().get();
        infoTag.put("KnotBlockPos", NbtUtils.writeBlockPos(blockPos));
        return getCommonCompoundTag(info, infoTag);
    }

    private static @NotNull CompoundTag getCommonCompoundTag(@NotNull LeashInfo info, CompoundTag infoTag) {
        if(info.holderIdOpt().isEmpty()) {
            throw new IllegalArgumentException("LeashInfo.intId is empty");
        }
        infoTag.putInt("HolderID", info.holderIdOpt().get());
        infoTag.putString("LeashItem", info.reserved());
        infoTag.putDouble("MaxDistance", info.maxDistance());
        infoTag.putDouble("ElasticDistance", info.elasticDistance());
        infoTag.putInt("KeepLeashTicks", info.keepLeashTicks());
        infoTag.putInt("MaxKeepLeashTicks", info.maxKeepLeashTicks());
        return infoTag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        leashHolders.clear();
        leashKnots.clear();
        delayedHolders.clear();
        if (nbt.contains("LeashHolders", ListTag.TAG_LIST)) {
            ListTag holdersList = nbt.getList("LeashHolders", ListTag.TAG_COMPOUND);

            for (int i = 0; i < holdersList.size(); i++) {
                CompoundTag infoTag = holdersList.getCompound(i);
                if (infoTag.contains("HolderUUID")) {
                    LeashInfo uuidLeashDataFormListTag = getUUIDLeashDataFormListTag(infoTag);
                    leashHolders.put(uuidLeashDataFormListTag.holderUUIDOpt().orElseThrow(), uuidLeashDataFormListTag);
                } else {
                    LeashInfo blockPosLeashDataFormListTag = getBlockPosLeashDataFormListTag(infoTag);
                    leashKnots.put(blockPosLeashDataFormListTag.blockPosOpt().orElseThrow(), blockPosLeashDataFormListTag);
                }
            }
        }
        if (nbt.contains("DelayedHolders", ListTag.TAG_LIST)) {
            ListTag delayedHolderList = nbt.getList("DelayedHolders", ListTag.TAG_COMPOUND);
            for (int i = 0; i < delayedHolderList.size(); i++) {
                CompoundTag infoTag = delayedHolderList.getCompound(i);
                UUID delayedUUIDFormListTag = getDelayedUUIDFormListTag(infoTag);
                delayedHolders.add(delayedUUIDFormListTag);
            }
        }
    }
    private static @NotNull UUID getDelayedUUIDFormListTag(@NotNull CompoundTag infoTag) {
        if (infoTag.contains("DelayHolderUUID"))
            return infoTag.getUUID("DelayHolderUUID");
        throw new IllegalArgumentException("LeashInfo.intId is empty");
    }
    @Contract("_ -> new")
    private static @NotNull LeashInfo getUUIDLeashDataFormListTag(@NotNull CompoundTag infoTag) {
        if (infoTag.contains("HolderUUID")){
            return new LeashInfo(
                    infoTag.getUUID("HolderUUID"),
                    infoTag.getInt("HolderID"),
                    infoTag.getString("LeashItem"),
                    infoTag.getDouble("MaxDistance"),
                    infoTag.contains("ElasticDistance") ? infoTag.getDouble("ElasticDistance") : 6.0,
                    infoTag.getInt("KeepLeashTicks"),
                    infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
            );
        } else
            throw new IllegalArgumentException("Unknown LeashInfo");
    }
    @Contract("_ -> new")
    private static @NotNull LeashInfo getBlockPosLeashDataFormListTag(@NotNull CompoundTag infoTag) {
        if (infoTag.contains("KnotBlockPos")) {
            return new LeashInfo(
                    NbtUtils.readBlockPos(infoTag.getCompound("KnotBlockPos")),
                    infoTag.getInt("HolderID"),
                    infoTag.getString("LeashItem"),
                    infoTag.getDouble("MaxDistance"),
                    infoTag.contains("ElasticDistance") ? infoTag.getDouble("ElasticDistance") : 6.0,
                    infoTag.getInt("KeepLeashTicks"),
                    infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
            );
        } else
            throw new IllegalArgumentException("Unknown LeashInfo");
    }

    @Override
    public boolean canBeLeashed() {
        return (leashHolders.size() + leashKnots.size()) <= CommonEventHandler.leashConfigManager.getMaxLeashesPerEntity();
    }

    @Override
    public Optional<UUID> occupyLeash() {
        if (canBeLeashed() || delayedHolders.isEmpty()) return Optional.empty();
        // 从 Set 随机取一个 UUID
        int size = delayedHolders.size();
        int index = (int) (Math.random() * size); // 0 ~ size-1
        UUID selected = null;
        int i = 0;
        for (UUID uuid : delayedHolders) {
            if (i == index) {
                selected = uuid;
                break;
            }
            i++;
        }

        if (selected != null) {
            delayedHolders.remove(selected);
            return Optional.of(selected);
        }

        return Optional.empty(); // 理论上不会到这里
    }



    public static @NotNull List<Entity> leashableInArea(Level pLevel, Vec3 pPos, Predicate<Entity> filter) {
        return leashableInArea(pLevel, pPos, filter, 1024D);
    }
    public static @NotNull List<Entity> leashableInArea(@NotNull Level pLevel, Vec3 pPos, Predicate<Entity> filter, double fetchDistance) {
        AABB box = AABB.ofSize(pPos, fetchDistance, fetchDistance, fetchDistance);
        return pLevel.getEntitiesOfClass(Entity.class, box, e -> LeashDataImpl.isLeashable(e) && filter.test(e));
    }
    public static @NotNull List<Entity> leashableInArea(@NotNull Entity entity, Predicate<Entity> filter, double fetchDistance) {
        return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), filter, fetchDistance);
    }
    public static @NotNull List<Entity> leashableInArea(Entity entity, Predicate<Entity> filter) {
        return leashableInArea(entity, filter, 1024D);
    }
    public static @NotNull List<Entity> leashableInArea(Entity holder) {
        return leashableInArea(holder, i -> isLeashHolder(i, holder), 1024D);
    }
    public boolean canBeAttachedTo(Entity pEntity) {
        if (pEntity == entity) {
            return false;
        } else {
            Optional<LeashInfo> leashInfo = getLeashInfo(pEntity);
            return leashInfo.isEmpty() && (entity.distanceTo(pEntity) <= CommonEventHandler.leashConfigManager.getElasticDistance() * CommonEventHandler.leashConfigManager.getExtremeSnapFactor()) && canBeLeashed();//距离最大,则不可以被固定或转移
        }
    }
    public static boolean isLeashHolder(@NotNull Entity pEntity, Entity pTestHolder) {
        return pTestHolder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                isLeashHolder(pEntity, superLeashKnotEntity.getPos()) :
                isLeashHolder(pEntity, pTestHolder.getUUID());
    }

    public static boolean isLeashHolder(@NotNull Entity pEntity, UUID pHolderUUID) {
        return LeashDataAPI.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pHolderUUID))
                .orElse(false);
    }

    public static boolean isLeashHolder(@NotNull Entity pEntity, BlockPos pKnotPos) {
        return LeashDataAPI.getLeashData(pEntity)
                .map(leashData -> leashData.isLeashedBy(pKnotPos))
                .orElse(false);
    }
}
