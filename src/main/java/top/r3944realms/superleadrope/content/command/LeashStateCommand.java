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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;

import static top.r3944realms.superleadrope.content.command.Command.*;

/**
 * The type Leash state command.
 */
//TODO: 未来扩展，启用
public class LeashStateCommand {

    /**
     * The constant SLP_LEASH_STATE_MESSAGE_.
     */
    public static final String SLP_LEASH_STATE_MESSAGE_ = Command.BASE_ + "leash_state.message.";

    // ==================== 重置操作消息键 ====================
    public static final String RESET_ALL_HOLDER_ = SLP_LEASH_STATE_MESSAGE_ + "reset_all_holder.";
    public static final String RESET_ALL_HOLDER_SUC = RESET_ALL_HOLDER_ + SUC;

    public static final String RESET_HOLDER_FOR_ = SLP_LEASH_STATE_MESSAGE_ + "reset_holder_for.";
    public static final String RESET_HOLDER_FOR_SUC = RESET_HOLDER_FOR_ + SUC;

    public static final String RESET_HOLDER_FOR_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "reset_holder_for_block_pos.";
    public static final String RESET_HOLDER_FOR_BLOCK_POS_SUC = RESET_HOLDER_FOR_BLOCK_POS_ + SUC;

    public static final String RESET_APPLY_ENTITY_ALL_ = SLP_LEASH_STATE_MESSAGE_ + "reset_apply_entity_all.";
    public static final String RESET_APPLY_ENTITY_ALL_SUC = RESET_APPLY_ENTITY_ALL_ + SUC;

    // ==================== 设置操作消息键 ====================
    public static final String SET_HOLDER_FOR_ = SLP_LEASH_STATE_MESSAGE_ + "set_holder_for.";
    public static final String SET_HOLDER_FOR_SUC = SET_HOLDER_FOR_ + SUC;
    public static final String SET_HOLDER_FOR_SUC_FAIL = SET_HOLDER_FOR_ + SUC_FAIL;
    public static final String SET_HOLDER_FOR_FAIL = SET_HOLDER_FOR_ + FAIL;

    public static final String SET_HOLDER_FOR_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "set_holder_for_block_pos.";
    public static final String SET_HOLDER_FOR_BLOCK_POS_SUC = SET_HOLDER_FOR_BLOCK_POS_ + SUC;
    public static final String SET_HOLDER_FOR_BLOCK_POS_SUC_FAIL = SET_HOLDER_FOR_BLOCK_POS_ + SUC_FAIL;
    public static final String SET_HOLDER_FOR_BLOCK_POS_FAIL = SET_HOLDER_FOR_BLOCK_POS_ + FAIL;

    public static final String SET_APPLY_ENTITY_ = SLP_LEASH_STATE_MESSAGE_ + "set_apply_entity.";
    public static final String SET_APPLY_ENTITY_SUC = SET_APPLY_ENTITY_ + SUC;
    public static final String SET_APPLY_ENTITY_SUC_FAIL = SET_APPLY_ENTITY_ + SUC_FAIL;
    public static final String SET_APPLY_ENTITY_FAIL = SET_APPLY_ENTITY_ + FAIL;

    // ==================== 添加操作消息键 ====================
    public static final String ADD_HOLDER_TO_ = SLP_LEASH_STATE_MESSAGE_ + "add_holder_to.";
    public static final String ADD_HOLDER_TO_SUC = ADD_HOLDER_TO_ + SUC;
    public static final String ADD_HOLDER_TO_SUC_FAIL = ADD_HOLDER_TO_ + SUC_FAIL;
    public static final String ADD_HOLDER_TO_FAIL = ADD_HOLDER_TO_ + FAIL;

    public static final String ADD_HOLDER_TO_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "add_holder_to_block_pos.";
    public static final String ADD_HOLDER_TO_BLOCK_POS_SUC = ADD_HOLDER_TO_BLOCK_POS_ + SUC;
    public static final String ADD_HOLDER_TO_BLOCK_POS_SUC_FAIL = ADD_HOLDER_TO_BLOCK_POS_ + SUC_FAIL;
    public static final String ADD_HOLDER_TO_BLOCK_POS_FAIL = ADD_HOLDER_TO_BLOCK_POS_ + FAIL;

    public static final String ADD_APPLY_ENTITY_ = SLP_LEASH_STATE_MESSAGE_ + "add_apply_entity.";
    public static final String ADD_APPLY_ENTITY_SUC = ADD_APPLY_ENTITY_ + SUC;
    public static final String ADD_APPLY_ENTITY_SUC_FAIL = ADD_APPLY_ENTITY_ + SUC_FAIL;
    public static final String ADD_APPLY_ENTITY_FAIL = ADD_APPLY_ENTITY_ + FAIL;

    // ==================== 移除操作消息键 ====================
    public static final String REMOVE_HOLDER_FOR_ = SLP_LEASH_STATE_MESSAGE_ + "remove_holder_for.";
    public static final String REMOVE_HOLDER_FOR_SUC = REMOVE_HOLDER_FOR_ + SUC;
    public static final String REMOVE_HOLDER_FOR_SUC_FAIL = REMOVE_HOLDER_FOR_ + SUC_FAIL;
    public static final String REMOVE_HOLDER_FOR_FAIL = REMOVE_HOLDER_FOR_ + FAIL;

    public static final String REMOVE_HOLDER_FOR_BLOCK_POS_ = SLP_LEASH_STATE_MESSAGE_ + "remove_holder_for_block_pos.";
    public static final String REMOVE_HOLDER_FOR_BLOCK_POS_SUC = REMOVE_HOLDER_FOR_BLOCK_POS_ + SUC;
    public static final String REMOVE_HOLDER_FOR_BLOCK_POS_SUC_FAIL = REMOVE_HOLDER_FOR_BLOCK_POS_ + SUC_FAIL;
    public static final String REMOVE_HOLDER_FOR_BLOCK_POS_FAIL = REMOVE_HOLDER_FOR_BLOCK_POS_ + FAIL;

    public static final String REMOVE_HOLDER_ALL_ = SLP_LEASH_STATE_MESSAGE_ + "remove_holder_all.";
    public static final String REMOVE_HOLDER_ALL_SUC = REMOVE_HOLDER_ALL_ + SUC;

    public static final String REMOVE_ALL_HOLDER_UUIDS_ = SLP_LEASH_STATE_MESSAGE_ + "remove_all_holder_uuids.";
    public static final String REMOVE_ALL_HOLDER_UUIDS_SUC = REMOVE_ALL_HOLDER_UUIDS_ + SUC;

    public static final String REMOVE_ALL_HOLDER_BLOCK_POSES_ = SLP_LEASH_STATE_MESSAGE_ + "remove_all_holder_block_poses.";
    public static final String REMOVE_ALL_HOLDER_BLOCK_POSES_SUC = REMOVE_ALL_HOLDER_BLOCK_POSES_ + SUC;

    public static final String REMOVE_APPLY_ENTITY_ = SLP_LEASH_STATE_MESSAGE_ + "remove_apply_entity.";
    public static final String REMOVE_APPLY_ENTITY_SUC = REMOVE_APPLY_ENTITY_ + SUC;

    // ==================== 查询操作消息键 ====================
    public static final String QUERY_HAS_STATE_ = SLP_LEASH_STATE_MESSAGE_ + "query.has_state.";
    public static final String QUERY_HAS_STATE_SUC = QUERY_HAS_STATE_ + SUC;

    public static final String GET_ALL_UUID_STATES_ = SLP_LEASH_STATE_MESSAGE_ + "get_all_uuid_states.";
    public static final String GET_ALL_UUID_STATES_SUC = GET_ALL_UUID_STATES_ + SUC;

    public static final String GET_ALL_BLOCK_POS_STATES_ = SLP_LEASH_STATE_MESSAGE_ + "get_all_block_pos_states.";
    public static final String GET_ALL_BLOCK_POS_STATES_SUC = GET_ALL_BLOCK_POS_STATES_ + SUC;

    public static final String GET_APPLY_ENTITY_OFFSET_ = SLP_LEASH_STATE_MESSAGE_ + "get_apply_entity_offset.";
    public static final String GET_APPLY_ENTITY_OFFSET_SUC = GET_APPLY_ENTITY_OFFSET_ + SUC;
    public static final String GET_APPLY_ENTITY_OFFSET_NONE = GET_APPLY_ENTITY_OFFSET_ + "none";

    public static final String GET_DEFAULT_APPLY_ENTITY_OFFSET_ = SLP_LEASH_STATE_MESSAGE_ + "get_default_apply_entity_offset.";
    public static final String GET_DEFAULT_APPLY_ENTITY_OFFSET_SUC = GET_DEFAULT_APPLY_ENTITY_OFFSET_ + SUC;

    /**
     * Register.
     *
     * @param dispatcher the dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("leashstate")
                .requires(source -> source.hasPermission(2)) // 需要权限等级2

                // ==================== 重置操作 ====================
                .then(Commands.literal("resetAllHolder")
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

                // ==================== 添加操作 ====================
                .then(Commands.literal("addHolderTo")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("holder", EntityArgument.entity())
                                        .then(Commands.argument("offset", Vec3Argument.vec3())
                                                .executes(context -> addHolderTo(context,
                                                        EntityArgument.getEntity(context, "entity"),
                                                        EntityArgument.getEntity(context, "holder"),
                                                        Vec3Argument.getVec3(context, "offset")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("addHolderToBlockPos")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("offset", Vec3Argument.vec3())
                                                .executes(context -> addHolderToBlockPos(context,
                                                        EntityArgument.getEntity(context, "entity"),
                                                        BlockPosArgument.getLoadedBlockPos(context, "pos"),
                                                        Vec3Argument.getVec3(context, "offset")))
                                        )
                                )
                        )
                )
                .then(Commands.literal("addApplyEntity")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("offset", Vec3Argument.vec3())
                                        .executes(context -> addApplyEntity(context,
                                                EntityArgument.getEntity(context, "entity"),
                                                Vec3Argument.getVec3(context, "offset")))
                                )
                        )
                )

                // ==================== 移除操作 ====================
                .then(Commands.literal("removeHolderFor")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("holder", EntityArgument.entity())
                                        .executes(context -> removeHolderFor(context,
                                                EntityArgument.getEntity(context, "entity"),
                                                EntityArgument.getEntity(context, "holder")))
                                )
                        )
                )
                .then(Commands.literal("removeHolderForBlockPos")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> removeHolderForBlockPos(context,
                                                EntityArgument.getEntity(context, "entity"),
                                                BlockPosArgument.getLoadedBlockPos(context, "pos")))
                                )
                        )
                )
                .then(Commands.literal("removeHolderAll")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> removeHolderAll(context, EntityArgument.getEntity(context, "entity")))
                        )
                )
                .then(Commands.literal("removeAllHolderUUIDs")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> removeAllHolderUUIDs(context, EntityArgument.getEntity(context, "entity")))
                        )
                )
                .then(Commands.literal("removeAllHolderBlockPoses")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> removeAllHolderBlockPoses(context, EntityArgument.getEntity(context, "entity")))
                        )
                )
                .then(Commands.literal("removeApplyEntity")
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> removeApplyEntity(context, EntityArgument.getEntity(context, "entity")))
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
                )
        );
    }

    // ==================== 重置操作实现 ====================

    private static int resetAllHolder(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.resetAllHolder(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_ALL_HOLDER_SUC, getSLPName(entity)), false);
        return 1;
    }

    private static int resetHolderFor(CommandContext<CommandSourceStack> context, Entity entity, Entity holder) {
        LeashStateInnerAPI.Offset.resetHolderFor(entity, holder);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder)), false);
        return 1;
    }

    private static int resetHolderForBlockPos(CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos) {
        LeashStateInnerAPI.Offset.resetHolderFor(entity, pos);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos)), false);
        return 1;
    }

    private static int resetApplyEntityAll(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.resetApplyEntityAll(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(RESET_APPLY_ENTITY_ALL_SUC, getSLPName(entity)), false);
        return 1;
    }

    // ==================== 设置操作实现 ====================

    private static int setHolderFor(CommandContext<CommandSourceStack> context, Entity entity, Entity holder) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, holder);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder)), false);
        return 1;
    }

    private static int setHolderForWithOffset(CommandContext<CommandSourceStack> context, Entity entity, Entity holder, Vec3 offset) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, holder, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder), offset), false);
        return 1;
    }

    private static int setHolderForBlockPos(CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, pos);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos)), false);
        return 1;
    }

    private static int setHolderForBlockPosWithOffset(CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos, Vec3 offset) {
        LeashStateInnerAPI.Offset.setHolderFor(entity, pos, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos), offset), false);
        return 1;
    }

    private static int setApplyEntity(CommandContext<CommandSourceStack> context, Entity entity, Vec3 offset) {
        LeashStateInnerAPI.Offset.setApplyEntity(entity, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(SET_APPLY_ENTITY_SUC, getSLPName(entity), offset), false);
        return 1;
    }

    // ==================== 添加操作实现 ====================

    private static int addHolderTo(CommandContext<CommandSourceStack> context, Entity entity, Entity holder, Vec3 offset) {
        LeashStateInnerAPI.Offset.addHolderTo(entity, holder, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(ADD_HOLDER_TO_SUC, getSLPName(entity), getSLPName(holder), offset), false);
        return 1;
    }

    private static int addHolderToBlockPos(CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos, Vec3 offset) {
        LeashStateInnerAPI.Offset.addHolderTo(entity, pos, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(ADD_HOLDER_TO_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos), offset), false);
        return 1;
    }

    private static int addApplyEntity(CommandContext<CommandSourceStack> context, Entity entity, Vec3 offset) {
        LeashStateInnerAPI.Offset.addApplyEntity(entity, offset);
        context.getSource().sendSuccess(() ->
                Component.translatable(ADD_APPLY_ENTITY_SUC, getSLPName(entity), offset), false);
        return 1;
    }

    // ==================== 移除操作实现 ====================

    private static int removeHolderFor(CommandContext<CommandSourceStack> context, Entity entity, Entity holder) {
        LeashStateInnerAPI.Offset.removeHolderFor(entity, holder);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_HOLDER_FOR_SUC, getSLPName(entity), getSLPName(holder)), false);
        return 1;
    }

    private static int removeHolderForBlockPos(CommandContext<CommandSourceStack> context, Entity entity, BlockPos pos) {
        LeashStateInnerAPI.Offset.removeHolderFor(entity, pos);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_HOLDER_FOR_BLOCK_POS_SUC, getSLPName(entity), getSLPName(pos)), false);
        return 1;
    }

    private static int removeHolderAll(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.removeHolderAll(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_HOLDER_ALL_SUC, getSLPName(entity)), false);
        return 1;
    }

    private static int removeAllHolderUUIDs(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.removeAllHolderUUIDs(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_ALL_HOLDER_UUIDS_SUC, getSLPName(entity)), false);
        return 1;
    }

    private static int removeAllHolderBlockPoses(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.removeAllHolderBlockPoses(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_ALL_HOLDER_BLOCK_POSES_SUC, getSLPName(entity)), false);
        return 1;
    }

    private static int removeApplyEntity(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.removeApplyEntity(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(REMOVE_APPLY_ENTITY_SUC, getSLPName(entity)), false);
        return 1;
    }

    // ==================== 查询操作实现 ====================

    private static int queryHasState(CommandContext<CommandSourceStack> context, Entity entity) {
        boolean hasState = LeashStateInnerAPI.Query.hasState(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(QUERY_HAS_STATE_SUC, getSLPName(entity), hasState), false);
        return 1;
    }

    private static int getAllUUIDStates(CommandContext<CommandSourceStack> context, Entity entity) {
        var states = LeashStateInnerAPI.Query.getAllUUIDStates(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(GET_ALL_UUID_STATES_SUC, getSLPName(entity), states.size()), false);
        states.forEach((uuid, state) -> {
            context.getSource().sendSuccess(() ->
                    Component.literal("  UUID: " + uuid + ", 状态: " + state), false);
        });
        return 1;
    }

    private static int getAllBlockPosStates(CommandContext<CommandSourceStack> context, Entity entity) {
        var states = LeashStateInnerAPI.Query.getAllBlockPosStates(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(GET_ALL_BLOCK_POS_STATES_SUC, getSLPName(entity), states.size()), false);
        states.forEach((pos, state) -> {
            context.getSource().sendSuccess(() ->
                    Component.literal("  位置: " + pos + ", 状态: " + state), false);
        });
        return 1;
    }

    private static int getApplyEntityOffset(CommandContext<CommandSourceStack> context, Entity entity) {
        LeashStateInnerAPI.Offset.getApplyEntityOffset(entity).ifPresentOrElse(
                offset -> context.getSource().sendSuccess(() ->
                        Component.translatable(GET_APPLY_ENTITY_OFFSET_SUC, getSLPName(entity), offset), false),
                () -> context.getSource().sendSuccess(() ->
                        Component.translatable(GET_APPLY_ENTITY_OFFSET_NONE, getSLPName(entity)), false)
        );
        return 1;
    }

    private static int getDefaultApplyEntityOffset(CommandContext<CommandSourceStack> context, Entity entity) {
        Vec3 offset = LeashStateInnerAPI.Offset.getDefaultApplyEntityOffset(entity);
        context.getSource().sendSuccess(() ->
                Component.translatable(GET_DEFAULT_APPLY_ENTITY_OFFSET_SUC, getSLPName(entity), offset), false);
        return 1;
    }
}