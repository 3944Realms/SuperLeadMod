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

package top.r3944realms.superleadrope.content.capability.impi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.core.punishment.IObligationCompletion;
import top.r3944realms.superleadrope.core.punishment.PunishmentDefinition;
import top.r3944realms.superleadrope.core.register.SLPObligationCompletionRegistry;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.EternalPotatoSyncCapPacket;

import java.util.UUID;

/**
 * The type Eternal potato.
 */
public class EternalPotatoImpl implements IEternalPotato {
    private ItemStackSync itemStackSync;
    private Player boundPlayer;
    private UUID ownerUUID;
    private volatile UUID itemUUID;
    private String ownerName;
    private boolean initializing = false;
    private boolean dirtyDuringInit = false;
    /**
     * 每日任务次数
     */
    private int dailyObligations = 0;
    private int gracePeriod = 0;
    /**
     * 累计未完成触发的惩罚次数
     */
    private int pendingPunishments = 0;
    private String lastReset = "";
    private String lastPunishDate = "";  // yyyy-MM-dd
    private PunishmentDefinition punishment = PunishmentDefinition.DEFAULT;

    private IObligationCompletion completionRule = IObligationCompletion.NONE;
    /**
     * The constant TAG_LAST_PUNISH_DATE.
     */
// NBT Keys
    public static final String TAG_LAST_PUNISH_DATE = "last_punish_date";
    /**
     * The constant TAG_PENDING_PUNISHMENTS.
     */
    public static final String TAG_PENDING_PUNISHMENTS = "pending_punishments";
    /**
     * The constant TAG_GRACE_PERIOD.
     */
    public static final String TAG_GRACE_PERIOD = "grace_period";
    /**
     * The constant TAG_OWNER_UUID.
     */
    public static final String TAG_OWNER_UUID = "owner_uuid";
    /**
     * The constant TAG_ITEM_UUID.
     */
    public static final String TAG_ITEM_UUID = "item_uuid";
    /**
     * The constant TAG_OWNER_NAME.
     */
    public static final String TAG_OWNER_NAME = "owner_name";
    /**
     * The constant TAG_OBLIGATIONS.
     */
    public static final String TAG_OBLIGATIONS = "obligations";
    /**
     * The constant TAG_LAST_RESET.
     */
    public static final String TAG_LAST_RESET = "last_reset";
    /**
     * The constant TAG_PUNISHMENT_TYPE.
     */
    public static final String TAG_PUNISHMENT_TYPE = "punishment_type";
    /**
     * The constant TAG_PUNISHMENT_STRENGTH.
     */
    public static final String TAG_PUNISHMENT_STRENGTH = "punishment_strength";
    /**
     * The constant TAG_PUNISHMENT_AFFECT_OTHERS.
     */
    public static final String TAG_PUNISHMENT_AFFECT_OTHERS = "punishment_affect_others";
    /**
     * The constant TAG_COMPLETION_ID.
     */
    public static final String TAG_COMPLETION_ID = "completion_id";

    @Override
    public void bindItemStackSync(ItemStackSync callback) {
        this.itemStackSync = callback;
    }

    /**
     * 开始初始化（禁止立即发包）
     */
    public void beginInit() {
        initializing = true;
        dirtyDuringInit = false;
    }

    /**
     * 结束初始化（如果期间有改动，就统一发一次包）
     */
    public void endInit() {
       initializing = false;
        if (dirtyDuringInit) {
            // 同步到玩家
            if (boundPlayer != null) {
                syncToClient(boundPlayer);
            }

            // 写回 ItemStack NBT
            if (itemStackSync != null) {
                itemStackSync.markDirtyForItem();
            }

            // 重置脏标记
            dirtyDuringInit = false;
        }
    }


    public void bindSyncContext(Player player) {
        if (isNetworkSyncNonRequired()) return;
        this.boundPlayer = player;
    }
    public Player getBoundPlayer() {
        return boundPlayer;
    }

    private void markDirty() {
        if (initializing) {
            dirtyDuringInit = true;
        } else {
            if (boundPlayer != null) {
                syncToClient(boundPlayer);
            }
            if (itemStackSync != null) {
                itemStackSync.markDirtyForItem();
            }
        }
    }

    @Override
    public void setItemUUID(UUID uuid) {
        if (uuid == null) throw new IllegalArgumentException("Item UUID cannot be null");
        this.itemUUID = uuid;
    }

    @Override
    public UUID getItemUUID() {
        return itemUUID;
    }

    @Override
    public void setOwner(UUID uuid, String name) {
        if (uuid == null) throw new IllegalArgumentException("Owner UUID cannot be null");
        if (name == null) name = "Unknown";
        this.ownerUUID = uuid;
        this.ownerName = name;
        markDirty();
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public String getOwnerName() {
        return ownerName != null ? ownerName : "Unknown";
    }

    @Override
    public void setDailyObligations(int count) {
        if (count < 0) count = 0;
        this.dailyObligations = count;
        markDirty();
    }

    @Override
    public int getDailyObligations() {
        return dailyObligations;
    }

    @Override
    public int getPendingPunishments() {
        return pendingPunishments;
    }

    @Override
    public void setPendingPunishments(int count) {
        if (count < 0) count = 0;
        this.pendingPunishments = count;
        markDirty();
    }

    @Override
    public int getGracePunishments() {
        return gracePeriod;
    }

    @Override
    public void setGracePunishments(int count) {
        if (count < 0) count = 0;
        this.gracePeriod = count;
        markDirty();
    }

    @Override
    public void syncToClient(Player player) {
        if (isNetworkSyncNonRequired() || player.level().isClientSide) return;

        EternalPotatoSyncCapPacket packet =
                new EternalPotatoSyncCapPacket(
                        itemUUID, ownerUUID, ownerName,
                        dailyObligations,pendingPunishments, gracePeriod,
                        lastReset, lastPunishDate, punishment, completionRule
                );
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                NetworkHandler.sendToPlayer(packet, p);
            }
        }
    }

    @Override
    public void setLastReset(String date) {
        this.lastReset = date;
        markDirty();
    }

    @Override
    public String getLastReset() { return lastReset; }

    @Override
    public PunishmentDefinition getPunishment() { return punishment; }

    @Override
    public void setPunishment(PunishmentDefinition definition) {
        this.punishment = definition != null ? definition : PunishmentDefinition.DEFAULT;
        markDirty();
    }

    @Override
    public void setLastPunishDate(String date) {
        this.lastPunishDate = date;
        markDirty();
    }

    @Override
    public String getLastPunishDate() {
        return lastPunishDate;
    }

    @Override
    public IObligationCompletion getCompletionRule() {
        return completionRule;
    }

    @Override
    public void setCompletionRule(IObligationCompletion completion) {
        this.completionRule = completion != null ? completion : IObligationCompletion.NONE;
        markDirty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (ownerUUID != null) tag.putUUID(TAG_OWNER_UUID, ownerUUID);
        if (itemUUID != null) tag.putUUID(TAG_ITEM_UUID, itemUUID);
        if (ownerName != null) tag.putString(TAG_OWNER_NAME, ownerName);
        tag.putInt(TAG_OBLIGATIONS, dailyObligations);
        tag.putInt(TAG_GRACE_PERIOD, gracePeriod);
        if (lastReset != null) tag.putString(TAG_LAST_RESET, lastReset);

        // 序列化 PunishmentDefinition record
        if (punishment != null && punishment != PunishmentDefinition.DEFAULT) {
            tag.putString(TAG_PUNISHMENT_TYPE, punishment.type().name());
            tag.putFloat(TAG_PUNISHMENT_STRENGTH, punishment.strength());
            tag.putBoolean(TAG_PUNISHMENT_AFFECT_OTHERS, punishment.affectOthers());
        }

        // 序列化任务完成规则
        if (completionRule != null && completionRule != IObligationCompletion.NONE) {
            tag.putString(TAG_COMPLETION_ID, completionRule.getId());
        }
        if (lastPunishDate != null) tag.putString(TAG_LAST_PUNISH_DATE, lastPunishDate);
        tag.putInt(TAG_PENDING_PUNISHMENTS, pendingPunishments);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt == null) return; // 卫语句防空
        if (nbt.hasUUID(TAG_OWNER_UUID)) ownerUUID = nbt.getUUID(TAG_OWNER_UUID);
        if (nbt.contains(TAG_ITEM_UUID)) itemUUID = nbt.getUUID(TAG_ITEM_UUID);
        if (nbt.contains(TAG_OWNER_NAME)) ownerName = nbt.getString(TAG_OWNER_NAME);
        if (nbt.contains(TAG_GRACE_PERIOD)) gracePeriod = Math.max(0, nbt.getInt(TAG_GRACE_PERIOD));
        if (nbt.contains(TAG_OBLIGATIONS)) dailyObligations = Math.max(0, nbt.getInt(TAG_OBLIGATIONS));
        if (nbt.contains(TAG_PENDING_PUNISHMENTS)) pendingPunishments = Math.max(0, nbt.getInt(TAG_PENDING_PUNISHMENTS));
        if (nbt.contains(TAG_LAST_RESET)) lastReset = nbt.getString(TAG_LAST_RESET);
        if (nbt.contains(TAG_LAST_PUNISH_DATE)) lastPunishDate = nbt.getString(TAG_LAST_PUNISH_DATE);

        // PunishmentDefinition 反序列化
        if (nbt.contains(TAG_PUNISHMENT_TYPE) && nbt.contains(TAG_PUNISHMENT_STRENGTH) && nbt.contains(TAG_PUNISHMENT_AFFECT_OTHERS)) {
            try {
                PunishmentDefinition.Type type = PunishmentDefinition.Type.valueOf(nbt.getString(TAG_PUNISHMENT_TYPE));
                float strength = nbt.getFloat(TAG_PUNISHMENT_STRENGTH);
                boolean affectOthers = nbt.getBoolean(TAG_PUNISHMENT_AFFECT_OTHERS);
                punishment = new PunishmentDefinition(type, strength, affectOthers);
            } catch (IllegalArgumentException e) {
                punishment = PunishmentDefinition.DEFAULT;
            }
        }

        // 完成规则反序列化
        if (nbt.contains(TAG_COMPLETION_ID)) {
            String id = nbt.getString(TAG_COMPLETION_ID);
            IObligationCompletion rule = SLPObligationCompletionRegistry.byId(id);
            completionRule = rule != null ? rule : IObligationCompletion.NONE;
        }
    }
}

