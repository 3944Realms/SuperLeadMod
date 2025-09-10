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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.LeashDataSyncPacket;
import top.r3944realms.superleadrope.util.riding.RindingLeash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class LeashDataImpl implements ILeashDataCapability {
    private static final double LEASH_ELASTIC_DIST = 6.0; // 弹性距离
    private static final double LEASH_EXTREME_SNAP_DIST_FACTOR = 2.0; // 断裂距离 = 最大距离 * 2 //TODO:未来可配置
    private static final float SPRING_DAMPENING = 0.7f; // 阻尼系数
    private static final Vec3 AXIS_SPECIFIC_ELASTICITY = new Vec3(0.8, 0.2, 0.8); // 轴向弹性系数（Y轴较弱）
    private static final int MAX_LEASHES_PER_ENTITY = 6;//一个实体最多链接多少个拴绳 //TODO:未来可配置
    private final Entity entity;
    private boolean needsSync = false;
    private long lastSyncTime;
    private final Set<UUID> delayedHolders = new CopyOnWriteArraySet<>();
    private final Map<UUID, LeashInfo> leashHolders = new ConcurrentHashMap<>();
    // 引入解决 绳结不保存导致第二进入持有者不存在的问题
    private final Map<BlockPos, LeashInfo> leashKnots = new ConcurrentHashMap<>();
//    private CompoundTag lastSyncedData = new CompoundTag();

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

    /** 立即同步，无视时间间隔 */
    @Override
    public void immediateSync() {
        syncNow();
    }

    /** 定期调用，每 tick 或每几秒检测 */
    @Override
    public void checkSync() {
        if (!needsSync || entity.level().isClientSide) return;

        long now = System.currentTimeMillis();
        // 每隔 2 秒同步一次
        if (now - lastSyncTime > 2000) {
            syncNow();
        }
    }

    /** 内部统一同步方法，避免重复逻辑 */
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

    // 添加拴绳（支持自定义最大长度和弹性距离）
    @Override
    public boolean addLeash(Entity holder, ItemStack leashStack, double maxDistance) {
        boolean result = addLeash(holder, leashStack, maxDistance, LEASH_ELASTIC_DIST, 0);
        if (result) markForSync();
        return result;
    }
    @Override
    public boolean addLeash(Entity holder, ItemStack leashStack, double maxDistance, double elasticDistance, int maxKeepLeashTicks) {
        boolean isSuperKnot = holder instanceof SuperLeashKnotEntity;
        if ((!isSuperKnot && leashHolders.containsKey(holder.getUUID()) || (isSuperKnot && leashKnots.containsKey(((SuperLeashKnotEntity) holder).getPos())))) {
            return false;
        }
        if (!canBeLeashed()) {
            Optional<UUID> uuidOptional = occupyLeash();
            if (uuidOptional.isEmpty()) {
                return false;
            }
            removeLeash(uuidOptional.get());
        }
        LeashInfo info = LeashInfo.CreateLeashInfo(
                holder,
                leashStack.getItem().getDescription().toString(),
                calculateAttachOffset(entity),
                maxDistance,
                elasticDistance,
                maxKeepLeashTicks,
                maxKeepLeashTicks
        );
        if (holder instanceof SuperLeashKnotEntity s) {
            leashKnots.put(s.getPos(), info);
        }
        else leashHolders.put(holder.getUUID(), info);
        markForSync();
        return true;
    }

    @Override
    public boolean addLeash(Entity holder, LeashInfo leashInfo) {
        return addLeash(holder, ItemStack.EMPTY, leashInfo.maxDistance(), leashInfo.elasticDistance(), leashInfo.maxKeepLeashTicks());
    }

    @Override
    public boolean addDelayedLeash(Player holderPlayer) {
        return delayedHolders.add(holderPlayer.getUUID());
    }

    @Override
    public boolean removeDelayedLeash(UUID onceHolderUUID) {
        return delayedHolders.remove(onceHolderUUID);
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

    // 动态修改最大拴绳长度
    @Override
    public boolean setMaxDistance(UUID holderUUID, double newMaxDistance) {
        LeashInfo info = leashHolders.get(holderUUID);
        if (info == null || info.holderUUIDOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;
        leashHolders.put(holderUUID, new LeashInfo(
                info.holderUUIDOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                newMaxDistance,
                info.elasticDistance(), // 保持原有弹性距离
                info.keepLeashTicks(),
                info.maxKeepLeashTicks()
        ));
        markForSync();
        return true;
    }
    @Override
    public boolean setMaxDistance(UUID holderUUID, double newMaxDistance, int newMaxKeepLeashTicks) {
        LeashInfo info = leashHolders.get(holderUUID);
        if (info == null || info.holderUUIDOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;
        leashHolders.put(holderUUID, new LeashInfo(
                info.holderUUIDOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                newMaxDistance,
                info.elasticDistance(), // 保持原有弹性距离
                newMaxKeepLeashTicks,
                info.maxKeepLeashTicks()
        ));
        markForSync();
        return true;
    }

    @Override
    public boolean setMaxDistance(BlockPos knotPos, double newMaxDistance) {
        LeashInfo info = leashKnots.get(knotPos);
        if (info == null || info.blockPosOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;
        leashKnots.put(knotPos, new LeashInfo(
                info.blockPosOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                newMaxDistance,
                info.elasticDistance(), // 保持原有弹性距离
                info.keepLeashTicks(),
                info.maxKeepLeashTicks()
        ));
        markForSync();
        return true;
    }

    @Override
    public boolean setMaxDistance(BlockPos knotPos, double newMaxDistance, int newMaxKeepLeashTicks) {
        LeashInfo info = leashKnots.get(knotPos);
        if (info == null || info.blockPosOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;
        leashKnots.put(knotPos, new LeashInfo(
                info.blockPosOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                newMaxDistance,
                info.elasticDistance(), // 保持原有弹性距离
                info.keepLeashTicks(),
                newMaxKeepLeashTicks
        ));
        markForSync();
        return true;
    }

    @Override
    public boolean setElasticDistance(Entity holder, double newElasticDistance) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistance(superLeashKnotEntity.getPos(), newElasticDistance) :
                setElasticDistance(holder.getUUID(), newElasticDistance);
    }

    // 动态修改弹性距离
    @Override
    public boolean setElasticDistance(UUID holderUUID, double newElasticDistance) {
        LeashInfo info = leashHolders.get(holderUUID);
        if (info == null || info.holderUUIDOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;

        leashHolders.put(holderUUID, new LeashInfo(
                info.holderUUIDOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                info.maxDistance(),
                newElasticDistance,
                info.keepLeashTicks(),
                info.maxKeepLeashTicks()
        ));
        markForSync();
        return true;
    }

    @Override
    public boolean setElasticDistance(BlockPos knotPos, double newElasticDistance) {
        LeashInfo info = leashKnots.get(knotPos);
        if (info == null || info.blockPosOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;

        leashKnots.put(knotPos, new LeashInfo(
                info.blockPosOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                info.maxDistance(),
                newElasticDistance,
                info.keepLeashTicks(),
                info.maxKeepLeashTicks()
        ));
        markForSync();
        return true;
    }

    @Override
    public boolean setElasticDistance(Entity holder, double newElasticDistance, int newMaxKeepLeashTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistance(superLeashKnotEntity.getPos(), newElasticDistance, newMaxKeepLeashTicks) :
                setElasticDistance(holder.getUUID(), newElasticDistance, newMaxKeepLeashTicks);
    }

    // 动态修改弹性距离
    @Override
    public boolean setElasticDistance(UUID holderUUID, double newElasticDistance, int newMaxKeepLeashTicks) {
        LeashInfo info = leashHolders.get(holderUUID);
        if (info == null || info.holderUUIDOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;

        leashHolders.put(holderUUID, new LeashInfo(
                info.holderUUIDOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                info.maxDistance(),
                newElasticDistance,
                Math.min(info.keepLeashTicks(), newMaxKeepLeashTicks), // 限制剩余Tick不超过新最大值
                newMaxKeepLeashTicks
        ));
        return true;
    }

    @Override
    public boolean setElasticDistance(BlockPos knotPos, double newElasticDistance, int newMaxKeepLeashTicks) {
        LeashInfo info = leashKnots.get(knotPos);
        if (info == null || info.blockPosOpt().isEmpty() || info.holderIdOpt().isEmpty()) return false;

        leashKnots.put(knotPos, new LeashInfo(
                info.blockPosOpt().get(),
                info.holderIdOpt().get(),
                info.reserved(),
                info.attachOffset(),
                info.maxDistance(),
                newElasticDistance,
                Math.min(info.keepLeashTicks(), newMaxKeepLeashTicks), // 限制剩余Tick不超过新最大值
                newMaxKeepLeashTicks
        ));
        return true;
    }

    /**
     * 计算拴绳拉力（防抖动逻辑）
     */
    @Override
    public void applyLeashForces() {
        Vec3 combinedForce = Vec3.ZERO; // 初始化合力向量

        // 计算所有拴绳的合力
        for (Map.Entry<UUID, LeashInfo> entry : leashHolders.entrySet()) {
            Vec3 force = calculateLeashForceForUUID(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
            }
        }

        for (Map.Entry<BlockPos, LeashInfo> entry : leashKnots.entrySet()) {
            Vec3 force = calculateLeashForceForBlockPos(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
            }
        }

        boolean hasForce = !combinedForce.equals(Vec3.ZERO);
        Entity finalApplyEntity = RindingLeash.getFinalEntityForLeashIfForce(entity, hasForce);
        if (hasForce) {

            if (finalApplyEntity instanceof ServerPlayer player) {
                RindingLeash.applyForceToPlayer(player, combinedForce);
                return;
            } else {
                finalApplyEntity.setDeltaMovement(finalApplyEntity.getDeltaMovement().add(combinedForce));
                finalApplyEntity.hurtMarked = true;
            }

            RindingLeash.protectAnimalMovement(finalApplyEntity, true);
        } else {
            RindingLeash.protectAnimalMovement(finalApplyEntity, false);
        }
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
                SuperLeadRope.logger.error("Could not apply leash forces for {}, because it is not found(it will be removed from list).", uuid);
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
        Vec3 entityPos = entity.position().add(info.attachOffset());
        double distance = holderPos.distanceTo(entityPos);
        double extremeSnapDist = info.maxDistance() * LEASH_EXTREME_SNAP_DIST_FACTOR;

        // 1. 检查是否超出断裂距离
        if (distance > extremeSnapDist) {
            if (info.keepLeashTicks() > 0) {
                // 计算临界拉力
                Vec3 pullForce = calculateCriticalPullForce(holderPos, entityPos, distance, info);
                entry.setValue(info.decrementKeepLeashTicks());
                return pullForce;
            }
            // 断裂
            removeLeash(holder);
            return null;
        }

        // 2. 正常弹性拉力逻辑
        Vec3 pullForce = Vec3.ZERO;
        if (distance > info.elasticDistance()) {
            pullForce = calculatePullForce(holderPos, entityPos, distance, info);

            // 生物添加跟随逻辑（保持不变）
            if(entity instanceof Mob mob) {
                Vec3 vec3 = (new Vec3(holder.getX() - entity.getX(), holder.getY() - entity.getY(), holder.getZ() - entity.getZ()))
                        .normalize()
                        .scale(Math.max(distance - 2.0F, 0.0F));
                double speed = mob instanceof Llama ? 2.0 : 1.0;
                mob.getNavigation().moveTo(entity.getX() + vec3.x, entity.getY() + vec3.y, entity.getZ() + vec3.z, speed);
            }
        }

        // 3. 重置缓冲Tick
        if (distance <= info.maxDistance() && info.keepLeashTicks() < info.maxKeepLeashTicks()) {
            entry.setValue(info.resetKeepLeashTicks());
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
                (distance - info.elasticDistance()) * pullStrength * SPRING_DAMPENING
        );

        return new Vec3(
                pullForce.x * AXIS_SPECIFIC_ELASTICITY.x,
                pullForce.y * AXIS_SPECIFIC_ELASTICITY.y,
                pullForce.z * AXIS_SPECIFIC_ELASTICITY.z
        );
    }

    // 计算临界拉力（保持不变）
    private @NotNull Vec3 calculateCriticalPullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance, @NotNull LeashInfo info) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();
        double excessRatio = (distance - info.maxDistance()) / info.maxDistance();
        double pullStrength = 1.0 + excessRatio * 2.0;

        Vec3 pullForce = pullDirection.scale(
                (distance - info.elasticDistance()) * pullStrength * SPRING_DAMPENING
        );

        return new Vec3(
                pullForce.x * AXIS_SPECIFIC_ELASTICITY.x,
                pullForce.y * AXIS_SPECIFIC_ELASTICITY.y,
                pullForce.z * AXIS_SPECIFIC_ELASTICITY.z
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
        if (removed)
            markForSync();
        return removed;
    }

    @Override
    public boolean removeLeash(BlockPos knotPos) {
        boolean removed = leashKnots.remove(knotPos) != null;
        if (removed)
            markForSync();
        return removed;
    }

    @Override
    public void removeAllLeashes() {
        leashHolders.clear();
        leashKnots.clear();
        markForSync();
    }

    @Override
    public void removeAllHolderLeashes() {
        leashHolders.clear();
        markForSync();
    }

    @Override
    public void removeAllKnotLeashes() {
        leashKnots.clear();
        markForSync();
    }

    @Override
    public boolean transferLeash(Entity holder, Entity newHolder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                transferLeash(superLeashKnotEntity.getPos(), newHolder) :
                transferLeash(holder.getUUID(), newHolder);
    }

    @Override
    public boolean transferLeash(Entity holder, Entity newHolder, ItemStack stack) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                transferLeash(superLeashKnotEntity.getPos(), newHolder, stack) :
                transferLeash(holder.getUUID(), newHolder, stack);
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
        markForSync();
        return true;
    }
    @Override
    public boolean transferLeash(UUID oldHolderUUID, Entity newHolder, ItemStack stack) {
        LeashInfo info = leashHolders.remove(oldHolderUUID);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, stack.getDescriptionId());
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, stack.getDescriptionId());
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
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
        markForSync();
        return true;
    }

    @Override
    public boolean transferLeash(BlockPos knotPos, Entity newHolder, ItemStack stack) {
        LeashInfo info = leashKnots.remove(knotPos);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, stack.getDescriptionId());
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, stack.getDescriptionId());
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
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
        infoTag.putInt("HolderX", blockPos.getX());
        infoTag.putInt("HolderY", blockPos.getY());
        infoTag.putInt("HolderZ", blockPos.getZ());
        return getCommonCompoundTag(info, infoTag);
    }

    private static @NotNull CompoundTag getCommonCompoundTag(@NotNull LeashInfo info, CompoundTag infoTag) {
        if(info.holderIdOpt().isEmpty()) {
            throw new IllegalArgumentException("LeashInfo.intId is empty");
        }
        infoTag.putInt("HolderID", info.holderIdOpt().get());
        infoTag.putString("LeashItem", info.reserved());
        infoTag.putDouble("OffsetX", info.attachOffset().x);
        infoTag.putDouble("OffsetY", info.attachOffset().y);
        infoTag.putDouble("OffsetZ", info.attachOffset().z);
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

    @Override
    public boolean canBeLeashed() {
        return (leashHolders.size() + leashKnots.size()) <= MAX_LEASHES_PER_ENTITY;
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
                    new Vec3(infoTag.getDouble("OffsetX"), infoTag.getDouble("OffsetY"), infoTag.getDouble("OffsetZ")),
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
        if (infoTag.contains("HolderX")) {
            return new LeashInfo(
                    new BlockPos(infoTag.getInt("HolderX"), infoTag.getInt("HolderY"), infoTag.getInt("HolderZ")),
                    infoTag.getInt("HolderID"),
                    infoTag.getString("LeashItem"),
                    new Vec3(infoTag.getDouble("OffsetX"), infoTag.getDouble("OffsetY"), infoTag.getDouble("OffsetZ")),
                    infoTag.getDouble("MaxDistance"),
                    infoTag.contains("ElasticDistance") ? infoTag.getDouble("ElasticDistance") : 6.0,
                    infoTag.getInt("KeepLeashTicks"),
                    infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
            );
        } else
            throw new IllegalArgumentException("Unknown LeashInfo");
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
        if(pEntity == entity) {
            return false;
        } else {
            Optional<LeashInfo> leashInfo = getLeashInfo(pEntity);
            return leashInfo.isEmpty() && (entity.distanceTo(pEntity) <= LEASH_ELASTIC_DIST * LEASH_EXTREME_SNAP_DIST_FACTOR) && canBeLeashed();//距离最大,则不可以被固定或转移
        }
    }
    public static boolean isLeashHolder(@NotNull Entity pEntity, UUID pHolderUUID) {
        AtomicBoolean isTarget = new AtomicBoolean(false);
        pEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(i -> isTarget.set(i.isLeashedBy(pHolderUUID)));
        return isTarget.get();
    }
    public static boolean isLeashHolder(@NotNull Entity pEntity, BlockPos pKnotPos) {
        AtomicBoolean isTarget = new AtomicBoolean(false);
        pEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(i -> isTarget.set(i.isLeashedBy(pKnotPos)));
        return isTarget.get();
    }
    public static boolean isLeashHolder(@NotNull Entity pEntity, Entity pTestHolder) {
        return pTestHolder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                isLeashHolder(pEntity, superLeashKnotEntity.getPos()) :
                isLeashHolder(pEntity, pTestHolder.getUUID());
    }


    // 计算拴绳附着点
    @Contract("_ -> new")
    private @NotNull Vec3 calculateAttachOffset(@NotNull Entity entity) {
        EntityType<?> type = entity.getType();
        if (type == EntityType.HORSE || type == EntityType.DONKEY) {
            return new Vec3(0, 1.4, 0.3);
        } else if (type == EntityType.IRON_GOLEM) {
            return new Vec3(0, 1.8, 0);
        }
        //TODO: 未来自定义配置
        return new Vec3(0, entity.getBbHeight() * 0.8, 0);
    }


}
