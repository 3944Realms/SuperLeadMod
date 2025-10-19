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
    /**
     * The Message leashdata get title.
     */
    MESSAGE_LEASHDATA_GET_TITLE(
            LeashDataCommand.TITLE, ModPartEnum.COMMAND,
            "=== Leash Data for %s ===",
            "=== %s 的拴绳数据 ===",
            "=== %s 的拴繩數據 ===",
            "=== %s 之繫繩數據 ==="
    ),
    /**
     * The Message leashdata get total.
     */
    MESSAGE_LEASHDATA_GET_TOTAL(
            LeashDataCommand.TOTAL, ModPartEnum.COMMAND,
            "Total leashes: %d",
            "总拴绳数: %d",
            "總拴繩數: %d",
            "繫繩總數: %d"
    ),
    /**
     * Message leashdata get block slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_BLOCK(
            LeashDataCommand.BLOCK, ModPartEnum.COMMAND,
            "§7Block: §e%s",
            "§7方块: §e%s",
            "§7方塊: §e%s",
            "§7磚石: §e%s"
    ),
    /**
     * Message leashdata get uuid slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_UUID(
            LeashDataCommand.UUID, ModPartEnum.COMMAND,
            "§7UUID: §b%s",
            "§7UUID: §b%s",
            "§7UUID: §b%s",
            "§7UUID: §b%s"
    ),
    /**
     * Message leashdata get max slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_MAX(
            LeashDataCommand.MAX, ModPartEnum.COMMAND,
            "§7Max: §a%.1f",
            "§7最大距离: §a%.1f",
            "§7最大距離: §a%.1f",
            "§7極距: §a%.1f"
    ),
    /**
     * Message leashdata get elastic slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_ELASTIC(
            LeashDataCommand.ELASTIC, ModPartEnum.COMMAND,
            "§7Elastic: §6%.1f",
            "§7弹性距离: §6%.1f",
            "§7彈性距離: §6%.1f",
            "§7彈距: §6%.1f"
    ),
    /**
     * Message leashdata get keep slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_KEEP(
            LeashDataCommand.KEEP, ModPartEnum.COMMAND,
            "§7Keep: §c%d§7/§c%d",
            "§7保持: §c%d§7/§c%d",
            "§7保持: §c%d§7/§c%d",
            "§7持時: §c%d§7/§c%d"
    ),
    /**
     * Message leashdata get reserved slp lang key value.
     */
    MESSAGE_LEASHDATA_GET_RESERVED(
            LeashDataCommand.RESERVED, ModPartEnum.COMMAND,
            "§7Reserved: §d%s",
            "§7保留字段: §d%s",
            "§7保留字段: §d%s",
            "§7備註: §d%s"
    ),
    /**
     * The Message leashdata add success.
     */
    MESSAGE_LEASHDATA_ADD_SUCCESS(
            "command.leashdata.addApplyEntity.success", ModPartEnum.COMMAND,
            "§bAdded leash successfully. §a%s §7→ §e%s",
            "§b添加拴绳成功. §a%s §7→ §e%s",
            "§b添加拴繩成功. §a%s §7→ §e%s",
            "§b繫繩既添. §a%s §7→ §e%s"
    ),
    /**
     * The Message leashdata remove success.
     */
    MESSAGE_LEASHDATA_REMOVE_SUCCESS(
            "command.leashdata.removeApplyEntity.success", ModPartEnum.COMMAND,
            "§bRemoved leash successfully. §a%s §7- §e%s",
            "§b移除拴绳成功. §a%s §7- §e%s",
            "§b移除拴繩成功. §a%s §7- §e%s",
            "§b繫繩既除. §a%s §7- §e%s"
    ),
    /**
     * The Message leashdata transfer success.
     */
    MESSAGE_LEASHDATA_TRANSFER_SUCCESS(
            "command.leashdata.transfer.success", ModPartEnum.COMMAND,
            "§bTransferred leash successfully. §a%s §7→ §e%s §7→ §6%s",
            "§b转移拴绳成功. §a%s §7→ §e%s §7→ §6%s",
            "§b轉移拴繩成功. §a%s §7→ §e%s §7→ §6%s",
            "§b繫繩既移. §a%s §7→ §e%s §7→ §6%s"
    ),
    /**
     * The Message leashdata set success.
     */
    MESSAGE_LEASHDATA_SET_SUCCESS(
            "command.leashdata.setApplyEntity.success", ModPartEnum.COMMAND,
            "§bSet leash property successfully. §a%s §7: §e%s §7= §6%.1f",
            "§b设置拴绳属性成功. §a%s §7: §e%s §7= §6%.1f",
            "§b設置拴繩屬性成功. §a%s §7: §e%s §7= §6%.1f",
            "§b繫繩性既定. §a%s §7: §e%s §7= §6%.1f"
    );

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
