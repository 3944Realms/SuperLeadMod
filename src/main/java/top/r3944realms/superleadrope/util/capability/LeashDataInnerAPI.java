/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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

package top.r3944realms.superleadrope.util.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.api.SLPCapability;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;

import java.util.*;

/**
 * 拴绳数据API - 提供统一的API接口操作拴绳数据能力
 */
@SuppressWarnings("unused")
@ApiStatus.Internal
public final class LeashDataInnerAPI {
    /**
     * Gets leash data.
     *
     * @param entity the entity
     * @return the leash data
     */
// ==================== 基础能力获取 ====================
    public static @NotNull Optional<ILeashData> getLeashData(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(SLPCapability.LEASH_DATA_CAP).resolve();
    }
    // ==================== 拴绳数据管理 API ====================

    /**
     * The type Leash operations.
     */
    public static final class LeashOperations {
        private LeashOperations() {}

        /**
         * Attach boolean.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the boolean
         */
// ---------------------- 添加拴绳 ----------------------
        public static boolean attach(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.addLeash(holder)).orElse(false);
        }

        /**
         * Attach boolean.
         *
         * @param entity   the entity
         * @param holder   the holder
         * @param reserved the reserved
         * @return the boolean
         */
        public static boolean attach(Entity entity, Entity holder, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, reserved)).orElse(false);
        }

        /**
         * Attach boolean.
         *
         * @param entity      the entity
         * @param holder      the holder
         * @param maxDistance the max distance
         * @return the boolean
         */
        public static boolean attach(Entity entity, Entity holder, @Nullable Double maxDistance) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance)).orElse(false);
        }

        /**
         * Attach boolean.
         *
         * @param entity                the entity
         * @param holder                the holder
         * @param maxDistance           the max distance
         * @param elasticdDistanceScale the elastic distance
         * @param maxKeepTicks          the max keep ticks
         * @return the boolean
         */
        public static boolean attach(Entity entity, Entity holder, @Nullable Double maxDistance, @Nullable Double elasticdDistanceScale, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, elasticdDistanceScale, maxKeepTicks)).orElse(false);
        }

        /**
         * Attach boolean.
         *
         * @param entity      the entity
         * @param holder      the holder
         * @param maxDistance the max distance
         * @param reserved    the reserved
         * @return the boolean
         */
        public static boolean attach(Entity entity, Entity holder, @Nullable Double maxDistance, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, reserved)).orElse(false);
        }

        /**
         * Attach boolean.
         *
         * @param entity               the entity
         * @param holder               the holder
         * @param maxDistance          the max distance
         * @param elasticDistanceScale the elastic distance
         * @param maxKeepTicks         the max keep ticks
         * @param reserved             the reserved
         * @return the boolean
         */
        public static boolean attach(Entity entity, Entity holder, @Nullable Double maxDistance, @Nullable Double elasticDistanceScale, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, elasticDistanceScale, maxKeepTicks, reserved)).orElse(false);
        }

        /**
         * Attach with info.
         *
         * @param entity the entity
         * @param holder the holder
         * @param info   the info
         */
        public static void attachWithInfo(Entity entity, Entity holder, LeashInfo info) {
            getLeashData(entity).ifPresent(data -> data.addLeash(holder, info));
        }

        /**
         * Attach delayed.
         *
         * @param entity the entity
         * @param holder the holder
         */
// ---------------------- 延迟拴绳 ----------------------
        public static void attachDelayed(Entity entity, Entity holder) {
            getLeashData(entity).ifPresent(data -> data.addDelayedLeash(holder));
        }

        /**
         * Attach delayed.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         */
        public static void attachDelayed(Entity entity, UUID holderUUID) {
            getLeashData(entity).ifPresent(data -> data.addDelayedLeash(holderUUID));
        }

        /**
         * Remove delayed.
         *
         * @param entity               the entity
         * @param onceHolderPlayerUUID the once holder player uuid
         */
        public static void removeDelayed(Entity entity, UUID onceHolderPlayerUUID) {
            getLeashData(entity).ifPresent(data -> data.removeDelayedLeash(onceHolderPlayerUUID));
        }

        /**
         * Detach boolean.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the boolean
         */
// ---------------------- 移除拴绳 ----------------------
        public static boolean detach(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.removeLeash(holder)).orElse(false);
        }

        /**
         * Detach boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the boolean
         */
        public static boolean detach(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.removeLeash(holderUUID)).orElse(false);
        }

        /**
         * Detach boolean.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @return the boolean
         */
        public static boolean detach(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).map(data -> data.removeLeash(knotPos)).orElse(false);
        }

        /**
         * Detach all.
         *
         * @param entity the entity
         */
        public static void detachAll(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllLeashes);
        }

        /**
         * Detach all holders.
         *
         * @param entity the entity
         */
        public static void detachAllHolders(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllHolderLeashes);
        }

        /**
         * Detach all knots.
         *
         * @param entity the entity
         */
        public static void detachAllKnots(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllKnotLeashes);
        }
    }

    // ==================== 拴绳属性修改 API ====================

    /**
     * The type Property operations.
     */
    public static final class PropertyOperations {
        private PropertyOperations() {}

        /**
         * Set static max distance.
         *
         * @param entity   the entity
         * @param distance the distance
         */
        public static void setStaticMaxDistance(Entity entity, @Nullable Double distance) {
           getLeashData(entity).ifPresent(data -> data.setStaticMaxDistance(distance));
        }

        /**
         * Set static elastic distance scale.
         *
         * @param entity   the entity
         * @param distance the distance
         */
        public static void setElasticDistanceScale(Entity entity, @Nullable Double distance) {
            getLeashData(entity).ifPresent(data -> data.setStaticElasticDistanceScale(distance));
        }

        /**
         * Sets max distance.
         *
         * @param entity   the entity
         * @param holder   the holder
         * @param distance the distance
         * @return the max distance
         */
// ---------------------- 设置最大距离 ----------------------
        public static boolean setMaxDistance(Entity entity, Entity holder, @Nullable Double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holder, distance)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity   the entity
         * @param holder   the holder
         * @param distance the distance
         * @param reserved the reserved
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, Entity holder, @Nullable Double distance, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data -> reserved != null ?
                            data.setMaxDistance(holder, distance, reserved) :
                            data.setMaxDistance(holder, distance))
                    .orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param holder       the holder
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, Entity holder, @Nullable Double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holder, distance, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param holder       the holder
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, Entity holder, @Nullable Double distance, int maxKeepTicks, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(data -> reserved != null ?
                            data.setMaxDistance(holder, distance, maxKeepTicks, reserved) :
                            data.setMaxDistance(holder, distance, maxKeepTicks))
                    .orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param distance   the distance
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, UUID holderUUID, @Nullable Double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holderUUID, distance)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param holderUUID   the holder uuid
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, UUID holderUUID, @Nullable Double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holderUUID, distance, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param holderUUID   the holder uuid
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, UUID holderUUID, @Nullable Double distance, int maxKeepTicks,@Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.setMaxDistance(holderUUID, distance, maxKeepTicks, reserved) :
                                    data.setMaxDistance(holderUUID, distance, maxKeepTicks)
                    )
                    .orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity   the entity
         * @param knotPos  the knot pos
         * @param distance the distance
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, @Nullable Double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(knotPos, distance)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param knotPos      the knot pos
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, @Nullable Double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(knotPos, distance, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets max distance.
         *
         * @param entity       the entity
         * @param knotPos      the knot pos
         * @param distance     the distance
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the max distance
         */
        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, @Nullable Double distance, int maxKeepTicks,@Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.setMaxDistance(knotPos, distance, maxKeepTicks, reserved) :
                                    data.setMaxDistance(knotPos, distance, maxKeepTicks)
                    )
                    .orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity the entity
         * @param holder the holder
         * @param scale  the scale
         * @return the elastic scale
         */
// ---------------------- 设置弹性距离 ----------------------
        public static boolean setElasticDistanceScale(Entity entity, Entity holder, @Nullable Double scale) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(holder, scale)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param holder       the holder
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, Entity holder, @Nullable Double scale, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(holder, scale, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param holder       the holder
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, Entity holder, @Nullable Double scale, int maxKeepTicks, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.setElasticDistanceScale(holder, scale, maxKeepTicks, reserved) :
                                    data.setElasticDistanceScale(holder, scale, maxKeepTicks)
                    )
                    .orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param scale      the scale
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, UUID holderUUID, @Nullable Double scale) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(holderUUID, scale)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param holderUUID   the holder uuid
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, UUID holderUUID, @Nullable Double scale, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(holderUUID, scale, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param holderUUID   the holder uuid
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, UUID holderUUID, @Nullable Double scale, int maxKeepTicks, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.setElasticDistanceScale(holderUUID, scale, maxKeepTicks, reserved) :
                                    data.setElasticDistanceScale(holderUUID, scale, maxKeepTicks)
                    )
                    .orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @param scale   the scale
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, BlockPos knotPos, @Nullable Double scale) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(knotPos, scale)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param knotPos      the knot pos
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, BlockPos knotPos, @Nullable Double scale, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistanceScale(knotPos, scale, maxKeepTicks)).orElse(false);
        }

        /**
         * Sets elastic scale.
         *
         * @param entity       the entity
         * @param knotPos      the knot pos
         * @param scale        the scale
         * @param maxKeepTicks the max keep ticks
         * @param reserved     the reserved
         * @return the elastic scale
         */
        public static boolean setElasticDistanceScale(Entity entity, BlockPos knotPos, @Nullable Double scale, int maxKeepTicks, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.setElasticDistanceScale(knotPos, scale, maxKeepTicks, reserved) :
                                    data.setElasticDistanceScale(knotPos, scale, maxKeepTicks)
                    )
                    .orElse(false);
        }
    }

    // ==================== 物理应用 API ====================

    /**
     * The type Physics operations.
     */
    public static final class PhysicsOperations {
        private PhysicsOperations() {}

        /**
         * Apply forces.
         *
         * @param entity the entity
         */
        public static void applyForces(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::applyLeashForces);
        }
    }

    // ==================== 拴绳转移 API ====================

    /**
     * The type Transfer operations.
     */
    public static final class TransferOperations {
        private TransferOperations() {}

        /**
         * Transfer boolean.
         *
         * @param entity    the entity
         * @param holder    the holder
         * @param newHolder the new holder
         * @return the boolean
         */
        public static boolean transfer(Entity entity, Entity holder, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(holder, newHolder)).orElse(false);
        }

        /**
         * Transfer boolean.
         *
         * @param entity    the entity
         * @param holder    the holder
         * @param newHolder the new holder
         * @param reserved  the reserved
         * @return the boolean
         */
        public static boolean transfer(Entity entity, Entity holder, Entity newHolder, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.transferLeash(holder, newHolder, reserved) :
                                    data.transferLeash(holder, newHolder)
                    )
                    .orElse(false);
        }

        /**
         * Transfer boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param newHolder  the new holder
         * @return the boolean
         */
        public static boolean transfer(Entity entity, UUID holderUUID, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(holderUUID, newHolder)).orElse(false);
        }

        /**
         * Transfer boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param newHolder  the new holder
         * @param reserved   the reserved
         * @return the boolean
         */
        public static boolean transfer(Entity entity, UUID holderUUID, Entity newHolder, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.transferLeash(holderUUID, newHolder, reserved) :
                                    data.transferLeash(holderUUID, newHolder)
                    )
                    .orElse(false);
        }

        /**
         * Transfer boolean.
         *
         * @param entity    the entity
         * @param knotPos   the knot pos
         * @param newHolder the new holder
         * @return the boolean
         */
        public static boolean transfer(Entity entity, BlockPos knotPos, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(knotPos, newHolder)).orElse(false);
        }

        /**
         * Transfer boolean.
         *
         * @param entity    the entity
         * @param knotPos   the knot pos
         * @param newHolder the new holder
         * @param reserved  the reserved
         * @return the boolean
         */
        public static boolean transfer(Entity entity, BlockPos knotPos, Entity newHolder, @Nullable String reserved) {
            return getLeashData(entity)
                    .map(
                            data ->  reserved != null ?
                                    data.transferLeash(knotPos, newHolder, reserved) :
                                    data.transferLeash(knotPos, newHolder)
                    )
                    .orElse(false);
        }
    }

    // ==================== 查询操作 API ====================

    /**
     * The type Query operations.
     */
    public static final class QueryOperations {
        private QueryOperations() {}

        /**
         * Has leash boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasLeash).orElse(false);
        }

        /**
         * Has knot leash boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasKnotLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasKnotLeash).orElse(false);
        }

        /**
         * Has holder leash boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasHolderLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasHolderLeash).orElse(false);
        }

        /**
         * Gets all leashes.
         *
         * @param entity the entity
         * @return the all leashes
         */
        public static Collection<LeashInfo> getAllLeashes(Entity entity) {
            return getLeashData(entity).map(ILeashData::getAllLeashes).orElse(Collections.emptyList());
        }

        /**
         * Is leashed by boolean.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the boolean
         */
        public static boolean isLeashedBy(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.isLeashedBy(holder)).orElse(false);
        }

        /**
         * Is leashed by boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the boolean
         */
        public static boolean isLeashedBy(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.isLeashedBy(holderUUID)).orElse(false);
        }

        /**
         * Is leashed by boolean.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @return the boolean
         */
        public static boolean isLeashedBy(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).map(data -> data.isLeashedBy(knotPos)).orElse(false);
        }

        /**
         * Is in delayed leash boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the boolean
         */
        public static boolean isInDelayedLeash(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.isInDelayedLeash(holderUUID)).orElse(false);
        }

        /**
         * Gets leash info.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the leash info
         */
        public static Optional<LeashInfo> getLeashInfo(Entity entity, Entity holder) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(holder));
        }

        /**
         * Gets leash info.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the leash info
         */
        public static Optional<LeashInfo> getLeashInfo(Entity entity, UUID holderUUID) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(holderUUID));
        }

        /**
         * Gets leash info.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @return the leash info
         */
        public static Optional<LeashInfo> getLeashInfo(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(knotPos));
        }

        /**
         * Can be leashed boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean canBeLeashed(Entity entity) {
            return getLeashData(entity).map(ILeashData::canBeLeashed).orElse(false);
        }

        /**
         * Can be attached to boolean.
         *
         * @param entity the entity
         * @param target the target
         * @return the boolean
         */
        public static boolean canBeAttachedTo(Entity entity, Entity target) {
            return getLeashData(entity).map(data -> data.canBeAttachedTo(target)).orElse(false);
        }
    }

    // ==================== 占用和同步 API ====================

    /**
     * The type Management operations.
     */
    public static final class ManagementOperations {
        private ManagementOperations() {}

        /**
         * Occupy leash optional.
         *
         * @param entity the entity
         * @return the optional
         */
        public static Optional<UUID> occupyLeash(Entity entity) {
            return getLeashData(entity).flatMap(ILeashData::occupyLeash);
        }

        /**
         * Mark for sync.
         *
         * @param entity the entity
         */
        public static void markForSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::markForSync);
        }

        /**
         * Immediate sync.
         *
         * @param entity the entity
         */
        public static void immediateSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::immediateSync);
        }

        /**
         * Check sync.
         *
         * @param entity the entity
         */
        public static void checkSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::checkSync);
        }
    }

    // ==================== 工具方法 ====================

    /**
     * The type Utils.
     */
    public static final class Utils {
        private Utils() {}

        /**
         * Has leash data boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasLeashData(Entity entity) {
            return getLeashData(entity).isPresent();
        }
    }
}