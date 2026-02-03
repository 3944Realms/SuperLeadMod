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
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.punishment.IObligationCompletion;
import top.r3944realms.superleadrope.core.punishment.PunishmentDefinition;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * The type Eternal potato sync cap packet.
 */
public record EternalPotatoSyncCapPacket(
        UUID itemUUID,
        UUID ownerUUID,
        String ownerName,
        int dailyObligations,
        int pendingPunishments,
        int gracePunishments,
        String lastReset,
        String lastPunishDate,
        PunishmentDefinition punishment,
        IObligationCompletion completionRule
) {

    /**
     * Encode.
     *
     * @param msg the msg
     * @param buf the buf
     */
// 编码
    public static void encode(EternalPotatoSyncCapPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.itemUUID);

        buf.writeBoolean(msg.ownerUUID != null);
        if (msg.ownerUUID != null) buf.writeUUID(msg.ownerUUID);

        buf.writeUtf(msg.ownerName != null ? msg.ownerName : "");
        buf.writeInt(msg.dailyObligations);
        buf.writeInt(msg.pendingPunishments);
        buf.writeInt(msg.gracePunishments);
        buf.writeUtf(msg.lastReset != null ? msg.lastReset : "");
        buf.writeUtf(msg.lastPunishDate != null ? msg.lastPunishDate : "");

        buf.writeBoolean(msg.punishment != null);
        if (msg.punishment != null) {
            msg.punishment.toNetwork(buf);
        }

        buf.writeBoolean(msg.completionRule != null);
        if (msg.completionRule != null) {
            msg.completionRule.toNetwork(buf);
        }
    }

    /**
     * Decode eternal potato sync cap packet.
     *
     * @param buf the buf
     * @return the eternal potato sync cap packet
     */
// 解码
    public static EternalPotatoSyncCapPacket decode(FriendlyByteBuf buf) {
        UUID itemUUID = buf.readUUID();
        UUID ownerUUID = buf.readBoolean() ? buf.readUUID() : null;
        String name = buf.readUtf();
        int daily = buf.readInt();
        int pending = buf.readInt();
        int grace = buf.readInt();
        String lastReset = buf.readUtf();
        String lastPunishDate = buf.readUtf();

        PunishmentDefinition punishment = null;
        if (buf.readBoolean()) {
            punishment = PunishmentDefinition.fromNetwork(buf);
        }

        IObligationCompletion completionRule = null;
        if (buf.readBoolean()) {
            completionRule = IObligationCompletion.fromNetwork(buf);
        }

        return new EternalPotatoSyncCapPacket(itemUUID, ownerUUID, name, daily, pending, grace,
                lastReset, lastPunishDate, punishment, completionRule);
    }

    /**
     * Handle.
     *
     * @param msg the msg
     * @param ctx the ctx
     */
// 处理
    public static void handle(EternalPotatoSyncCapPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 获取全局能力实例
            IEternalPotato cap = EternalPotatoFacade.getOrCreate(msg.itemUUID);
            // 更新数据
            cap.beginInit();
            cap.setOwner(msg.ownerUUID, msg.ownerName);
            cap.setDailyObligations(msg.dailyObligations);
            cap.setPendingPunishments(msg.pendingPunishments);
            cap.setGracePunishments(msg.gracePunishments);
            cap.setLastReset(msg.lastReset);
            cap.setLastPunishDate(msg.lastPunishDate);
            if (msg.punishment != null) cap.setPunishment(msg.punishment);
            if (msg.completionRule != null) cap.setCompletionRule(msg.completionRule);
            cap.endInit();
        });
        ctx.get().setPacketHandled(true);
    }
}