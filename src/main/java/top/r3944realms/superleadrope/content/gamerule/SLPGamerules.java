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

package top.r3944realms.superleadrope.content.gamerule;

import top.r3944realms.superleadrope.core.register.SLPGameruleRegistry;

import java.util.HashMap;

public class SLPGamerules {
    public static final String GAMERULE_PREFIX = "SLP.";
    public static final SLPGameruleRegistry GAMERULE_REGISTRY = SLPGameruleRegistry.INSTANCE;
    public static final HashMap<String, Boolean> gamerulesBooleanValuesClient = new HashMap<>();
    public static final HashMap<String, Integer> gameruleIntegerValuesClient = new HashMap<>();
    public static final String RULE_KEY_PERFiX_ = "gamerule." + GAMERULE_PREFIX;
    public static String getDescriptionKey(Class<?> gameRuleClass) {
        return RULE_KEY_PERFiX_ + gameRuleClass.getSimpleName() + ".description";
    }
    public static String getDescriptionKey(String gameRuleName) {
        return RULE_KEY_PERFiX_ + gameRuleName + ".description";
    }
    public static String getGameruleName(Class<?> clazz) {
        return SLPGamerules.GAMERULE_PREFIX + clazz.getSimpleName();
    }
    public static String getGameruleName(String gamerulesName) {
        return SLPGamerules.GAMERULE_PREFIX + gamerulesName;
    }

    public static String getNameKey(Class<?> gameRuleClass) {
        return RULE_KEY_PERFiX_ + gameRuleClass.getSimpleName();
    }

}
