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

package top.r3944realms.superleadrope.network.toClient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import top.r3944realms.superleadrope.api.SLPCapability;

import java.util.function.Supplier;

public record LeashStateSyncPacket(int entityId, CompoundTag leashState) {
    public static void encode(LeashStateSyncPacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.entityId);
        buffer.writeNbt(msg.leashState);
    }

    public static LeashStateSyncPacket decode(FriendlyByteBuf buffer) {
        return new LeashStateSyncPacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(LeashStateSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(msg.entityId);
                if (entity != null) {
                    entity.getCapability(SLPCapability.LEASH_STATE_CAP).ifPresent(cap -> {
                        // 只在数据确实变化时更新
                        CompoundTag current = cap.serializeNBT();
                        if (!current.equals(msg.leashState)) {
                            cap.deserializeNBT(msg.leashState);//更新
                        }
                    });
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
