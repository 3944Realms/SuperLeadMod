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
 * 专用服务器 + 远程客户端使用
 * <p>
 * 服务端：维护权威数据，变更时发包
 * <p>
 * 客户端：本地缓存，收到服务端同步包时更新
 */
class SyncedEternalPotatoManager implements IEternalPotatoManager {
    private final Map<UUID, EternalPotatoImpl> GLOBAL_DATA = new ConcurrentHashMap<>();
    private final boolean isServer;

    /**
     * Instantiates a new Synced eternal potato manager.
     *
     * @param isServer the is server
     */
    public SyncedEternalPotatoManager(boolean isServer) {
        this.isServer = isServer;
    }
    public IEternalPotato getOrCreate(UUID uuid) {
        return GLOBAL_DATA.computeIfAbsent(uuid, k -> {
            EternalPotatoImpl impl = new EternalPotatoImpl();
            impl.setItemUUID(uuid);
            return impl;
        });
    }

    // 可选：移除数据，防止内存泄漏
    public void remove(UUID uuid) {
        GLOBAL_DATA.remove(uuid);
    }

    @Override
    public void clear() {
        GLOBAL_DATA.clear();
    }

    @Override
    public CompoundTag saveAll() {
        if (!isServer) {
            return new CompoundTag(); // 客户端不存盘
        }
        CompoundTag root = new CompoundTag();
        GLOBAL_DATA.forEach((uuid, impl) -> {
            root.put(uuid.toString(), impl.serializeNBT());
        });
        return root;
    }

    @Override
    public void loadAll(CompoundTag tag) {
        GLOBAL_DATA.clear();
        for (String key : tag.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                EternalPotatoImpl impl = new EternalPotatoImpl();
                impl.deserializeNBT(tag.getCompound(key));
                impl.setItemUUID(uuid);
                GLOBAL_DATA.put(uuid, impl);
            } catch (IllegalArgumentException e) {
                SuperLeadRope.logger.error("Could not load UUID: {}", key, e);
            }
        }
    }

    /**
     * Is server boolean.
     *
     * @return the boolean
     */
    public boolean isServer() {
        return isServer;
    }
}
