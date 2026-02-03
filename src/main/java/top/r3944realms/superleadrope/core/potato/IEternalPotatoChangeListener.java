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

package top.r3944realms.superleadrope.core.potato;

import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;

import java.util.UUID;

/**
 * The interface Eternal potato change listener.
 */
public interface IEternalPotatoChangeListener {
    /**
     * On potato changed.
     *
     * @param uuid   the uuid
     * @param potato the potato
     */
    void onPotatoChanged(UUID uuid, IEternalPotato potato);
}
