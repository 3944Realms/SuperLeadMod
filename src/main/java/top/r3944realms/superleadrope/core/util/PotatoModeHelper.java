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

package top.r3944realms.superleadrope.core.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * The type Potato mode helper.
 */
public class PotatoModeHelper {

    /**
     * Gets current mode.
     *
     * @return the current mode
     */
    public static PotatoMode getCurrentMode() {
        if (FMLEnvironment.dist.isClient()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.hasSingleplayerServer()) {
                return PotatoMode.INTEGRATED;
            }
            return PotatoMode.REMOTE_CLIENT;
        } else {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null && server.isDedicatedServer()) {
                return PotatoMode.DEDICATED;
            }
            // 如果不是专用服，那就是集成服服务端部分
            return PotatoMode.INTEGRATED;
        }
    }
}