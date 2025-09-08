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

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.core.util.PotatoMode;
import top.r3944realms.superleadrope.core.util.PotatoModeHelper;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.PacketEternalPotatoRemovePacket;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 外观类：统一入口
 * 调用方只管 Facade，不关心底层是 Local 还是 Synced。
 */
public class EternalPotatoFacade {
    private static IEternalPotatoManager manager;
    private static PotatoSavedData savedData;
    // 全局监听器
    private static final List<IEternalPotatoChangeListener> listeners = new CopyOnWriteArrayList<>();
    public static void addListener(IEternalPotatoChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(IEternalPotatoChangeListener listener) {
        listeners.remove(listener);
    }

    // 内部方法，用于通知变化
    static void notifyChange(UUID uuid, IEternalPotato potato) {
        listeners.forEach(l -> l.onPotatoChanged(uuid, potato));
    }

    public static IEternalPotatoManager getManager() {
        return manager;
    }
    public static PotatoSavedData getSavedData() {
        return savedData;
    }
    public static void initSavedData(ServerLevel serverLevel) {
        savedData = PotatoSavedData.create(serverLevel);
    }
    /**
     * 初始化（进入世界时调用）
     * @param mode 当前运行模式
     * @param isServer 是否在服务端
     */
    public static void init(PotatoMode mode, boolean isServer) {
        if (manager != null) {
            return; // 已经有 manager，说明重复初始化，直接返回
        }
        switch (mode) {
            case INTEGRATED -> manager = new LocalEternalPotatoManager();
            case DEDICATED, REMOTE_CLIENT -> manager = new SyncedEternalPotatoManager(isServer);
        }
    }

    public static IEternalPotato getOrCreate(UUID uuid) {
        if (manager == null) throw new IllegalStateException("EternalPotatoFacade not initialized!");
        IEternalPotato potato = manager.getOrCreate(uuid);

        // 绑定统一回调
        potato.bindItemStackSync(() -> {
            if (savedData != null) savedData.setDirty(); // 标记 SavedData 脏
            notifyChange(uuid, potato);                  // 通知全局监听器
        });

        return potato;
    }

    public static void remove(UUID uuid) {
        if (manager != null) manager.remove(uuid);

        // Synced 模式才发包
        if (PotatoModeHelper.getCurrentMode().isSynced() && ServerLifecycleHooks.getCurrentServer() != null) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> {
                PacketEternalPotatoRemovePacket packet = new PacketEternalPotatoRemovePacket(uuid);
                NetworkHandler.sendToPlayer(packet, player);
            });
        }
    }

    public static void clear() {
        if (manager != null) manager.clear();
    }

    public static boolean isServer() {
        return (manager instanceof SyncedEternalPotatoManager synced) && synced.isServer();
    }
}