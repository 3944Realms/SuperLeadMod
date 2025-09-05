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

package top.r3944realms.superleadrope.content.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;

public class SuperLeashEntity extends Entity {
    private Entity controlled;
    public SuperLeashEntity(EntityType<? extends SuperLeashEntity> entityType, Level level) {
        super(entityType, level);
    }
    public SuperLeashEntity(Level level, Entity controlled) {
        super(SLPEntityTypes.SUPER_LEASH.get(), level);
        if (!LeashDataImpl.isLeashable(controlled)) {
            throw new IllegalArgumentException("Controlled entity " + controlled.getClass().getName() + "is not a leashable entity");
        }
        this.controlled = controlled;
    }

    public Entity getControlled() {
        return controlled;
    }

    @Override
    public void tick() {
        super.tick();
        if (controlled == null || !controlled.isAlive()) {
            this.discard();
        }
        this.setPos(controlled.getX(), controlled.getY(), controlled.getZ());
        if(!level().isClientSide) controlled.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(ILeashDataCapability::applyLeashForces);
    }

    @Override
    public void kill() {

    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {

    }
}
