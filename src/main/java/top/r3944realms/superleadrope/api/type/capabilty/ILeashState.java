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

package top.r3944realms.superleadrope.api.type.capabilty;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Capability interface for managing leash states of entities and knots.
 */
public interface ILeashState extends INBTSerializable<CompoundTag> {
    /**
     * Self entity.
     *
     * @return the entity
     */
    Entity self();

    /**
     * Has leash state boolean.
     *
     * @return the boolean
     */
    /* ----------------------
     * Query leash states
     * ---------------------- */
    boolean hasLeashState();

    /**
     * Gets holder leash states.
     *
     * @return the holder leash states
     */
    Map<UUID, LeashState> getHolderLeashStates();

    /**
     * Gets knot leash states.
     *
     * @return the knot leash states
     */
    Map<BlockPos, LeashState> getKnotLeashStates();

    /**
     * Gets leash state.
     *
     * @param entity the entity
     * @return the leash state
     */
    Optional<LeashState> getLeashState(Entity entity);

    /**
     * Gets leash state.
     *
     * @param uuid the uuid
     * @return the leash state
     */
    Optional<LeashState> getLeashState(UUID uuid);

    /**
     * Gets leash state.
     *
     * @param pos the pos
     * @return the leash state
     */
    Optional<LeashState> getLeashState(BlockPos pos);

    /**
     * Gets holder location offset.
     *
     * @param entity the entity
     * @return the holder location offset
     */
    /* ----------------------
     * Get offsets
     * ---------------------- */
    Optional<Vec3> getHolderLocationOffset(Entity entity);

    /**
     * Gets holder location offset.
     *
     * @param uuid the uuid
     * @return the holder location offset
     */
    Optional<Vec3> getHolderLocationOffset(UUID uuid);

    /**
     * Gets holder location offset.
     *
     * @param pos the pos
     * @return the holder location offset
     */
    Optional<Vec3> getHolderLocationOffset(BlockPos pos);

    /**
     * Gets leash apply entity location offset.
     *
     * @return the leash apply entity location offset
     */
    Optional<Vec3> getLeashApplyEntityLocationOffset();

    /**
     * Gets default leash apply entity location offset.
     *
     * @return the default leash apply entity location offset
     */
    Vec3 getDefaultLeashApplyEntityLocationOffset();


    /**
     * Reset all leash apply entity locations offset.
     */
    /* ----------------------
     * Reset offsets (setApplyEntity null)
     * ---------------------- */
    void resetAllLeashApplyEntityLocationsOffset();

    /**
     * Reset all leash holder locations offset.
     */
    void resetAllLeashHolderLocationsOffset();

    /**
     * Reset leash holder location offset.
     *
     * @param holder the holder
     */
    void resetLeashHolderLocationOffset(Entity holder);

    /**
     * Reset leash holder location offset.
     *
     * @param holderUUID the holder uuid
     */
    void resetLeashHolderLocationOffset(UUID holderUUID);

    /**
     * Reset leash holder location offset.
     *
     * @param knotPos the knot pos
     */
    void resetLeashHolderLocationOffset(BlockPos knotPos);

    /**
     * Sets leash holder location offset.
     *
     * @param holder the holder
     * @param offset the offset
     */
    /* ----------------------
     * Set offsets (can setApplyEntity null)
     * ---------------------- */
    void setLeashHolderLocationOffset(Entity holder,@Nullable Vec3 offset);

    /**
     * Sets leash holder location offset.
     *
     * @param holderUUID the holder uuid
     * @param offset     the offset
     */
    void setLeashHolderLocationOffset(UUID holderUUID,@Nullable Vec3 offset);

    /**
     * Sets leash holder location offset.
     *
     * @param knotPos the knot pos
     * @param offset  the offset
     */
    void setLeashHolderLocationOffset(BlockPos knotPos,@Nullable Vec3 offset);

    /**
     * Sets leash apply entity location offset.
     *
     * @param offset the offset
     */
    void setLeashApplyEntityLocationOffset(Vec3 offset);

    /**
     * Add leash holder location offset.
     *
     * @param holder the holder
     * @param offset the offset
     */
    /* ----------------------
     * Add offsets
     * ---------------------- */
    void addLeashHolderLocationOffset(Entity holder, Vec3 offset);

    /**
     * Add leash holder location offset.
     *
     * @param holderUUID the holder uuid
     * @param offset     the offset
     */
    void addLeashHolderLocationOffset(UUID holderUUID, Vec3 offset);

    /**
     * Add leash holder location offset.
     *
     * @param knotPos the knot pos
     * @param offset  the offset
     */
    void addLeashHolderLocationOffset(BlockPos knotPos, Vec3 offset);

    /**
     * Add leash apply entity location offset.
     *
     * @param offset the offset
     */
    void addLeashApplyEntityLocationOffset(Vec3 offset);

    /**
     * Remove leash holder location offset.
     *
     * @param holder the holder
     */
    /* ----------------------
     * Remove offsets (delete)
     * ---------------------- */
    void removeLeashHolderLocationOffset(Entity holder);

    /**
     * Remove leash holder location offset.
     *
     * @param holderUUID the holder uuid
     */
    void removeLeashHolderLocationOffset(UUID holderUUID);

    /**
     * Remove leash holder location offset.
     *
     * @param knotPos the knot pos
     */
    void removeLeashHolderLocationOffset(BlockPos knotPos);

    /**
     * Remove all leash holder location offset.
     */
    void removeAllLeashHolderLocationOffset();

    /**
     * Remove all leash holder uuid location offset.
     */
    void removeAllLeashHolderUUIDLocationOffset();

    /**
     * Remove all leash holder block pos location offset.
     */
    void removeAllLeashHolderBlockPosLocationOffset();

    /**
     * Remove leash apply entity location offset.
     */
    void removeLeashApplyEntityLocationOffset();

    /**
     * Copy.
     *
     * @param other     the other
     * @param newEntity the new entity
     */
    /* ----------------------
     * Utility & sync
     * ---------------------- */
    void copy(ILeashState other, Entity newEntity);

    /**
     * Mark for sync.
     */
    void markForSync();

    /**
     * Immediate sync.
     */
    void immediateSync();

    /**
     * Check sync.
     */
    void checkSync();

    /**
     * The type Leash state.
     */
    /* ----------------------
     * Data record
     * ---------------------- */
    record LeashState(
            @Nullable Vec3 holderLocationOffset,
            Vec3 applyEntityLocationOffset,
            Vec3 defaultHolderLocationOffset
    ) {
        /**
         * Reset holder location offset leash state.
         *
         * @return the leash state
         */
        @Contract(" -> new")
        public @NotNull LeashState resetHolderLocationOffset() {
            return new LeashState(null, applyEntityLocationOffset, defaultHolderLocationOffset);
        }

        /**
         * Sets holder location offset.
         *
         * @param holderLocationOffset the holder location offset
         * @return the holder location offset
         */
        @Contract("_ -> new")
        public @NotNull LeashState setHolderLocationOffset(@Nullable Vec3 holderLocationOffset) {
            return new LeashState(holderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }

        /**
         * Sets apply entity location offset.
         *
         * @param applyEntityLocationOffset the apply entity location offset
         * @return the apply entity location offset
         */
        @Contract("_ -> new")
        public @NotNull LeashState setApplyEntityLocationOffset(@NotNull Vec3 applyEntityLocationOffset) {
            return new LeashState(holderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }

        /**
         * Sets default holder location offset.
         *
         * @param defaultHolderLocationOffset the default holder location offset
         * @return the default holder location offset
         */
        @Contract("_ -> new")
        public @NotNull LeashState setDefaultHolderLocationOffset(@NotNull Vec3 defaultHolderLocationOffset) {
            return new LeashState(holderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }
    }
}
