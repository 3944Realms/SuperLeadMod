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

package top.r3944realms.superleadrope.content.gamerule.server;

import net.minecraft.world.level.GameRules;
import top.r3944realms.superleadrope.content.gamerule.SLPGamerules;

import static top.r3944realms.superleadrope.content.gamerule.SLPGamerules.GAMERULE_REGISTRY;

public class TeleportWithLeashedPlayers {
    public static final boolean DEFAULT_VALUE = true;
    public static final String ID = SLPGamerules.getGameruleName(TeleportWithLeashedPlayers.class);
    public static final String DESCRIPTION_KEY = SLPGamerules.getDescriptionKey(TeleportWithLeashedPlayers.class);
    public static final String NAME_KEY = SLPGamerules.getNameKey(TeleportWithLeashedPlayers.class);
    public static final GameRules.Category CATEGORY = GameRules.Category.PLAYER;

    public static void register() {
        GAMERULE_REGISTRY.registerGamerule(ID, CATEGORY, DEFAULT_VALUE);
    }
}
