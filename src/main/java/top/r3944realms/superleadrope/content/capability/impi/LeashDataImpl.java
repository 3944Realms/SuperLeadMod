/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.content.capability.impi;

import net.minecraft.client.player.LocalPlayer;
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
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.api.event.SuperLeadRopeEvent;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.compat.CurtainCompat;
import top.r3944realms.superleadrope.compat.LuckPermsCompat;
import top.r3944realms.superleadrope.config.LeashConfigManager;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.LeashDataSyncPacket;
import top.r3944realms.superleadrope.network.toClient.UpdatePlayerMovementPacket;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;
import top.r3944realms.superleadrope.util.riding.RindingLeash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
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
 *             <th>距离 ≤ maxDistance * elasticDistanceScale</th>
 *             <th>正常弹性拉力，重置 keepLeashTicks 为最大值</th>
 *         </tr>
 *         <tr>
 *             <th>maxDistance * elasticDistanceScale < distance ≤ 配置extremeSnapFactor * maxDistance * elasticDistanceScale</th>
 *             <th>增强拉力，并减少 keepLeashTicks（每Tick减1）</th>
 *         </tr>
 *         <tr>
 *             <th>distance > 配置extremeSnapFactor * maxDistance * elasticDistanceScale && keepLeashTicks > 0</th>
 *              <th>施加更强拉力并减少Tick</th>
 *         </tr>
 *         <tr>
 *             <th>distance > 配置extremeSnapFactor * maxDistance * elasticDistanceScale && keepLeashTicks == 0</th>
 *              <th>立即断裂</th>
 *         </tr>
 *     </tbody>
 * </table>
 */
@SuppressWarnings("DuplicatedCode")
public class LeashDataImpl implements ILeashData {
    private final Entity entity;
    private boolean needsSync = false;
    private long lastSyncTime;
    @Nullable
    private Double staticMaxDistance;
    private double defaultMaxDistance = 6.0;
    @Nullable
    private Double staticElasticDistanceScale;
    private double defaultElasticDistanceScale = 1.0;
    private final Set<UUID> delayedHolders = new CopyOnWriteArraySet<>();
    private final Map<UUID, LeashInfo> leashHolders = new ConcurrentHashMap<>();
    // 引入解决 绳结不保存导致第二进入持有者不存在的问题
    private final Map<BlockPos, LeashInfo> leashKnots = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Leash data.
     *
     * @param entity the entity
     */
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
    public Entity self() {
        return entity;
    }

    @Override
    public @Nullable Double getStaticMaxDistance() {
        return staticMaxDistance;
    }

    @Override
    public void setStaticMaxDistance(@Nullable Double distance) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(distance)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, getStaticMaxDistance(), distance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return;
        staticMaxDistance = distance;
    }

    @Override
    public double getDefaultMaxDistance() {
        boolean isNotClient = !this.entity.level().isClientSide();
        if (isNotClient) {
            defaultMaxDistance = CommonEventHandler.leashConfigManager.getMaxLeashLength();
        }
        return defaultMaxDistance;
    }

    @Override
    public double getCurrentMaxDistance() {
        return staticMaxDistance != null ? staticMaxDistance : defaultMaxDistance;
    }

    @Override
    public void updateAllMaxDistance() {
        leashHolders.forEach((uuid, leashInfo) -> {
            if (leashInfo.isNeedUpdateDistance()) {
                setMaxDistanceInner(uuid, getCurrentMaxDistance());
            }
        });
        leashKnots.forEach((blockPos, leashInfo) -> {
           if (leashInfo.isNeedUpdateDistance()) {
               setMaxDistanceInner(blockPos, getCurrentMaxDistance());
           }
        });
        markForSync();
    }

    @Override
    public @Nullable Double getStaticElasticDistanceScale() {
        return staticElasticDistanceScale;
    }

    @Override
    public void setStaticElasticDistanceScale(@Nullable Double scale) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, getStaticElasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return;
        staticElasticDistanceScale = scale;
    }

    @Override
    public double getDefaultElasticDistanceScale() {
        boolean isNotClient = !this.entity.level().isClientSide();
        if (isNotClient) {
            defaultElasticDistanceScale = CommonEventHandler.leashConfigManager.getElasticDistanceScale();
        }
        return defaultElasticDistanceScale;
    }

    @Override
    public double getCurrentElasticDistanceScale() {
        return staticElasticDistanceScale != null ? staticElasticDistanceScale : defaultElasticDistanceScale;
    }

    @Override
    public void updateAllElasticDistanceScale() {
        leashHolders.forEach((uuid, leashInfo) -> {
            if (leashInfo.isNeedUpdateScale()) {
                setElasticDistanceScaleInner(uuid, getCurrentElasticDistanceScale());
            }
        });
        leashKnots.forEach((blockPos, leashInfo) -> {
            if (leashInfo.isNeedUpdateScale()) {
                setElasticDistanceScaleInner(blockPos, getCurrentMaxDistance());
            }
        });
        markForSync();
    }

    @Override
    public boolean addLeash(Entity holder) {
        return addLeash(holder, (Double) null);
    }

    @Override
    public boolean addLeash(Entity holder, String reserved) {
        return addLeash(holder, null, reserved);
    }

    // 添加拴绳（支持自定义最大长度）
    @Override
    public boolean addLeash(Entity holder, Double maxDistance) {
        return addLeash(holder, maxDistance, null, 0, "");
    }

    @Override
    public boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale) {
        return addLeash(holder, maxDistance, elasticDistanceScale, 0);
    }

    // 添加拴绳（支持自定义最大长度和弹性距离）
    @Override
    public boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepLeashTicks) {
        return addLeash(holder, maxDistance, elasticDistanceScale, maxKeepLeashTicks, "");
    }

    @Override
    public boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, String reserved) {
        return addLeash(holder, maxDistance, elasticDistanceScale, 0, reserved);
    }

    // 添加拴绳（支持自定义最大长度 + reserved 字段）
    @Override
    public boolean addLeash(Entity holder, Double maxDistance, String reserved) {
        return addLeash(holder, maxDistance, null, 0, reserved);
    }

    // 添加拴绳（最终实现：支持最大长度、弹性距离、保持 Tick、reserved）
    @Override
    public boolean addLeash(Entity holder, Double maxDistance,
                            Double elasticDistanceScale, int maxKeepLeashTicks, String reserved) {
        if(holder == entity) return false;
        boolean isSuperKnot = holder instanceof SuperLeashKnotEntity;
        if ((!isSuperKnot && leashHolders.containsKey(holder.getUUID()))
                || (isSuperKnot && leashKnots.containsKey(((SuperLeashKnotEntity) holder).getPos()))) {
            return false;
        }
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(maxDistance) || !LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(elasticDistanceScale)) return false;
        SuperLeadRopeEvent.AddLeash event = new SuperLeadRopeEvent.AddLeash(this.entity, holder, maxDistance, elasticDistanceScale, maxKeepLeashTicks);
        //再次检查
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getMaxLeashDistance()) && LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getElasticDistanceScale()))) return false;
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
                event.getMaxLeashDistance(),
                event.getElasticDistanceScale(),
                event.getMaxKeepLeashTicks(),
                maxKeepLeashTicks
        );

        if (isSuperKnot) {
            leashKnots.put(((SuperLeashKnotEntity) holder).getPos(), info);
        } else {
            leashHolders.put(holder.getUUID(), info);
        }
        LeashStateInnerAPI.Offset.setHolderFor(entity, holder);
        markForSync();
        return true;
    }

    // 使用已有的 LeashInfo 添加拴绳（直接走最终实现）
    @Override
    public void addLeash(Entity holder, @NotNull LeashInfo leashInfo) {
        addLeash(holder,
                leashInfo.maxDistance(),
                leashInfo.elasticDistanceScale(),
                leashInfo.maxKeepLeashTicks(),
                leashInfo.reserved()
        );
    }

    @Override
    public void addDelayedLeash(@NotNull Player holderPlayer) {
        delayedHolders.add(holderPlayer.getUUID());
    }

    @Override
    public void removeDelayedLeash(UUID onceHolderUUID) {
        delayedHolders.remove(onceHolderUUID);
    }

    private <K> boolean updateLeashInfo(
            @NotNull Map<K, LeashInfo> map,
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

    private <K> void updateLeashInfoInner(
            @NotNull Map<K, LeashInfo> map,
            K key,
            Function<LeashInfo, LeashInfo> updater
    ) {
        LeashInfo old = map.get(key);
        if (old == null || old.holderIdOpt().isEmpty()) return;

        LeashInfo updated = updater.apply(old);
        if (updated == null) return;

        map.put(key, updated);
    }

    @Override
    public boolean setMaxDistance(Entity holder, @Nullable Double newMaxDistance) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), newMaxDistance) :
                setMaxDistance(holder.getUUID(), newMaxDistance);
    }

    @Override
    public boolean setMaxDistance(Entity holder, Double distance, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), distance, reserved) :
                setMaxDistance(holder.getUUID(),distance , reserved);
    }

    @Override
    public boolean setMaxDistance(Entity holder, @Nullable Double newMaxDistance, int newMaxKeepLeashTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), newMaxDistance, newMaxKeepLeashTicks) :
                setMaxDistance(holder.getUUID(), newMaxDistance, newMaxKeepLeashTicks);
    }

    @Override
    public boolean setMaxDistance(Entity holder, @Nullable Double newMaxDistance, int maxKeepTicks, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxDistance(superLeashKnotEntity.getPos(), newMaxDistance, maxKeepTicks, reserved) :
                setMaxDistance(holder.getUUID(), newMaxDistance, maxKeepTicks, reserved);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, @Nullable Double newMaxDistance) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, Double newMaxDistance, String reserved) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    /**
     * Sets max distance inner.
     *
     * @param holderUUID     the holder uuid
     * @param newMaxDistance the new max distance
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setMaxDistanceInner(UUID holderUUID, @Nullable Double newMaxDistance) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return;
        updateLeashInfoInner(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, @Nullable Double newMaxDistance, int newMaxKeepLeashTicks) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(UUID holderUUID, @Nullable Double newMaxDistance, int maxKeepTicks, String reserved) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                event.getNewValue(),
                old.elasticDistanceScale(),
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, @Nullable Double newMaxDistance) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, Double newMaxDistance, String reserved) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    /**
     * Sets max distance inner.
     *
     * @param knotPos        the knot pos
     * @param newMaxDistance the new max distance
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setMaxDistanceInner(BlockPos knotPos, @Nullable Double newMaxDistance) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return;
        updateLeashInfoInner(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, @Nullable Double newMaxDistance, int newMaxKeepLeashTicks) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                event.getNewValue(),
                old.elasticDistanceScale(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxDistance(BlockPos knotPos, @Nullable Double newMaxDistance, int maxKeepTicks, String reserved) {
        if (!LeashConfigManager.MAX_DISTANCE_CHECK.test(newMaxDistance)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).maxDistance(), newMaxDistance, SuperLeadRopeEvent.ModifyValue.Type.MAX_DISTANCE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.MAX_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                event.getNewValue(),
                old.elasticDistanceScale(),
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @Override
    public boolean setElasticDistanceScale(Entity holder, @Nullable Double scale) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistanceScale(superLeashKnotEntity.getPos(), scale) :
                setElasticDistanceScale(holder.getUUID(), scale);
    }

    // 动态修改弹性距离
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(UUID holderUUID, @Nullable Double scale) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    /**
     * Sets elastic distance scale inner.
     *
     * @param holderUUID the holder uuid
     * @param scale      the scale
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setElasticDistanceScaleInner(UUID holderUUID, @Nullable Double scale) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return;
        updateLeashInfoInner(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(BlockPos knotPos, @Nullable Double scale) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    /**
     * Sets elastic distance scale inner.
     *
     * @param knotPos the knot pos
     * @param scale   the scale
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void setElasticDistanceScaleInner(BlockPos knotPos, @Nullable Double scale) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return;
        updateLeashInfoInner(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @Override
    public boolean setElasticDistanceScale(Entity holder, @Nullable Double scale, int newMaxKeepLeashTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistanceScale(superLeashKnotEntity.getPos(), scale, newMaxKeepLeashTicks) :
                setElasticDistanceScale(holder.getUUID(), scale, newMaxKeepLeashTicks);
    }

    @Override
    public boolean setElasticDistanceScale(Entity holder, @Nullable Double scale, int maxKeepTicks, String reserved) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setElasticDistanceScale(superLeashKnotEntity.getPos(), scale, maxKeepTicks, reserved) :
                setElasticDistanceScale(holder.getUUID(), scale, maxKeepTicks, reserved);
    }

    // 动态修改弹性距离
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(UUID holderUUID, @Nullable Double scale, int newMaxKeepLeashTicks) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(UUID holderUUID, @Nullable Double scale, int maxKeepTicks, String reserved) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, leashHolders.get(holderUUID).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                old.maxDistance(),
                event.getNewValue(),
                Math.min(old.keepLeashTicks(), maxKeepTicks),
                maxKeepTicks
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(BlockPos knotPos, @Nullable Double scale, int newMaxKeepLeashTicks) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                event.getNewValue(),
                old.keepLeashTicks(),
                old.maxKeepLeashTicks()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setElasticDistanceScale(BlockPos knotPos, @Nullable Double scale, int newMaxKeepLeashTicks, String reserved) {
        if (!LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(scale)) return false;
        SuperLeadRopeEvent.ModifyValue<Double> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, leashKnots.get(knotPos).elasticDistanceScale(), scale, SuperLeadRopeEvent.ModifyValue.Type.ELASTIC_DISTANCE_SCALE);
        if (MinecraftForge.EVENT_BUS.post(event) || event.isModified() && !(LeashConfigManager.ELASTIC_DISTANCE_CHECK.test(event.getNewValue()))) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.blockPosOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                reserved,
                old.maxDistance(),
                event.getNewValue(),
                Math.min(old.keepLeashTicks(), newMaxKeepLeashTicks),
                newMaxKeepLeashTicks
        ));
    }
    @Override
    public void applyLeashForcesClientPlayer() {
        if (entity instanceof ServerPlayer player && CurtainCompat.isNotFakePlayer(player)) return;
        Vec3 combinedForce = Vec3.ZERO;
        Vec3 combinedDirection = Vec3.ZERO;
        Map<Integer, LeashInfo> result = leashHolders.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getValue().holderIdOpt().orElseThrow(),
                        Map.Entry::getValue,
                        (existing, replacement) -> existing
                ));
        // 计算所有拴绳的合力和平均方向
        for (Map.Entry<Integer, LeashInfo> entry : result.entrySet()) {
            Vec3 force = calculateLeashForceForID(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
            }
        }

        for (Map.Entry<BlockPos, LeashInfo> entry : leashKnots.entrySet()) {
            Vec3 force = calculateLeashForceForBlockPos(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
            }
        }
        boolean hasForce = !combinedForce.equals(Vec3.ZERO);
        Entity targetEntity = RindingLeash.getFinalEntityForLeashIfForce(entity, hasForce);
        if(targetEntity instanceof LocalPlayer && hasForce){
            entity.addDeltaMovement(combinedForce);
        }
    }

    /**
     * 计算拴绳拉力（防抖动逻辑）
     */
    @Override
    public void applyLeashForces() {
        Vec3 combinedForce = Vec3.ZERO;
        Vec3 combinedDirection = Vec3.ZERO;
        int validLeashes = 0;
        Map<UUID, LeashInfo> vaildLeashHolders = new HashMap<>();
        Map<BlockPos, LeashInfo> vaildLeashKnots = new HashMap<>();
        // 计算所有拴绳的合力和平均方向
        for (Map.Entry<UUID, LeashInfo> entry : leashHolders.entrySet()) {
            Vec3 force = calculateLeashForceForUUID(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
                validLeashes++;
                vaildLeashHolders.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<BlockPos, LeashInfo> entry : leashKnots.entrySet()) {
            Vec3 force = calculateLeashForceForBlockPos(entry);
            if (force != null) {
                combinedForce = combinedForce.add(force);
                combinedDirection = combinedDirection.add(force.normalize());
                validLeashes++;
                vaildLeashKnots.put(entry.getKey(), entry.getValue());
            }
        }

        boolean hasForce = !combinedForce.equals(Vec3.ZERO);
        Entity targetEntity = RindingLeash.getFinalEntityForLeashIfForce(entity, hasForce);
        if (targetEntity != null && hasForce) {
            SuperLeadRopeEvent.hasFocus hasFocus = new SuperLeadRopeEvent.hasFocus(this.entity, targetEntity, combinedForce, vaildLeashHolders, vaildLeashKnots);
            if (MinecraftForge.EVENT_BUS.post(hasFocus)) return;
            combinedForce = hasFocus.getCombinedForce();
            // 玩家与普通实体统一力应用
            if (targetEntity instanceof ServerPlayer player && CurtainCompat.isNotFakePlayer(player) ) {
                // 是真实玩家则交给客户端自行处理拴绳逻辑
                // DO NOTHING
                if(targetEntity != entity){
                    NetworkHandler.sendToPlayer(new UpdatePlayerMovementPacket(UpdatePlayerMovementPacket.Operation.ADD, combinedForce), player);
                }
                return;
            } else {
                applyForceToNonPlayerEntity(targetEntity, combinedForce, validLeashes, combinedDirection);
            }
        }

        // 保护动物移动
        RindingLeash.protectAnimalMovement(targetEntity, hasForce);
    }
    /**
     * 给非玩家实体施加拴绳力（取消阻力）
     */
    private void applyForceToNonPlayerEntity(@NotNull Entity entity, Vec3 combinedForce,
                                             int validLeashes, Vec3 combinedDirection) {
        // 直接施加合力，不再加阻力
        entity.setDeltaMovement(entity.getDeltaMovement().add(combinedForce));
        entity.hurtMarked = true;

        // 如果是生物，处理导航
        if (entity instanceof Mob mob) {
            if(mob.tickCount % 5 == 0){
                if (validLeashes > 0 && canMobMove(mob)) {
                    moveMobTowardsCombinedDirection(mob, combinedDirection, combinedForce.length());
                } else {
                    mob.getNavigation().stop();
                }
            }
        }
    }

    /**
     * 检查生物是否能够移动
     */
    private boolean canMobMove(@NotNull Mob mob) {
        // 检查各种无法移动的情况
        return !mob.isDeadOrDying() &&         // 没有死亡或濒死
                mob.isEffectiveAi() &&          // AI有效
                mob.getDeltaMovement().lengthSqr() < 25.0; // 降低速度阈值
    }

    /**
     * 让生物朝着合力方向移动
     */
    private void moveMobTowardsCombinedDirection(Mob mob, @NotNull Vec3 combinedDirection, double forceMagnitude) {
        if (combinedDirection.equals(Vec3.ZERO) || !canMobMove(mob)) {
            return;// 减少不必要的stop调用
        }

        // 计算平均方向（简化计算）
        Vec3 averageDirection = combinedDirection.normalize();

        // 根据力的大小调整移动速度
        double speed = calculateMobSpeed(mob, forceMagnitude);

        // 计算目标位置（缩短距离）
        Vec3 targetPos = mob.position().add(averageDirection.scale(2.0)); // 从3格减少到2格

        // 使用快速可达性检查
        if (isPositionQuickReachable(mob, targetPos)) {
            // 设置移动目标
            mob.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, speed);

            // 降低朝向更新频率
            if (mob.tickCount % 10 == 0) {
                mob.getLookControl().setLookAt(targetPos);
            }
        }
    }
    /**
     * 快速可达性检查（替代原来的路径查找）
     */
    private boolean isPositionQuickReachable(@NotNull Mob mob, Vec3 targetPos) {
        // 1. 距离检查（缩短最大距离）
        double distance = mob.position().distanceTo(targetPos);
        if (distance > 10.0) return false; // 从20格减少到10格

        // 2. 高度差检查
        double heightDiff = Math.abs(targetPos.y - mob.position().y);
        if (heightDiff > 2.0) return false;

        // 3. 简单的视线检查
        // 如果性能仍然有问题，可以注释掉这个检查
        if (mob.level().isLoaded(BlockPos.containing(targetPos))) {
            BlockHitResult hitResult = mob.level().clip(new ClipContext(
                    mob.position(),
                    targetPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    mob
            ));
            return hitResult.getType() == HitResult.Type.MISS;
        }

        return true;
    }

//    /**
//     * 检查位置是否可达
//     */
//    private boolean isPositionReachable(Mob mob, Vec3 targetPos) {
//        // 简单的距离检查
//        if (mob.position().distanceTo(targetPos) > 20.0) return false;// 距离太远
//        // 检查是否有导航路径
//        Path path = mob.getNavigation().createPath(targetPos.x, targetPos.y, targetPos.z, 0);
//        return path != null && !path.isDone();
//    }

    /**
     * 根据生物类型和力的大小计算移动速度（优化版）
     */
    private double calculateMobSpeed(Mob mob, double forceMagnitude) {
        double baseSpeed = mob instanceof Llama ? 1.5 : 1.0; // 降低基础速度

        // 力越大，移动速度越快（降低加速效果）
        double forceFactor = Math.min(forceMagnitude * 0.3, 1.5); // 降低加速系数和上限

        return baseSpeed * (1.0 + forceFactor);
    }
    private @Nullable Vec3 calculateLeashForceForID(Map.@NotNull Entry<Integer, LeashInfo> entry) {
        Integer id = entry.getKey();
        Entity idHolder = entity.level().getEntity(id);
        if (idHolder != null) {
            return calculateLeashForce(idHolder, entry);
        }
        return null;
    }

    /**
     * 为UUID拴绳计算力
     */
    private @Nullable Vec3 calculateLeashForceForUUID(Map.@NotNull Entry<UUID, LeashInfo> entry) {
        UUID uuid = entry.getKey();
        Entity uuidHolder = ((ServerLevel) entity.level()).getEntity(uuid);
        if (uuidHolder != null) {
            return calculateLeashForce(uuidHolder, entry);
        } else {
            if (!delayedHolders.contains(uuid)) {
                SuperLeadRope.logger.warn("Could not apply leash forces for {}, because it is not found(it will be removed from list).", uuid);
                leashHolders.remove(uuid); //保持系统稳定的移除，不走事件
            }
            return null;
        }
    }

    /**
     * 为方块位置拴绳计算力
     */
    private Vec3 calculateLeashForceForBlockPos(Map.@NotNull Entry<BlockPos, LeashInfo> entry) {
        SuperLeashKnotEntity orCreateKnot = SuperLeashKnotEntity.getOrCreateKnot(entity.level(), entry.getKey());
        return calculateLeashForce(orCreateKnot, entry);
    }

    private @Nullable Vec3 calculateLeashForce(@NotNull Entity holder, Map.@NotNull Entry<?, LeashInfo> entry) {
        Vec3 holderPos = holder.position().add(0, holder.getBbHeight() * 0.7, 0);
        LeashInfo info = entry.getValue();
        Vec3 entityPos = entity.position();
        double distance = holderPos.distanceTo(entityPos);

        double maxDistance = info.maxDistance() == null ? getCurrentMaxDistance() : info.maxDistance();
        double extremeSnapDist = maxDistance * CommonEventHandler.leashConfigManager.getExtremeSnapFactor();
        double elasticDistanceScale = info.elasticDistanceScale() == null ? getCurrentElasticDistanceScale() : info.elasticDistanceScale();
        // 修正：计算弹性限度距离
        double elasticLimitDistance = maxDistance * elasticDistanceScale;
        // 1. 检查是否超出断裂距离
        if (distance > extremeSnapDist) {
            if (info.keepLeashTicks() >= 0) {
                // 计算临界拉力
                Vec3 pullForce = calculateCriticalPullForce(holderPos, entityPos, distance, maxDistance, elasticLimitDistance);
                entry.setValue(info.decrementKeepTicks());
                MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.keepNotBreakTick(this.entity, entry.getValue().keepLeashTicks(), holder, entry));
                if (entry.getValue().keepLeashTicks() > 0) return pullForce;
            }
            // 断裂
            removeLeash(holder);
            entity.level().playSound(null, holder.getOnPos(), SLPSoundEvents.LEAD_BREAK.get(), SoundSource.PLAYERS);
            return null;
        }



        // 2. 正常弹性拉力逻辑
        Vec3 pullForce = Vec3.ZERO;
        if (distance > elasticLimitDistance) {
            pullForce = calculatePullForce(holderPos, entityPos, distance, maxDistance, elasticLimitDistance);
        }

        // 3. 重置缓冲Tick
        if (distance <= extremeSnapDist && info.keepLeashTicks() < info.maxKeepLeashTicks()) {
            entry.setValue(info.resetKeepTicks());
        }

        return pullForce;
    }

    // 计算正常拉力
    @Contract("_, _, _, _, _ -> new")
    private @NotNull Vec3 calculatePullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance, double maxDistance, double elasticLimitDistance) {
        double extremeSnapDistance = maxDistance * CommonEventHandler.leashConfigManager.getExtremeSnapFactor();

        if (distance <= elasticLimitDistance) {
            // 场景1：距离 ≤ elasticLimitDistance - 正常弹性拉力
            return calculateNormalPullForce(holderPos, entityPos, distance, elasticLimitDistance);
        } else if (distance <= extremeSnapDistance) {
            // 场景2：elasticLimitDistance < distance ≤ extremeSnapDistance - 增强拉力
            return calculateEnhancedPullForce(holderPos, entityPos, distance, elasticLimitDistance, extremeSnapDistance);
        } else {
            // 场景3：distance > extremeSnapDistance - 临界拉力
            return calculateCriticalPullForce(holderPos, entityPos, distance, elasticLimitDistance, extremeSnapDistance);
        }
    }

    private @NotNull Vec3 calculateNormalPullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance, double elasticLimitDistance) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();
        double basePullStrength = 0.2;

        // 轻微拉力，保持弹性
        double excessDistance = Math.max(0, distance - (elasticLimitDistance * 0.8)); // 在接近极限时开始轻微拉动
        Vec3 pullForce = pullDirection.scale(
                excessDistance * basePullStrength * CommonEventHandler.leashConfigManager.getSpringDampening()
        );

        return applyElasticityFactors(pullForce);
    }
    private @NotNull Vec3 calculateEnhancedPullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance,
                                                     double elasticLimitDistance, double extremeSnapDistance) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();

        // 计算超出弹性限度的比例
        double excessRatio = (distance - elasticLimitDistance) / (extremeSnapDistance - elasticLimitDistance);

        // 拉力强度随超出比例增加而增强
        double pullStrength = 0.5 + excessRatio * 1.5; // 从0.5线性增加到2.0

        double excessDistance = distance - elasticLimitDistance;
        Vec3 pullForce = pullDirection.scale(
                excessDistance * pullStrength * CommonEventHandler.leashConfigManager.getSpringDampening()
        );

        return applyElasticityFactors(pullForce);
    }

    // 计算临界拉力（修正版）
    private @NotNull Vec3 calculateCriticalPullForce(@NotNull Vec3 holderPos, Vec3 entityPos, double distance,
                                                     double elasticLimitDistance, double extremeSnapDistance) {
        Vec3 pullDirection = holderPos.subtract(entityPos).normalize();

        // 计算超出临界距离的比例
        double criticalExcessRatio = (distance - extremeSnapDistance) / extremeSnapDistance;

        // 在临界状态下使用更强的拉力
        double pullStrength = 2.0 + criticalExcessRatio * 3.0; // 从2.0开始，随超出比例增加

        double excessDistance = distance - elasticLimitDistance;
        Vec3 pullForce = pullDirection.scale(
                excessDistance * pullStrength * CommonEventHandler.leashConfigManager.getSpringDampening()
        );

        return applyElasticityFactors(pullForce);
    }

    private @NotNull Vec3 applyElasticityFactors(@NotNull Vec3 force) {
        return new Vec3(
                force.x * CommonEventHandler.leashConfigManager.getXElasticity(),
                force.y * CommonEventHandler.leashConfigManager.getYElasticity(),
                force.z * CommonEventHandler.leashConfigManager.getZElasticity()
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
        if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.RemoveLeash(this.entity, holderUUID))) return false;
        boolean removed = leashHolders.remove(holderUUID) != null;
        if (removed) {
            LeashStateInnerAPI.Operations.detach(entity, holderUUID);
            markForSync();
        }
        return removed;
    }

    private void removeLeashInner(BlockPos knotPos) {
        if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.RemoveLeash(this.entity, knotPos))) return;
        boolean removed = leashKnots.remove(knotPos) != null;
        if (removed) {
            LeashStateInnerAPI.Operations.detach(entity, knotPos);
        }
    }

    private void removeLeashInner(UUID holderUUID) {
        if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.RemoveLeash(this.entity, holderUUID))) return;
        boolean removed = leashHolders.remove(holderUUID) != null;
        if (removed) {
            LeashStateInnerAPI.Operations.detach(entity, holderUUID);
        }
    }

    @Override
    public boolean removeLeash(BlockPos knotPos) {
        if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.RemoveLeash(this.entity, knotPos))) return false;
        boolean removed = leashKnots.remove(knotPos) != null;
        if (removed) {
            LeashStateInnerAPI.Operations.detach(entity, knotPos);
        }
        return removed;
    }

    @Override
    public void removeAllLeashes() {
        removeAllHolderLeashesInner();
        removeAllKnotLeashes();
        markForSync();
    }

    private void removeAllHolderLeashesInner() {
       leashHolders.forEach((uuid, leashHolder) -> removeLeashInner(uuid));
    }

    private void removeAllKnotLeashesInner() {
        leashKnots.forEach((blockPos, leashHolder) -> removeLeashInner(blockPos));
    }

    @Override
    public void removeAllHolderLeashes() {
        removeAllHolderLeashesInner();
        markForSync();
    }

    @Override
    public void removeAllKnotLeashes() {
        removeAllKnotLeashesInner();
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
        LeashInfo preInfo = leashHolders.get(oldHolderUUID);
        if(preInfo == null) return false;
        SuperLeadRopeEvent.TransferLeash event = new SuperLeadRopeEvent.TransferLeash(this.entity, oldHolderUUID, newHolder, preInfo.maxKeepLeashTicks());
        if(MinecraftForge.EVENT_BUS.post(event)) return false;
        LeashInfo info = leashHolders.remove(oldHolderUUID);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, event.getMaxKeepLeashTicks());
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, event.getMaxKeepLeashTicks());
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateInnerAPI.Operations.transfer(entity, oldHolderUUID, newHolder);
        markForSync();
        return true;
    }
    @Override
    public boolean transferLeash(UUID oldHolderUUID, Entity newHolder, String reserved) {
        LeashInfo preInfo = leashHolders.get(oldHolderUUID);
        if(preInfo == null) return false;
        SuperLeadRopeEvent.TransferLeash event = new SuperLeadRopeEvent.TransferLeash(this.entity, oldHolderUUID, newHolder, preInfo.maxKeepLeashTicks());
        if(MinecraftForge.EVENT_BUS.post(event)) return false;
        LeashInfo info = leashHolders.remove(oldHolderUUID);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, event.getMaxKeepLeashTicks(), reserved);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, event.getMaxKeepLeashTicks(), reserved);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateInnerAPI.Operations.transfer(entity, oldHolderUUID, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean transferLeash(BlockPos knotPos, Entity newHolder) {
        LeashInfo preInfo = leashKnots.get(knotPos);
        if(preInfo == null) return false;
        SuperLeadRopeEvent.TransferLeash event = new SuperLeadRopeEvent.TransferLeash(this.entity, knotPos, newHolder, preInfo.maxKeepLeashTicks());
        if (MinecraftForge.EVENT_BUS.post(event)) return false;
        LeashInfo info = leashKnots.remove(knotPos);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, event.getMaxKeepLeashTicks());
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, event.getMaxKeepLeashTicks());
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateInnerAPI.Operations.transfer(entity, knotPos, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean transferLeash(BlockPos knotPos, Entity newHolder, String reserved) {
        LeashInfo preInfo = leashKnots.get(knotPos);
        if(preInfo == null) return false;
        SuperLeadRopeEvent.TransferLeash event = new SuperLeadRopeEvent.TransferLeash(this.entity, knotPos, newHolder, preInfo.maxKeepLeashTicks());
        if(MinecraftForge.EVENT_BUS.post(event)) return false;
        LeashInfo info = leashKnots.remove(knotPos);
        if (info == null || newHolder == null) return false;
        if(newHolder instanceof SuperLeashKnotEntity superLeashKnotEntity) {
            LeashInfo leashInfo = info.transferHolder(superLeashKnotEntity, event.getMaxKeepLeashTicks(), reserved);
            leashKnots.put(superLeashKnotEntity.getPos(), leashInfo);
        } else {
            LeashInfo leashInfo = info.transferHolder(newHolder, event.getMaxKeepLeashTicks(), reserved);
            leashHolders.put(newHolder.getUUID(), leashInfo);
        }
        LeashStateInnerAPI.Operations.transfer(entity, knotPos, newHolder);
        markForSync();
        return true;
    }

    @Override
    public boolean setMaxKeepTicks(Entity holder, int maxKeepTicks) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                setMaxKeepTicks(superLeashKnotEntity.getPos(), maxKeepTicks) :
                setMaxKeepTicks(holder.getUUID(), maxKeepTicks);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxKeepTicks(UUID holderUUID, int maxKeepTicks) {
        if (maxKeepTicks < 0) return false;
        LeashInfo info = leashHolders.get(holderUUID);
        if (info == null) return false;
        SuperLeadRopeEvent.ModifyValue<Integer> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, holderUUID, info.keepLeashTicks(), maxKeepTicks, SuperLeadRopeEvent.ModifyValue.Type.MAX_KEEP_LEASH_TICKS);
        if (MinecraftForge.EVENT_BUS.post(event) && (event.getNewValue() == null || event.getNewValue() < 0)) return false;
        return updateLeashInfo(leashHolders, holderUUID, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                old.elasticDistanceScale(),
                event.getNewValue(),
                event.getNewValue()
        ));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    public boolean setMaxKeepTicks(BlockPos knotPos, int maxKeepTicks) {
        if (maxKeepTicks < 0) return false;
        LeashInfo info = leashKnots.get(knotPos);
        if (info == null) return false;
        SuperLeadRopeEvent.ModifyValue<Integer> event = new SuperLeadRopeEvent.ModifyValue<>(this.entity, knotPos, info.keepLeashTicks(), maxKeepTicks, SuperLeadRopeEvent.ModifyValue.Type.MAX_KEEP_LEASH_TICKS);
        if (MinecraftForge.EVENT_BUS.post(event) && (event.getNewValue() == null || event.getNewValue() < 0)) return false;
        return updateLeashInfo(leashKnots, knotPos, old -> new LeashInfo(
                old.holderUUIDOpt().get(),
                old.holderIdOpt().get(),
                old.marks(),
                old.reserved(),
                old.maxDistance(),
                old.elasticDistanceScale(),
                event.getNewValue(),
                event.getNewValue()
        ));
    }

    @Override
    public int getMaxKeepTicks(Entity holder) {
        return holder instanceof SuperLeashKnotEntity superLeashKnotEntity ?
                getMaxKeepTicks(superLeashKnotEntity.getPos()) :
                getMaxKeepTicks(holder.getUUID());
    }

    @Override
    public int getMaxKeepTicks(UUID holderUUID) {
        return Optional.ofNullable(leashHolders.get(holderUUID)).map(LeashInfo::maxKeepLeashTicks).orElse(-1);
    }

    @Override
    public int getMaxKeepTicks(BlockPos knotPos) {
        return Optional.ofNullable(leashKnots.get(knotPos)).map(LeashInfo::maxKeepLeashTicks).orElse(-1);
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

    /**
     * Is leashable boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
//只能系在这些实体上，在这里，其它情况一律忽略
    //TODO: 未来更新计划 - 标签支持控制
    public static boolean isLeashable(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof Boat || entity instanceof AbstractMinecart;
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
    public Collection<LeashInfo> getAllHolderLeashes() {
        return Collections.unmodifiableCollection(leashHolders.values());
    }

    @Override
    public Collection<LeashInfo> getAllKnotLeashes() {
        return Collections.unmodifiableCollection(leashKnots.values());
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
        if (staticMaxDistance != null) tag.putDouble("StaticMaxDistance", staticMaxDistance);
        tag.putDouble("DefaultMaxDistance", getDefaultMaxDistance());
        if (staticElasticDistanceScale != null) tag.putDouble("StaticElasticDistanceScale", staticElasticDistanceScale);
        tag.putDouble("DefaultElasticDistanceScale", getDefaultElasticDistanceScale());
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
        CompoundTag marks = new CompoundTag();
        Set<String> markSet = info.marks();
        if (!markSet.isEmpty()) {
            marks.putInt("MarkSize", markSet.size());
            int i = 0;
            for (String mark : markSet) {
                marks.putString("Mark" + i++, mark);
            }
            infoTag.put("Marks", marks);
        }
        infoTag.putString("Reserved", info.reserved());
        if (info.maxDistance() != null) {
            infoTag.putDouble("MaxDistance", info.maxDistance());
        }
        if (info.elasticDistanceScale() != null) {
            infoTag.putDouble("ElasticDistanceScale", info.elasticDistanceScale());
        }
        infoTag.putInt("KeepLeashTicks", info.keepLeashTicks());
        infoTag.putInt("MaxKeepLeashTicks", info.maxKeepLeashTicks());
        return infoTag;
    }

    @Override
    public void deserializeNBT(@NotNull CompoundTag nbt) {
        leashHolders.clear();
        leashKnots.clear();
        delayedHolders.clear();
        if (nbt.contains("DefaultMaxDistance")) {
            defaultMaxDistance = nbt.getDouble("DefaultMaxDistance");
            if (nbt.contains("StaticMaxDistance")) {
                staticMaxDistance = nbt.getDouble("StaticMaxDistance");
            } else staticMaxDistance = null;

        } else defaultMaxDistance = CommonEventHandler.leashConfigManager.getMaxLeashLength();
        if (nbt.contains("DefaultElasticDistanceScale")) {
            defaultElasticDistanceScale = nbt.getDouble("DefaultElasticDistanceScale");
            if (nbt.contains("StaticElasticDistanceScale")) {
                staticElasticDistanceScale = nbt.getDouble("StaticElasticDistanceScale");
            } else staticElasticDistanceScale = null;
        } else defaultElasticDistanceScale = CommonEventHandler.leashConfigManager.getElasticDistanceScale();
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
            if (infoTag.contains("Marks")) {
                return new LeashInfo(
                        infoTag.getUUID("HolderUUID"),
                        infoTag.getInt("HolderID"),
                        getMarks(infoTag),
                        infoTag.getString("Reserved"),
                        infoTag.contains("MaxDistance") ? infoTag.getDouble("MaxDistance") : null,
                        infoTag.contains("ElasticDistanceScale") ? infoTag.getDouble("ElasticDistanceScale") : null,
                        infoTag.getInt("KeepLeashTicks"),
                        infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
                );
            } else return new LeashInfo(
                    infoTag.getUUID("HolderUUID"),
                    infoTag.getInt("HolderID"),
                    infoTag.getString("Reserved"),
                    infoTag.contains("MaxDistance") ? infoTag.getDouble("MaxDistance") : null,
                    infoTag.contains("ElasticDistanceScale") ? infoTag.getDouble("ElasticDistanceScale") : null,
                    infoTag.getInt("KeepLeashTicks"),
                    infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
            );
        } else throw new IllegalArgumentException("Unknown LeashInfo");
    }
    private static Set<String> getMarks(@NotNull CompoundTag infoTag) {
        if (infoTag.contains("Marks")) {
            CompoundTag marks = infoTag.getCompound("Marks");
            int size = marks.getInt("MarkSize");
            Set<String> markSet = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                markSet.add(marks.getString("Mark" + i));
            }
            return markSet;
        } else return Set.of();
    }
    @Contract("_ -> new")
    private static @NotNull LeashInfo getBlockPosLeashDataFormListTag(@NotNull CompoundTag infoTag) {
        if (infoTag.contains("KnotBlockPos")) {
            if (infoTag.contains("Marks")){
                return new LeashInfo(
                        NbtUtils.readBlockPos(infoTag.getCompound("KnotBlockPos")),
                        infoTag.getInt("HolderID"),
                        getMarks(infoTag),
                        infoTag.getString("Reserved"),
                        infoTag.contains("MaxDistance") ? infoTag.getDouble("MaxDistance") : null,
                        infoTag.contains("ElasticDistanceScale") ? infoTag.getDouble("ElasticDistanceScale") : null,
                        infoTag.getInt("KeepLeashTicks"),
                        infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20
                );
            } return new LeashInfo(
                    NbtUtils.readBlockPos(infoTag.getCompound("KnotBlockPos")),
                    infoTag.getInt("HolderID"),
                    infoTag.getString("Reserved"),
                    infoTag.contains("MaxDistance") ? infoTag.getDouble("MaxDistance") : null,
                    infoTag.contains("ElasticDistanceScale") ? infoTag.getDouble("ElasticDistanceScale") : null,
                    infoTag.getInt("KeepLeashTicks"),
                    infoTag.contains("MaxKeepLeashTicks") ? infoTag.getInt("MaxKeepLeashTicks") : 20)
            ;
        } else throw new IllegalArgumentException("Unknown LeashInfo");
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

    public boolean canBeAttachedTo(Entity pEntity) {
        if (pEntity == entity && !LuckPermsCompat.getOrCreateLPC().isLeashedBypass(entity)) {
            return false;
        } else {
            Optional<LeashInfo> leashInfo = getLeashInfo(pEntity);
            return leashInfo.isEmpty() && (entity.distanceTo(pEntity) <= CommonEventHandler.leashConfigManager.getMaxLeashLength() * CommonEventHandler.leashConfigManager.getExtremeSnapFactor()) && canBeLeashed();//距离最大,则不可以被固定或转移
        }
    }
}
