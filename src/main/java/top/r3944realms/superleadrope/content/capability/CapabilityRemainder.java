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

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import top.r3944realms.superleadrope.util.capability.LeashUtil;

public class CapabilityRemainder {
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player newEntity = event.getEntity();
        if(newEntity instanceof ServerPlayer newPlayer) {
            Player original = event.getOriginal();
            original.reviveCaps();
            LeashUtil.getLeashState(original)
                    .ifPresent(oldCap ->
                            LeashUtil.getLeashState(newPlayer)
                                    .ifPresent(newData ->
                                            newData.copy(oldCap, newEntity)
                                    )
                    );
            original.invalidateCaps();
        }
    }
}
