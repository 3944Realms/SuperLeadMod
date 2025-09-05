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

package top.r3944realms.superleadrope.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.network.toClient.LeashDataSyncPacket;
import top.r3944realms.superleadrope.network.toClient.UpdatePlayerMovementPacket;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int cid = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SuperLeadRope.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static void register() {
        INSTANCE.messageBuilder(LeashDataSyncPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LeashDataSyncPacket::decode)
                .encoder(LeashDataSyncPacket::encode)
                .consumerNetworkThread(LeashDataSyncPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdatePlayerMovementPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(UpdatePlayerMovementPacket::decode)
                .encoder(UpdatePlayerMovementPacket::encode)
                .consumerNetworkThread(UpdatePlayerMovementPacket::handle)
                .add();
    }
    public static <MSG> void sendAllPlayer(MSG message){
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
