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

package top.r3944realms.superleadrope.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.network.toClient.*;
import top.r3944realms.superleadrope.network.toServer.SyncCommonConfigRequestPacket;


/**
 * The type Network handler.
 */
public class NetworkHandler {
    private static int cid = 0;
    /**
     * The constant INSTANCE.
     */
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SuperLeadRope.MOD_ID, "main"),
            () -> SuperLeadRope.ModInfo.VERSION,
            SuperLeadRope.ModInfo.VERSION::equals,
            SuperLeadRope.ModInfo.VERSION::equals
    );

    /**
     * Register.
     */
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
        INSTANCE.messageBuilder(EternalPotatoSyncCapPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EternalPotatoSyncCapPacket::decode)
                .encoder(EternalPotatoSyncCapPacket::encode)
                .consumerNetworkThread(EternalPotatoSyncCapPacket::handle)
                .add();
        INSTANCE.messageBuilder(PacketEternalPotatoRemovePacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketEternalPotatoRemovePacket::decode)
                .encoder(PacketEternalPotatoRemovePacket::encode)
                .consumerNetworkThread(PacketEternalPotatoRemovePacket::handle)
                .add();
        INSTANCE.messageBuilder(LeashStateSyncPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LeashStateSyncPacket::decode)
                .encoder(LeashStateSyncPacket::encode)
                .consumerNetworkThread(LeashStateSyncPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncCommonConfigPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncCommonConfigPacket::decode)
                .encoder(SyncCommonConfigPacket::encode)
                .consumerNetworkThread(SyncCommonConfigPacket::handle)
                .add();
        INSTANCE.messageBuilder(SyncCommonConfigRequestPacket.class, cid++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SyncCommonConfigRequestPacket::decode)
                .encoder(SyncCommonConfigRequestPacket::encode)
                .consumerNetworkThread(SyncCommonConfigRequestPacket::handle)
                .add();
        INSTANCE.messageBuilder(CommonConfigHashInformPacket.class, cid++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(CommonConfigHashInformPacket::decode)
                .encoder(CommonConfigHashInformPacket::encode)
                .consumerNetworkThread(CommonConfigHashInformPacket::handle)
                .add();
    }

    /**
     * Send to player.
     *
     * @param <MSG>   the type parameter
     * @param message the message
     * @param player  the player
     */
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * Send to all player.
     *
     * @param <MSG>   the type parameter
     * @param message the message
     */
    public static <MSG> void sendToAllPlayer(MSG message){
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * Send to player.
     *
     * @param <MSG>             the type parameter
     * @param <T>               the type parameter
     * @param message           the message
     * @param entity            the entity
     * @param packetDistributor the packet distributor
     */
    public static <MSG, T> void sendToPlayer(MSG message, T entity, PacketDistributor<T> packetDistributor){
        INSTANCE.send(packetDistributor.with(() -> entity), message);
    }
}
