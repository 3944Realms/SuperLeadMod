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

package top.r3944realms.superleadrope.content.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.config.LeashCommonConfig;

import java.util.List;

public class Command {
    public static final String PREFIX = LeashCommonConfig.COMMON.SLPModCommandPrefix.get();
    public static boolean SHOULD_USE_PREFIX = LeashCommonConfig.COMMON.EnableSLPModCommandPrefix.get();
    static LiteralArgumentBuilder<CommandSourceStack> getLiterArgumentBuilderOfCSS(String name, boolean shouldAddToList, @Nullable List<LiteralArgumentBuilder<CommandSourceStack>> list) {
        LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal(name);
        if (shouldAddToList) {
            assert list != null;
            list.add(literal);
        }
        return literal;
    }
}