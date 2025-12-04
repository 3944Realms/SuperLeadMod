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

package top.r3944realms.superleadrope.util.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.SLPCapability;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Leash state inner api.
 */
@SuppressWarnings("unused")
@ApiStatus.Internal
public final class LeashStateInnerAPI {

    private LeashStateInnerAPI() {
    } // 防止实例化

    /**
     * Gets leash state.
     *
     * @param entity the entity
     * @return the leash state
     */
    public static @NotNull Optional<ILeashState> getLeashState(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(SLPCapability.LEASH_STATE_CAP).resolve();
    }

    // ==================== 查询操作 ====================

    /**
     * The type Query.
     */
    public static final class Query {
        private Query() {
        }

        /**
         * Has leash state boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasLeashState(Entity entity) {
            return getLeashState(entity).map(ILeashState::hasLeashState).orElse(false);
        }

        /**
         * Gets all uuid states.
         *
         * @param entity the entity
         * @return the all uuid states
         */
        public static Map<UUID, ILeashState.LeashState> getAllUUIDStates(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getHolderLeashStates)
                    .orElse(Map.of());
        }

        /**
         * Gets all block pos states.
         *
         * @param entity the entity
         * @return the all block pos states
         */
        public static Map<BlockPos, ILeashState.LeashState> getAllBlockPosStates(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getKnotLeashStates)
                    .orElse(Map.of());
        }

        /**
         * Gets state.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the state
         */
        public static Optional<ILeashState.LeashState> getState(Entity entity, Entity holder) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(holder));
        }

        /**
         * Gets state.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the state
         */
        public static Optional<ILeashState.LeashState> getState(Entity entity, UUID holderUUID) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(holderUUID));
        }

        /**
         * Gets state.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @return the state
         */
        public static Optional<ILeashState.LeashState> getState(Entity entity, BlockPos knotPos) {
            return getLeashState(entity).flatMap(state -> state.getLeashState(knotPos));
        }

        /**
         * Has state boolean.
         *
         * @param entity the entity
         * @return the boolean
         */
        public static boolean hasState(Entity entity) {
            return getLeashState(entity).isPresent();
        }

        /**
         * Has state for boolean.
         *
         * @param entity the entity
         * @param holder the holder
         * @return the boolean
         */
        public static boolean hasStateFor(Entity entity, Entity holder) {
            return getState(entity, holder).isPresent();
        }

        /**
         * Has state for boolean.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @return the boolean
         */
        public static boolean hasStateFor(Entity entity, UUID holderUUID) {
            return getState(entity, holderUUID).isPresent();
        }

        /**
         * Has state for boolean.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @return the boolean
         */
        public static boolean hasStateFor(Entity entity, BlockPos knotPos) {
            return getState(entity, knotPos).isPresent();
        }
    }

    // ==================== 偏移量操作 ====================

    /**
     * The type Offset.
     */
    public static final class Offset {
        private Offset() {
        }

        /**
         * Reset all holder.
         *
         * @param entity the entity
         */
// ---------------------- 重置操作 ----------------------
        public static void resetAllHolder(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::resetAllLeashHolderLocationsOffset);
        }

        /**
         * Reset holder for.
         *
         * @param entity the entity
         * @param holder the holder
         */
        public static void resetHolderFor(Entity entity, Entity holder) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(holder));
        }

        /**
         * Reset holder for.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         */
        public static void resetHolderFor(Entity entity, UUID holderUUID) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(holderUUID));
        }

        /**
         * Reset holder for.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         */
        public static void resetHolderFor(Entity entity, BlockPos knotPos) {
            getLeashState(entity).ifPresent(state -> state.resetLeashHolderLocationOffset(knotPos));
        }

        /**
         * Sets holder for.
         *
         * @param entity the entity
         * @param holder the holder
         * @param offset the offset
         */
// ---------------------- 设置操作 ----------------------
        public static void setHolderFor(Entity entity, Entity holder, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holder, offset));
        }

        /**
         * Sets holder for.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param offset     the offset
         */
        public static void setHolderFor(Entity entity, UUID holderUUID, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holderUUID, offset));
        }

        /**
         * Sets holder for.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @param offset  the offset
         */
        public static void setHolderFor(Entity entity, BlockPos knotPos, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(knotPos, offset));
        }

        /**
         * Sets holder for.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         */
        public static void setHolderFor(Entity entity, BlockPos knotPos) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(knotPos, null));
        }

        /**
         * Sets holder for.
         *
         * @param entity the entity
         * @param holder the holder
         */
        public static void setHolderFor(Entity entity, Entity holder) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holder, null));
        }

        /**
         * Sets holder for.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         */
        public static void setHolderFor(Entity entity, UUID holderUUID) {
            getLeashState(entity).ifPresent(state -> state.setLeashHolderLocationOffset(holderUUID, null));
        }

        /**
         * Add holder to.
         *
         * @param entity the entity
         * @param holder the holder
         * @param offset the offset
         */
// ---------------------- 添加操作 ----------------------
        public static void addHolderTo(Entity entity, Entity holder, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(holder, offset));
        }

        /**
         * Add holder to.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         * @param offset     the offset
         */
        public static void addHolderTo(Entity entity, UUID holderUUID, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(holderUUID, offset));
        }

        /**
         * Add holder to.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         * @param offset  the offset
         */
        public static void addHolderTo(Entity entity, BlockPos knotPos, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashHolderLocationOffset(knotPos, offset));
        }

        /**
         * Remove holder for.
         *
         * @param entity the entity
         * @param holder the holder
         */
// ---------------------- 移除操作 ----------------------
        public static void removeHolderFor(Entity entity, Entity holder) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(holder));
        }

        /**
         * Remove holder for.
         *
         * @param entity     the entity
         * @param holderUUID the holder uuid
         */
        public static void removeHolderFor(Entity entity, UUID holderUUID) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(holderUUID));
        }

        /**
         * Remove holder for.
         *
         * @param entity  the entity
         * @param knotPos the knot pos
         */
        public static void removeHolderFor(Entity entity, BlockPos knotPos) {
            getLeashState(entity).ifPresent(state -> state.removeLeashHolderLocationOffset(knotPos));
        }

        /**
         * Remove holder all.
         *
         * @param entity the entity
         */
        public static void removeHolderAll(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderLocationOffset);
        }

        /**
         * Remove all holder uui ds.
         *
         * @param entity the entity
         */
        public static void removeAllHolderUUIDs(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderUUIDLocationOffset);
        }

        /**
         * Remove all holder block poses.
         *
         * @param entity the entity
         */
        public static void removeAllHolderBlockPoses(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeAllLeashHolderBlockPosLocationOffset);
        }

        /**
         * Gets apply entity offset.
         *
         * @param entity the entity
         * @return the apply entity offset
         */
        public static Optional<Vec3> getApplyEntityOffset(Entity entity) {
            return getLeashState(entity).flatMap(ILeashState::getLeashApplyEntityLocationOffset);
        }

        /**
         * Gets default apply entity offset.
         *
         * @param entity the entity
         * @return the default apply entity offset
         */
        public static Vec3 getDefaultApplyEntityOffset(Entity entity) {
            return getLeashState(entity)
                    .map(ILeashState::getDefaultLeashApplyEntityLocationOffset)
                    .orElse(Vec3.ZERO);
        }

        /**
         * Reset apply entity all.
         *
         * @param entity the entity
         */
        public static void resetApplyEntityAll(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::resetAllLeashApplyEntityLocationsOffset);
        }

        /**
         * Remove apply entity.
         *
         * @param entity the entity
         */
        public static void removeApplyEntity(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::removeLeashApplyEntityLocationOffset);
        }

        /**
         * Sets apply entity.
         *
         * @param entity the entity
         * @param offset the offset
         */
        public static void setApplyEntity(Entity entity, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.setLeashApplyEntityLocationOffset(offset));
        }

        /**
         * Add apply entity.
         *
         * @param entity the entity
         * @param offset the offset
         */
        public static void addApplyEntity(Entity entity, Vec3 offset) {
            getLeashState(entity).ifPresent(state -> state.addLeashApplyEntityLocationOffset(offset));
        }
    }


    // ==================== 同步操作 ====================

    /**
     * The type Sync.
     */
    public static final class Sync {
        private Sync() {
        }

        /**
         * Mark.
         *
         * @param entity the entity
         */
        public static void mark(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::markForSync);
        }

        /**
         * Immediate.
         *
         * @param entity the entity
         */
        public static void immediate(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::immediateSync);
        }

        /**
         * Check.
         *
         * @param entity the entity
         */
        public static void check(Entity entity) {
            getLeashState(entity).ifPresent(ILeashState::checkSync);
        }
    }

    // ==================== 高级操作 ====================

    /**
     * The type Operations.
     */
    public static final class Operations {
        private Operations() {
        }

        /**
         * Attach.
         *
         * @param leashed the leashed
         * @param holder  the holder
         */
        public static void attach(Entity leashed, Entity holder) {
            getLeashState(leashed).ifPresent(state ->
                    state.setLeashHolderLocationOffset(holder,
                            null)
            );
        }

        /**
         * Detach.
         *
         * @param leashed the leashed
         * @param holder  the holder
         */
        public static void detach(Entity leashed, Entity holder) {
            Offset.removeHolderFor(leashed, holder);
        }

        /**
         * Detach.
         *
         * @param leashed    the leashed
         * @param holderUUID the holder uuid
         */
        public static void detach(Entity leashed, UUID holderUUID) {
            Offset.removeHolderFor(leashed, holderUUID);
        }

        /**
         * Detach.
         *
         * @param leashed the leashed
         * @param knotPos the knot pos
         */
        public static void detach(Entity leashed, BlockPos knotPos) {
            Offset.removeHolderFor(leashed, knotPos);
        }

        /**
         * Transfer.
         *
         * @param leashed   the leashed
         * @param oldHolder the old holder
         * @param newHolder the new holder
         */
        public static void transfer(Entity leashed, Entity oldHolder, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldHolder);
                state.setLeashHolderLocationOffset(newHolder,
                        null);
            });
        }

        /**
         * Transfer.
         *
         * @param leashed       the leashed
         * @param oldHolderUUID the old holder uuid
         * @param newHolder     the new holder
         */
        public static void transfer(Entity leashed, UUID oldHolderUUID, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldHolderUUID);
                state.setLeashHolderLocationOffset(newHolder,
                        null);
            });
        }

        /**
         * Transfer.
         *
         * @param leashed    the leashed
         * @param oldKnotPos the old knot pos
         * @param newHolder  the new holder
         */
        public static void transfer(Entity leashed, BlockPos oldKnotPos, Entity newHolder) {
            getLeashState(leashed).ifPresent(state -> {
                state.removeLeashHolderLocationOffset(oldKnotPos);
                state.setLeashHolderLocationOffset(newHolder,
                        null);
            });
        }

        /**
         * Copy.
         *
         * @param source the source
         * @param target the target
         */
        public static void copy(Entity source, Entity target) {
            getLeashState(source).ifPresent(sourceState ->
                    getLeashState(target).ifPresent(targetState ->
                            targetState.copy(sourceState, target)
                    )
            );
        }
    }
}
