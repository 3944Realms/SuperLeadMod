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

package top.r3944realms.superleadrope.content.capability.inter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.punishment.IObligationCompletion;
import top.r3944realms.superleadrope.core.punishment.PunishmentDefinition;

import java.util.UUID;

public interface IEternalPotato {
    interface ItemStackSync {
        void markDirtyForItem();
    }
    void bindItemStackSync(ItemStackSync callback);
    void beginInit();
    void endInit();

    void setItemUUID(UUID uuid);
    UUID getItemUUID();
    void setOwner(UUID uuid, String name);
    UUID getOwnerUUID();
    String getOwnerName();

    void setDailyObligations(int count);
    int getDailyObligations();

    int getPendingPunishments();
    void setPendingPunishments(int count);
    /**
     * 计算今日总任务次数 = 未完成惩罚次数 + 当日剩余次数
     */
    default int getFinalTaskCount() {
        return getPendingPunishments() + getDailyObligations();
    }
    default boolean isNetworkSyncNonRequired() {
        return !EternalPotatoFacade.isServer();
    }
    default boolean isGlobalEffect() {
        // 根据当前惩罚、任务规则决定
        return getPunishment() != null && getPunishment().affectOthers();
    }
    /**
     * 是否在宽限期内
     */
    default boolean isWithinGracePeriod() {
        return getPendingPunishments() <= getGracePunishments();
    }
    /**
     * 宽限惩罚数
     */
    int getGracePunishments();
    void setGracePunishments(int count);
    void syncToClient(Player player);
    void bindSyncContext(Player player);
    Player getBoundPlayer();
    void setLastReset(String date);
    String getLastReset();

    PunishmentDefinition getPunishment();
    void setPunishment(PunishmentDefinition definition);

    void setLastPunishDate(String date);
    String getLastPunishDate();

    IObligationCompletion getCompletionRule();
    void setCompletionRule(IObligationCompletion completion);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag nbt);
}
