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

package top.r3944realms.superleadrope.datagen.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.command.Command;
import top.r3944realms.superleadrope.content.command.LeashDataCommand;
import top.r3944realms.superleadrope.content.command.MotionCommand;
import top.r3944realms.superleadrope.content.gamerule.server.CreateSuperLeashKnotEntityIfAbsent;
import top.r3944realms.superleadrope.content.gamerule.server.TeleportWithLeashedEntities;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.util.lang.LanguageEnum;
import top.r3944realms.superleadrope.util.lang.ModPartEnum;

import javax.annotation.Nullable;
import java.util.function.Supplier;


/**
 * The enum Slp lang key value.
 */
public enum SLPLangKeyValue {
    /**
     * The Item super lead rope.
     */
    ITEM_SUPER_LEAD_ROPE(
            SLPItems.SUPER_LEAD_ROPE, ModPartEnum.ITEM,
            "Super Lead Rope", "超级拴绳", "超級拴繩","神駒羈縻索"
    ),

    /**
     * The Item eternal potato.
     */
    ITEM_ETERNAL_POTATO(
            SLPItems.ETERNAL_POTATO, ModPartEnum.ITEM,
            "Eternal Potato", "永恒土豆", "永恆馬鈴薯", "不滅薯", true
    ),

    /**
     * The Ep tooltip title.
     */
    EP_TOOLTIP_TITLE(
            EternalPotatoItem.getDescKey("title"), ModPartEnum.DESCRIPTION,
            "§6Mythical Item §7- §6Eternal Potato",
            "§6神话物品 §7- §6永恒土豆",
            "§6神話物品 §7- §6永恒土豆",
            "§6永恒土豆 §7- §6传奇之物"
    ),

    /**
     * The Ep desc tooltip.
     */
    EP_DESC_TOOLTIP(
            EternalPotatoItem.getDescKey("desc"), ModPartEnum.DESCRIPTION,
            "§7Symbol of server-wide contract, cannot be discarded",
            "§7象征全服契约，不可丢弃",
            "§7象徵全服契約，不可丟棄",
            "§7象征全服契约，绝不可弃"
    ),

    /**
     * The Ep bind owner.
     */
    EP_BIND_OWNER(
            EternalPotatoItem.getDescKey("bind_owner"), ModPartEnum.DESCRIPTION,
            "§bBound Owner: §f%s",
            "§b绑定主人: §f%s",
            "§b綁定主人: §f%s",
            "§b绑定主人: §f%s"
    ),

    /**
     * Ep unbound slp lang key value.
     */
    EP_UNBOUND(EternalPotatoItem.getDescKey("unbound"), ModPartEnum.DESCRIPTION,
            "§cUnbound",
            "§c未绑定主人",
            "§c未綁定主人",
            "§c尚未绑定主人"
    ),

    /**
     * The Ep obligation tooltip.
     */
    EP_OBLIGATION_TOOLTIP(
            EternalPotatoItem.getDescKey("obligation"), ModPartEnum.DESCRIPTION,
            "§7Daily obligations remaining: §a%d §c(+%d§c overdue)",
            "§7今日剩余义务: §a%d §c(+%d §c逾期未完成)",
            "§7今日剩餘義務: §a%d §c(+%d §c逾期未完成)",
            "§7今日责务尚余: §a%d §c(+%d §c逾期未尽)"
    ),

    /**
     * The Ep punish tooltip.
     */
    EP_PUNISH_TOOLTIP(
            EternalPotatoItem.getDescKey("punish"), ModPartEnum.DESCRIPTION,
            "§cOverdue punishments: §4%d §7(will be applied), grace exceeded: §4%d",
            "§c逾期未完成责务: §4%d §7(将会受罚)，超出宽限数: §4%d",
            "§c逾期未完成责務: §4%d §7(將會受罰)，超出寬限數: §4%d",
            "§c逾期责务尚未完成: §4%d §7(將受懲罰)，超出寬限數: §4%d"
    ),

    /**
     * The Ep obligation info.
     */
    EP_OBLIGATION_INFO(
            EternalPotatoItem.getMsgKey("obligation_info"), ModPartEnum.MESSAGE,
            "§e[Eternal Potato] §fThis is the server-wide shared person, remaining obligations today: §a%d§f.",
            "§e[永恒土豆] §f这是全服共有之人，今日义务剩余：§a%d§f次。",
            "§e[永恒土豆] §f這是全服共有之人，今日義務剩餘：§a%d§f次。",
            "§e[永恒土豆] §f此为全服共享之人，今日责务尚余：§a%d§f次。"
    ),

    /**
     * The Ep potato heal.
     */
    EP_POTATO_HEAL(
            EternalPotatoItem.getMsgKey("potato_heal"), ModPartEnum.MESSAGE,
            "§aThe power of the Eternal Potato comforts you, it won't disappear.",
            "§a永恒土豆的力量抚慰了你，但它不会消失。",
            "§a永恆土豆的力量撫慰了你，但它不會消失。",
            "§a永恒土豆之力慰心，永不消逝。"
    ),

    /**
     * The Ep cannot drop.
     */
    EP_CANNOT_DROP(
            EternalPotatoItem.getMsgKey("cannot_drop"), ModPartEnum.MESSAGE,
            "§cThe Eternal Potato cannot be dropped! +%d punishments.",
            "§c永恒土豆是不可丢弃的，惩罚数加%d！",
            "§c永恆土豆不可丟棄，懲罰數加%d！",
            "§c永恒土豆不可丟棄，懲罰數增加%d！"
    ),

    /**
     * The Ep bind msg.
     */
    EP_BIND_MSG(
            EternalPotatoItem.getMsgKey("bind_msg"), ModPartEnum.MESSAGE,
            "§6Bound to you as the server-wide shared person.",
            "§6已与你绑定，成为全服共有之人。",
            "§6已與你綁定，成為全服共有之人。",
            "§6已与汝绑定，为全服共享之人。"
    ),


    /**
     * The Ep obligation done.
     */
    EP_OBLIGATION_DONE(
            EternalPotatoItem.getMsgKey("obligation_done"), ModPartEnum.MESSAGE,
            "§eObligation completed, remaining: §a%d§e",
            "§e义务完成一次，剩余 §a%d §e次。",
            "§e義務完成一次，剩餘 §a%d §e次。",
            "§e责务完成，尚余 §a%d §e次。"
    ),

    /**
     * The Ep obligation full.
     */
    EP_OBLIGATION_FULL(
            EternalPotatoItem.getMsgKey("obligation_full"), ModPartEnum.MESSAGE,
            "§aAll obligations completed today!",
            "§a今日义务已全部完成！",
            "§a今日義務已全部完成！",
            "§a今日责务尽矣！"
    ),

    /**
     * The Ep punish msg.
     */
    EP_PUNISH_MSG(
            EternalPotatoItem.getMsgKey("punish_msg"), ModPartEnum.MESSAGE,
            "§cYesterday obligations incomplete, punished!",
            "§c未完成昨日义务，受到惩罚！",
            "§c未完成昨日義務，受到懲罰！",
            "§c昨日之责未尽，受罚矣！"
    ),

    /**
     * The Ep obligation countdown.
     */
    EP_OBLIGATION_COUNTDOWN(EternalPotatoItem.getMsgKey("obligation_countdown"), ModPartEnum.MESSAGE,
            "Punish Countdown: §a%d §fseconds remaining",
            "惩罚倒计时: §a%d §f秒",
            "懲罰倒計時: §a%d §f秒",
            "受罚倒数：§a%d §f瞬"
    ),

    /**
     * The Ep pickup not owner.
     */
    EP_PICKUP_NOT_OWNER(
            EternalPotatoItem.getMsgKey("pickup_not_owner"), ModPartEnum.MESSAGE,
            "§cYou are not the rightful owner and cannot pick this up!",
            "§c非绑定主人无法拾取此物品！",
            "§c非綁定主人無法拾取此物品！",
            "§c非汝所主，勿取！"
    ),

    /**
     * The Ep punish not owner.
     */
    EP_PUNISH_NOT_OWNER(
            EternalPotatoItem.getMsgKey("punish_not_owner"), ModPartEnum.MESSAGE,
            "§cYou are not the rightful owner, punished by lightning!",
            "§c非绑定主人使用，受到闪电惩罚！",
            "§c非綁定主人使用，受到閃電懲罰！",
            "§c非汝所主，雷霆降身！"
    ),
    /**
     * The Ep punish not owner death msg.
     */
    EP_PUNISH_NOT_OWNER_DEATH_MSG(
            "death.attack.eternal_potato_not_owner", ModPartEnum.MESSAGE,
            "§c%1$s was not the rightful owner, struck by lightning!",
            "§c%1$s 因使用非自己绑定物品，受到闪电惩罚！",
            "§c%1$s 因使用非自己綁定物品，受到閃電懲罰！",
            "§c%1$s 非汝所主，雷霆降身！"
    ),
    /**
     * The Ep punish not compete death msg.
     */
    EP_PUNISH_NOT_COMPETE_DEATH_MSG(
            "death.attack.eternal_potato_not_complete", ModPartEnum.MESSAGE,
            "§c%1$s was not the rightful owner, struck by lightning!",
            "§c%1$s 因使用非自己绑定物品，受到闪电惩罚！",
            "§c%1$s 因使用非自己綁定物品，受到閃電懲罰！",
            "§c%1$s 非汝所主，雷霆降身！"
    ),
    /**
     * The Sound subtitle super lead break.
     */
    SOUND_SUBTITLE_SUPER_LEAD_BREAK(
            SLPSoundEvents.getSubTitleTranslateKey("lead_break"), ModPartEnum.SOUND,
            "Lead Break", "拴绳断裂", "拴繩斷裂", "索絕"
    ),

    /**
     * The Sound subtitle super lead tied.
     */
    SOUND_SUBTITLE_SUPER_LEAD_TIED(
            SLPSoundEvents.getSubTitleTranslateKey("lead_tied"), ModPartEnum.SOUND,
            "Lead Tied", "拴绳系上", "拴繩係上", "繫索"
    ),

    /**
     * The Sound subtitle super lead untied.
     */
    SOUND_SUBTITLE_SUPER_LEAD_UNTIED(
            SLPSoundEvents.getSubTitleTranslateKey("lead_untied"), ModPartEnum.SOUND,
            "Lead Untie", "拴绳解开", "拴繩解開", "解索"
    ),

    /**
     * The Entity super lead knot.
     */
    ENTITY_SUPER_LEAD_KNOT(
            SLPEntityTypes.getEntityNameKey("super_lead_knot"), ModPartEnum.ENTITY,
            "Super Lead Knot", "超级拴绳结", "超級拴繩結", "神駒羈縻索結"
    ),
    /**
     * The Teleport with leashed entities name.
     */
    TELEPORT_WITH_LEASHED_ENTITIES_NAME(
            TeleportWithLeashedEntities.NAME_KEY, ModPartEnum.GAME_RULE,
            "Teleport leashed player with holder",
            "被拴实体随持有者传送",
            "被拴实体随持有者傳送",
            "繫畜隨持者傳送"
    ),
    /**
     * The Create super leash knot entity if absent name.
     */
    CREATE_SUPER_LEASH_KNOT_ENTITY_IF_ABSENT_NAME(
            CreateSuperLeashKnotEntityIfAbsent.NAME_KEY, ModPartEnum.NAME,
            "Create Leash Fence Knot Entity if absent",
            "如果缺失则创建超级拴绳结",
            "如果缺失則創建超級拴繩結",
            "若阙则创超级繫绳结"
    ),
    /**
     * The Create super leash knot entity if absent description.
     */
    CREATE_SUPER_LEASH_KNOT_ENTITY_IF_ABSENT_DESCRIPTION(
            CreateSuperLeashKnotEntityIfAbsent.DESCRIPTION_KEY, ModPartEnum.DESCRIPTION,
            "Create LeashKnot Entity if it's absent on fence or other supported positions",
            "如果在栅栏等支持处缺失超级拴绳结，则创建它",
            "如果在柵欄等支持處缺失超級拴繩結，則創建它",
            "若栅等支处阙超级繫绳结，则创之"
    ),
    /**
     * The Teleport with leashed description.
     */
    TELEPORT_WITH_LEASHED_DESCRIPTION(
            TeleportWithLeashedEntities.DESCRIPTION_KEY, ModPartEnum.DESCRIPTION,
            "Holder will teleport with their leashed players ",
            "传送时将被拴实体与持有者一起传送",
            "將被拴实体將隨持有者一起傳送",
            "傳送時繫畜隨持者同傳"
    ),
    /**
     * The Message motion adder successful.
     */
    MESSAGE_MOTION_ADDER_SUCCESSFUL(
            MotionCommand.MOTION_ADDER_SUCCESSFUL, ModPartEnum.COMMAND,
            "§bAdd Successfully.§a%s§7:§f[§eVec§7:§a(§f%.2f§7,§f%.2f§7,§f%.2f§7)§f]§r",
            "§b添加成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b添加成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b增益既成.§a%s§7:§f[§e速勢§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r"
    ),
    /**
     * The Message motion setter successful.
     */
    MESSAGE_MOTION_SETTER_SUCCESSFUL(
            MotionCommand.MOTION_SETTER_SUCCESSFUL, ModPartEnum.COMMAND,
            "§bSet Successfully.§a%s§7:§f[§eVec§7:§a(§f%.2f§7,§f%.2f§7,§f%.2f§7)§f]§r",
            "§b设置成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b設置成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b定值既成.§a%s§7:§f[§e速勢§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r"
    ),
    /**
     * The Message motion multiply successful.
     */
    MESSAGE_MOTION_MULTIPLY_SUCCESSFUL(
            MotionCommand.MOTION_MULTIPLY_SUCCESSFUL, ModPartEnum.COMMAND,
            "§bMultiply Successfully.§a%s§7:§f[§eVec§7:§a(§f%.2f§7,§f%.2f§7,§f%.2f§7)§f]§r",
            "§b倍乘成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b倍乘成功.§a%s§7:§f[§e加速§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r",
            "§b倍乘既成.§a%s§7:§f[§e速勢§7:(§a%.2f§7,§a%.2f§7,§a%.2f§7)§f]§r"
    ),
    MESSAGE_ABBREVIATION(
            Command.ABBREVIATION, ModPartEnum.COMMAND,
            "...",
            "...",
            "...",
            "..."
    ),
    MESSAGE_END(
            Command.END, ModPartEnum.COMMAND,
            ".",
            "。",
            "。",
            "。"
    ),
    MESSAGE_COLON(
            Command.COLON, ModPartEnum.COMMAND,
            ":",
            ":",
            ":",
            ":"
    ),
    MESSAGE_BLOCK_POS(
            Command.BLOCK_POS, ModPartEnum.COMMAND,
            "§7[§fX: %d, Y: %d, Z: %d§7]",
            "§7[§fX: %d, Y: %d, Z: %d§7]",
            "§7[§fX: %d, Y: %d, Z: %d§7]",
            "§7[§fX: %d, Y: %d, Z: %d§7]"
    ),
    MESSAGE_NONE(
            Command.NONE, ModPartEnum.COMMAND,
            "<None>",
            "无",
            "無",
            "無"
    ),
    MESSAGE_LEASHDATA_ALL_KNOTS(
            LeashDataCommand.ALL_KNOTS, ModPartEnum.COMMAND,
            "All Knots",
            "所有绳结",
            "所有繩結",
            "諸結"
    ),
    MESSAGE_LEASHDATA_ALL_HOLDERS(
            LeashDataCommand.ALL_HOLDERS, ModPartEnum.COMMAND,
            "All Holders",
            "所有持有者",
            "所有持有者",
            "諸持者"
    ),
    MESSAGE_LEASHDATA_SET_STATIC_MAX_DISTANCE(
            LeashDataCommand.SET_STATIC_MAX_DISTANCE_SUC, ModPartEnum.COMMAND,
            "Successfully set the static max distance of leash to %.2f from %s",
            "已成功设置%.2f为%s的全局最大距离",
            "已成功設定%.2f為%s的全域最大距離",
            "繩距定為%.2f，已立%s之全域極距"
    ),
    MESSAGE_LEASHDATA_RESET_STATIC_MAX_DISTANCE(
            LeashDataCommand.RESET_STATIC_MAX_DISTANCE_SUC, ModPartEnum.COMMAND,
            "Successfully reset the static max distance of leash from %s",
            "已成功重置%s的全局最大距离",
            "已成功重置%s的全域最大距離",
            "%s之全域極距，今已復初"
    ),
    MESSAGE_LEASHDATA_SET_STATIC_ELASTIC_DISTANCE_SCALE(
            LeashDataCommand.SET_STATIC_ELASTIC_DISTANCE_SCALE_SUC, ModPartEnum.COMMAND,
            "Successfully set the static elastic distance scale of leash to %.2f from %s",
            "已成功设置%.2f为%s的全局弹性距离比例",
            "已成功設定%.2f為%s的全域彈性距離比例",
            "繩距彈性比例定為%.2f，已立%s之全域伸縮度"
    ),
    MESSAGE_LEASHDATA_RESET_STATIC_ELASTIC_DISTANCE_SCALE(
            LeashDataCommand.RESET_STATIC_ELASTIC_DISTANCE_SCALE_SUC, ModPartEnum.COMMAND,
            "Successfully reset the static elastic distance scale of leash from %s",
            "成功重置%s的全局弹性距离比例",
            "成功重置%s的全域彈性距離比例",
            "%s之全域伸縮比例，今已復初"
    ),
    MESSAGE_LEASHDATA_SET_MAX_DISTANCE_SUC(
            LeashDataCommand.SET_MAX_DISTANCE_SUC, ModPartEnum.COMMAND,
            "Successfully adjusted the max distance of leash from %s to %s",
            "成功调整%s到%s的拴绳最大距离",
            "成功調整%s到%s的拴繩最大距離",
            "%s至%s之拴繩極距，今已定"
    ),
    MESSAGE_LEASHDATA_SET_MAX_DISTANCE_SUC_FAIL(
            LeashDataCommand.SET_MAX_DISTANCE_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to adjust it from %s to %s",
            "，但未能调整%s到%s的拴绳最大距离",
            "，但未能調整%s到%s的拴繩最大距離",
            "，然%s至%s之拴繩極距未定"
    ),
    MESSAGE_LEASHDATA_SET_MAX_DISTANCE_FAIL(
            LeashDataCommand.SET_MAX_DISTANCE_FAIL, ModPartEnum.COMMAND,
            "Failed to adjust the max distance of leash from %s to %s",
            "未能调整%s到%s的拴绳最大距离",
            "未能調整%s到%s的拴繩最大距離",
            "%s至%s之拴繩極距未成"
    ),
    MESSAGE_LEASHDATA_REMOVE_ALL_BLOCK_LEASHES(
            LeashDataCommand.REMOVE_ALL_BLOCK_LEASHES, ModPartEnum.COMMAND,
            "Successfully removed all holders' leash to %s",
            "已成功移除所有持有者对%s的牵引",
            "已成功移除所有持有者對%s的牽引",
            "%s之所有羈絆，今已盡釋"
    ),
    MESSAGE_LEASHDATA_REMOVE_REMOVE_ALL_HOLDER_LEASHES(
            LeashDataCommand.REMOVE_ALL_HOLDER_LEASHES, ModPartEnum.COMMAND,
            "Successfully removed all holders' leash to %s",
            "已成功移除所有持有者对%s的牵引",
            "已成功移除所有持有者對%s的牽引",
            "%s之所有繫繩，今已盡除"
    ),
    MESSAGE_LEASHDATA_TRANSFER_FROM_BLOCK_SUC(
            LeashDataCommand.TRANSFER_FROM_BLOCK_SUC, ModPartEnum.COMMAND,
            "Successfully transferred leash from %s to %s",
            "已成功将牵引从%s转移至%s",
            "已成功將牽引從%s轉移至%s",
            "繫繩自%s移至%s，其事已成"
    ),
    MESSAGE_LEASHDATA_TRANSFER_FROM_BLOCK_SUC_FAIL(
            LeashDataCommand.TRANSFER_FROM_BLOCK_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to transfer leash from %s to %s.",
            "，但未能将牵引从%s转移至%s",
            "，但未能將牽引從%s轉移至%s",
            "，然自%s至%s之遷移未竟"
    ),
    MESSAGE_LEASHDATA_TRANSFER_FROM_BLOCK_FAIL(
            LeashDataCommand.TRANSFER_FROM_BLOCK_FAIL, ModPartEnum.COMMAND,
            "Failed to transfer leash from %s to %s.",
            "未能将牵引从%s转移至%s",
            "未能將牽引從%s轉移至%s",
            "繫繩自%s遷於%s之舉未遂"
    ),
    MESSAGE_LEASHDATA_SET_ELASTIC_DISTANCE_SCALE_SUC(
            LeashDataCommand.SET_ELASTIC_DISTANCE_SCALE_SUC, ModPartEnum.COMMAND,
            "Successfully adjusted the elastic distance scale of leash from %s to %s",
            "成功调整%s到%s的拴绳弹性距离比例",
            "成功調整%s到%s的拴繩彈性距離比例",
            "%s至%s之拴繩伸縮比例，今已定"
    ),
    MESSAGE_LEASHDATA_SET_ELASTIC_DISTANCE_SCALE_SUC_FAIL(
            LeashDataCommand.SET_ELASTIC_DISTANCE_SCALE_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to adjust it from %s to %s",
            "，但未能调整%s到%s的拴绳弹性距离比例",
            "，但未能調整%s到%s的拴繩彈性距離比例",
            "，然%s至%s之拴繩伸縮比例未定"
    ),
    MESSAGE_LEASHDATA_SET_ELASTIC_DISTANCE_SCALE_FAIL(
            LeashDataCommand.SET_ELASTIC_DISTANCE_SCALE_FAIL, ModPartEnum.COMMAND,
            "Failed to adjust the elastic distance scale of leash from %s to %s",
            "未能调整%s到%s的拴绳弹性距离比例",
            "未能調整%s到%s的拴繩彈性距離比例",
            "%s至%s之拴繩伸縮比例未成"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_MAX_DISTANCE_SUC(
            LeashDataCommand.SET_BLOCK_MAX_DISTANCE_SUC, ModPartEnum.COMMAND,
            "Successfully adjusted the max distance of leash from %s to %s",
            "成功调整%s到%s的拴绳最大距离",
            "成功調整%s到%s的拴繩最大距離",
            "%s至%s之拴繩極距，今已定"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_MAX_DISTANCE_SUC_FAIL(
            LeashDataCommand.SET_BLOCK_MAX_DISTANCE_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to adjust it from %s to %s",
            "，但未能调整%s到%s的拴绳最大距离",
            "，但未能調整%s到%s的拴繩最大距離",
            "，然%s至%s之拴繩極距未定"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_MAX_DISTANCE_FAIL(
            LeashDataCommand.SET_BLOCK_MAX_DISTANCE_FAIL, ModPartEnum.COMMAND,
            "Failed to adjust the max distance of leash from %s to %s",
            "未能调整%s到%s的拴绳最大距离",
            "未能調整%s到%s的拴繩最大距離",
            "%s至%s之拴繩極距未成"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC(
            LeashDataCommand.SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC, ModPartEnum.COMMAND,
            "Successfully adjusted the elastic distance scale of leash from %s to %s",
            "成功调整%s到%s的拴绳弹性距离比例",
            "成功調整%s到%s的拴繩彈性距離比例",
            "%s至%s之拴繩伸縮比例，今已定"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC_FAIL(
            LeashDataCommand.SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to adjust it from %s to %s",
            "，但未能调整%s到%s的拴绳弹性距离比例",
            "，但未能調整%s到%s的拴繩彈性距離比例",
            "，然%s至%s之拴繩伸縮比例未定"
    ),
    MESSAGE_LEASHDATA_SET_BLOCK_ELASTIC_DISTANCE_SCALE_FAIL(
            LeashDataCommand.SET_BLOCK_ELASTIC_DISTANCE_SCALE_FAIL, ModPartEnum.COMMAND,
            "Failed to adjust the elastic distance scale of leash from %s to %s",
            "未能调整%s到%s的拴绳弹性距离比例",
            "未能%s調整為拴繩彈性距離比例從%s",
            "繩距伸縮比例自%s易為%s之舉未遂"
    ),
    MESSAGE_LEASHDATA_LEASH_DATA_HEAD(
            LeashDataCommand.LEASH_DATA_HEAD, ModPartEnum.COMMAND,
            "LeashData:",
            "拴绳数据:",
            "拴繩數據:",
            "繫繩錄:"
    ),
    MESSAGE_LEASHDATA_LEASH_DATA_ITEM(
            LeashDataCommand.LEASH_DATA_ITEM, ModPartEnum.COMMAND,
            "%s { Holder:%s, BlockPos:%s }",
            "%s { 持有者: %s, 坐标: %s }",
            "%s { 持有者: %s, 座標: %s }",
            "%s { 持者: %s, 位: %s }"
    ),
    /**
     * Message leashdata get block slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_BLOCK(
            LeashDataCommand.BLOCK, ModPartEnum.COMMAND,
            "Block",
            "方块",
            "方塊",
            "磚石"
    ),
    /**
     * Message leashdata get uuid slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_UUID(
            LeashDataCommand.UUID, ModPartEnum.COMMAND,
            "UUID",
            "UUID",
            "UUID",
            "UUID"
    ),
    /**
     * Message leashdata get max slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_MAX(
            LeashDataCommand.MAX, ModPartEnum.COMMAND,
            "Max Distance",
            "最大距离",
            "最大距離",
            "極距"
    ),
    /**
     * Message leashdata get elastic slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_ELASTIC(
            LeashDataCommand.ELASTIC, ModPartEnum.COMMAND,
            "Elastic Scale",
            "弹性尺度",
            "彈性尺度",
            "彈距度"
    ),
    /**
     * Message leashdata get keep slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_KEEP(
            LeashDataCommand.KEEP, ModPartEnum.COMMAND,
            "Keep Ticks",
            "保持刻",
            "保持刻",
            "持時"
    ),
    /**
     * Message leashdata get reserved slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_RESERVED(
            LeashDataCommand.RESERVED, ModPartEnum.COMMAND,
            "Reserved",
            "保留字段",
            "保留字段",
            "備註"
    ),
    MESSAGE_LEASHDATA_ENTITY(
            LeashDataCommand.ENTITY, ModPartEnum.COMMAND,
            "§7[ §l§fEntity §r§7]",
            "§7[ §l§f实体 §r§7]",
            "§7[ §l§f實體 §r§7]",
            "§7[ §l§f實者 §r§7]"
    ),

    MESSAGE_LEASHDATA_KNOT(
            LeashDataCommand.KNOT, ModPartEnum.COMMAND,
            "Knot",
            "§7[ §l§f绳结 §r§7]",
            "§7[ §l§f繩結 §r§7]",
            "§7[ §l§f結 §r§7]"
    ),
    MESSAGE_LEASHDATA_LEASH_INFO_HEAD(
            LeashDataCommand.LEASH_INFO_HEAD, ModPartEnum.COMMAND,
            "LeashInfo:",
            "拴绳信息:",
            "拴繩資訊:",
            "繫繩訊:"
    ),
    MESSAGE_LEASHDATA_LEASH_INFO_ITEM(
            LeashDataCommand.LEASH_INFO_ITEM, ModPartEnum.COMMAND,
            "%s { Info: %s }",
            "%s { 信息: %s }",
            "%s { 資訊: %s }",
            "%s { 訊: %s }"
    ),
    MESSAGE_LEASHDATA_ADD_HOLDER_LEASHES_SUC(
            LeashDataCommand.ADD_HOLDER_LEASHES_SUC, ModPartEnum.COMMAND,
            "Successfully attached %s leash from %s to %s",
            "成功将%s拴绳从%s连接到%s",
            "成功將%s拴繩從%s連接到%s",
            "%s繩自%s繫於%s，其事已成"
    ),
    MESSAGE_LEASHDATA_ADD_HOLDER_LEASHES_SUC_FAIL(
            LeashDataCommand.ADD_HOLDER_LEASHES_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to attached %s leash from %s to %s.",
            "，但未能将%s拴绳从%s连接到%s",
            "，但未能將%s拴繩從%s連接到%s",
            "，然%s繩自%s繫於%s未竟"
    ),
    MESSAGE_LEASHDATA_ADD_HOLDER_LEASHES_FAIL(
            LeashDataCommand.ADD_HOLDER_LEASHES_FAIL, ModPartEnum.COMMAND,
            "Failed to attached %s leash from %s to %s.",
            "未能将%s拴绳从%s连接到%s",
            "未能將%s拴繩從%s連接到%s",
            "%s繩自%s繫於%s之舉未遂"
    ),
    MESSAGE_LEASHDATA_ADD_BLOCK_LEASHES_SUC(
            LeashDataCommand.ADD_BLOCK_LEASHES_SUC, ModPartEnum.COMMAND,
            "Successfully attached %s leash from %s to %s",
            "成功将%s拴绳从%s连接到%s",
            "成功將%s拴繩從%s連接到%s",
            "%s繩自%s繫於%s，其事已成"
    ),
    MESSAGE_LEASHDATA_ADD_BLOCK_LEASHES_SUC_FAIL(
            LeashDataCommand.ADD_BLOCK_LEASHES_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to attached %s leash from %s to %s.",
            "，但未能将%s拴绳从%s连接到%s",
            "，但未能將%s拴繩從%s連接到%s",
            "，然%s繩自%s繫於%s未竟"
    ),
    MESSAGE_LEASHDATA_ADD_BLOCK_LEASHES_FAIL(
            LeashDataCommand.ADD_BLOCK_LEASHES_FAIL, ModPartEnum.COMMAND,
            "Failed to attached %s leash from %s to %s.",
            "未能将%s拴绳从%s连接到%s",
            "未能將%s拴繩從%s連接到%s",
            "%s繩自%s繫於%s之舉未遂"
    ),
    MESSAGE_LEASHDATA_ADD_BLOCK_LEASHES_FAIL_NO_KNOT_FOUND(
            LeashDataCommand.ADD_BLOCK_LEASHES_FAIL_NO_KNOT_FOUND, ModPartEnum.COMMAND,
            "Unable to tie the towing rope to %s as there is no knot in the position.",
            "无法将拴绳系到%s，因为该位置没有绳结",
            "無法將拴繩繫到%s，因為該位置沒有繩結",
            "%s處無結，拴繩難繫"
    ),
    MESSAGE_LEASHDATA_DEFAULT(
            LeashDataCommand.DEFAULT, ModPartEnum.COMMAND,
            "Default",
            "默认值",
            "默認值",
            "原值"
    ),
    MESSAGE_LEASHDATA_REMOVE_HOLDER_LEASHES_SUC(
            LeashDataCommand.REMOVE_HOLDER_LEASHES_SUC, ModPartEnum.COMMAND,
            "Successfully detached leash from %s to %s",
            "成功解除%s到%s的拴绳连接",
            "成功解除%s到%s的拴繩連接",
            "%s至%s之拴繩，今已解"
    ),
    MESSAGE_LEASHDATA_REMOVE_HOLDER_LEASHES_SUC_FAIL(
            LeashDataCommand.REMOVE_HOLDER_LEASHES_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to detach leash from %s to %s",
            "未能解除%s到%s的拴绳连接",
            "未能解除%s到%s的拴繩連接",
            "%s至%s之拴繩未除"
    ),
    MESSAGE_LEASHDATA_REMOVE_HOLDER_LEASHES_FAIL(
            LeashDataCommand.REMOVE_HOLDER_LEASHES_FAIL, ModPartEnum.COMMAND,
            "Failed to detach leash from %s to %s.",
            "成功解除%s到%s的拴绳连接",
            "成功解除%s到%s的拴繩連接",
            "%s至%s之拴繩，今已解"
    ),
    MESSAGE_LEASHDATA_REMOVE_BLOCK_LEASHES_SUC(
            LeashDataCommand.REMOVE_BLOCK_LEASHES_SUC, ModPartEnum.COMMAND,
            "Successfully detached leash from %s to %s",
            "，但未能解除%s到%s的拴绳连接",
            "，但未能解除%s到%s的拴繩連接",
            "，然%s至%s之拴繩未解"
    ),
    MESSAGE_LEASHDATA_REMOVE_BLOCK_LEASHES_SUC_FAIL(
            LeashDataCommand.REMOVE_BLOCK_LEASHES_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to detach leash from %s to %s",
            "未能解除%s到%s的拴绳连接",
            "未能解除%s到%s的拴繩連接",
            "%s至%s之拴繩未除"
    ),
    MESSAGE_LEASHDATA_REMOVE_BLOCK_LEASHES_FAIL(
            LeashDataCommand.REMOVE_BLOCK_LEASHES_FAIL, ModPartEnum.COMMAND,
            "Failed to detach leash from %s to %s.",
            "成功解除%s的所有拴绳连接",
            "成功解除%s的所有拴繩連接",
            "%s之諸拴繩，今盡解"
    ),
    MESSAGE_LEASHDATA_REMOVE_ALL_LEASHES(
            LeashDataCommand.REMOVE_ALL_LEASHES, ModPartEnum.COMMAND,
            "Successfully detached all leash from %s",
            "成功解除%s的所有拴绳连接",
            "成功解除%s的所有拴繩連接",
            "%s之諸拴繩，今盡解"
    ),
    MESSAGE_LEASHDATA_TRANSFER_LEASH_SUC(
            LeashDataCommand.TRANSFER_LEASH_SUC, ModPartEnum.COMMAND,
            "Successfully transferred leash from %s to %s",
            "成功将拴绳从%s转移至%s",
            "成功將拴繩從%s轉移至%s",
            "拴繩自%s移至%s，其事已成"
    ),
    MESSAGE_LEASHDATA_TRANSFER_LEASH_SUC_FAIL(
            LeashDataCommand.TRANSFER_LEASH_SUC_FAIL, ModPartEnum.COMMAND,
            ", but failed to transfer leash from %s to %s.",
            "，但未能将拴绳从%s转移至%s",
            "，但未能將拴繩從%s轉移至%s",
            "，然自%s至%s之遷移未竟"
    ),
    MESSAGE_LEASHDATA_TRANSFER_LEASH_FAIL(
            LeashDataCommand.TRANSFER_LEASH_FAIL, ModPartEnum.COMMAND,
            "Failed to transfer leash from %s to %s.",
            "未能将拴绳从%s转移至%s",
            "未能將拴繩從%s轉移至%s",
            "拴繩自%s遷於%s之舉未遂"
    ),
    MESSAGE_LEASHDATA_APPLY_FORCE(
            LeashDataCommand.APPLY_FORCE, ModPartEnum.COMMAND,
            "Successfully applied force on %s",
            "成功触发%s的力",
            "成功觸發%s的力",
            "%s之力，今已發"
    ),

    ;
    private final Supplier<?> supplier;
    private String key;
    private final String US_EN;
    private final String SIM_CN;
    private final String TRA_CN;
    private final String LZH;
    private final Boolean Default;
    private final ModPartEnum MPE;

    SLPLangKeyValue(Supplier<?> Supplier, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, String LZH, Boolean isDefault) {
        this.supplier = Supplier;
        this.MPE = MPE;
        this.US_EN = US_EN;
        this.SIM_CN = SIM_CN;
        this.TRA_CN = TRA_CN;
        this.LZH = LZH;
        this.Default = isDefault;
    }
    SLPLangKeyValue(@NotNull String ResourceKey, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, String LZH, Boolean isDefault) {
        this.supplier = null;
        this.key = ResourceKey;
        this.MPE = MPE;
        this.US_EN = US_EN;
        this.SIM_CN = SIM_CN;
        this.TRA_CN = TRA_CN;
        this.LZH = LZH;
        this.Default = isDefault;
    }
    SLPLangKeyValue(Supplier<?> Supplier, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, String LZH) {
        this(Supplier, MPE, US_EN, SIM_CN, TRA_CN, LZH, false);
    }
    SLPLangKeyValue(Supplier<?> Supplier, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, Boolean isDefault) {
        this(Supplier, MPE, US_EN, SIM_CN, TRA_CN, null, isDefault);
    }
    SLPLangKeyValue(@NotNull String ResourceKey, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, Boolean isDefault) {
        this(ResourceKey, MPE, US_EN, SIM_CN, TRA_CN, null, isDefault);
    }
    SLPLangKeyValue(@NotNull String ResourceKey, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN, String LZH) {
        this(ResourceKey, MPE, US_EN, SIM_CN, TRA_CN, LZH, false);
    }
    SLPLangKeyValue(Supplier<?> Supplier, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN) {
        this(Supplier, MPE, US_EN, SIM_CN, TRA_CN, null, false);
    }
    SLPLangKeyValue(@NotNull String ResourceKey, ModPartEnum MPE, String US_EN, String SIM_CN, String TRA_CN) {
        this(ResourceKey, MPE, US_EN, SIM_CN, TRA_CN, null, false);
    }

    /**
     * Gets lan.
     *
     * @param lan the lan
     * @param key the key
     * @return the lan
     */
    public static String getLan(LanguageEnum lan, SLPLangKeyValue key) {
        if (lan == null || lan == LanguageEnum.English) return getEnglish(key);
        else {
            switch (lan) {
                case SimpleChinese -> {
                    return getSimpleChinese(key);
                }
                case TraditionalChinese -> {
                    return getTraditionalChinese(key);
                }
                case LiteraryChinese -> {
                    return getLiteraryChinese(key);
                }
                default -> {
                    return getEnglish(key);
                }
            }
        }
    }
    private static String getEnglish(SLPLangKeyValue key) {
        return key.US_EN;
    }
    private static String getSimpleChinese(SLPLangKeyValue key) {
        return key.SIM_CN;
    }
    private static String getTraditionalChinese(SLPLangKeyValue key) {
        return key.TRA_CN;
    }

    /**
     * Gets literary chinese.
     *
     * @param key the key
     * @return the literary chinese
     */
    @Nullable
    public static String getLiteraryChinese(SLPLangKeyValue key) {
        return key.LZH;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        if(key == null){
            switch (MPE) {//Don't need to use "break;"[Java feature];
                case CREATIVE_TAB, MESSAGE, INFO, DEFAULT, COMMAND, CONFIG -> throw new UnsupportedOperationException("The Key value is NULL! Please use the correct constructor and write the parameters correctly");
                case ITEM -> key = (getItem()).getDescriptionId();
                case BLOCK -> key =(getBlock()).getDescriptionId();

            }
            //需要完善
        }
        return key;
    }

    /**
     * Gets item.
     *
     * @return the item
     */
    @SuppressWarnings("null")
    public Item getItem() {
        assert supplier != null;
        return (Item)supplier.get();
    }

    /**
     * Gets block.
     *
     * @return the block
     */
    @SuppressWarnings("null")
    public Block getBlock() {
        assert supplier != null;
        return (Block)supplier.get();
    }

    /**
     * Is default item boolean.
     *
     * @return the boolean
     */
    public boolean isDefaultItem(){
        return MPE == ModPartEnum.ITEM && Default;
    }

    /**
     * Is default block boolean.
     *
     * @return the boolean
     */
    public boolean isDefaultBlock() {
        return MPE == ModPartEnum.BLOCK && Default;
    }
}
