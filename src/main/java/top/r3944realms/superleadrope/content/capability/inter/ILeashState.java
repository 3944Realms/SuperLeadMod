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

package top.r3944realms.superleadrope.content.capability.inter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
/**
 * Capability interface for managing leash states of entities and knots.
 */
public interface ILeashState extends INBTSerializable<CompoundTag> {

    /* ----------------------
     * Query leash states
     * ---------------------- */
    Map<UUID, LeashState> getHolderLeashStates();
    Map<BlockPos, LeashState> getKnotLeashStates();

    Optional<LeashState> getLeashState(Entity entity);
    Optional<LeashState> getLeashState(UUID uuid);
    Optional<LeashState> getLeashState(BlockPos pos);

    /* ----------------------
     * Reset holder offsets
     * ---------------------- */
    void resetAllLeashHolderLocationsOffset();
    void resetLeashHolderLocationOffset(Entity holder);
    void resetLeashHolderLocationOffset(UUID holderUUID);
    void resetLeashHolderLocationOffset(BlockPos knotPos);

    /* ----------------------
     * Set holder offsets
     * ---------------------- */
    void setLeashHolderLocationOffset(Entity holder, Vec3 offset);
    void setLeashHolderLocationOffset(UUID holderUUID, Vec3 offset);
    void setLeashHolderLocationOffset(BlockPos knotPos, Vec3 offset);

    /* ----------------------
     * Add holder offsets
     * ---------------------- */
    void addLeashHolderLocationOffset(Entity holder, Vec3 offset);
    void addLeashHolderLocationOffset(UUID holderUUID, Vec3 offset);
    void addLeashHolderLocationOffset(BlockPos knotPos, Vec3 offset);

    /* ----------------------
     * Remove holder offsets
     * ---------------------- */
    void removeLeashHolderLocationOffset(Entity holder);
    void removeLeashHolderLocationOffset(UUID holderUUID);
    void removeLeashHolderLocationOffset(BlockPos knotPos);
    void removeAllLeashHolderLocationOffset();
    void removeAllLeashHolderUUIDLocationOffset();
    void removeAllLeashHolderBlockPosLocationOffset();

    /* ----------------------
     * Apply-entity offset
     * ---------------------- */
    Optional<Vec3> getLeashApplyEntityLocationOffset();
    Vec3 getDefaultLeashApplyEntityLocationOffset();

    void resetAllLeashApplyEntityLocationsOffset();
    void removeLeashApplyEntityLocationOffset();
    void setLeashApplyEntityLocationOffset(Vec3 offset);
    void addLeashApplyEntityLocationOffset(Vec3 offset);

    /* ----------------------
     * Utility & sync
     * ---------------------- */
    void copy(ILeashState other, Entity newEntity);

    void markForSync();
    void immediateSync();
    void checkSync();

    /* ----------------------
     * Data record
     * ---------------------- */
    record LeashState(
            Vec3 holderLocationOffset,
            Vec3 applyEntityLocationOffset,
            Vec3 defaultHolderLocationOffset
    ) {
        public LeashState resetHolderLocationOffset() {
            return new LeashState(defaultHolderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }

        public LeashState setHolderLocationOffset(Vec3 holderLocationOffset) {
            return new LeashState(holderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }

        public LeashState setApplyEntityLocationOffset(Vec3 applyEntityLocationOffset) {
            return new LeashState(holderLocationOffset, applyEntityLocationOffset, defaultHolderLocationOffset);
        }
    }
}
