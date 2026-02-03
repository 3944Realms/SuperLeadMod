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

package top.r3944realms.superleadrope.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The type Nbt reader.
 */
public class NBTReader {
    private NBTReader() {}

    /**
     * Read vec 3 vec 3.
     *
     * @param nbt the nbt
     * @return the vec 3
     */
    @Contract("_ -> new")
    public static @NotNull Vec3 readVec3(@NotNull CompoundTag nbt) {
        if (nbt.contains("X") && nbt.contains("Y") && nbt.contains("Z")) {
            return new Vec3(
                    nbt.getDouble("X"),
                    nbt.getDouble("Y"),
                    nbt.getDouble("Z")
            );
        } else {
            throw new IllegalArgumentException("NBT is missing X, Y, or Z value for Vec3");
        }
    }
}
