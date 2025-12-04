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

package top.r3944realms.superleadrope.content.capability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.content.capability.provider.EternalPotatoProvider;
import top.r3944realms.superleadrope.content.capability.provider.LeashDataProvider;
import top.r3944realms.superleadrope.content.capability.provider.LeashStateProvider;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;

/**
 * The type Capability handler.
 */
public class CapabilityHandler {
    /**
     * The constant ETERNAL_POTATO_CAP.
     */
    public static final Capability<IEternalPotato> ETERNAL_POTATO_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    /**
     * Register capability.
     *
     * @param event the event
     */
    public static void registerCapability(@NotNull RegisterCapabilitiesEvent event) {
        event.register(ILeashData.class);
        event.register(IEternalPotato.class);
        event.register(ILeashState.class);
    }

    /**
     * Attach capability.
     *
     * @param event the event
     */
    public static void attachCapability(@NotNull AttachCapabilitiesEvent<?> event) {
        Object object = event.getObject();
        if(object instanceof Entity entity &&
                (LeashDataImpl.isLeashable(entity))//只对活体 船 矿车添加CAP
        ) {
            event.addCapability(LeashDataProvider.LEASH_DATA_REL, new LeashDataProvider(entity));
            event.addCapability(LeashStateProvider.LEASH_STATE_REL, new LeashStateProvider(entity));
        } else if (object instanceof ItemStack stack && stack.getItem() instanceof EternalPotatoItem) {
            event.addCapability(EternalPotatoProvider.ETERNAL_POTATO_DATA_REL, new EternalPotatoProvider(stack));
        }
    }

}
