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

package top.r3944realms.superleadrope.util.capability;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashData;
import top.r3944realms.superleadrope.content.capability.inter.ILeashState;

import java.util.Objects;
import java.util.Optional;

public class LeashUtil {
    public static Optional<ILeashData> getLeashData(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).resolve();
    }
    public static Optional<ILeashState> getLeashState(@NotNull Entity entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");
        return entity.getCapability(CapabilityHandler.LEASH_STATE_CAP).resolve();
    }
}
