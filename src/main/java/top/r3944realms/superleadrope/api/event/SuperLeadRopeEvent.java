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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The type Super lead rope event.
 */
@SuppressWarnings("unused")
public abstract class SuperLeadRopeEvent extends Event implements IModBusEvent {
    private final Entity LeashedEntity;
    /**
     * The Has modified.
     */
    protected boolean hasModified;

    /**
     * Is modified boolean.
     *
     * @return the boolean
     */
    public boolean isModified() {
        return hasModified;
    }

    /**
     * Mark modified.
     */
    public void markModified() {
        this.hasModified = true;
    }


    /**
     * Instantiates a new Super lead rope event.
     *
     * @param leashedEntity the leashed entity
     */
    protected SuperLeadRopeEvent(Entity leashedEntity) {
        LeashedEntity = leashedEntity;
    }

    /**
     * Gets leashed entity.
     *
     * @return the leashed entity
     */
    public Entity getLeashedEntity() {
        return LeashedEntity;
    }

    /**
     * The type Add leash.
     */
// ADD LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class AddLeash extends SuperLeadRopeEvent {
        private final Entity holderEntity;
        @Nullable
        private Double maxLeashDistance;
        @Nullable
        private Double elasticDistanceScale;
        private int maxKeepLeashTicks;

        /**
         * Instantiates a new Add leash.
         *
         * @param leashedEntity     the leashed entity
         * @param holderEntity      the holder entity
         * @param maxKeepLeashTicks the max keep leash ticks
         */
        public AddLeash(Entity leashedEntity, Entity holderEntity, int maxKeepLeashTicks) {
           this(leashedEntity, holderEntity, null, null, maxKeepLeashTicks);
        }

        /**
         * Instantiates a new Add leash.
         *
         * @param leashedEntity        the leashed entity
         * @param holderEntity         the holder entity
         * @param maxLeashDistance     the max leash distance
         * @param elasticDistanceScale the elastic distance scale
         * @param maxKeepLeashTicks    the max keep leash ticks
         */
        public AddLeash(Entity leashedEntity, Entity holderEntity, @Nullable Double maxLeashDistance, @Nullable Double elasticDistanceScale, int maxKeepLeashTicks) {
            super(leashedEntity);
            this.holderEntity = holderEntity;
            this.maxLeashDistance = maxLeashDistance;
            this.elasticDistanceScale = elasticDistanceScale;
            this.maxKeepLeashTicks = maxKeepLeashTicks;
        }

        /**
         * Sets elastic distance scale.
         *
         * @param elasticDistanceScale the elastic distance scale
         */
        public void setElasticDistanceScale(@Nullable Double elasticDistanceScale) {
            markModified();
            this.elasticDistanceScale = elasticDistanceScale;
        }

        /**
         * Sets max leash distance.
         *
         * @param maxLeashDistance the max leash distance
         */
        public void setMaxLeashDistance(@Nullable Double maxLeashDistance) {
            markModified();
            this.maxLeashDistance = maxLeashDistance;
        }

        /**
         * Sets max keep leash ticks.
         *
         * @param maxKeepLeashTicks the max keep leash ticks
         */
        public void setMaxKeepLeashTicks(int maxKeepLeashTicks) {
            if (maxKeepLeashTicks < 0) return;
            markModified();
            this.maxKeepLeashTicks = maxKeepLeashTicks;
        }

        /**
         * Gets max keep leash ticks.
         *
         * @return the max keep leash ticks
         */
        public int getMaxKeepLeashTicks() {
            return maxKeepLeashTicks;
        }

        /**
         * Gets holder entity.
         *
         * @return the holder entity
         */
        public Entity getHolderEntity() {
            return holderEntity;
        }

        /**
         * Gets max leash distance.
         *
         * @return the max leash distance
         */
        public @Nullable Double getMaxLeashDistance() {
            return maxLeashDistance;
        }

        /**
         * Gets elastic distance scale.
         *
         * @return the elastic distance scale
         */
        public @Nullable Double getElasticDistanceScale() {
            return elasticDistanceScale;
        }
    }

    /**
     * The type Remove leash.
     */
// REMOVE LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class RemoveLeash extends SuperLeadRopeEvent {
        private final LeashHolder leashHolder;

        /**
         * Instantiates a new Remove leash.
         *
         * @param leashedEntity the leashed entity
         * @param holderEntity  the holder entity
         */
        public RemoveLeash(Entity leashedEntity, UUID holderEntity) {
            this(leashedEntity, holderEntity, null, false);
        }

        /**
         * Instantiates a new Remove leash.
         *
         * @param leashedEntity the leashed entity
         * @param holderKnot    the holder knot
         */
        public RemoveLeash(Entity leashedEntity, BlockPos holderKnot) {
            this(leashedEntity, null, holderKnot, true);
        }
        private RemoveLeash(Entity leashedEntity, @Nullable UUID holderEntity, @Nullable BlockPos holderPos, boolean isSuperLeadRopeKnot) {
            super(leashedEntity);
            if (isSuperLeadRopeKnot) {
                leashHolder = new LeashHolder(holderPos);
            } else leashHolder = new LeashHolder(holderEntity);
        }

        /**
         * Gets leash holder.
         *
         * @return the leash holder
         */
        public LeashHolder getLeashHolder() {
            return leashHolder;
        }
    }

    /**
     * The type Transfer leash.
     */
// TRANSFORM LEASH
    @SuppressWarnings("unused")
    @Cancelable
    public static class TransferLeash extends SuperLeadRopeEvent {
        private final LeashHolder oldLeashHolder;
        private final Entity newLeashHolder;
        private int maxKeepLeashTicks;

        /**
         * Instantiates a new Transfer leash.
         *
         * @param leashedEntity     the leashed entity
         * @param holderEntity      the holder entity
         * @param newLeashHolder    the new leash holder
         * @param maxKeepLeashTicks the max keep leash ticks
         */
        public TransferLeash(Entity leashedEntity, UUID holderEntity, Entity newLeashHolder, int maxKeepLeashTicks) {
            this(leashedEntity, holderEntity, null, false , newLeashHolder, maxKeepLeashTicks);
        }

        /**
         * Instantiates a new Transfer leash.
         *
         * @param leashedEntity     the leashed entity
         * @param holderKnot        the holder knot
         * @param newLeashHolder    the new leash holder
         * @param maxKeepLeashTicks the max keep leash ticks
         */
        public TransferLeash(Entity leashedEntity, BlockPos holderKnot, Entity newLeashHolder, int maxKeepLeashTicks) {
            this(leashedEntity, null, holderKnot, true, newLeashHolder, maxKeepLeashTicks);
        }
        private TransferLeash(Entity leashedEntity, @Nullable UUID holderEntity, @Nullable BlockPos holderPos, boolean isSuperLeadRopeKnot, Entity newLeashHolder, int maxKeepLeashTicks) {
            super(leashedEntity);
            if (isSuperLeadRopeKnot) {
                oldLeashHolder = new LeashHolder(holderPos);
            } else oldLeashHolder = new LeashHolder(holderEntity);
            this.newLeashHolder = newLeashHolder;
            this.maxKeepLeashTicks = maxKeepLeashTicks;
        }

        /**
         * Gets new leash holder.
         *
         * @return the new leash holder
         */
        public Entity getNewLeashHolder() {
            return newLeashHolder;
        }

        /**
         * Gets old leash holder.
         *
         * @return the old leash holder
         */
        public LeashHolder getOldLeashHolder() {
            return oldLeashHolder;
        }

        /**
         * Sets max keep leash ticks.
         *
         * @param maxKeepLeashTicks the max keep leash ticks
         */
        public void setMaxKeepLeashTicks(int maxKeepLeashTicks) {
            if(maxKeepLeashTicks < 0) return;
            markModified();
            this.maxKeepLeashTicks = maxKeepLeashTicks;
        }

        /**
         * Gets max keep leash ticks.
         *
         * @return the max keep leash ticks
         */
        public int getMaxKeepLeashTicks() {
            return maxKeepLeashTicks;
        }
    }

    /**
     * The type Modify value.
     *
     * @param <T> the type parameter
     */
// MODIFY LEASH MAX_LEASH_LENGTH / ELASTIC_DISTANCE_SCALE
    @SuppressWarnings("unused")
    @Cancelable
    public static class ModifyValue<T> extends SuperLeadRopeEvent {
        @Nullable
        private final LeashHolder holder;
        @Nullable
        private final T oldValue;
        @Nullable
        private T newValue;
        private final Type type;
        private final Scope scope;

        /**
         * The enum Type.
         */
        public enum Type {
            /**
             * Max distance type.
             */
            MAX_DISTANCE(Double.class),
            /**
             * Elastic distance scale type.
             */
            ELASTIC_DISTANCE_SCALE(Double.class),
            /**
             * Max keep leash ticks type.
             */
            MAX_KEEP_LEASH_TICKS(Integer.class),
            /**
             * Custom data type.
             */
            CUSTOM_DATA(String.class); // 支持更多类型

            private final Class<?> valueType;

            Type(Class<?> valueType) {
                this.valueType = valueType;
            }

            /**
             * Gets value type.
             *
             * @return the value type
             */
            public Class<?> getValueType() {
                return valueType;
            }
        }

        /**
         * The enum Scope.
         */
        public enum Scope {
            /**
             * Instance scope.
             */
            INSTANCE,
            /**
             * Static scope.
             */
            STATIC
        }

        /**
         * Instantiates a new Modify value.
         *
         * @param leashedEntity the leashed entity
         * @param holderUUID    the holder uuid
         * @param oldValue      the old value
         * @param newValue      the new value
         * @param type          the type
         */
// 构造方法 - UUID holder
        public ModifyValue(Entity leashedEntity, UUID holderUUID,
                           @Nullable T oldValue, @Nullable T newValue,
                           Type type) {
            super(leashedEntity);
            this.holder = new LeashHolder(holderUUID);
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.INSTANCE;
        }

        /**
         * Instantiates a new Modify value.
         *
         * @param leashedEntity the leashed entity
         * @param knotBlockpos  the knot blockpos
         * @param oldValue      the old value
         * @param newValue      the new value
         * @param type          the type
         */
// 构造方法 - BlockPos holder
        public ModifyValue(Entity leashedEntity, BlockPos knotBlockpos,
                           @Nullable T oldValue, @Nullable T newValue,
                           Type type) {
            super(leashedEntity);
            this.holder = new LeashHolder(knotBlockpos);
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.INSTANCE;
        }

        /**
         * Instantiates a new Modify value.
         *
         * @param leashedEntity the leashed entity
         * @param oldValue      the old value
         * @param newValue      the new value
         * @param type          the type
         */
// 构造方法 - 静态作用域
        public ModifyValue(Entity leashedEntity,
                           @Nullable T oldValue, @Nullable T newValue,
                           Type type) {
            super(leashedEntity);
            this.holder = null;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.type = type;
            this.scope = Scope.STATIC;
        }


        /**
         * Gets old value as.
         *
         * @param <R>   the type parameter
         * @param clazz the clazz
         * @return the old value as
         */
// 类型安全的获取方法
        @SuppressWarnings("unchecked")
        public <R> R getOldValueAs(Class<R> clazz) {
            if (clazz.isInstance(oldValue)) {
                return (R) oldValue;
            }
            return null;
        }

        /**
         * Gets new value as.
         *
         * @param <R>   the type parameter
         * @param clazz the clazz
         * @return the new value as
         */
        @SuppressWarnings("unchecked")
        public <R> R getNewValueAs(Class<R> clazz) {
            if (clazz.isInstance(newValue)) {
                return (R) newValue;
            }
            return null;
        }

        /**
         * Gets old value.
         *
         * @return the old value
         */
        @SuppressWarnings("unchecked")
        public T getOldValue() {
            return (T) getOldValueAs(type.valueType);
        }

        /**
         * Gets new value.
         *
         * @return the new value
         */
        @SuppressWarnings("unchecked")
        public T getNewValue() {
            return (T) getNewValueAs(type.valueType);
        }


        /**
         * Sets new value.
         *
         * @param newValue the new value
         */
        public void setNewValue(@Nullable T newValue) {
            if (newValue != null && !type.valueType.isInstance(newValue)) {
                throw new IllegalArgumentException(
                        "Expected value of type " + type.valueType + ", but got " + newValue.getClass());
            }
            markModified();
            this.newValue = newValue;
        }

    }

    /**
     * The type Has focus.
     */
// HAS FOCUS
    @SuppressWarnings("unused")
    @Cancelable
    public static class hasFocus extends SuperLeadRopeEvent {
        private final Map<UUID, LeashInfo> vaildLeashHolders ;
        private final Map<BlockPos, LeashInfo> vaildLeashKnots;
        private final Entity finalForceTarget;
        private Vec3 combinedForce;

        /**
         * Instantiates a new Has focus.
         *
         * @param leashedEntity     the leashed entity
         * @param finalForceTarget  the final force target
         * @param combinedForce     the combined force
         * @param vaildLeashHolders the vaild leash holders
         * @param vaildLeashKnots   the vaild leash knots
         */
        public hasFocus(Entity leashedEntity, Entity finalForceTarget, Vec3 combinedForce, Map<UUID, LeashInfo> vaildLeashHolders, Map<BlockPos, LeashInfo> vaildLeashKnots) {
            super(leashedEntity);
            this.finalForceTarget = finalForceTarget;
            this.combinedForce = combinedForce;
            this.vaildLeashHolders = new HashMap<>(vaildLeashHolders);
            this.vaildLeashKnots = new HashMap<>(vaildLeashKnots);
        }

        /**
         * Gets final force target.
         *
         * @return the final force target
         */
        public Entity getFinalForceTarget() {
            return finalForceTarget;
        }

        /**
         * Gets combined force.
         *
         * @return the combined force
         */
        public Vec3 getCombinedForce() {
            return combinedForce;
        }

        /**
         * Sets combined force.
         *
         * @param combinedForce the combined force
         */
        public void setCombinedForce(Vec3 combinedForce) {
            markModified();
            this.combinedForce = combinedForce;
        }

        /**
         * Gets vaild leash holders.
         *
         * @return the vaild leash holders
         */
        public Map<UUID, LeashInfo> getVaildLeashHolders() {
            return vaildLeashHolders;
        }

        /**
         * Gets vaild leash knots.
         *
         * @return the vaild leash knots
         */
        public Map<BlockPos, LeashInfo> getVaildLeashKnots() {
            return vaildLeashKnots;
        }
    }

    /**
     * The type Keep not break tick.
     */
// KEEP NOT BREAK TICK
    @SuppressWarnings("unused")
    public static class keepNotBreakTick extends SuperLeadRopeEvent {
        private final int remainedTicks;
        private final Entity holderEntity;
        private final Map.Entry<?, LeashInfo> entry;

        /**
         * Instantiates a new Keep not break tick.
         *
         * @param leashedEntity the leashed entity
         * @param remainedTicks the remained ticks
         * @param holderEntity  the holder entity
         * @param entry         the entry
         */
        public keepNotBreakTick(Entity leashedEntity, int remainedTicks, Entity holderEntity, Map.Entry<?, LeashInfo> entry) {
            super(leashedEntity);
            this.remainedTicks = remainedTicks;
            this.holderEntity = holderEntity;
            this.entry = entry;
        }

        /**
         * Gets holder entity.
         *
         * @return the holder entity
         */
        public Entity getHolderEntity() {
            return holderEntity;
        }

        /**
         * Gets remained ticks.
         *
         * @return the remained ticks
         */
        public int getRemainedTicks() {
            return remainedTicks;
        }

        /**
         * Reset remained ticks.
         */
        public void resetRemainedTicks() {
            entry.setValue(entry.getValue().resetKeepTicks());
        }

        /**
         * Gets max keep ticks.
         *
         * @return the max keep ticks
         */
        public int getMaxKeepTicks() {
            return entry.getValue().maxKeepLeashTicks();
        }

    }

    /**
     * The type Teleport with holder.
     */
// TELEPORT
    @Cancelable
    @SuppressWarnings("unused")
    public static class teleportWithHolder extends SuperLeadRopeEvent {
        private final Entity holderEntity;
        private final Level originalLevel, newLevel;
        private final Vec3 originalPosition, newPosition;

        /**
         * Instantiates a new Teleport with holder.
         *
         * @param leashedEntity    the leashed entity
         * @param holderEntity     the holder entity
         * @param originalLevel    the original level
         * @param newLevel         the new level
         * @param originalPosition the original position
         * @param newPosition      the new position
         */
        public teleportWithHolder(Entity leashedEntity, Entity holderEntity, Level originalLevel, Level newLevel, Vec3 originalPosition, Vec3 newPosition) {
            super(leashedEntity);
            this.holderEntity = holderEntity;
            this.originalLevel = originalLevel;
            this.newLevel = newLevel;
            this.originalPosition = originalPosition;
            this.newPosition = newPosition;
        }

        /**
         * Gets holder entity.
         *
         * @return the holder entity
         */
        public Entity getHolderEntity() {
            return holderEntity;
        }

        /**
         * Gets original position.
         *
         * @return the original position
         */
        public Vec3 getOriginalPosition() {
            return originalPosition;
        }

        /**
         * Gets original level.
         *
         * @return the original level
         */
        public Level getOriginalLevel() {
            return originalLevel;
        }

        /**
         * Gets new level.
         *
         * @return the new level
         */
        public Level getNewLevel() {
            return newLevel;
        }

        /**
         * Gets new position.
         *
         * @return the new position
         */
        public Vec3 getNewPosition() {
            return newPosition;
        }

    }

}
