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
    /**
     * Self entity.
     *
     * @return the entity
     */
    Entity self();

    /**
     * Gets static max distance.
     *
     * @return the static max distance
     */
    Double getStaticMaxDistance();

    /**
     * Sets static max distance.
     *
     * @param distance the distance
     */
    void setStaticMaxDistance(Double distance);

    /**
     * Gets default max distance.
     *
     * @return the default max distance
     */
    double getDefaultMaxDistance();

    /**
     * Gets current max distance.
     *
     * @return the current max distance
     */
    double getCurrentMaxDistance();

    /**
     * Update all max distance.
     */
    void updateAllMaxDistance();

    /**
     * Gets static elastic distance scale.
     *
     * @return the static elastic distance scale
     */
    Double getStaticElasticDistanceScale();

    /**
     * Sets static elastic distance scale.
     *
     * @param distance the distance
     */
    void setStaticElasticDistanceScale(Double distance);

    /**
     * Gets default elastic distance scale.
     *
     * @return the default elastic distance scale
     */
    double getDefaultElasticDistanceScale();

    /**
     * Gets current elastic distance scale.
     *
     * @return the current elastic distance scale
     */
    double getCurrentElasticDistanceScale();

    /**
     * Update all elastic distance scale.
     */
    void updateAllElasticDistanceScale();

    /**
     * Update all.
     */
    default void updateAll() {
        updateAllMaxDistance();
        updateAllElasticDistanceScale();
    }

    /**
     * Add leash boolean.
     *
     * @param holder the holder
     * @return the boolean
     */
    /* ----------------------
     * Add / removeApplyEntity leashes
     * ---------------------- */
    boolean addLeash(Entity holder);

    /**
     * Add leash boolean.
     *
     * @param holder   the holder
     * @param reserved the reserved
     * @return the boolean
     */
    boolean addLeash(Entity holder, String reserved);

    /**
     * Add leash boolean.
     *
     * @param holder      the holder
     * @param maxDistance the max distance
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance);

    /**
     * Add leash boolean.
     *
     * @param holder               the holder
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale);

    /**
     * Add leash boolean.
     *
     * @param holder               the holder
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param maxKeepTicks         the max keep ticks
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks);

    /**
     * Add leash boolean.
     *
     * @param holder               the holder
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param reserved             the reserved
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, String reserved);

    /**
     * Add leash boolean.
     *
     * @param holder      the holder
     * @param maxDistance the max distance
     * @param reserved    the reserved
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance, String reserved);

    /**
     * Add leash boolean.
     *
     * @param holder               the holder
     * @param maxDistance          the max distance
     * @param elasticDistanceScale the elastic distance scale
     * @param maxKeepTicks         the max keep ticks
     * @param reserved             the reserved
     * @return the boolean
     */
    boolean addLeash(Entity holder, Double maxDistance, Double elasticDistanceScale, int maxKeepTicks, String reserved);

    /**
     * Add leash.
     *
     * @param holder the holder
     * @param info   the info
     */
    void addLeash(Entity holder, LeashInfo info);

    /**
     * Add delayed leash.
     *
     * @param holderPlayer the holder player
     */
    void addDelayedLeash(Player holderPlayer);

    /**
     * Remove delayed leash.
     *
     * @param onceHolderPlayerUUID the once holder player uuid
     */
    void removeDelayedLeash(UUID onceHolderPlayerUUID);

    /**
     * Remove leash boolean.
     *
     * @param holder the holder
     * @return the boolean
     */
    boolean removeLeash(Entity holder);

    /**
     * Remove leash boolean.
     *
     * @param holderUUID the holder uuid
     * @return the boolean
     */
    boolean removeLeash(UUID holderUUID);

    /**
     * Remove leash boolean.
     *
     * @param knotPos the knot pos
     * @return the boolean
     */
    boolean removeLeash(BlockPos knotPos);

    /**
     * Remove all leashes.
     */
    void removeAllLeashes();

    /**
     * Remove all holder leashes.
     */
    void removeAllHolderLeashes();

    /**
     * Remove all knot leashes.
     */
    void removeAllKnotLeashes();

    /**
     * Sets max distance.
     *
     * @param holder   the holder
     * @param distance the distance
     * @return the max distance
     */
    /* ----------------------
     * Modify leash properties
     * ---------------------- */
    boolean setMaxDistance(Entity holder, Double distance);

    /**
     * Sets max distance.
     *
     * @param holder       the holder
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @return the max distance
     */
    boolean setMaxDistance(Entity holder, Double distance, int maxKeepTicks);

    /**
     * Sets max distance.
     *
     * @param holder       the holder
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the max distance
     */
    boolean setMaxDistance(Entity holder, Double distance, int maxKeepTicks, String reserved);

    /**
     * Sets max distance.
     *
     * @param holderUUID the holder uuid
     * @param distance   the distance
     * @return the max distance
     */
    boolean setMaxDistance(UUID holderUUID, Double distance);

    /**
     * Sets max distance.
     *
     * @param holderUUID   the holder uuid
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @return the max distance
     */
    boolean setMaxDistance(UUID holderUUID, Double distance, int maxKeepTicks);

    /**
     * Sets max distance.
     *
     * @param holderUUID   the holder uuid
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the max distance
     */
    boolean setMaxDistance(UUID holderUUID, Double distance, int maxKeepTicks, String reserved);

    /**
     * Sets max distance.
     *
     * @param knotPos  the knot pos
     * @param distance the distance
     * @return the max distance
     */
    boolean setMaxDistance(BlockPos knotPos, Double distance);

    /**
     * Sets max distance.
     *
     * @param knotPos      the knot pos
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @return the max distance
     */
    boolean setMaxDistance(BlockPos knotPos, Double distance, int maxKeepTicks);

    /**
     * Sets max distance.
     *
     * @param knotPos      the knot pos
     * @param distance     the distance
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the max distance
     */
    boolean setMaxDistance(BlockPos knotPos, Double distance, int maxKeepTicks, String reserved);

    /**
     * Sets elastic distance scale.
     *
     * @param holder the holder
     * @param scale  the scale
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(Entity holder, Double scale);

    /**
     * Sets elastic distance scale.
     *
     * @param holder       the holder
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(Entity holder, Double scale, int maxKeepTicks);

    /**
     * Sets elastic distance scale.
     *
     * @param holder       the holder
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(Entity holder, Double scale, int maxKeepTicks, String reserved);

    /**
     * Sets elastic distance scale.
     *
     * @param holderUUID the holder uuid
     * @param scale      the scale
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(UUID holderUUID, Double scale);

    /**
     * Sets elastic distance scale.
     *
     * @param holderUUID   the holder uuid
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(UUID holderUUID, Double scale, int maxKeepTicks);

    /**
     * Sets elastic distance scale.
     *
     * @param holderUUID   the holder uuid
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(UUID holderUUID, Double scale, int maxKeepTicks, String reserved);

    /**
     * Sets elastic distance scale.
     *
     * @param knotPos the knot pos
     * @param scale   the scale
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(BlockPos knotPos, Double scale);

    /**
     * Sets elastic distance scale.
     *
     * @param knotPos      the knot pos
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(BlockPos knotPos, Double scale, int maxKeepTicks);

    /**
     * Sets elastic distance scale.
     *
     * @param knotPos      the knot pos
     * @param scale        the scale
     * @param maxKeepTicks the max keep ticks
     * @param reserved     the reserved
     * @return the elastic distance scale
     */
    boolean setElasticDistanceScale(BlockPos knotPos, Double scale, int maxKeepTicks, String reserved);

    /**
     * Apply leash forces.
     */
    /* ----------------------
     * Apply physics
     * ---------------------- */
    void applyLeashForces();

    /**
     * Transfer leash boolean.
     *
     * @param holder    the holder
     * @param newHolder the new holder
     * @return the boolean
     */
    /* ----------------------
     * Transfer leash holders
     * ---------------------- */
    boolean transferLeash(Entity holder, Entity newHolder);

    /**
     * Transfer leash boolean.
     *
     * @param holder    the holder
     * @param newHolder the new holder
     * @param reserved  the reserved
     * @return the boolean
     */
    boolean transferLeash(Entity holder, Entity newHolder, String reserved);

    /**
     * Transfer leash boolean.
     *
     * @param holderUUID the holder uuid
     * @param newHolder  the new holder
     * @return the boolean
     */
    boolean transferLeash(UUID holderUUID, Entity newHolder);

    /**
     * Transfer leash boolean.
     *
     * @param holderUUID the holder uuid
     * @param newHolder  the new holder
     * @param reserved   the reserved
     * @return the boolean
     */
    boolean transferLeash(UUID holderUUID, Entity newHolder, String reserved);

    /**
     * Transfer leash boolean.
     *
     * @param knotPos   the knot pos
     * @param newHolder the new holder
     * @return the boolean
     */
    boolean transferLeash(BlockPos knotPos, Entity newHolder);

    /**
     * Transfer leash boolean.
     *
     * @param knotPos   the knot pos
     * @param newHolder the new holder
     * @param reserved  the reserved
     * @return the boolean
     */
    boolean transferLeash(BlockPos knotPos, Entity newHolder, String reserved);


    /**
     * Sets max keep ticks.
     *
     * @param holder       the holder
     * @param maxKeepTicks the max keep ticks
     * @return the max keep ticks
     */
    boolean setMaxKeepTicks(Entity holder, int maxKeepTicks);

    /**
     * Sets max keep ticks.
     *
     * @param holderUUID   the holder uuid
     * @param maxKeepTicks the max keep ticks
     * @return the max keep ticks
     */
    boolean setMaxKeepTicks(UUID holderUUID, int maxKeepTicks);

    /**
     * Sets max keep ticks.
     *
     * @param knotPos      the knot pos
     * @param maxKeepTicks the max keep ticks
     * @return the max keep ticks
     */
    boolean setMaxKeepTicks(BlockPos knotPos, int maxKeepTicks);

    /**
     * Gets max keep ticks.
     *
     * @param holder the holder
     * @return the max keep ticks
     */
    int getMaxKeepTicks(Entity holder);

    /**
     * Gets max keep ticks.
     *
     * @param holderUUID the holder uuid
     * @return the max keep ticks
     */
    int getMaxKeepTicks(UUID holderUUID);

    /**
     * Gets max keep ticks.
     *
     * @param knotPos the knot pos
     * @return the max keep ticks
     */
    int getMaxKeepTicks(BlockPos knotPos);

    /**
     * Has leash boolean.
     *
     * @return the boolean
     */
    /* ----------------------
     * Query state
     * ---------------------- */
    boolean hasLeash();

    /**
     * Has knot leash boolean.
     *
     * @return the boolean
     */
    boolean hasKnotLeash();

    /**
     * Has holder leash boolean.
     *
     * @return the boolean
     */
    boolean hasHolderLeash();

    /**
     * Gets all leashes.
     *
     * @return the all leashes
     */
    Collection<LeashInfo> getAllLeashes();

    /**
     * Is leashed by boolean.
     *
     * @param holder the holder
     * @return the boolean
     */
    boolean isLeashedBy(Entity holder);

    /**
     * Is leashed by boolean.
     *
     * @param holderUUID the holder uuid
     * @return the boolean
     */
    boolean isLeashedBy(UUID holderUUID);

    /**
     * Is leashed by boolean.
     *
     * @param knotPos the knot pos
     * @return the boolean
     */
    boolean isLeashedBy(BlockPos knotPos);

    /**
     * Is in delayed leash boolean.
     *
     * @param holderUUID the holder uuid
     * @return the boolean
     */
    boolean isInDelayedLeash(UUID holderUUID);

    /**
     * Gets leash info.
     *
     * @param holder the holder
     * @return the leash info
     */
    Optional<LeashInfo> getLeashInfo(Entity holder);

    /**
     * Gets leash info.
     *
     * @param holderUUID the holder uuid
     * @return the leash info
     */
    Optional<LeashInfo> getLeashInfo(UUID holderUUID);

    /**
     * Gets leash info.
     *
     * @param knotPos the knot pos
     * @return the leash info
     */
    Optional<LeashInfo> getLeashInfo(BlockPos knotPos);

    /**
     * Can be leashed boolean.
     *
     * @return the boolean
     */
    boolean canBeLeashed();

    /**
     * Can be attached to boolean.
     *
     * @param entity the entity
     * @return the boolean
     */
    boolean canBeAttachedTo(Entity entity);

    /* ----------------------
     * Occupy / sync
     * ---------------------- */

    /**
     * 抢占位（已离线玩家）。
     * 用于解决玩家下线后所持有对象会移除持有者的问题（实际上是占用个弱集合）
     *
     * @return the optional
     */
    Optional<UUID> occupyLeash();

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

}