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
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Capability interface for managing leash data of entities and knots.
 */
public interface ILeashData extends INBTSerializable<CompoundTag> {

    /* ----------------------
     * Add / removeApplyEntity leashes
     * ---------------------- */
    boolean addLeash(Entity holder);
    boolean addLeash(Entity holder, String reserved);
    boolean addLeash(Entity holder, double maxDistance);
    boolean addLeash(Entity holder, double maxDistance, double elasticDistance, int maxKeepTicks);
    boolean addLeash(Entity holder, double maxDistance, String reserved);
    boolean addLeash(Entity holder, double maxDistance, double elasticDistance, int maxKeepTicks, String reserved);

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
    boolean setMaxDistance(Entity holder, double distance);
    boolean setMaxDistance(Entity holder, double distance, int maxKeepTicks);
    boolean setMaxDistance(Entity holder, double distance, int maxKeepTicks, String reserved);

    boolean setMaxDistance(UUID holderUUID, double distance);
    boolean setMaxDistance(UUID holderUUID, double distance, int maxKeepTicks);
    boolean setMaxDistance(UUID holderUUID, double distance, int maxKeepTicks, String reserved);

    boolean setMaxDistance(BlockPos knotPos, double distance);
    boolean setMaxDistance(BlockPos knotPos, double distance, int maxKeepTicks);
    boolean setMaxDistance(BlockPos knotPos, double distance, int maxKeepTicks, String reserved);

    boolean setElasticDistance(Entity holder, double distance);
    boolean setElasticDistance(Entity holder, double distance, int maxKeepTicks);
    boolean setElasticDistance(Entity holder, double distance, int maxKeepTicks, String reserved);

    boolean setElasticDistance(UUID holderUUID, double distance);
    boolean setElasticDistance(UUID holderUUID, double distance, int maxKeepTicks);
    boolean setElasticDistance(UUID holderUUID, double distance, int maxKeepTicks, String reserved);

    boolean setElasticDistance(BlockPos knotPos, double distance);
    boolean setElasticDistance(BlockPos knotPos, double distance, int maxKeepTicks);
    boolean setElasticDistance(BlockPos knotPos, double distance, int maxKeepTicks, String reserved);

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

    /* ----------------------
     * Data record
     * ---------------------- */
    record LeashInfo(
            Optional<BlockPos> blockPosOpt,
            Optional<UUID> holderUUIDOpt,
            Optional<Integer> holderIdOpt, // Only for client side use
            String reserved,               // 保留字段
            double maxDistance,
            double elasticDistance,
            int keepLeashTicks,            // 剩余 Tick 数
            int maxKeepLeashTicks          // 最大保持 Tick 数
    ) {
        public static final LeashInfo EMPTY = new LeashInfo(
                Optional.empty(), Optional.empty(), Optional.empty(),
                "", 12.0D, 6.0D, 0, 0
        );

        /* ---------- Factory ---------- */
        public static LeashInfo create(
                Entity entity,
                String reserved,
                double maxDistance,
                double elasticDistance,
                int keepTicks,
                int maxKeepTicks
        ) {
            return entity instanceof SuperLeashKnotEntity knot
                    ? new LeashInfo(knot.getPos(), entity.getId(), reserved,
                    maxDistance, elasticDistance, keepTicks, maxKeepTicks)
                    : new LeashInfo(entity.getUUID(), entity.getId(), reserved, maxDistance, elasticDistance, keepTicks, maxKeepTicks);
        }

        public LeashInfo(UUID holderUUID, int holderId, String reserved,
                         double maxDistance, double elasticDistance, int keepTicks, int maxKeepTicks) {
            this(Optional.empty(), Optional.of(holderUUID), Optional.of(holderId),
                    reserved, maxDistance, elasticDistance, keepTicks, maxKeepTicks);
        }

        public LeashInfo(BlockPos knotPos, int holderId, String reserved,
                         double maxDistance, double elasticDistance, int keepTicks, int maxKeepTicks) {
            this(Optional.of(knotPos), Optional.empty(), Optional.of(holderId),
                    reserved, maxDistance, elasticDistance, keepTicks, maxKeepTicks);
        }

        /* ---------- State updates ---------- */
        public LeashInfo decrementKeepTicks() {
            return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, reserved,
                    maxDistance, elasticDistance,
                    Math.max(0, keepLeashTicks - 1), maxKeepLeashTicks);
        }

        public LeashInfo resetKeepTicks() {
            return new LeashInfo(blockPosOpt, holderUUIDOpt, holderIdOpt, reserved,
                    maxDistance, elasticDistance,
                    maxKeepLeashTicks, maxKeepLeashTicks);
        }

        public LeashInfo transferHolder(Entity entity) {
            return transferHolder(entity, reserved);
        }

        public LeashInfo transferHolder(Entity entity, String newReserved) {
            boolean isKnot = entity instanceof SuperLeashKnotEntity;
            return new LeashInfo(
                    isKnot ? Optional.of(((SuperLeashKnotEntity) entity).getPos()) : Optional.empty(),
                    !isKnot ? Optional.of(entity.getUUID()) : Optional.empty(),
                    Optional.of(entity.getId()),
                    newReserved, maxDistance, elasticDistance,
                    keepLeashTicks, maxKeepLeashTicks
            );
        }
    }
}