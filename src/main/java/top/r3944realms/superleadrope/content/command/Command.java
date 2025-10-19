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
import top.r3944realms.superleadrope.CommonEventHandler;

import java.util.List;

/**
 * The type Command.
 */
public class Command {
    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = CommonEventHandler.leashConfigManager.getCommandPrefix();
    /**
     * The constant SHOULD_USE_PREFIX.
     */
    public static boolean SHOULD_USE_PREFIX = CommonEventHandler.leashConfigManager.isCommandPrefixEnabled();

    /**
     * Gets liter argument builder of css.
     *
     * @param name            the name
     * @param shouldAddToList the should add to list
     * @param list            the list
     * @return the liter argument builder of css
     */
    static LiteralArgumentBuilder<CommandSourceStack> getLiterArgumentBuilderOfCSS(String name, boolean shouldAddToList, @Nullable List<LiteralArgumentBuilder<CommandSourceStack>> list) {
        LiteralArgumentBuilder<CommandSourceStack> literal = Commands.literal(name);
        if (shouldAddToList) {
            assert list != null;
            list.add(literal);
        }
        return literal;
    }
}