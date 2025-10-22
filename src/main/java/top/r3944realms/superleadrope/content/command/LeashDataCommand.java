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
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.config.LeashConfigManager;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.content.gamerule.server.CreateSuperLeashKnotEntityIfAbsent;
import top.r3944realms.superleadrope.core.register.SLPGameruleRegistry;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static top.r3944realms.superleadrope.content.command.Command.*;

/**
 * The type Leash data command.
 */
public class LeashDataCommand {
    /**
     * The constant SLP_LEASH_MESSAGE_.
     */
    public static final String SLP_LEASH_MESSAGE_ = Command.BASE_ + "leash.message.";
    /**
     * The constant LEASH_DATA_GET_.
     */
    public static final String LEASH_DATA_GET_ = SLP_LEASH_MESSAGE_ + ".get.",
    /**
     * The Title.
     */
    TITLE       = LEASH_DATA_GET_ + "title",
    /**
     * The Total.
     */
    TOTAL       = LEASH_DATA_GET_ + "total",
    /**
     * The Block.
     */
    BLOCK       = LEASH_DATA_GET_ + "block",
    /**
     * The Uuid.
     */
    UUID        = LEASH_DATA_GET_ + "uuid",
    /**
     * The Max.
     */
    MAX         = LEASH_DATA_GET_ + "max",
    /**
     * The Elastic.
     */
    ELASTIC     = LEASH_DATA_GET_ + "elastic",
    /**
     * The Keep.
     */
    KEEP        = LEASH_DATA_GET_ + "keep",
    /**
     * The Reserved.
     */
    RESERVED    = LEASH_DATA_GET_ + "reserved"
    ;

    /**
     * Register.
     *
     * @param dispatcher the dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        @Nullable List<LiteralArgumentBuilder<CommandSourceStack>> nodeList = SHOULD_USE_PREFIX ? null : new ArrayList<>();
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal(PREFIX);
        LiteralArgumentBuilder<CommandSourceStack> $$leashDataRoot = getLiterArgumentBuilderOfCSS("leashdata", !SHOULD_USE_PREFIX, nodeList);
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> $$$add$holder = Commands.argument("holder", EntityArgument.entity())
                .executes(context -> addLeash(context,
                        EntityArgument.getEntity(context, "holder")
                        )
                )
                .then(Commands.argument("maxDistance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                        .executes(context -> addLeash(context,
                                EntityArgument.getEntity(context, "holder"),
                                DoubleArgumentType.getDouble(context, "maxDistance")
                                )
                        )
                        .then(Commands.argument("elasticDistanceScale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                .executes(context -> addLeash(context,
                                        EntityArgument.getEntity(context, "holder"),
                                        DoubleArgumentType.getDouble(context, "maxDistance"),
                                        DoubleArgumentType.getDouble(context, "elasticDistanceScale")
                                        )
                                )
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> addLeash(context,
                                                EntityArgument.getEntity(context, "holder"),
                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                IntegerArgumentType.getInteger(context, "keepTicks")
                                                )
                                        )
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> addLeash(context,
                                                                EntityArgument.getEntity(context, "holder"),
                                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")
                                                        )
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$add$pos = Commands.literal("block")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(LeashDataCommand::addBlockLeash)
                        .then(Commands.argument("maxDistance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                                .executes(context -> addBlockLeash(context,
                                        DoubleArgumentType.getDouble(context, "maxDistance")
                                        )
                                )
                                .then(Commands.argument("elasticDistanceScale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                        .executes(context -> addBlockLeash(context,
                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale")
                                                )
                                        )
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> addBlockLeash(context,
                                                        DoubleArgumentType.getDouble(context, "maxDistance"),
                                                        DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks")
                                                        )
                                                )
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> addBlockLeash(context,
                                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$add = Commands.literal("add")
                .then(Commands.argument("targets", EntityArgument.entities())
                        // 实体拴绳
                        .then($$$add$holder)

                        // 方块拴绳
                        .then($$$add$pos)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$remove = Commands.literal("remove")
                .then(Commands.argument("targets", EntityArgument.entities())
                        // 移除特定实体拴绳
                        .then(Commands.argument("holder", EntityArgument.entity())
                                .executes(context -> removeLeash(
                                        context,
                                        EntityArgument.getEntity(context, "holder")
                                        )
                                )
                        )

                        // 移除方块拴绳
                        .then(Commands.literal("block")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> removeBlockLeash(
                                                context,
                                                BlockPosArgument.getBlockPos(context, "pos")
                                                )
                                        )
                                )
                        )

                        // 批量移除
                        .then(Commands.literal("all")
                                .executes(LeashDataCommand::removeAllLeashes)
                        )
                        .then(Commands.literal("holders")
                                .executes(LeashDataCommand::removeAllHolderLeashes)
                        )
                        .then(Commands.literal("blocks")
                                .executes(LeashDataCommand::removeAllBlockLeashes)
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$transfer = Commands.literal("transfer")
                .then(Commands.argument("targets", EntityArgument.entities())
                        // 实体到实体转移
                        .then(Commands.argument("from", EntityArgument.entity())
                                .then(Commands.argument("to", EntityArgument.entity())
                                        .executes(context -> transferLeash(
                                                context,
                                                EntityArgument.getEntity(context, "from"),
                                                EntityArgument.getEntity(context, "to")
                                                )
                                        )
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> transferLeash(context,
                                                        EntityArgument.getEntity(context, "from"),
                                                        EntityArgument.getEntity(context, "to"),
                                                        StringArgumentType.getString(context, "reserved")
                                                        )
                                                )
                                        )
                                )
                        )

                        // 方块到实体转移
                        .then(Commands.literal("fromBlock")
                                .then(Commands.argument("fromPos", BlockPosArgument.blockPos())
                                        .then(Commands.argument("to", EntityArgument.entity())
                                                .executes(LeashDataCommand::transferFromBlock)
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> transferFromBlock(context,
                                                                StringArgumentType.getString(context, "reserved")))
                                                )
                                        )
                                )
                        )
                );
        RequiredArgumentBuilder<CommandSourceStack, EntitySelector> $$$set$holder = Commands.argument("holder", EntityArgument.entity())
                // 设置最大距离
                .then(Commands.literal("maxDistance")
                        .executes(LeashDataCommand::setMaxDistance)
                        .then(Commands.argument("distance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                                .executes(context -> setMaxDistance(
                                        context,
                                        DoubleArgumentType.getDouble(context,"distance")
                                        )
                                )
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> setMaxDistance(context,
                                                DoubleArgumentType.getDouble(context,"distance"),
                                                IntegerArgumentType.getInteger(context, "keepTicks")
                                                )
                                        )
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> setMaxDistance(context,
                                                        DoubleArgumentType.getDouble(context,"distance"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")
                                                        )
                                                )
                                        )
                                )
                        )
                )

                // 设置弹性距离比例
                .then(Commands.literal("elasticDistanceScale")
                        .executes(LeashDataCommand::setElasticDistanceScale)
                        .then(Commands.argument("scale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                .executes(context -> setElasticDistanceScale(
                                        context,
                                        DoubleArgumentType.getDouble(context,"scale")
                                        )
                                )
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> setElasticDistanceScale(
                                                context,
                                                DoubleArgumentType.getDouble(context,"scale"),
                                                IntegerArgumentType.getInteger(context, "keepTicks")
                                                )
                                        )
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> setElasticDistanceScale(context,
                                                        DoubleArgumentType.getDouble(context,"scale"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")
                                                        )
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$set$pos = Commands.literal("block")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        // 设置最大距离
                        .then(Commands.literal("maxDistance")
                                .executes(LeashDataCommand::setBlockMaxDistance)
                                .then(Commands.argument("distance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                                        .executes(context -> setBlockMaxDistance(
                                                context,
                                                DoubleArgumentType.getDouble(context, "distance")
                                                )
                                        )
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> setBlockMaxDistance(context,
                                                        DoubleArgumentType.getDouble(context, "distance"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks")
                                                        )
                                                )
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> setBlockMaxDistance(context,
                                                                DoubleArgumentType.getDouble(context, "distance"),
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )

                        // 设置弹性距离比例
                        .then(Commands.literal("elasticDistanceScale")
                                .executes(LeashDataCommand::setBlockElasticDistanceScale)
                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                        .executes(context -> setBlockElasticDistanceScale(
                                                context,
                                                DoubleArgumentType.getDouble(context, "scale")
                                                )
                                        )
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> setBlockElasticDistanceScale(context,
                                                        DoubleArgumentType.getDouble(context, "scale"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks")
                                                        )
                                                )
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> setBlockElasticDistanceScale(context,
                                                                DoubleArgumentType.getDouble(context, "scale"),
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$set = Commands.literal("set")
                .then(Commands.argument("targets", EntityArgument.entities())
                        // 实体拴绳设置
                        .then($$$set$holder)

                        // 方块拴绳设置
                        .then($$$set$pos)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$applayForces = Commands.literal("applyForces")
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(LeashDataCommand::applyForces)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$get = Commands.literal("get")
                .then(Commands.literal("data")
                        .then(Commands.argument("targets", EntityArgument.entities()).executes(LeashDataCommand::getLeashData)))
                .then(Commands.literal("info")
                        .then(Commands.argument("target", EntityArgument.entity()).executes(LeashDataCommand::getLeashInfo))
                );
        $$leashDataRoot
                .requires(source -> source.hasPermission(2)) // 需要OP权限

                // ==================== GET 命令 ====================
                .then($$$get)

                // ==================== ADD 命令 ====================
                .then($$$add)

                // ==================== REMOVE 命令 ====================
                .then($$$remove)

                // ==================== TRANSFER 命令 ====================
                .then($$$transfer)

                // ==================== SET 命令 ====================
                .then($$$set)

                // ==================== APPLY FORCES 命令 ====================
                .then($$$applayForces);
        if(SHOULD_USE_PREFIX){
            literalArgumentBuilder.then($$leashDataRoot);
            dispatcher.register(literalArgumentBuilder);
        } else {
            if (nodeList != null) {
                nodeList.forEach(dispatcher::register);
            }
        }
    }

    /**
     * The constant SET_MAX_DISTANCE.
     */
    public static final String SET_MAX_DISTANCE_ = SLP_LEASH_MESSAGE_ + "set.max_distance.";

    private static int setMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setMaxDistance(context, -1/* -1 -> null*/);
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance) throws CommandSyntaxException {
        return setMaxDistance(context, maxDistance, 0);
    }    private static int setMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setMaxDistance(context, maxDistance, keepTicks, null);
    }

    private static int setMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks,@Nullable String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        Entity holder = EntityArgument.getEntity(context, "holder");
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setMaxDistance(holder, target, maxDistance== -1 ? null : maxDistance, keepTicks, reserved);
        }
        // Successfully adjusted leash to the max distance from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
        // [if failed then add ", but failed to adjust from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
        return -1;
    }

    /**
     * The constant REMOVE_ALL_BLOCK_LEASHES.
     */
    public static final String REMOVE_ALL_BLOCK_LEASHES = SLP_LEASH_MESSAGE_ + "remove_apply_entity.all_block_leashes";
    private static int removeAllBlockLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAllKnots(target);
        }
        // Successfully adjusted leash to the max distance from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
        // [if failed then add ", but failed to adjust from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
        return -1;
    }

    /**
     * The constant REMOVE_ALL_HOLDER_LEASHES.
     */
    public static final String REMOVE_ALL_HOLDER_LEASHES = SLP_LEASH_MESSAGE_ + "remove_apply_entity.all_holder_leashes";
    private static int removeAllHolderLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAllHolders(target);
        }
        return -1;
    }

    /**
     * The constant TRANSFER_FROM_BLOCK.
     */
    public static final String TRANSFER_FROM_BLOCK = SLP_LEASH_MESSAGE_ + "transfer.from_block";
    private static int transferFromBlock(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return transferFromBlock(context, "");
    }
    private static int transferFromBlock(CommandContext<CommandSourceStack> context, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        Entity from = EntityArgument.getEntity(context, "from");
        Entity to = EntityArgument.getEntity(context, "to");
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.TransferOperations.transfer(target, from, to, reserved);
        }
        return -1;
    }

    /**
     * The constant SET_ELASTIC_DISTANCE_SCALE.
     */
    public static final String SET_ELASTIC_DISTANCE_SCALE = SLP_LEASH_MESSAGE_ + "set_apply_entity.elastic_distance_scale";
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setElasticDistanceScale(context, -1);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, double maxDistance) throws CommandSyntaxException {
        return setElasticDistanceScale(context, maxDistance ,0);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setElasticDistanceScale(context, maxDistance, keepTicks,null);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        Entity holder = EntityArgument.getEntity(context, "holder");
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setMaxDistance(holder, target, maxDistance == -1 ? null : maxDistance, keepTicks, reserved);
        }
        // Successfully adjusted leash to the elastic distance scale from %s[if more than 4 items, display in abbreviated form] to %s[Entity]
        // [if failed then add ", but failed to adjust from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
        return -1;
    }

    /**
     * The constant SET_BLOCK_MAX_DISTANCE.
     */
    public static final String SET_BLOCK_MAX_DISTANCE = SLP_LEASH_MESSAGE_ + "set_apply_entity.block_max_distance";
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockMaxDistance(context, -1);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance) throws CommandSyntaxException {
        return setBlockMaxDistance(context, maxDistance, 0);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setBlockMaxDistance(context, maxDistance, keepTicks, null);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance, int keepTicks, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        Entity holder = EntityArgument.getEntity(context, "holder");
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setMaxDistance(holder, target, maxDistance == -1 ? null : maxDistance, keepTicks, reserved);
        }
        // Successfully adjusted leash to the max distance from %s[if more than 4 items, display in abbreviated form] to %s[Entity]
        // [if failed then add ", but failed to adjust from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
        return -1;
    }

    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE_SCALE.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE_SCALE = SLP_LEASH_MESSAGE_ + "set_apply_entity.block_elastic_distance_scale";
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, -1);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, double scale) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, scale ,0);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, double scale, int keepTicks) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, scale ,keepTicks, null);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, double scale, int keepTicks,@Nullable String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        Entity holder = EntityArgument.getEntity(context, "holder");
        for (Entity target : targets) {

            boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setElasticDistanceScale(holder, target, scale == -1 ? null : scale, keepTicks, reserved);
        }
        // Successfully adjusted leash to the elastic distance scale from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
        // [if failed then add ", but failed to adjust from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
        return -1;
    }

    // ==================== 命令执行方法 ====================
    private static int getLeashData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();

        for (Entity target : targets) {
            Collection<LeashInfo> leashes = LeashDataInnerAPI.QueryOperations.getAllLeashes(target);
            source.sendSuccess(() -> Component.literal("=== Leash Data for " + target.getName().getString() + " ==="), false);
            source.sendSuccess(() -> Component.literal("Total leashes: " + leashes.size()), false);
            // TODO:翻译支持 HoverTip实现部分信息简化显示
            for (LeashInfo leash : leashes) {
                StringBuilder info = new StringBuilder();
                leash.blockPosOpt().ifPresent(pos -> info.append("Block: ").append(pos.toShortString()).append(" "));
                leash.holderUUIDOpt().ifPresent(uuid -> info.append("UUID: ").append(uuid).append(" "));
                info.append("Max: ").append(leash.maxDistance()).append(" ");
                info.append("Elastic: ").append(leash.elasticDistanceScale()).append(" ");
                info.append("Keep: ").append(leash.keepLeashTicks()).append("/").append(leash.maxKeepLeashTicks());
                if (!leash.reserved().isEmpty()) {
                    info.append(" Reserved: ").append(leash.reserved());
                }

                source.sendSuccess(() -> Component.literal(info.toString()), false);
            }
            // LeashData: [Entity]{Holder:{[Entity]}, BlockPos{[BlockPos]}} ...
        }

        return targets.size();
    }
    private static int getLeashInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, "target");
        CommandSourceStack source = context.getSource();


        Collection<LeashInfo> leashes = LeashDataInnerAPI.QueryOperations.getAllLeashes(target);
        // +++

        source.sendSuccess(() -> Component.literal("=== Leash Info from " + target.getName().getString() + " ==="), false);
        source.sendSuccess(() -> Component.literal("Total leashes: " + leashes.size()), false);
        // TODO:翻译支持 HoverTip实现部分信息简化显示
        for (LeashInfo leash : leashes) {
            StringBuilder info = new StringBuilder();
            leash.blockPosOpt().ifPresent(pos -> info.append("Block: ").append(pos.toShortString()).append(" "));
            leash.holderUUIDOpt().ifPresent(uuid -> info.append("UUID: ").append(uuid).append(" "));
            info.append("Max: ").append(leash.maxDistance()).append(" ");
            info.append("Elastic: ").append(leash.elasticDistanceScale()).append(" ");
            info.append("Keep: ").append(leash.keepLeashTicks()).append("/").append(leash.maxKeepLeashTicks());
            if (!leash.reserved().isEmpty()) {
                info.append(" Reserved: ").append(leash.reserved());
            }

            source.sendSuccess(() -> Component.literal(info.toString()), false);
        }
        // LeashInfo: [Entity]{LeashInfo: {B/U, M, E, K, R}  ..，}


        return 0;
    }
    private static int addLeash(CommandContext<CommandSourceStack> context, Entity holder) throws CommandSyntaxException {
        return addLeash(context, holder,-1);
    }
    private static int addLeash(CommandContext<CommandSourceStack> context, Entity holder,
                                     double maxDistance) throws CommandSyntaxException {
        return addLeash(context, holder, maxDistance, -1);
    }

    private static int addLeash(CommandContext<CommandSourceStack> context, Entity holder,
                                double maxDistance, double elasticDistance) throws CommandSyntaxException {
        return addLeash(context, holder, maxDistance, elasticDistance, 0);
    }
    private static int addLeash(CommandContext<CommandSourceStack> context, Entity holder,
                                double maxDistance, double elasticDistance, int keepTicks) throws CommandSyntaxException {
        return addLeash(context, holder, maxDistance, elasticDistance, keepTicks, "");//add 默认保留"",其余方法缺省为null
    }

    /**
     * The constant ADD_HOLDER_LEASHES_.
     */
    public static final String ADD_HOLDER_LEASHES_ = SLP_LEASH_MESSAGE_ + "add_holder.";
    /**
     * The constant ADD_HOLDER_LEASHES_SUC.
     */
    public static final String ADD_HOLDER_LEASHES_SUC = ADD_HOLDER_LEASHES_ + "suc";
    /**
     * The constant ADD_HOLDER_LEASHES_SUC_FAIL.
     */
    public static final String ADD_HOLDER_LEASHES_SUC_FAIL = ADD_HOLDER_LEASHES_ + "suc_fail";
    /**
     * The constant ADD_HOLDER_LEASHES_FAIL.
     */
    public static final String ADD_HOLDER_LEASHES_FAIL = ADD_HOLDER_LEASHES_ + "fail";
    private static int addLeash(CommandContext<CommandSourceStack> context, Entity holder,
                                double maxDistance, double elasticDistanceScale, int keepTicks, String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");

        CommandSourceStack source = context.getSource();
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.LeashOperations.attach(target, holder, maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
                failure++;
                if (++failure <= MAX_SHOW_NUMBER - 1) {
                    failEntities[failure] = target;
                }
            }
        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        generateEntityComponent(failEntities, failure, failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success >= 0) {
            // Successfully attached >leash<[Basic Info] from %s[if more than 4 items, display in abbreviated form] to %s[Entity]
            send.append(Component.translatable(ADD_HOLDER_LEASHES_SUC,  showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), successEntitiesComponent, Command.getSLPName(holder)));
            if (failure >= 0) {
                // , but failed to attached >leash<[Basic Info] from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
                send.append(Component.translatable(ADD_HOLDER_LEASHES_SUC_FAIL,  showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), failureEntitiesComponent, Command.getSLPName(holder)));
            } //.
            else send.append(".");
        } else {
            // Failed to attached >leash<[Basic Info] from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(ADD_HOLDER_LEASHES_FAIL, showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), failureEntitiesComponent, Command.getSLPName(holder)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
        //
        // [if failed then add ", but failed to attached leash from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
//        todo: source.sendSuccess(() -> Component.translatable(/*成功{}，失败{}*/), true);
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return addBlockLeash(context, -1);
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance) throws CommandSyntaxException {
        return addBlockLeash(context, maxDistance, -1);
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance, double elasticDistanceScale) throws CommandSyntaxException {
        return addBlockLeash(context, maxDistance, elasticDistanceScale, 0);
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance, double elasticDistanceScale, int keepTicks) throws CommandSyntaxException {
        return addBlockLeash(context, maxDistance, elasticDistanceScale, keepTicks, "");//add 默认保留"",其余方法缺省为null
    }

    /**
     * The constant ADD_BLOCK_LEASHES_.
     */
    public static final String ADD_BLOCK_LEASHES_ = SLP_LEASH_MESSAGE_ + "add_block.";
    /**
     * The constant ADD_BLOCK_LEASHES_SUC.
     */
    public static final String ADD_BLOCK_LEASHES_SUC = ADD_BLOCK_LEASHES_ + "suc";
    /**
     * The constant ADD_BLOCK_LEASHES_SUC_FAIL.
     */
    public static final String ADD_BLOCK_LEASHES_SUC_FAIL = ADD_BLOCK_LEASHES_ + "suc_fail";
    /**
     * The constant ADD_BLOCK_LEASHES_FAIL.
     */
    public static final String ADD_BLOCK_LEASHES_FAIL = ADD_BLOCK_LEASHES_ + "fail";
    /**
     * The constant ADD_BLOCK_LEASHES_FAIL_NO_KNOT_FOUND.
     */
    public static final String ADD_BLOCK_LEASHES_FAIL_NO_KNOT_FOUND = ADD_BLOCK_LEASHES_ + "fail.no_knot_found";
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance, double elasticDistanceScale, int keepTicks, String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        SuperLeashKnotEntity knotEntity = SuperLeashKnotEntity.get(level, pos)
                .or(() -> {
                    if (SLPGameruleRegistry.getGameruleBoolValue(level, CreateSuperLeashKnotEntityIfAbsent.NAME_KEY))
                        return Optional.of(SuperLeashKnotEntity.createKnot(level, pos, true));
                    else return Optional.empty();
                }).orElse(null);
        if (knotEntity == null) {
            // Failed to attach leash to %s because there is not existed knot in pos."]
            source.sendFailure(Component.translatable(ADD_BLOCK_LEASHES_FAIL_NO_KNOT_FOUND, Command.getSLPName(pos)));
//           todo: source.sendFailure(Component.translatable(/*失败，目标上无拴绳结*/));
            return -1;
        }
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.LeashOperations.attach(target, knotEntity, maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
                failure++;
                if (++failure <= MAX_SHOW_NUMBER - 1) {
                    failEntities[failure] = target;
                }
            }
        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        generateEntityComponent(failEntities, failure, failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success >= 0) {
            // Successfully attached >leash<[Basic Info] from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(ADD_BLOCK_LEASHES_SUC,  showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), successEntitiesComponent, Command.getSLPName(pos)));
            if (failure >= 0) {
                // , but failed to attached >leash<[Basic Info] from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
                send.append(Component.translatable(ADD_BLOCK_LEASHES_SUC_FAIL,  showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), failureEntitiesComponent, Command.getSLPName(pos)));
            } //.
            else send.append(".");
        } else {
            // Failed to attached >leash<[Basic Info] from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(ADD_BLOCK_LEASHES_FAIL, showLeashInfo(maxDistance == -1 ? null : maxDistance, elasticDistanceScale == -1 ? null : elasticDistanceScale, keepTicks, reserved), failureEntitiesComponent, Command.getSLPName(pos)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
        //
        // [if failed then add ", but failed to attach leash from %s[if more than 4 items, display in abbreviated form] to %s", else "."]
//        todo: source.sendSuccess(() -> Component.translatable(/*成功{}，失败{}*/), true);
    }

    /**
     * The constant LEASH_INFO.
     */
    public static final String LEASH_INFO = SLP_LEASH_MESSAGE_ + "leash.info";
    /**
     * The constant DEFAULT.
     */
    public static final String DEFAULT = SLP_LEASH_MESSAGE_ + "default";
    private static Component showLeashInfo(@Nullable Double maxDistance, @Nullable Double elasticDistanceScale, int keepTicks, String reserved) {
        return Component.translatable(LEASH_INFO, maxDistance == null ? DEFAULT : maxDistance , elasticDistanceScale == null ? DEFAULT : elasticDistanceScale, keepTicks, reserved);
    }

    /**
     * The constant REMOVE_HOLDER_LEASHES_.
     */
    public static final String REMOVE_HOLDER_LEASHES_ = SLP_LEASH_MESSAGE_ + "remove_holder.";
    /**
     * The constant REMOVE_HOLDER_LEASHES_SUC.
     */
    public static final String REMOVE_HOLDER_LEASHES_SUC = REMOVE_HOLDER_LEASHES_ + "suc";
    /**
     * The constant REMOVE_HOLDER_LEASHES_SUC_FAIL.
     */
    public static final String REMOVE_HOLDER_LEASHES_SUC_FAIL = REMOVE_HOLDER_LEASHES_ + "suc_fail";
    /**
     * The constant REMOVE_HOLDER_LEASHES_FAIL.
     */
    public static final String REMOVE_HOLDER_LEASHES_FAIL = REMOVE_HOLDER_LEASHES_ + "fail";
    private static int removeLeash(CommandContext<CommandSourceStack> context, Entity holder) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.LeashOperations.detach(target, holder);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
                failure++;
                if (++failure <= MAX_SHOW_NUMBER - 1) {
                    failEntities[failure] = target;
                }
            }
        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        generateEntityComponent(failEntities, failure, failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success >= 0) {
            // Successfully detached leash from %s[if more than 4 items, display in abbreviated form] to %s[Entity]
            send.append(Component.translatable(REMOVE_HOLDER_LEASHES_SUC, successEntitiesComponent, Command.getSLPName(holder)));
            if (failure >= 0) {
                // , but failed to detach leash from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(REMOVE_HOLDER_LEASHES_SUC_FAIL, failureEntitiesComponent, Command.getSLPName(holder)));
            } // .
            else send.append(".");
        } else {
            // Failed to detach leash from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(REMOVE_HOLDER_LEASHES_FAIL, failureEntitiesComponent, Command.getSLPName(holder)));
        }
       source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant REMOVE_BLOCK_LEASHES_.
     */
    public static final String REMOVE_BLOCK_LEASHES_ = SLP_LEASH_MESSAGE_ + "remove_knot.";
    /**
     * The constant REMOVE_BLOCK_LEASHES_SUC.
     */
    public static final String REMOVE_BLOCK_LEASHES_SUC = REMOVE_BLOCK_LEASHES_ + "suc";
    /**
     * The constant REMOVE_BLOCK_LEASHES_SUC_FAIL.
     */
    public static final String REMOVE_BLOCK_LEASHES_SUC_FAIL = REMOVE_BLOCK_LEASHES_ + "suc_fail";
    /**
     * The constant REMOVE_BLOCK_LEASHES_FAIL.
     */
    public static final String REMOVE_BLOCK_LEASHES_FAIL = REMOVE_BLOCK_LEASHES_ + "fail";
    private static int removeBlockLeash(CommandContext<CommandSourceStack> context, BlockPos pos) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.LeashOperations.detach(target, pos);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
                failure++;
                if (++failure <= MAX_SHOW_NUMBER - 1) {
                    failEntities[failure] = target;
                }
            }
        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        generateEntityComponent(failEntities, failure, failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success >= 0) {
            // Successfully detached leash from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(REMOVE_BLOCK_LEASHES_SUC, successEntitiesComponent, Command.getSLPName(pos)));
            if (failure >= 0) {
                // , but failed to detach leash from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(REMOVE_BLOCK_LEASHES_SUC_FAIL, failureEntitiesComponent, Command.getSLPName(pos)));
            } //.
            else send.append(".");
        } else {
            // Failed to detach leash from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(REMOVE_BLOCK_LEASHES_FAIL, failureEntitiesComponent, Command.getSLPName(pos)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant REMOVE_ALL_LEASHES.
     */
    public static final String REMOVE_ALL_LEASHES = SLP_LEASH_MESSAGE_ + "remove_all_leashes";
    private static int removeAllLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAll(target);
            if (++success <= MAX_SHOW_NUMBER - 1) {
                sucEntities[success] = target;
            }
        }
        MutableComponent successEntitiesComponent = Component.empty(), send = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        // Successfully detached all leash from %s[if more than 4 items, display in abbreviated form]
        send.append(Component.translatable(REMOVE_ALL_LEASHES, successEntitiesComponent));
        source.sendSuccess(() -> send, true);
        return 0;
    }

    private static int transferLeash(CommandContext<CommandSourceStack> context, Entity from, Entity to) throws CommandSyntaxException {
        return transferLeash(context, from, to,null);
    }

    /**
     * The constant TRANSFER_LEASH_.
     */
    public static final String TRANSFER_LEASH_  = SLP_LEASH_MESSAGE_ + "transfer_leash.";
    /**
     * The constant TRANSFER_LEASH_FAIL.
     */
    public static final String TRANSFER_LEASH_FAIL = TRANSFER_LEASH_ + "fail";
    /**
     * The constant TRANSFER_LEASH_SUC.
     */
    public static final String TRANSFER_LEASH_SUC = TRANSFER_LEASH_ + "suc";
    /**
     * The constant TRANSFER_LEASH_SUC_FAIL.
     */
    public static final String TRANSFER_LEASH_SUC_FAIL = TRANSFER_LEASH_ + "suc_fail";
    private static int transferLeash(CommandContext<CommandSourceStack> context, Entity from, Entity to, @Nullable String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.TransferOperations.transfer(target, from, to, reserved);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
                failure++;
                if (++failure <= MAX_SHOW_NUMBER - 1) {
                    failEntities[failure] = target;
                }
            }
        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        generateEntityComponent(failEntities, failure, failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success >= 0) {
            // Successfully transferred leash from %s[BlockPos/Entity] to %s[BlockPos/Entity] [Leashed ((>1)Entities/Entity): [if more than 4 items, display in abbreviated form]]
            send.append(Component.translatable(TRANSFER_LEASH_SUC, Command.getSLPName(from), Command.getSLPName(to), successEntitiesComponent));
            if (failure >= 0) {
                // , but failed to transfer leash from %s to %s [Leashed ((>1)Entities/Entity): [if more than 4 items, display in abbreviated form]].
                send.append(Component.translatable(TRANSFER_LEASH_SUC_FAIL, Command.getSLPName(from), Command.getSLPName(to), failureEntitiesComponent));
            } //.
            else send.append(".");
        } else {
            // Failed to transfer leash from %s to %s [Leashed ((>1)Entities/Entity): [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(TRANSFER_LEASH_FAIL, Command.getSLPName(from), Command.getSLPName(to), failureEntitiesComponent));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant APPLY_FORCE.
     */
    public static final String APPLY_FORCE = SLP_LEASH_MESSAGE_ + "apply_forces";
    private static int applyForces(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int successful = -1;
        Entity[] entities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            LeashDataInnerAPI.PhysicsOperations.applyForces(target);
            if (++successful <= MAX_SHOW_NUMBER - 1) {
                entities[successful] = target;
            }
        }
        MutableComponent entitiesComponent = Component.empty();
        generateEntityComponent(entities, successful, entitiesComponent);
        // Successfully applied force on %s[if more than 4 items, display in abbreviated form]
        source.sendSuccess(() -> Component.translatable(APPLY_FORCE, entitiesComponent), true);
        return 0;
    }

    private static void generateEntityComponent(Entity @NotNull [] entities, int successful, MutableComponent entitiesComponent) {
        for (int i = 0; i < entities.length; i++) {
            entitiesComponent.append(entities[i].getName());
            if (i < entities.length - 1) {
                entitiesComponent.append(", ");
            }
        }
        if (successful > MAX_SHOW_NUMBER - 1) {
            entitiesComponent.append(Component.translatable(ABBREVIATION));
        }
    }
}