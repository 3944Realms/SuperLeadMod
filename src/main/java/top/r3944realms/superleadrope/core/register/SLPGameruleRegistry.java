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

package top.r3944realms.superleadrope.core.register;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import top.r3944realms.superleadrope.content.gamerule.SLPGamerules;
import top.r3944realms.superleadrope.content.gamerule.server.TeleportWithLeashedEntities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum SLPGameruleRegistry {
    INSTANCE;
    public static final Map<String, GameRules.Key<?>> gamerules = new HashMap<>();;
    public static final Map<String, RuleDataType> gameruleDataTypes = new HashMap<>();
    public enum RuleDataType {
        BOOLEAN,
        INTEGER,
    }
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

    public void registerGamerule(String gameruleName, GameRules.Category category, boolean pDefault) {
        registerGamerule(gameruleName, category, pDefault, (s,i)->{});//最后一个仅占位无用
    }
    public void registerGamerule(String gameruleName, GameRules.Category category, boolean pDefault, BiConsumer<MinecraftServer, GameRules.BooleanValue> pChangeListener) {
        gamerules.put(gameruleName, GameRules.register(gameruleName, category, GameRules.BooleanValue.create(pDefault, pChangeListener)));
        gameruleDataTypes.put(gameruleName, RuleDataType.BOOLEAN);
    }
    public void registerGamerule(String gameruleName, GameRules.Category category, int pDefault) {
        registerGamerule(gameruleName, category, pDefault, (BiConsumer<MinecraftServer, GameRules.IntegerValue>) (s, i)->{});//最后一个仅占位无用
    }
    public void registerGamerule(String gameruleName, GameRules.Category category, int pDefault, BiConsumer<MinecraftServer, GameRules.IntegerValue> pChangeListener) {
        gamerules.put(gameruleName, GameRules.register(gameruleName, category, GameRules.IntegerValue.create(pDefault, pChangeListener)));
        gameruleDataTypes.put(gameruleName, RuleDataType.INTEGER);
    }
    public static void register() {
        TeleportWithLeashedEntities.register();
    }

}