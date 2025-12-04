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
