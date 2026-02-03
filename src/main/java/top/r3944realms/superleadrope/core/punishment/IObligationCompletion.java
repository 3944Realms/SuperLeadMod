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

package top.r3944realms.superleadrope.core.punishment;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.r3944realms.superleadrope.core.register.SLPObligationCompletionRegistry;

import java.util.Map;

/**
 * 判定单次任务是否完成
 */
public interface IObligationCompletion {
    /**
     * 判断某次操作是否算作完成义务
     *
     * @param player 执行玩家
     * @param stack  操作的物品
     * @return true = 完成一次义务
     */
    boolean isCompleted(ServerPlayer player, ItemStack stack);

    /**
     * 当义务完成时执行（比如减少计数、提示）
     *
     * @param player 执行玩家
     * @param stack  操作的物品
     */
    void onCompleted(ServerPlayer player, ItemStack stack);

    /**
     * 获取注册 ID
     *
     * @return the id
     */
    default String getId() {
        for (Map.Entry<String, IObligationCompletion> entry : SLPObligationCompletionRegistry.getAll().entrySet()) {
            if (entry.getValue() == this) return entry.getKey();
        }
        return "none";
    }

    /**
     * To network.
     *
     * @param buf the buf
     */
// --- 网络序列化 ---
    default void toNetwork(FriendlyByteBuf buf) {
        buf.writeUtf(this.getId());
    }

    /**
     * From network obligation completion.
     *
     * @param buf the buf
     * @return the obligation completion
     */
    static IObligationCompletion fromNetwork(FriendlyByteBuf buf) {
        String id = buf.readUtf();
        return SLPObligationCompletionRegistry.byId(id); // 如果没找到，返回 NONE
    }

    /**
     * 一个便捷的静态空实现（默认永不完成）
     */
    IObligationCompletion NONE = new IObligationCompletion() {
        @Override
        public boolean isCompleted(ServerPlayer player, ItemStack stack) {
            return false;
        }

        @Override
        public void onCompleted(ServerPlayer player, ItemStack stack) {
            // no-op
        }
    };
}
