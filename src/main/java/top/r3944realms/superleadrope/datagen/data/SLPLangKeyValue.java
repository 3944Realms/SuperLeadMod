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
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.register.SLPSoundEvents;
import top.r3944realms.superleadrope.utils.lang.LanguageEnum;
import top.r3944realms.superleadrope.utils.lang.ModPartEnum;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public enum SLPLangKeyValue {
    ITEM_SUPER_LEAD_ROPE(
            SLPItems.SUPER_LEAD_ROPE, ModPartEnum.ITEM,
            "Super Lead Rope", "超级拴绳", "超級拴繩","神駒羈縻索"
    ),

    SOUND_SUBTITLE_SUPER_LEAD_BREAK(
            SLPSoundEvents.getSubTitleTranslateKey("lead_break"), ModPartEnum.SOUND,
            "Lead Break", "拴绳断裂", "拴繩斷裂", "索絕"
    ),

    SOUND_SUBTITLE_SUPER_LEAD_TIED(
            SLPSoundEvents.getSubTitleTranslateKey("lead_tied"), ModPartEnum.SOUND,
            "Lead Tied", "拴绳系上", "拴繩係上", "繫索"
    ),

    SOUND_SUBTITLE_SUPER_LEAD_UNTIED(
            SLPSoundEvents.getSubTitleTranslateKey("lead_untied"), ModPartEnum.SOUND,
            "Lead Untie", "拴绳解开", "拴繩解開", "解索"
    ),

    ENTITY_SUPER_LEAD_KNOT(
            SLPEntityTypes.getEntityNameKey("super_lead_knot"), ModPartEnum.ENTITY,
            "Super Lead Knot", "超级拴绳结", "超級拴繩結", "神駒羈縻索結"
    ),

    ENTITY_SUPER_LEASH(
        SLPEntityTypes.getEntityNameKey("super_leash"), ModPartEnum.ENTITY,
        "Super Leash", "超级拴绳", "超級拴繩","神駒羈縻索"
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
    @Nullable
    public static String getLiteraryChinese(SLPLangKeyValue key) {
        return key.LZH;
    }
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
    @SuppressWarnings("null")
    public Item getItem() {
        assert supplier != null;
        return (Item)supplier.get();
    }
    @SuppressWarnings("null")
    public Block getBlock() {
        assert supplier != null;
        return (Block)supplier.get();
    }
    public boolean isDefaultItem(){
        return MPE == ModPartEnum.ITEM && Default;
    }
    public boolean isDefaultBlock() {
        return MPE == ModPartEnum.BLOCK && Default;
    }
}
