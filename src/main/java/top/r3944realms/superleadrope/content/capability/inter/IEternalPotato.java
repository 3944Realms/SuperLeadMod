/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope.content.capability.inter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.punishment.IObligationCompletion;
import top.r3944realms.superleadrope.core.punishment.PunishmentDefinition;

import java.util.UUID;

/**
 * The interface Eternal potato.
 */
public interface IEternalPotato {
    /**
     * The interface Item stack sync.
     */
    interface ItemStackSync {
        /**
         * Mark dirty for item.
         */
        void markDirtyForItem();
    }

    /**
     * Bind item stack sync.
     *
     * @param callback the callback
     */
    void bindItemStackSync(ItemStackSync callback);

    /**
     * Begin init.
     */
    void beginInit();

    /**
     * End init.
     */
    void endInit();

    /**
     * Sets item uuid.
     *
     * @param uuid the uuid
     */
    void setItemUUID(UUID uuid);

    /**
     * Gets item uuid.
     *
     * @return the item uuid
     */
    UUID getItemUUID();

    /**
     * Sets owner.
     *
     * @param uuid the uuid
     * @param name the name
     */
    void setOwner(UUID uuid, String name);

    /**
     * Gets owner uuid.
     *
     * @return the owner uuid
     */
    UUID getOwnerUUID();

    /**
     * Gets owner name.
     *
     * @return the owner name
     */
    String getOwnerName();

    /**
     * Sets daily obligations.
     *
     * @param count the count
     */
    void setDailyObligations(int count);

    /**
     * Gets daily obligations.
     *
     * @return the daily obligations
     */
    int getDailyObligations();

    /**
     * Gets pending punishments.
     *
     * @return the pending punishments
     */
    int getPendingPunishments();

    /**
     * Sets pending punishments.
     *
     * @param count the count
     */
    void setPendingPunishments(int count);

    /**
     * 计算今日总任务次数 = 未完成惩罚次数 + 当日剩余次数
     *
     * @return the final task count
     */
    default int getFinalTaskCount() {
        return getPendingPunishments() + getDailyObligations();
    }

    /**
     * Is network sync non required boolean.
     *
     * @return the boolean
     */
    default boolean isNetworkSyncNonRequired() {
        return !EternalPotatoFacade.isServer();
    }

    /**
     * Is global effect boolean.
     *
     * @return the boolean
     */
    default boolean isGlobalEffect() {
        // 根据当前惩罚、任务规则决定
        return getPunishment() != null && getPunishment().affectOthers();
    }

    /**
     * 是否在宽限期内
     *
     * @return the boolean
     */
    default boolean isWithinGracePeriod() {
        return getPendingPunishments() <= getGracePunishments();
    }

    /**
     * 宽限惩罚数
     *
     * @return the grace punishments
     */
    int getGracePunishments();

    /**
     * Sets grace punishments.
     *
     * @param count the count
     */
    void setGracePunishments(int count);

    /**
     * Sync to client.
     *
     * @param player the player
     */
    void syncToClient(Player player);

    /**
     * Bind sync context.
     *
     * @param player the player
     */
    void bindSyncContext(Player player);

    /**
     * Gets bound player.
     *
     * @return the bound player
     */
    Player getBoundPlayer();

    /**
     * Sets last reset.
     *
     * @param date the date
     */
    void setLastReset(String date);

    /**
     * Gets last reset.
     *
     * @return the last reset
     */
    String getLastReset();

    /**
     * Gets punishment.
     *
     * @return the punishment
     */
    PunishmentDefinition getPunishment();

    /**
     * Sets punishment.
     *
     * @param definition the definition
     */
    void setPunishment(PunishmentDefinition definition);

    /**
     * Sets last punish date.
     *
     * @param date the date
     */
    void setLastPunishDate(String date);

    /**
     * Gets last punish date.
     *
     * @return the last punish date
     */
    String getLastPunishDate();

    /**
     * Gets completion rule.
     *
     * @return the completion rule
     */
    IObligationCompletion getCompletionRule();

    /**
     * Sets completion rule.
     *
     * @param completion the completion
     */
    void setCompletionRule(IObligationCompletion completion);

    /**
     * Serialize nbt compound tag.
     *
     * @return the compound tag
     */
    CompoundTag serializeNBT();

    /**
     * Deserialize nbt.
     *
     * @param nbt the nbt
     */
    void deserializeNBT(CompoundTag nbt);
}
