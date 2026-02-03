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

package top.r3944realms.superleadrope.compat;

import dev.dubhe.curtain.features.player.patches.EntityPlayerMPFake;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * The type Curtain compat.
 */
public class CurtainCompat{
    /**
     * The constant isModLoaded.
     */
    public final static boolean isModLoaded = ModList.get().isLoaded("curtain");

    /**
     * Is not fake player boolean.
     *
     * @param player the player
     * @return the boolean
     */
    public static boolean isNotFakePlayer(Player player) {
        if (isModLoaded) {
            return !(player instanceof EntityPlayerMPFake);
        }
        return true;
    }
}
