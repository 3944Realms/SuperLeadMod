/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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

package top.r3944realms.superleadrope.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;

/**
 * The type Slp capability.
 */
public class SLPCapability {
    /**
     * The constant LEASH_DATA_CAP.
     */
    public static final Capability<ILeashData> LEASH_DATA_CAP = CapabilityManager.get(new CapabilityToken<>(){});
    /**
     * The constant LEASH_STATE_CAP.
     */
    public static final Capability<ILeashState> LEASH_STATE_CAP = CapabilityManager.get(new CapabilityToken<>() {});
}
