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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashState;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings("unused")
public final class LeashStateAPI {

    private LeashStateAPI() {
    } // 防止实例化

    public static Optional<ILeashState> getLeashState(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(CapabilityHandler.LEASH_STATE_CAP).resolve();
    }

    // ==================== 查询操作 ====================

    public static final class Query {
        private Query() {
        }

        public static Map<UUID, ILeashState.LeashState> getAllUUIDStates(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getHolderLeashStates)
                    .orElse(Map.of());
        }

        public static Map<BlockPos, ILeashState.LeashState> getAllBlockPosStates(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getKnotLeashStates)
                    .orElse(Map.of());
        }

        public static Optional<ILeashState.LeashState> getState(Entity entity, Entity holder) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(holder));
        }

        public static Optional<ILeashState.LeashState> getState(Entity entity, UUID holderUUID) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(holderUUID));
        }

        public static Optional<ILeashState.LeashState> getState(Entity entity, BlockPos knotPos) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(knotPos));
        }

        public static boolean hasState(Entity entity) {
            return getLeashState(entity).isPresent();
        }

        public static boolean hasStateFor(Entity entity, Entity holder) {
            return getState(entity, holder).isPresent();
        }

        public static boolean hasStateFor(Entity entity, UUID holderUUID) {
            return getState(entity, holderUUID).isPresent();
        }

        public static boolean hasStateFor(Entity entity, BlockPos knotPos) {
            return getState(entity, knotPos).isPresent();
        }
    }

    // ==================== 偏移量操作 ====================

    public static final class Offset {
        private Offset() {
        }

        // ---------------------- 重置操作 ----------------------
        public static void resetAll(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::resetAllLeashHolderLocationsOffset);
        }

        public static void resetFor(Entity entity, Entity holder) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(holder));
        }

        public static void resetFor(Entity entity, UUID holderUUID) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(holderUUID));
        }

        public static void resetFor(Entity entity, BlockPos knotPos) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(knotPos));
        }

        // ---------------------- 设置操作 ----------------------
        public static void setFor(Entity entity, Entity holder, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holder, offset));
        }

        public static void setFor(Entity entity, UUID holderUUID, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holderUUID, offset));
        }

        public static void setFor(Entity entity, BlockPos knotPos, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(knotPos, offset));
        }

        // ---------------------- 添加操作 ----------------------
        public static void addTo(Entity entity, Entity holder, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(holder, offset));
        }

        public static void addTo(Entity entity, UUID holderUUID, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(holderUUID, offset));
        }

        public static void addTo(Entity entity, BlockPos knotPos, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(knotPos, offset));
        }

        // ---------------------- 移除操作 ----------------------
        public static void removeFor(Entity entity, Entity holder) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(holder));
        }

        public static void removeFor(Entity entity, UUID holderUUID) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(holderUUID));
        }

        public static void removeFor(Entity entity, BlockPos knotPos) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(knotPos));
        }

        public static void removeAll(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderLocationOffset);
        }

        public static void removeAllUUIDs(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderUUIDLocationOffset);
        }

        public static void removeAllBlockPoses(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderBlockPosLocationOffset);
        }
    }

    // ==================== 应用实体偏移量操作 ====================

    public static final class ApplyEntity {
        private ApplyEntity() {
        }

        public static Optional<Vec3> getOffset(Entity entity) {
            return getLeashState(entity).flatMap(ILeashState::getLeashApplyEntityLocationOffset);
        }

        public static Vec3 getDefaultOffset(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getDefaultLeashApplyEntityLocationOffset)
                    .orElse(Vec3.ZERO);
        }

        public static void resetAll(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::resetAllLeashApplyEntityLocationsOffset);
        }

        public static void remove(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeLeashApplyEntityLocationOffset);
        }

        public static void set(Entity entity, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashApplyEntityLocationOffset(offset));
        }

        public static void add(Entity entity, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashApplyEntityLocationOffset(offset));
        }
    }

    // ==================== 同步操作 ====================

    public static final class Sync {
        private Sync() {
        }

        public static void mark(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::markForSync);
        }

        public static void immediate(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::immediateSync);
        }

        public static void check(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::checkSync);
        }
    }

    // ==================== 高级操作 ====================

    public static final class Operations {
        private Operations() {
        }

        public static void attach(Entity leashed, Entity holder) {
            getLeashState(leashed).ifPresent(state ->
                    state.addLeashHolderLocationOffset(holder,
                            CommonEventHandler.leashConfigManager.getEntityOffset(holder))
            );
        }

        public static void detach(Entity leashed, Entity holder) {
            Offset.removeFor(leashed, holder);
        }

        public static void detach(Entity leashed, UUID holderUUID) {
            Offset.removeFor(leashed, holderUUID);
        }

        public static void detach(Entity leashed, BlockPos knotPos) {
            Offset.removeFor(leashed, knotPos);
        }

        public static void transfer(Entity leashed, Entity oldHolder, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldHolder);
                state.addLeashHolderLocationOffset(newHolder,
                        CommonEventHandler.leashConfigManager.getEntityOffset(newHolder));
            });
        }

        public static void transfer(Entity leashed, UUID oldHolderUUID, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldHolderUUID);
                state.addLeashHolderLocationOffset(newHolder,
                        CommonEventHandler.leashConfigManager.getEntityOffset(newHolder));
            });
        }

        public static void transfer(Entity leashed, BlockPos oldKnotPos, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldKnotPos);
                state.addLeashHolderLocationOffset(newHolder,
                        CommonEventHandler.leashConfigManager.getEntityOffset(newHolder));
            });
        }

        public static void copy(Entity source, Entity target) {
            getLeashState(source).ifPresent(sourceState ->
                    getLeashState(target).ifPresent(targetState ->
                            targetState.copy(sourceState, target)
                    )
            );
        }
    }
}
