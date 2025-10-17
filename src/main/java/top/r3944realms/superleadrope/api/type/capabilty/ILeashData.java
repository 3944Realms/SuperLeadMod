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

package top.r3944realms.superleadrope.api.type.capabilty;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Capability interface for managing leash data of entities and knots.
 */
@SuppressWarnings("unused")
public interface ILeashData extends INBTSerializable<CompoundTag> {
    Double getStaticMaxDistance();
    void setStaticMaxDistance(Double distance);
    double getDefaultMaxDistance();
    double getCurrentMaxDistance();
    void updateAllMaxDistance();
    Double getStaticElasticDistanceScale();
    void setStaticElasticDistanceScale(Double distance);
    double getDefaultElasticDistanceScale();
    double getCurrentElasticDistanceScale();
    void updateAllElasticDistanceScale();
    default void updateAll() {
        updateAllMaxDistance();
        updateAllElasticDistanceScale();
    }
    /* ----------------------
     * Add / removeApplyEntity leashes
     * ---------------------- */
    boolean addLeash(Entity holder);
    boolean addLeash(Entity holder, String reserved);
    boolean addLeash(Entity holder, Double maxDistance);
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale);
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks);
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, String reserved);
    boolean addLeash(Entity holder, Double maxDistance, String reserved);
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks, String reserved);

    void addLeash(Entity holder, LeashInfo info);

    void addDelayedLeash(Player holderPlayer);
    void removeDelayedLeash(UUID onceHolderPlayerUUID);

    boolean removeLeash(Entity holder);
    boolean removeLeash(UUID holderUUID);
    boolean removeLeash(BlockPos knotPos);

    void removeAllLeashes();
    void removeAllHolderLeashes();
    void removeAllKnotLeashes();

    /* ----------------------
     * Modify leash properties
     * ---------------------- */
    boolean setMaxDistance(Entity holder, Double distance);
    boolean setMaxDistance(Entity holder, Double distance, int maxKeepTicks);
    boolean setMaxDistance(Entity holder, Double distance, int maxKeepTicks, String reserved);

    boolean setMaxDistance(UUID holderUUID, Double distance);
    boolean setMaxDistance(UUID holderUUID, Double distance, int maxKeepTicks);
    boolean setMaxDistance(UUID holderUUID, Double distance, int maxKeepTicks, String reserved);

    boolean setMaxDistance(BlockPos knotPos, Double distance);
    boolean setMaxDistance(BlockPos knotPos, Double distance, int maxKeepTicks);
    boolean setMaxDistance(BlockPos knotPos, Double distance, int maxKeepTicks, String reserved);

    boolean setElasticDistanceScale(Entity holder, Double scale);
    boolean setElasticDistanceScale(Entity holder, Double scale, int maxKeepTicks);
    boolean setElasticDistanceScale(Entity holder, Double scale, int maxKeepTicks, String reserved);

    boolean setElasticDistanceScale(UUID holderUUID, Double scale);
    boolean setElasticDistanceScale(UUID holderUUID, Double scale, int maxKeepTicks);
    boolean setElasticDistanceScale(UUID holderUUID, Double scale, int maxKeepTicks, String reserved);

    boolean setElasticDistanceScale(BlockPos knotPos, Double scale);
    boolean setElasticDistanceScale(BlockPos knotPos, Double scale, int maxKeepTicks);
    boolean setElasticDistanceScale(BlockPos knotPos, Double scale, int maxKeepTicks, String reserved);

    /* ----------------------
     * Apply physics
     * ---------------------- */
    void applyLeashForces();

    /* ----------------------
     * Transfer leash holders
     * ---------------------- */
    boolean transferLeash(Entity holder, Entity newHolder);
    boolean transferLeash(Entity holder, Entity newHolder, String reserved);

    boolean transferLeash(UUID holderUUID, Entity newHolder);
    boolean transferLeash(UUID holderUUID, Entity newHolder, String reserved);

    boolean transferLeash(BlockPos knotPos, Entity newHolder);
    boolean transferLeash(BlockPos knotPos, Entity newHolder, String reserved);

    /* ----------------------
     * Query state
     * ---------------------- */
    boolean hasLeash();
    boolean hasKnotLeash();
    boolean hasHolderLeash();

    Collection<LeashInfo> getAllLeashes();

    boolean isLeashedBy(Entity holder);
    boolean isLeashedBy(UUID holderUUID);
    boolean isLeashedBy(BlockPos knotPos);

    boolean isInDelayedLeash(UUID holderUUID);

    Optional<LeashInfo> getLeashInfo(Entity holder);
    Optional<LeashInfo> getLeashInfo(UUID holderUUID);
    Optional<LeashInfo> getLeashInfo(BlockPos knotPos);

    boolean canBeLeashed();
    boolean canBeAttachedTo(Entity entity);

    /* ----------------------
     * Occupy / sync
     * ---------------------- */
    /**
     * 抢占位（已离线玩家）。
     * 用于解决玩家下线后所持有对象会移除持有者的问题（实际上是占用个弱集合）
     */
    Optional<UUID> occupyLeash();

    void markForSync();
    void immediateSync();
    void checkSync();

}