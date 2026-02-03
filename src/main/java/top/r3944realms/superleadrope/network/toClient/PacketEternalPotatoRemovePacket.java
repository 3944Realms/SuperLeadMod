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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * The type Packet eternal potato remove packet.
 */
public record PacketEternalPotatoRemovePacket(UUID itemUUID) {
    /**
     * Encode.
     *
     * @param msg the msg
     * @param buf the buf
     */
    public static void encode(PacketEternalPotatoRemovePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.itemUUID());
    }


    /**
     * Decode packet eternal potato remove packet.
     *
     * @param buf the buf
     * @return the packet eternal potato remove packet
     */
    public static PacketEternalPotatoRemovePacket decode(FriendlyByteBuf buf) {
        return new PacketEternalPotatoRemovePacket(buf.readUUID());
    }

    /**
     * Handle.
     *
     * @param msg the msg
     * @param ctx the ctx
     */
    public static void handle(PacketEternalPotatoRemovePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 客户端收到移除请求
            EternalPotatoFacade.remove(msg.itemUUID());
        });
        ctx.get().setPacketHandled(true);
    }
}
