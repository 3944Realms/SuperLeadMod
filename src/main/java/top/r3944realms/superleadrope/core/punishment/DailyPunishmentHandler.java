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

package top.r3944realms.superleadrope.core.punishment;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.r3944realms.superleadrope.content.SLPDamageTypes;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Daily punishment handler.
 */
public class DailyPunishmentHandler {

    private static long lastProcessedDay = -1;
    private static final int COUNTDOWN_SECONDS = 10;

    /**
     * 玩家倒计时任务存储
     */
    private static final Map<UUID, Integer> countdownMap = new ConcurrentHashMap<>();


    /**
     * On server tick.
     */
    public static void onServerTick() {

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (ServerLevel level : server.getAllLevels()) {
            long currentDay = level.getDayTime() / 24000L; // 每 24000 tick 为一天

            if (currentDay != lastProcessedDay) {
                lastProcessedDay = currentDay;

                for (ServerPlayer player : level.getPlayers(p -> !(p.isCreative() || p.isSpectator()))) {
                    player.getInventory().items.stream()
                            .filter(stack -> !stack.isEmpty())
                            .forEach(stack -> stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {

                                // 将上日未完成每日任务加入 pendingPunishments
                                int dailyObl = cap.getDailyObligations();
                                if (dailyObl > 0) {
                                    cap.beginInit();
                                    cap.setPendingPunishments(cap.getPendingPunishments() + dailyObl);
                                    cap.setDailyObligations(0);
                                    cap.endInit();
                                }

                                // 超过可宽恕次数，开始倒计时惩罚
                                if (cap.getPendingPunishments() > cap.getGracePunishments()) {
                                    UUID playerId = player.getUUID();
                                    if (!countdownMap.containsKey(playerId)) {
                                        countdownMap.put(playerId, COUNTDOWN_SECONDS * 20); // 10秒 = 200 tick
                                    }
                                }
                            }));
                }
            }

            // 每 tick 更新倒计时
            countdownMap.forEach((uuid, ticksLeft) -> {
                ServerPlayer player = (ServerPlayer) level.getPlayerByUUID(uuid);
                if (player == null) return;

                player.getInventory().items.stream()
                        .filter(stack -> !stack.isEmpty())
                        .forEach(stack -> stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {
                            // 如果 pending 已经低于宽恕值或者是非生存/冒险模式下的玩家，中途取消倒计时
                            if (cap.getPendingPunishments() <= cap.getGracePunishments() || player.isCreative() || player.isSpectator()) {
                                countdownMap.remove(uuid);
                                return;
                            }

                            int newTicksLeft = ticksLeft - 1;
                            countdownMap.put(uuid, newTicksLeft);
                            int secondsLeft = (newTicksLeft + 19) / 20; // 转成秒

                            // 颜色渐变：红->黄->绿
                            int color;
                            if (secondsLeft > 6) color = 0xFF5555;      // 红
                            else if (secondsLeft > 3) color = 0xFFFF55; // 黄
                            else color = 0x55FF55;                      // 绿

                            player.displayClientMessage(
                                    Component.translatable(SLPLangKeyValue.EP_OBLIGATION_COUNTDOWN.getKey(), secondsLeft)
                                            .withStyle(style -> style.withColor(TextColor.fromRgb(color))),
                                    true
                            );

                            if (newTicksLeft <= 0) {
                                // 执行惩罚
                                PunishmentDefinition punishment = cap.getPunishment();
                                if (punishment != null) {
                                    punishment.execute(player,
                                            new DamageSource(Holder.direct(SLPDamageTypes.ETERNAL_POTATO_NOT_COMPLETE), player));
                                }
                                // 扣除 2 次 pendingPunishments
                                cap.beginInit();
                                cap.setPendingPunishments(Math.max(0, cap.getPendingPunishments() - 2));
                                cap.endInit();
                                countdownMap.remove(uuid);
                            }
                        }));
            });
        }
    }
}