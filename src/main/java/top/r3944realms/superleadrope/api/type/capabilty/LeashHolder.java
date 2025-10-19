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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The type Leash holder.
 */
public record LeashHolder(@Nullable UUID holderUUID, @Nullable BlockPos knotPos, boolean isKnot) {
    /**
     * Instantiates a new Leash holder.
     *
     * @param holderUUID the holder uuid
     */
    public LeashHolder(UUID holderUUID) {
        this (holderUUID, null, false);
    }

    /**
     * Instantiates a new Leash holder.
     *
     * @param knotPos the knot pos
     */
    public LeashHolder(BlockPos knotPos) {
        this(null, knotPos, true);
    }
}
