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

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * The type Update player movement packet.
 */
public record UpdatePlayerMovementPacket(Operation operation, double x, double y, double z) {
    /**
     * Encode.
     *
     * @param packet the packet
     * @param buffer the buffer
     */
    public static void encode(UpdatePlayerMovementPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.operation());
        buffer.writeDouble(packet.x());
        buffer.writeDouble(packet.y());
        buffer.writeDouble(packet.z());
    }

    /**
     * Instantiates a new Update player movement packet.
     *
     * @param operation the operation
     * @param vec       the vec
     */
    public UpdatePlayerMovementPacket(Operation operation, Vec3 vec) {
        this(operation, vec.x, vec.y, vec.z);
    }

    /**
     * Decode update player movement packet.
     *
     * @param buffer the buffer
     * @return the update player movement packet
     */
    public static UpdatePlayerMovementPacket decode(FriendlyByteBuf buffer) {
        return new UpdatePlayerMovementPacket(
                buffer.readEnum(Operation.class),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }

    /**
     * Handle.
     *
     * @param packet the packet
     * @param ctx    the ctx
     */
    public static void handle(UpdatePlayerMovementPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            assert player != null;
            switch (packet.operation) {
                        case ADD ->  player.addDeltaMovement(new Vec3(packet.x, packet.y, packet.z));
                        case SET ->   player.setDeltaMovement(new Vec3(packet.x, packet.y, packet.z));
                        case MULTIPLY -> player.addDeltaMovement(player.getDeltaMovement().multiply(packet.x, packet.y, packet.z));
                    }
                }
        );
        context.setPacketHandled(true);
    }

    /**
     * The enum Operation.
     */
    public enum Operation {
        /**
         * Set operation.
         */
        SET,
        /**
         * Add operation.
         */
        ADD,
        /**
         * Multiply operation.
         */
        MULTIPLY
    }
}
