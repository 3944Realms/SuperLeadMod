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

package top.r3944realms.superleadrope.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.api.type.capabilty.LeashHolder;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class SuperLeadRopeEvent extends Event implements IModBusEvent {
    private final Entity LeashedEntity;

    protected SuperLeadRopeEvent(Entity leashedEntity) {
        LeashedEntity = leashedEntity;
    }

    public Entity getLeashedEntity() {
        return LeashedEntity;
    }
    // ADD LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class AddLeash extends SuperLeadRopeEvent {
        private final Entity holderEntity;
        @Nullable
        private final Double maxLeashDistance;
        @Nullable
        private final Double elasticDistanceScale;
        public AddLeash(Entity leashedEntity, Entity holderEntity) {
           this(leashedEntity, holderEntity, null, null);
        }
        public AddLeash(Entity leashedEntity, Entity holderEntity, @Nullable Double maxLeashDistance, @Nullable Double elasticDistanceScale) {
            super(leashedEntity);
            this.holderEntity = holderEntity;
            this.maxLeashDistance = maxLeashDistance;
            this.elasticDistanceScale = elasticDistanceScale;
        }
        public Entity getHolderEntity() {
            return holderEntity;
        }
        public @Nullable Double getMaxLeashDistance() {
            return maxLeashDistance;
        }
        public @Nullable Double getElasticDistanceScale() {
            return elasticDistanceScale;
        }
    }
    // REMOVE LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class RemoveLeash extends SuperLeadRopeEvent {
        private final LeashHolder leashHolder;
        public RemoveLeash(Entity leashedEntity, UUID holderEntity) {
            this(leashedEntity, holderEntity, null, false);
        }
        public RemoveLeash(Entity leashedEntity, BlockPos holderKnot) {
            this(leashedEntity, null, holderKnot, true);
        }
        private RemoveLeash(Entity leashedEntity, @Nullable UUID holderEntity, @Nullable BlockPos holderPos, boolean isSuperLeadRopeKnot) {
            super(leashedEntity);
            if (isSuperLeadRopeKnot) {
                leashHolder = new LeashHolder(holderPos);
            } else leashHolder = new LeashHolder(holderEntity);
        }
        public LeashHolder getLeashHolder() {
            return leashHolder;
        }
    }

    // TRANSFORM LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class TransferLeash extends SuperLeadRopeEvent {
        private final LeashHolder oldLeashHolder;
        private final Entity newLeashHolder;
        public TransferLeash(Entity leashedEntity, UUID holderEntity, Entity newLeashHolder) {
            this(leashedEntity, holderEntity, null, false , newLeashHolder);
        }
        public TransferLeash(Entity leashedEntity, BlockPos holderKnot, Entity newLeashHolder) {
            this(leashedEntity, null, holderKnot, true, newLeashHolder);
        }
        private TransferLeash(Entity leashedEntity, @Nullable UUID holderEntity, @Nullable BlockPos holderPos, boolean isSuperLeadRopeKnot, Entity newLeashHolder) {
            super(leashedEntity);
            if (isSuperLeadRopeKnot) {
                oldLeashHolder = new LeashHolder(holderPos);
            } else oldLeashHolder = new LeashHolder(holderEntity);
            this.newLeashHolder = newLeashHolder;
        }

        public Entity getNewLeashHolder() {
            return newLeashHolder;
        }

        public LeashHolder getOldLeashHolder() {
            return oldLeashHolder;
        }
    }

    // MODIFY LEASH MAX_LEASH_LENGTH / ELASTIC_DISTANCE_SCALE
    @SuppressWarnings("unused")
    @Cancelable
    public static class ModifyValue extends SuperLeadRopeEvent {
        @Nullable
        private final LeashHolder holder;
        @Nullable
        private final Double oldValue;
        @Nullable
        private final Double newValue;
        private final Type type;
        private final Scope scope;
        public enum Type {
            MAX_DISTANCE,
            ELASTIC_DISTANCE_SCALE,
        }
        public enum Scope {
            STATIC,
            INSTANCE
        }
        public ModifyValue(Entity leashedEntity, UUID holderUUID, @Nullable Double oldValue, @Nullable Double newValue, Type type) {
            super(leashedEntity);
            this.holder = new LeashHolder(holderUUID);
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.INSTANCE;
        }
        public ModifyValue(Entity leashedEntity, BlockPos knotBlockpos, @Nullable Double oldValue, @Nullable Double newValue, Type type) {
            super(leashedEntity);
            this.holder = new LeashHolder(knotBlockpos);
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.INSTANCE;
        }
        public ModifyValue(Entity leashedEntity, @Nullable Double oldValue, @Nullable Double newValue, Type type) {
            super(leashedEntity);
            this.holder = null;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.STATIC;
        }
        public @Nullable LeashHolder getHolder() {
            return holder;
        }

        public @Nullable Double getOldValue() {
            return oldValue;
        }

        public @Nullable Double getNewValue() {
            return newValue;
        }

        public Type getType() {
            return type;
        }

        public Scope getScope() {
            return scope;
        }

    }

    // HAS FOCUS
    @SuppressWarnings("unused")
    @Cancelable
    public static class hasFocus extends SuperLeadRopeEvent {
        private final Map<UUID, LeashInfo> vaildLeashHolders ;
        private final Map<BlockPos, LeashInfo> vaildLeashKnots;
        private final Entity finalForceTarget;
        private Vec3 combinedForce;
        public hasFocus(Entity leashedEntity, Entity finalForceTarget, Vec3 combinedForce, Map<UUID, LeashInfo> vaildLeashHolders, Map<BlockPos, LeashInfo> vaildLeashKnots) {
            super(leashedEntity);
            this.finalForceTarget = finalForceTarget;
            this.combinedForce = combinedForce;
            this.vaildLeashHolders = new HashMap<>(vaildLeashHolders);
            this.vaildLeashKnots = new HashMap<>(vaildLeashKnots);
        }

        public Entity getFinalForceTarget() {
            return finalForceTarget;
        }
        public Vec3 getCombinedForce() {
            return combinedForce;
        }

        public void setCombinedForce(Vec3 combinedForce) {
            this.combinedForce = combinedForce;
        }
        public Map<UUID, LeashInfo> getVaildLeashHolders() {
            return vaildLeashHolders;
        }

        public Map<BlockPos, LeashInfo> getVaildLeashKnots() {
            return vaildLeashKnots;
        }
    }

    // KEEP NOT BREAK TICK
    @SuppressWarnings("unused")
    public static class keepNotBreakTick extends SuperLeadRopeEvent {
        private final int remainedTicks;
        private final Entity holderEntity;
        private final Map.Entry<?, LeashInfo> entry;
        public keepNotBreakTick(Entity leashedEntity, int remainedTicks, Entity holderEntity, Map.Entry<?, LeashInfo> entry) {
            super(leashedEntity);
            this.remainedTicks = remainedTicks;
            this.holderEntity = holderEntity;
            this.entry = entry;
        }
        public Entity getHolderEntity() {
            return holderEntity;
        }
        public int getRemainedTicks() {
            return remainedTicks;
        }
        public void resetRemainedTicks() {
            entry.setValue(entry.getValue().resetKeepTicks());
        }
        public int getMaxKeepTicks() {
            return entry.getValue().maxKeepLeashTicks();
        }

    }
    // TELEPORT
    @Cancelable
    @SuppressWarnings("unused")
    public static class teleportWithHolder extends SuperLeadRopeEvent {
        private final Entity holderEntity;
        private final Level originalLevel, newLevel;
        private final Vec3 originalPosition, newPosition;
        public teleportWithHolder(Entity leashedEntity, Entity holderEntity, Level originalLevel, Level newLevel, Vec3 originalPosition, Vec3 newPosition) {
            super(leashedEntity);
            this.holderEntity = holderEntity;
            this.originalLevel = originalLevel;
            this.newLevel = newLevel;
            this.originalPosition = originalPosition;
            this.newPosition = newPosition;
        }
        public Entity getHolderEntity() {
            return holderEntity;
        }
        public Vec3 getOriginalPosition() {
            return originalPosition;
        }

        public Level getOriginalLevel() {
            return originalLevel;
        }

        public Level getNewLevel() {
            return newLevel;
        }

        public Vec3 getNewPosition() {
            return newPosition;
        }

    }

}
