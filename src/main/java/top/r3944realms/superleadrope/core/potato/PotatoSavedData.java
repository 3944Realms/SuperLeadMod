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

package top.r3944realms.superleadrope.core.potato;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class PotatoSavedData extends SavedData {
    public static final String DATA_NAME = "eternal_potato";


    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        // 把所有数据塞进 tag
        tag.put(DATA_NAME, EternalPotatoFacade.getManager().saveAll());
        return tag;
    }

    public static PotatoSavedData load(CompoundTag tag) {
        IEternalPotatoManager manager = EternalPotatoFacade.getManager();
        PotatoSavedData data = new PotatoSavedData();
        if (tag.contains(DATA_NAME)) {
            manager.loadAll(tag.getCompound(DATA_NAME));
        }
        return data;
    }

    // 工厂方法（Forge 推荐写法）
    public static PotatoSavedData create(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                PotatoSavedData::load,
                PotatoSavedData::new,
                DATA_NAME
        );
    }
}
