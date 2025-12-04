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

package top.r3944realms.superleadrope.content.capability.provider;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.api.SLPCapability;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;

/**
 * The type Leash data provider.
 */
public class LeashDataProvider implements ICapabilitySerializable<CompoundTag> {
    /**
     * The constant LEASH_DATA_REL.
     */
    public static final ResourceLocation LEASH_DATA_REL = new ResourceLocation(SuperLeadRope.MOD_ID, "leash_data");
    private final ILeashData instance;
    private final LazyOptional<ILeashData> optional;

    /**
     * Instantiates a new Leash data provider.
     *
     * @param entity the entity
     */
    public LeashDataProvider(Entity entity) {
        this.instance = new LeashDataImpl(entity);
        this.optional = LazyOptional.of(() -> instance);
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return SLPCapability.LEASH_DATA_CAP.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.deserializeNBT(nbt);
    }
}
