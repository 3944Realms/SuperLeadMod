package top.r3944realms.superleadrope.network.toClient;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toServer.SyncCommonConfigRequestPacket;

import java.util.function.Supplier;

/**
 * The type Common config hash inform packet.
 */
public record CommonConfigHashInformPacket(int hash) {
    /**
     * Encode.
     *
     * @param packet the packet
     * @param buffer the buffer
     */
    public static void encode(CommonConfigHashInformPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.hash());
    }


    /**
     * Decode common config hash inform packet.
     *
     * @param buffer the buffer
     * @return the common config hash inform packet
     */
    public static CommonConfigHashInformPacket decode(FriendlyByteBuf buffer) {
        return new CommonConfigHashInformPacket(buffer.readInt());
    }

    /**
     * Handle.
     *
     * @param packet the packet
     * @param ctx    the ctx
     */
    public static void handle(CommonConfigHashInformPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            int hash = CommonEventHandler.leashConfigManager.calculateConfigHash();
            if (hash != packet.hash()) {
                      NetworkHandler.INSTANCE.sendToServer(new SyncCommonConfigRequestPacket(hash));
                  }
            }
        );
        context.setPacketHandled(true);
    }

}
