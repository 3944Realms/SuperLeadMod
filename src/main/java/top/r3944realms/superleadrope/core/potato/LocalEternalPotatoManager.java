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
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.capability.impi.EternalPotatoImpl;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单人世界 & 局域网主机使用
 * <p>
 * 因为客户端和服务端在同一 JVM，可以共用同一个 Map<UUID, Data>。
 * <p>
 * 特点：不需要发网络包，直接访问。
 */
class LocalEternalPotatoManager implements IEternalPotatoManager {
    private final Map<UUID, EternalPotatoImpl> LOCAL_DATA = new ConcurrentHashMap<>();

    public IEternalPotato getOrCreate(UUID uuid) {
        return LOCAL_DATA.computeIfAbsent(uuid, k -> {
            EternalPotatoImpl impl = new EternalPotatoImpl();
            impl.setItemUUID(uuid);
            return impl;
        });
    }

    @Override
    public void remove(UUID uuid) {
        LOCAL_DATA.remove(uuid);
    }

    public void clear() {
        LOCAL_DATA.clear();
    }

    @Override
    public CompoundTag saveAll() {
        CompoundTag root = new CompoundTag();
        LOCAL_DATA.forEach((uuid, impl) -> root.put(uuid.toString(), impl.serializeNBT()));
        return root;
    }

    @Override
    public void loadAll(CompoundTag tag) {
        LOCAL_DATA.clear();
        for (String key : tag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                EternalPotatoImpl impl = new EternalPotatoImpl();
                impl.deserializeNBT(tag.getCompound(key));
                impl.setItemUUID(uuid);
                LOCAL_DATA.put(uuid, impl);
            } catch (IllegalArgumentException e) {
                SuperLeadRope.logger.error("Could not load UUID: {}", key, e);
            }
        }
    }
}
