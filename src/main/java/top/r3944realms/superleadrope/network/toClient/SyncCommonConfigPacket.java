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
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;

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
    public static void handle(SyncCommonConfigPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            CompoundTag old = CommonEventHandler.leashConfigManager.serializeToNBT();
            CommonEventHandler.leashConfigManager.deserializeFromNBT(msg.config);
            if (CommonEventHandler.leashConfigManager.calculateConfigHash() != msg.hash) { //BACK
                SuperLeadRope.logger.error("Hash mismatch! Except:{}, Actual:{}", msg.hash, CommonEventHandler.leashConfigManager.calculateConfigHash());
                CommonEventHandler.leashConfigManager.deserializeFromNBT(old);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
