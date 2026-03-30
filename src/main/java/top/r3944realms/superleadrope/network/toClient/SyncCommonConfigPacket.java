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

package top.r3944realms.superleadrope.network.toClient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * The type Sync common config packet.
 */
public record SyncCommonConfigPacket(CompoundTag config, int hash) {
    /**
     * Encode.
     *
     * @param msg the msg
     * @param buf the buf
     */
    public static void encode(SyncCommonConfigPacket msg, FriendlyByteBuf buf) {
       buf.writeNbt(msg.config);
       buf.writeInt(msg.hash);
    }

    /**
     * Decode packet eternal potato remove packet.
     *
     * @param buf the buf
     * @return the packet eternal potato remove packet
     */
    public static SyncCommonConfigPacket decode(FriendlyByteBuf buf) {
        return new SyncCommonConfigPacket(buf.readNbt(), buf.readInt());
    }

    /**
     * Handle.
     *
     * @param msg the msg
     * @param ctx the ctx
     */
    public static void handle(SyncCommonConfigPacket msg, @NotNull Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 1. 保存当前配置（强制重新序列化，不使用缓存）
            CompoundTag currentConfig = CommonEventHandler.leashConfigManager.serializeToNBT();
            int currentHash = CommonEventHandler.leashConfigManager.calculateConfigHash();

            // 2. 应用新配置
            CommonEventHandler.leashConfigManager.deserializeFromNBT(msg.config);

            // 3. 验证哈希
            int newHash = CommonEventHandler.leashConfigManager.calculateConfigHash();
            if (newHash != msg.hash) {
                SuperLeadRope.logger.error("Hash mismatch! Expected: {}, Actual: {}", msg.hash, newHash);
                SuperLeadRope.logger.error("Current hash before deserialization: {}", currentHash);

                // 可选：打印差异详情
                if (currentConfig != null && msg.config != null) {
                    compareConfigs(currentConfig, msg.config);
                }

                // 4. 恢复旧配置
                CommonEventHandler.leashConfigManager.deserializeFromNBT(currentConfig);
            } else {
                SuperLeadRope.logger.debug("Config sync successful, hash: {}", msg.hash);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 辅助方法：比较配置差异
    private static void compareConfigs(CompoundTag oldConfig, CompoundTag newConfig) {
        Set<String> oldKeys = oldConfig.getAllKeys();
        Set<String> newKeys = newConfig.getAllKeys();

        // 找出只存在于旧配置的键
        for (String key : oldKeys) {
            if (!newConfig.contains(key)) {
                SuperLeadRope.logger.warn("Key only in old config: {}", key);
            }
        }

        // 找出只存在于新配置的键
        for (String key : newKeys) {
            if (!oldConfig.contains(key)) {
                SuperLeadRope.logger.warn("Key only in new config: {}", key);
            }
        }

        // 比较共同键的值
        for (String key : oldKeys) {
            if (newConfig.contains(key) && !Objects.equals(oldConfig.get(key), newConfig.get(key))) {
                SuperLeadRope.logger.warn("Value mismatch for key: {}", key);
                SuperLeadRope.logger.warn("  Old: {}", oldConfig.get(key));
                SuperLeadRope.logger.warn("  New: {}", newConfig.get(key));
            }
        }
    }
}
