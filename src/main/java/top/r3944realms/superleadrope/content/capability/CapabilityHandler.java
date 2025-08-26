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

package top.r3944realms.superleadrope.content.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;

public class CapabilityHandler {
    public static final Capability<ILeashDataCapability> LEASH_DATA_CAP = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapability(RegisterCapabilitiesEvent event) {
        event.register(ILeashDataCapability.class);
    }

    public static void attachCapability(AttachCapabilitiesEvent<?> event) {
        Object object = event.getObject();
        if(object instanceof Entity entity &&
                (LeashDataImpl.isLeashable(entity))//只对活体 船 矿车添加CAP
        ) {
            event.addCapability(LeashDataProvider.LEASH_DATA_REL, new LeashDataProvider(entity));
        }
    }
}
