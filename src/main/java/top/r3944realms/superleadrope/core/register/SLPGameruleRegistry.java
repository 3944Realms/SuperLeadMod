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

package top.r3944realms.superleadrope.core.register;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import top.r3944realms.superleadrope.content.gamerule.SLPGamerules;
import top.r3944realms.superleadrope.content.gamerule.server.CreateSuperLeashKnotEntityIfAbsent;
import top.r3944realms.superleadrope.content.gamerule.server.TeleportWithLeashedEntities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The enum Slp gamerule registry.
 */
public enum SLPGameruleRegistry {
    /**
     * Instance slp gamerule registry.
     */
    INSTANCE;
    /**
     * The constant gamerules.
     */
    public static final Map<String, GameRules.Key<?>> gamerules = new HashMap<>();;
    /**
     * The constant gameruleDataTypes.
     */
    public static final Map<String, RuleDataType> gameruleDataTypes = new HashMap<>();

    /**
     * The enum Rule data type.
     */
    public enum RuleDataType {
        /**
         * Boolean rule data type.
         */
        BOOLEAN,
        /**
         * Integer rule data type.
         */
        INTEGER,
    }

    /**
     * Gets gamerule bool value.
     *
     * @param level        the level
     * @param gameruleName the gamerule name
     * @return the gamerule bool value
     */
    @SuppressWarnings("unchecked")

    public static boolean getGameruleBoolValue(Level level, String gameruleName) {
        if (level.isClientSide && SLPGamerules.gamerulesBooleanValuesClient.containsKey(gameruleName)) {
            return SLPGamerules.gamerulesBooleanValuesClient.get(gameruleName);
        }
        if (gameruleDataTypes.get(gameruleName) != RuleDataType.BOOLEAN) {
            return false;
        }
        return level.getGameRules().getBoolean((GameRules.Key<GameRules.BooleanValue>) gamerules.get(gameruleName));
    }

    /**
     * Gets gamerule int value.
     *
     * @param level        the level
     * @param gameruleName the gamerule name
     * @return the gamerule int value
     */
    @SuppressWarnings("unchecked")
    public static Integer getGameruleIntValue(Level level, String gameruleName) {
        if (level.isClientSide && SLPGamerules.gameruleIntegerValuesClient.containsKey(gameruleName)) {
            return SLPGamerules.gameruleIntegerValuesClient.get(gameruleName);
        }
        if (gameruleDataTypes.get(gameruleName) != RuleDataType.INTEGER) {
            return 0;
        }
        return level.getGameRules().getInt((GameRules.Key<GameRules.IntegerValue>)gamerules.get(gameruleName));
    }

    /**
     * Register gamerule.
     *
     * @param gameruleName the gamerule name
     * @param category     the category
     * @param pDefault     the p default
     */
    public void registerGamerule(String gameruleName, GameRules.Category category, boolean pDefault) {
        registerGamerule(gameruleName, category, pDefault, (s,i)->{});//最后一个仅占位无用
    }

    /**
     * Register gamerule.
     *
     * @param gameruleName    the gamerule name
     * @param category        the category
     * @param pDefault        the p default
     * @param pChangeListener the p change listener
     */
    public void registerGamerule(String gameruleName, GameRules.Category category, boolean pDefault, BiConsumer<MinecraftServer, GameRules.BooleanValue> pChangeListener) {
        gamerules.put(gameruleName, GameRules.register(gameruleName, category, GameRules.BooleanValue.create(pDefault, pChangeListener)));
        gameruleDataTypes.put(gameruleName, RuleDataType.BOOLEAN);
    }

    /**
     * Register gamerule.
     *
     * @param gameruleName the gamerule name
     * @param category     the category
     * @param pDefault     the p default
     */
    public void registerGamerule(String gameruleName, GameRules.Category category, int pDefault) {
        registerGamerule(gameruleName, category, pDefault, (s, i)->{});//最后一个仅占位无用
    }

    /**
     * Register gamerule.
     *
     * @param gameruleName    the gamerule name
     * @param category        the category
     * @param pDefault        the p default
     * @param pChangeListener the p change listener
     */
    public void registerGamerule(String gameruleName, GameRules.Category category, int pDefault, BiConsumer<MinecraftServer, GameRules.IntegerValue> pChangeListener) {
        gamerules.put(gameruleName, GameRules.register(gameruleName, category, GameRules.IntegerValue.create(pDefault, pChangeListener)));
        gameruleDataTypes.put(gameruleName, RuleDataType.INTEGER);
    }

    /**
     * Register.
     */
    public static void register() {
        TeleportWithLeashedEntities.register();
        CreateSuperLeashKnotEntityIfAbsent.register();
    }

}