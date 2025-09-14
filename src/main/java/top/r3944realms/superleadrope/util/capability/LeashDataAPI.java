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

package top.r3944realms.superleadrope.util.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;

import java.util.*;

/**
 * 拴绳数据API - 提供统一的API接口操作拴绳数据能力
 */
@SuppressWarnings("unused")
public final class LeashDataAPI {
    // ==================== 基础能力获取 ====================
    public static Optional<ILeashData> getLeashData(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).resolve();
    }
    // ==================== 拴绳数据管理 API ====================

    public static final class LeashOperations {
        private LeashOperations() {}

        // ---------------------- 添加拴绳 ----------------------
        public static boolean attach(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.addLeash(holder)).orElse(false);
        }

        public static boolean attach(Entity entity, Entity holder, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, reserved)).orElse(false);
        }

        public static boolean attach(Entity entity, Entity holder, double maxDistance) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance)).orElse(false);
        }

        public static boolean attach(Entity entity, Entity holder, double maxDistance, double elasticDistance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, elasticDistance, maxKeepTicks)).orElse(false);
        }

        public static boolean attach(Entity entity, Entity holder, double maxDistance, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, reserved)).orElse(false);
        }

        public static boolean attach(Entity entity, Entity holder, double maxDistance, double elasticDistance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.addLeash(holder, maxDistance, elasticDistance, maxKeepTicks, reserved)).orElse(false);
        }

        public static void attachWithInfo(Entity entity, Entity holder, ILeashData.LeashInfo info) {
            getLeashData(entity).ifPresent(data -> data.addLeash(holder, info));
        }

        // ---------------------- 延迟拴绳 ----------------------
        public static void attachDelayed(Entity entity, Player holderPlayer) {
            getLeashData(entity).ifPresent(data -> data.addDelayedLeash(holderPlayer));
        }

        public static void removeDelayed(Entity entity, UUID onceHolderPlayerUUID) {
            getLeashData(entity).ifPresent(data -> data.removeDelayedLeash(onceHolderPlayerUUID));
        }

        // ---------------------- 移除拴绳 ----------------------
        public static boolean detach(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.removeLeash(holder)).orElse(false);
        }

        public static boolean detach(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.removeLeash(holderUUID)).orElse(false);
        }

        public static boolean detach(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).map(data -> data.removeLeash(knotPos)).orElse(false);
        }

        public static void detachAll(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllLeashes);
        }

        public static void detachAllHolders(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllHolderLeashes);
        }

        public static void detachAllKnots(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::removeAllKnotLeashes);
        }
    }

    // ==================== 拴绳属性修改 API ====================

    public static final class PropertyOperations {
        private PropertyOperations() {}

        // ---------------------- 设置最大距离 ----------------------
        public static boolean setMaxDistance(Entity entity, Entity holder, double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holder, distance)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, Entity holder, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holder, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, Entity holder, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holder, distance, maxKeepTicks, reserved)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, UUID holderUUID, double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holderUUID, distance)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, UUID holderUUID, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holderUUID, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, UUID holderUUID, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setMaxDistance(holderUUID, distance, maxKeepTicks, reserved)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, double distance) {
            return getLeashData(entity).map(data -> data.setMaxDistance(knotPos, distance)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setMaxDistance(knotPos, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setMaxDistance(Entity entity, BlockPos knotPos, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setMaxDistance(knotPos, distance, maxKeepTicks, reserved)).orElse(false);
        }

        // ---------------------- 设置弹性距离 ----------------------
        public static boolean setElasticDistance(Entity entity, Entity holder, double distance) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holder, distance)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, Entity holder, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holder, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, Entity holder, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holder, distance, maxKeepTicks, reserved)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, UUID holderUUID, double distance) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holderUUID, distance)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, UUID holderUUID, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holderUUID, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, UUID holderUUID, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setElasticDistance(holderUUID, distance, maxKeepTicks, reserved)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, BlockPos knotPos, double distance) {
            return getLeashData(entity).map(data -> data.setElasticDistance(knotPos, distance)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, BlockPos knotPos, double distance, int maxKeepTicks) {
            return getLeashData(entity).map(data -> data.setElasticDistance(knotPos, distance, maxKeepTicks)).orElse(false);
        }

        public static boolean setElasticDistance(Entity entity, BlockPos knotPos, double distance, int maxKeepTicks, String reserved) {
            return getLeashData(entity).map(data -> data.setElasticDistance(knotPos, distance, maxKeepTicks, reserved)).orElse(false);
        }
    }

    // ==================== 物理应用 API ====================

    public static final class PhysicsOperations {
        private PhysicsOperations() {}

        public static void applyForces(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::applyLeashForces);
        }
    }

    // ==================== 拴绳转移 API ====================

    public static final class TransferOperations {
        private TransferOperations() {}

        public static boolean transfer(Entity entity, Entity holder, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(holder, newHolder)).orElse(false);
        }

        public static boolean transfer(Entity entity, Entity holder, Entity newHolder, String reserved) {
            return getLeashData(entity).map(data -> data.transferLeash(holder, newHolder, reserved)).orElse(false);
        }

        public static boolean transfer(Entity entity, UUID holderUUID, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(holderUUID, newHolder)).orElse(false);
        }

        public static boolean transfer(Entity entity, UUID holderUUID, Entity newHolder, String reserved) {
            return getLeashData(entity).map(data -> data.transferLeash(holderUUID, newHolder, reserved)).orElse(false);
        }

        public static boolean transfer(Entity entity, BlockPos knotPos, Entity newHolder) {
            return getLeashData(entity).map(data -> data.transferLeash(knotPos, newHolder)).orElse(false);
        }

        public static boolean transfer(Entity entity, BlockPos knotPos, Entity newHolder, String reserved) {
            return getLeashData(entity).map(data -> data.transferLeash(knotPos, newHolder, reserved)).orElse(false);
        }
    }

    // ==================== 查询操作 API ====================

    public static final class QueryOperations {
        private QueryOperations() {}

        public static boolean hasLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasLeash).orElse(false);
        }

        public static boolean hasKnotLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasKnotLeash).orElse(false);
        }

        public static boolean hasHolderLeash(Entity entity) {
            return getLeashData(entity).map(ILeashData::hasHolderLeash).orElse(false);
        }

        public static Collection<ILeashData.LeashInfo> getAllLeashes(Entity entity) {
            return getLeashData(entity).map(ILeashData::getAllLeashes).orElse(Collections.emptyList());
        }

        public static boolean isLeashedBy(Entity entity, Entity holder) {
            return getLeashData(entity).map(data -> data.isLeashedBy(holder)).orElse(false);
        }

        public static boolean isLeashedBy(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.isLeashedBy(holderUUID)).orElse(false);
        }

        public static boolean isLeashedBy(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).map(data -> data.isLeashedBy(knotPos)).orElse(false);
        }

        public static boolean isInDelayedLeash(Entity entity, UUID holderUUID) {
            return getLeashData(entity).map(data -> data.isInDelayedLeash(holderUUID)).orElse(false);
        }

        public static Optional<ILeashData.LeashInfo> getLeashInfo(Entity entity, Entity holder) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(holder));
        }

        public static Optional<ILeashData.LeashInfo> getLeashInfo(Entity entity, UUID holderUUID) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(holderUUID));
        }

        public static Optional<ILeashData.LeashInfo> getLeashInfo(Entity entity, BlockPos knotPos) {
            return getLeashData(entity).flatMap(data -> data.getLeashInfo(knotPos));
        }

        public static boolean canBeLeashed(Entity entity) {
            return getLeashData(entity).map(ILeashData::canBeLeashed).orElse(false);
        }

        public static boolean canBeAttachedTo(Entity entity, Entity target) {
            return getLeashData(entity).map(data -> data.canBeAttachedTo(target)).orElse(false);
        }
    }

    // ==================== 占用和同步 API ====================

    public static final class ManagementOperations {
        private ManagementOperations() {}

        public static Optional<UUID> occupyLeash(Entity entity) {
            return getLeashData(entity).flatMap(ILeashData::occupyLeash);
        }

        public static void markForSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::markForSync);
        }

        public static void immediateSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::immediateSync);
        }

        public static void checkSync(Entity entity) {
            getLeashData(entity).ifPresent(ILeashData::checkSync);
        }
    }

    // ==================== 工具方法 ====================

    public static final class Utils {
        private Utils() {}

        public static boolean hasLeashData(Entity entity) {
            return getLeashData(entity).isPresent();
        }
    }
}