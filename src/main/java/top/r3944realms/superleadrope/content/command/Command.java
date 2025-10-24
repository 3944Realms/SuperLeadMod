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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

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
     * The constant BASE_.
     */
    public static final String BASE_ = SuperLeadRope.MOD_ID + ".command.";
    /**
     * The constant ABBREVIATION.
     */
    public static final String ABBREVIATION = BASE_ + "abbreviation";
    /**
     * The constant BLOCK_POS.
     */
    public static final String BLOCK_POS = BASE_ + ".block_pos";
    /**
     * The constant SUC.
     */
    public static final String SUC = "suc";
    /**
     * The constant FAIL.
     */
    public static final String FAIL = "fail";
    /**
     * The constant SUC_FAIL.
     */
    public static final String SUC_FAIL = "suc_fail";
    /**
     * The constant COLON.
     */
    public static final String COLON = BASE_ + "colon";

    /**
     * The constant MAX_SHOW_NUMBER.
     */
    public static final int MAX_SHOW_NUMBER = 4;
    public static final String END = BASE_ + "end";
    public static final String NONE = BASE_ + "none";
    /**
     * Gets slp name.
     *
     * @param entity the entity
     * @return the slp name
     */
    public static Component getSLPName(Entity entity) {
        if (entity instanceof SuperLeashKnotEntity superLeashKnot) {
            BlockPos pos = superLeashKnot.getPos();
            return Component.translatable(BLOCK_POS, pos.getX(), pos.getY(), pos.getZ());
        }
        return entity.getDisplayName();
    }

    /**
     * Gets slp name.
     *
     * @param pos the pos
     * @return the slp name
     */
    public static Component getSLPName(BlockPos pos) {
        return Component.translatable(BLOCK_POS, pos.getX(), pos.getY(), pos.getZ());
    }

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