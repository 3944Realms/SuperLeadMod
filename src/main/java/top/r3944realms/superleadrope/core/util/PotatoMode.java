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

package top.r3944realms.superleadrope.core.util;

/**
 * The enum Potato mode.
 */
public enum PotatoMode {
    /**
     * 单人 or 局域网主机
     */
    INTEGRATED,
    /**
     * 专用服务器
     */
    DEDICATED,
    /**
     * 远程连接的客户端
     */
    REMOTE_CLIENT;

    /**
     * Is synced boolean.
     *
     * @return the boolean
     */
    public boolean isSynced() {
        // Synced 模式：DEDICATED 服务端 + REMOTE_CLIENT 客户端
        return this == DEDICATED || this == REMOTE_CLIENT;
    }
}