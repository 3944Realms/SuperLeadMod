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

package top.r3944realms.superleadrope.core.leash;

import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

// 全局LeashData同步管理器
public class LeashSyncManager {
    static final Set<ILeashData> LEASH_DATA = Collections.newSetFromMap(new WeakHashMap<>());
    static final Set<ILeashState> LEASH_STATES = Collections.newSetFromMap(new WeakHashMap<>());
    public static class Data {
        public static void track(ILeashData instance) {
            LEASH_DATA.add(instance);
        }
        public static void untrack(ILeashData instance) {
            LEASH_DATA.remove(instance);
        }
        public static void forEach(Consumer<ILeashData> consumer) {
            LEASH_DATA.forEach(consumer);
        }
    }
    public static class State {
        public static void track(ILeashState instance) {
            LEASH_STATES.add(instance);
        }
        public static void untrack(ILeashState instance) {
            LEASH_STATES.remove(instance);
        }
        public static void forEach(Consumer<ILeashState> consumer) {
            LEASH_STATES.forEach(consumer);
        }
    }

}