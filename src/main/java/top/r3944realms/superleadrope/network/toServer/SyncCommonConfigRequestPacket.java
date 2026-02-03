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

package top.r3944realms.superleadrope.network.toServer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.config.LeashConfigManager;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.SyncCommonConfigPacket;

import java.util.function.Supplier;

/**
 * The type Sync common config request packet.
 */
public record SyncCommonConfigRequestPacket(int hash) {
    /**
     * Encode.
     *
     * @param msg the msg
     * @param buf the buf
     */
    public static void encode(SyncCommonConfigRequestPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.hash);
    }


    /**
     * Decode sync common config request packet.
     *
     * @param buf the buf
     * @return the sync common config request packet
     */
    public static SyncCommonConfigRequestPacket decode(FriendlyByteBuf buf) {
        return new SyncCommonConfigRequestPacket(buf.readInt());
    }

    /**
     * Handle.
     *
     * @param msg the msg
     * @param ctx the ctx
     */
    public static void handle(SyncCommonConfigRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (msg.hash != LeashConfigManager.cacheHash) {
                NetworkHandler.sendToPlayer(new SyncCommonConfigPacket(CommonEventHandler.leashConfigManager.serializeToNBT(), CommonEventHandler.leashConfigManager.calculateConfigHash()), ctx.get().getSender());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
