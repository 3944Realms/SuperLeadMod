/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;

import java.util.ArrayList;
import java.util.List;

import static top.r3944realms.superleadrope.content.command.Command.*;
import static top.r3944realms.superleadrope.content.command.LeashDataCommand.BLOCK;
import static top.r3944realms.superleadrope.content.command.LeashDataCommand.UUID;


/**
 * The type Leash state command.
 */
public class LeashStateCommand {

    /**
     * The constant SLP_LEASH_STATE_MESSAGE_.
     */
    public static final String SLP_LEASH_STATE_MESSAGE_ = Command.BASE_ + "leash_state.message.";
    /**
     * The constant DEFAULT_OFFSET.
     */
    public static final String DEFAULT_OFFSET = SLP_LEASH_STATE_MESSAGE_ + "default_offset";
    /**
     * The constant APPLY_OFFSET.
     */
    public static final String APPLY_OFFSET = SLP_LEASH_STATE_MESSAGE_ + "apply_offset";
    /**
     * The constant RESET_ALL_HOLDER_.
     */
// ==================== 重置操作消息键 ====================
    public static final String RESET_ALL_HOLDER_ = SLP_LEASH_STATE_MESSAGE_ + "reset_all_holder.";
    /**
     * The constant RESET_ALL_HOLDER_SUC.
     */
    public static final String RESET_ALL_HOLDER_SUC = RESET_ALL_HOLDER_ + SUC;

    /**
     * The constant RESET_HOLDER_FOR_.
     */
    public static final String RESET_HOLDER_FOR_ = SLP_LEASH_STATE_MESSAGE_ + "reset_holder_for.";
    /**
     * The constant RESET_HOLDER_FOR_SUC.
     */
    public static final String RESET_HOLDER_FOR_SUC = RESET_HOLDER_FOR_ + SUC;

    /**
     * The constant RESET_HOLDER_FOR_BLOCK_POS_.
     */
    public static final String RESET_HOLDER_FOR_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "reset_holder_for_block_pos.";
    /**
     * The constant RESET_HOLDER_FOR_BLOCK_POS_SUC.
     */
    public static final String RESET_HOLDER_FOR_BLOCK_POS_SUC = RESET_HOLDER_FOR_BLOCK_POS_ + SUC;

    /**
     * The constant RESET_APPLY_ENTITY_ALL_.
     */
    public static final String RESET_APPLY_ENTITY_ALL_ = SLP_LEASH_STATE_MESSAGE_ + "reset_apply_entity_all.";
    /**
     * The constant RESET_APPLY_ENTITY_ALL_SUC.
     */
    public static final String RESET_APPLY_ENTITY_ALL_SUC = RESET_APPLY_ENTITY_ALL_ + SUC;

    /**
     * The constant SET_HOLDER_FOR_.
     */
// ==================== 设置操作消息键 ====================
    public static final String SET_HOLDER_FOR_ = SLP_LEASH_STATE_MESSAGE_ + "set_holder_for.";
    /**
     * The constant SET_HOLDER_FOR_SUC.
     */
    public static final String SET_HOLDER_FOR_SUC = SET_HOLDER_FOR_ + SUC;

    /**
     * The constant SET_HOLDER_FOR_BLOCK_POS_.
     */
    public static final String SET_HOLDER_FOR_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "set_holder_for_block_pos.";
    /**
     * The constant SET_HOLDER_FOR_BLOCK_POS_SUC.
     */
    public static final String SET_HOLDER_FOR_BLOCK_POS_SUC = SET_HOLDER_FOR_BLOCK_POS_ + SUC;

    /**
     * The constant SET_APPLY_ENTITY_.
     */
    public static final String SET_APPLY_ENTITY_ = SLP_LEASH_STATE_MESSAGE_ + "set_apply_entity.";
    /**
     * The constant SET_APPLY_ENTITY_SUC.
     */
    public static final String SET_APPLY_ENTITY_SUC = SET_APPLY_ENTITY_ + SUC;

    /**
     * The constant QUERY_HAS_STATE_.
     */
// ==================== 查询操作消息键 ====================
    public static final String QUERY_HAS_STATE_ = SLP_LEASH_STATE_MESSAGE_ + "query.has_state.";
    /**
     * The constant QUERY_HAS_STATE_SUC.
     */
    public static final String QUERY_HAS_STATE_SUC = QUERY_HAS_STATE_ + SUC;

    /**
     * The constant GET_ALL_UUID_STATES_.
     */
    public static final String GET_ALL_UUID_STATES_ = SLP_LEASH_STATE_MESSAGE_ + "get_all_uuid_states.";
    /**
     * The constant GET_ALL_UUID_STATES_SUC.
     */
    public static final String GET_ALL_UUID_STATES_SUC = GET_ALL_UUID_STATES_ + SUC;

    /**
     * The constant GET_ALL_BLOCK_POS_STATES_.
     */
    public static final String GET_ALL_BLOCK_POS_STATES_ = SLP_LEASH_STATE_MESSAGE_ + "get_all_block_pos_states.";
    /**
     * The constant GET_ALL_BLOCK_POS_STATES_SUC.
     */
    public static final String GET_ALL_BLOCK_POS_STATES_SUC = GET_ALL_BLOCK_POS_STATES_ + SUC;

    /**
     * The constant GET_APPLY_ENTITY_OFFSET_.
     */
    public static final String GET_APPLY_ENTITY_OFFSET_ = SLP_LEASH_STATE_MESSAGE_ + "get_apply_entity_offset.";
    /**
     * The constant GET_APPLY_ENTITY_OFFSET_SUC.
     */
    public static final String GET_APPLY_ENTITY_OFFSET_SUC = GET_APPLY_ENTITY_OFFSET_ + SUC;
    /**
     * The constant GET_APPLY_ENTITY_OFFSET_NONE.
     */
    public static final String GET_APPLY_ENTITY_OFFSET_NONE = GET_APPLY_ENTITY_OFFSET_ + "none";

    /**
     * The constant GET_DEFAULT_APPLY_ENTITY_OFFSET_.
     */
    public static final String GET_DEFAULT_APPLY_ENTITY_OFFSET_ = SLP_LEASH_STATE_MESSAGE_ + "get_default_apply_entity_offset.";
    /**
     * The constant GET_DEFAULT_APPLY_ENTITY_OFFSET_SUC.
     */
    public static final String GET_DEFAULT_APPLY_ENTITY_OFFSET_SUC = GET_DEFAULT_APPLY_ENTITY_OFFSET_ + SUC;


    /**
     * Register.
     *
     * @param dispatcher the dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        @Nullable List<LiteralArgumentBuilder<CommandSourceStack>> nodeList = SHOULD_USE_PREFIX ? null : new ArrayList<>();
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal(PREFIX);
        LiteralArgumentBuilder<CommandSourceStack> $$leashStateRoot = getLiterArgumentBuilderOfCSS("leashstate", !SHOULD_USE_PREFIX, nodeList);

        $$leashStateRoot.then(
            // ==================== 重置操作 ====================
            Commands.literal("resetAllHolder")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .executes(context -> resetAllHolder(context, EntityArgument.getEntity(context, "entity")))
                    )
            )
            .then(Commands.literal("resetHolderFor")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .then(Commands.argument("holder", EntityArgument.entity())
                                    .executes(context -> resetHolderFor(context,
                                            EntityArgument.getEntity(context, "entity"),
                                            EntityArgument.getEntity(context, "holder")))
                            )
                    )
            )
            .then(Commands.literal("resetHolderForBlockPos")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(context -> resetHolderForBlockPos(context,
                                            EntityArgument.getEntity(context, "entity"),
                                            BlockPosArgument.getLoadedBlockPos(context, "pos")))
                            )
                    )
            )
            .then(Commands.literal("resetApplyEntityAll")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .executes(context -> resetApplyEntityAll(context, EntityArgument.getEntity(context, "entity")))
                    )
            )

            // ==================== 设置操作 ====================
            .then(Commands.literal("setHolderFor")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .then(Commands.argument("holder", EntityArgument.entity())
                                    .executes(context -> setHolderFor(context,
                                            EntityArgument.getEntity(context, "entity"),
                                            EntityArgument.getEntity(context, "holder")))
                                    .then(Commands.argument("offset", Vec3Argument.vec3())
                                            .executes(context -> setHolderForWithOffset(context,
                                                    EntityArgument.getEntity(context, "entity"),
                                                    EntityArgument.getEntity(context, "holder"),
                                                    Vec3Argument.getVec3(context, "offset")))
                                    )
                            )
                    )
            )
            .then(Commands.literal("setHolderForBlockPos")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(context -> setHolderForBlockPos(context,
                                            EntityArgument.getEntity(context, "entity"),
                                            BlockPosArgument.getLoadedBlockPos(context, "pos")))
                                    .then(Commands.argument("offset", Vec3Argument.vec3())
                                            .executes(context -> setHolderForBlockPosWithOffset(context,
                                                    EntityArgument.getEntity(context, "entity"),
                                                    BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                    Vec3Argument.getVec3(context, "offset")))
                                    )
                            )
                    )
            )
            .then(Commands.literal("setApplyEntity")
                    .then(Commands.argument("entity", EntityArgument.entity())
                            .then(Commands.argument("offset", Vec3Argument.vec3())
                                    .executes(context -> setApplyEntity(context,
                                            EntityArgument.getEntity(context, "entity"),
                                            Vec3Argument.getVec3(context, "offset")))
                            )
                    )
            )
            // ==================== 查询操作 ====================
            .then(Commands.literal("query")
                    .then(Commands.literal("hasState")
                            .then(Commands.argument("entity", EntityArgument.entity())
                                    .executes(context -> queryHasState(context, EntityArgument.getEntity(context, "entity")))
                            )
                    )
                    .then(Commands.literal("getAllUUIDStates")
                            .then(Commands.argument("entity", EntityArgument.entity())
                                    .executes(context -> getAllUUIDStates(context, EntityArgument.getEntity(context, "entity")))
                            )
                    )
                    .then(Commands.literal("getAllBlockPosStates")
                            .then(Commands.argument("entity", EntityArgument.entity())
                                    .executes(context -> getAllBlockPosStates(context, EntityArgument.getEntity(context, "entity")))
                            )
                    )
                    .then(Commands.literal("getApplyEntityOffset")
                            .then(Commands.argument("entity", EntityArgument.entity())
                                    .executes(context -> getApplyEntityOffset(context, EntityArgument.getEntity(context, "entity")))
                            )
                    )
                    .then(Commands.literal("getDefaultApplyEntityOffset")
                            .then(Commands.argument("entity", EntityArgument.entity())
                                    .executes(context -> getDefaultApplyEntityOffset(context, EntityArgument.getEntity(context, "entity")))
                            )
                    )
                     .then(Commands.literal("getAllStates")
                             .then(Commands.argument("entity", EntityArgument.entity())
                                     .executes(context -> getAllStates(context, EntityArgument.getEntity(context, "entity")))
                             )
                     )

        ).requires(source -> source.hasPermission(2));
        if(SHOULD_USE_PREFIX){
            literalArgumentBuilder.then($$leashStateRoot);
            dispatcher.register(literalArgumentBuilder);
        } else {
            if (nodeList != null) {
                nodeList.forEach(dispatcher::register);
            }
        }
    }

    // ==================== 重置操作实现 ====================

    private static int resetAllHolder(@NotNull CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.resetAllHolder(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_ALL_HOLDER_SUC, getSLPName(entity)), false);
        return 1;
    }

    private static int resetHolderFor(@NotNull CommandContext<CommandSourceStack> context, Entity entity, Entity holder) {
        LeashStateInnerAPI.Offset.resetHolderFor(entity, holder);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder)), false);
        return 1;
    }

    private static int resetHolderForBlockPos(@NotNull CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos) {
        LeashStateInnerAPI.Offset.resetHolderFor(entity, pos);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos)), false);
        return 1;
    }

    private static int resetApplyEntityAll(@NotNull CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.resetApplyEntityAll(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_APPLY_ENTITY_ALL_SUC, getSLPName(entity)), false);
        return 1;
    }

    // ==================== 设置操作实现 ====================

    private static int setHolderFor(@NotNull CommandContext<CommandSourceStack> context, Entity entity, Entity holder) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, holder);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder)), false);
        return 1;
    }

    private static int setHolderForWithOffset(@NotNull CommandContext<CommandSourceStack> context, Entity entity, Entity holder, Vec3 offset) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, holder, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder), offset), false);
        return 1;
    }

    private static int setHolderForBlockPos(@NotNull CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, pos);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos)), false);
        return 1;
    }

    private static int setHolderForBlockPosWithOffset(@NotNull CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos, Vec3 offset) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, pos, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos), offset), false);
        return 1;
    }

    private static int setApplyEntity(@NotNull CommandContext<CommandSourceStack> context, Entity entity, Vec3 offset) {
        LeashStateInnerAPI.Offset.setApplyEntity(entity, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_APPLY_ENTITY_SUC, getSLPName(entity), offset), false);
        return 1;
    }

    // ==================== 查询操作实现 ====================

    private static int queryHasState(@NotNull CommandContext<CommandSourceStack> context, Entity entity) {
        boolean hasState = LeashStateInnerAPI.Query.hasState(entity);
        MutableComponent send = Component.empty();
        send.append(Component.translatable(QUERY_HAS_STATE_SUC, getSLPName(entity), hasState));
        context.getSource().sendSuccess(() -> send, false);
        return 1;
    }

    private static int getAllUUIDStates(CommandContext<CommandSourceStack> context, Entity entity) {
        var states = LeashStateInnerAPI.Query.getAllUUIDStates(entity);
        MutableComponent head = Component.translatable(GET_ALL_UUID_STATES_SUC, getSLPName(entity), states.size()).append("\n");

        MutableComponent content = Component.empty();
        if (states.isEmpty()) {
            content.append(Component.translatable(NONE));
        } else {
            int count = 0;
            for (var entry : states.entrySet()) {
                if (count >= MAX_SHOW_NUMBER) {
                    content.append(Component.translatable(ABBREVIATION));
                    break;
                }

                // UUID: xxx, 状态: xxx
                MutableComponent stateInfo = Component.literal("  UUID: " + entry.getKey() + ", 状态: " + entry.getValue());

                // 添加悬停信息
                MutableComponent hover = Component.empty();
                hover.append(Component.translatable(UUID).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getKey().toString())).append("\n");
                hover.append(Component.translatable(STATE).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getValue().toString()));

                stateInfo.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));

                content.append(stateInfo);
                if (count < Math.min(states.size(), MAX_SHOW_NUMBER) - 1) {
                    content.append("\n");
                }
                count++;
            }
        }

        context.getSource().sendSuccess(() -> head.append(content), false);
        return 1;
    }

    private static int getAllBlockPosStates(CommandContext<CommandSourceStack> context, Entity entity) {
        var states = LeashStateInnerAPI.Query.getAllBlockPosStates(entity);
        MutableComponent head = Component.translatable(GET_ALL_BLOCK_POS_STATES_SUC, getSLPName(entity), states.size()).append("\n");

        MutableComponent content = Component.empty();
        if (states.isEmpty()) {
            content.append(Component.translatable(NONE));
        } else {
            int count = 0;
            for (var entry : states.entrySet()) {
                if (count >= MAX_SHOW_NUMBER) {
                    content.append(Component.translatable(ABBREVIATION));
                    break;
                }

                BlockPos pos = entry.getKey();
                // 位置: (x,y,z), 状态: xxx
                MutableComponent stateInfo = Component.literal(Component.translatable(BLOCK_POS, pos.getX(), pos.getY(), pos.getZ())  +"," + Component.translatable(STATE) + Component.translatable(COLON)  + entry.getValue());

                // 添加悬停信息
                MutableComponent hover = Component.empty();
                hover.append(Component.translatable(BLOCK).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(pos.toShortString())).append("\n");
                hover.append(Component.translatable(STATE).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getValue().toString()));

                // 添加点击事件（建议传送到该位置）
                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        String.format("/tp @s %d %d %d", pos.getX(), pos.getY(), pos.getZ()));

                stateInfo.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
                        .withClickEvent(clickEvent));

                content.append(stateInfo);
                if (count < Math.min(states.size(), MAX_SHOW_NUMBER) - 1) {
                    content.append("\n");
                }
                count++;
            }
        }

        context.getSource().sendSuccess(() -> head.append(content), false);
        return 1;
    }

    private static int getApplyEntityOffset(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.getApplyEntityOffset(entity).ifPresentOrElse(
                offset -> {
                    MutableComponent send = Component.empty();
                    send.append(Component.translatable(GET_APPLY_ENTITY_OFFSET_SUC, getSLPName(entity), offset));
                    context.getSource().sendSuccess(() -> send, false);
                },
                () -> {
                    MutableComponent send = Component.empty();
                    send.append(Component.translatable(GET_APPLY_ENTITY_OFFSET_NONE, getSLPName(entity)));
                    context.getSource().sendSuccess(() -> send, false);
                }
        );
        return 1;
    }

    private static int getDefaultApplyEntityOffset(@NotNull CommandContext<CommandSourceStack> context, Entity entity) {
        Vec3 offset = LeashStateInnerAPI.Offset.getDefaultApplyEntityOffset(entity);
        MutableComponent send = Component.empty();
        send.append(Component.translatable(GET_DEFAULT_APPLY_ENTITY_OFFSET_SUC, getSLPName(entity), offset));
        context.getSource().sendSuccess(() -> send, false);
        return 1;
    }

    /**
     * The constant GET_ALL_STATES_HEAD.
     */
    public static final String GET_ALL_STATES_HEAD = SLP_LEASH_STATE_MESSAGE_ + "get_all_states.head";

    private static int getAllStates(CommandContext<CommandSourceStack> context, @NotNull Entity entity) {
        MutableComponent head = Component.translatable(GET_ALL_STATES_HEAD, entity.getDisplayName()).append("\n");

        // 获取所有状态信息
        var uuidStates = LeashStateInnerAPI.Query.getAllUUIDStates(entity);
        var blockPosStates = LeashStateInnerAPI.Query.getAllBlockPosStates(entity);
        var applyOffset = LeashStateInnerAPI.Offset.getApplyEntityOffset(entity);
        var defaultOffset = LeashStateInnerAPI.Offset.getDefaultApplyEntityOffset(entity);

        MutableComponent content = Component.empty();

        // UUID 状态
        content.append(Component.translatable(UUID).withStyle(ChatFormatting.YELLOW)).append(": ");
        if (uuidStates.isEmpty()) {
            content.append(Component.translatable(NONE));
        } else {
            int count = 0;
            for (var entry : uuidStates.entrySet()) {
                if (count >= 4) { // 限制显示数量
                    content.append(Component.translatable(ABBREVIATION));
                    break;
                }

                MutableComponent stateComp = Component.literal("[U]").withStyle(ChatFormatting.GREEN);
                MutableComponent hover = Component.empty();
                hover.append(Component.translatable(UUID).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getKey().toString())).append("\n");
                hover.append(Component.translatable(STATE).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getValue().toString()));

                stateComp.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                content.append(stateComp);

                if (count < Math.min(uuidStates.size(), 4) - 1) {
                    content.append(", ");
                }
                count++;
            }
        }
        content.append("\n");

        // 方块位置状态
        content.append(Component.translatable(BLOCK).withStyle(ChatFormatting.YELLOW)).append(": ");
        if (blockPosStates.isEmpty()) {
            content.append(Component.translatable(NONE));
        } else {
            int count = 0;
            for (var entry : blockPosStates.entrySet()) {
                if (count >= 4) { // 限制显示数量
                    content.append(Component.translatable(ABBREVIATION));
                    break;
                }

                MutableComponent stateComp = Component.literal("[B]").withStyle(ChatFormatting.BLUE);
                MutableComponent hover = Component.empty();
                hover.append(Component.translatable(BLOCK).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getKey().toShortString())).append("\n");
                hover.append(Component.translatable(STATE).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(entry.getValue().toString()));

                stateComp.withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                content.append(stateComp);

                if (count < Math.min(blockPosStates.size(), 4) - 1) {
                    content.append(", ");
                }
                count++;
            }
        }
        content.append("\n");

        // 应用偏移量
        content.append(Component.translatable(APPLY_OFFSET).withStyle(ChatFormatting.YELLOW)).append(": ");
        applyOffset.ifPresentOrElse(
                offset -> content.append(Component.literal(String.format("(%.2f, %.2f, %.2f)", offset.x, offset.y, offset.z))),
                () -> content.append(Component.translatable(NONE))
        );
        content.append("\n");

        // 默认偏移量
        content.append(Component.translatable(DEFAULT_OFFSET).withStyle(ChatFormatting.YELLOW)).append(": ");
        content.append(Component.literal(String.format("(%.2f, %.2f, %.2f)", defaultOffset.x, defaultOffset.y, defaultOffset.z)));

        context.getSource().sendSuccess(() -> head.append(content), false);
        return 1;
    }
}