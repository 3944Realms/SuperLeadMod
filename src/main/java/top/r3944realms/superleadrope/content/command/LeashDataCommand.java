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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
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
import java.util.concurrent.atomic.AtomicInteger;

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
     * The constant ALL_KNOTS.
     */
    public static final String ALL_KNOTS = SLP_LEASH_MESSAGE_ + "all_knots";
    /**
     * The constant ALL_HOLDERS.
     */
    public static final String ALL_HOLDERS = SLP_LEASH_MESSAGE_ + "all_holders";

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
                                                .executes(context -> transferFromBlock(
                                                        context,
                                                        BlockPosArgument.getBlockPos(context, "fromPos"),
                                                        EntityArgument.getEntity(context, "to")
                                                        )
                                                )
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> transferFromBlock(
                                                                        context,
                                                                        BlockPosArgument.getBlockPos(context, "fromPos"),
                                                                        EntityArgument.getEntity(context, "to"),
                                                                        StringArgumentType.getString(context, "reserved")
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );

        LiteralArgumentBuilder<CommandSourceStack> $$$set$holder =  Commands.literal("entity")
                // 设置最大距离
                .then(Commands.literal("maxDistance")
                        .executes(LeashDataCommand::setMaxDistance)
                    .then(Commands.argument("holder", EntityArgument.entity())
                        .executes(
                                context -> setMaxDistance(
                                        context,
                                        EntityArgument.getEntity(context, "holder")
                                )
                        )
                        .then(Commands.argument("distance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                                .executes(context -> setMaxDistance(
                                        context,
                                        EntityArgument.getEntity(context, "holder"),
                                        DoubleArgumentType.getDouble(context,"distance")
                                        )
                                )
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> setMaxDistance(
                                                context,
                                                EntityArgument.getEntity(context, "holder"),
                                                DoubleArgumentType.getDouble(context,"distance"),
                                                IntegerArgumentType.getInteger(context, "keepTicks")
                                                )
                                        )
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> setMaxDistance(
                                                        context,
                                                        EntityArgument.getEntity(context, "holder"),
                                                        DoubleArgumentType.getDouble(context,"distance"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")
                                                        )
                                                )
                                        )
                                )
                        )
                    )

                )
                // 设置弹性距离比例
                .then(Commands.literal("elasticDistanceScale")
                        .executes(LeashDataCommand::setElasticDistanceScale)
                        .then(Commands.argument("holder", EntityArgument.entity())
                                .executes(
                                        context -> setElasticDistanceScale(
                                        context,
                                        EntityArgument.getEntity(context, "holder")
                                        )
                                )
                            .then(Commands.argument("scale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                    .executes(
                                            context -> setElasticDistanceScale(
                                            context,
                                            EntityArgument.getEntity(context, "holder"),
                                            DoubleArgumentType.getDouble(context,"scale")
                                            )
                                    )
                                    .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                            .executes(
                                                    context -> setElasticDistanceScale(
                                                    context,
                                                    EntityArgument.getEntity(context, "holder"),
                                                    DoubleArgumentType.getDouble(context,"scale"),
                                                    IntegerArgumentType.getInteger(context, "keepTicks")
                                                    )
                                            )
                                            .then(Commands.argument("reserved", StringArgumentType.string())
                                                    .executes(
                                                            context -> setElasticDistanceScale(context,
                                                            EntityArgument.getEntity(context, "holder"),
                                                            DoubleArgumentType.getDouble(context,"scale"),
                                                            IntegerArgumentType.getInteger(context, "keepTicks"),
                                                            StringArgumentType.getString(context, "reserved")
                                                            )
                                                    )
                                            )
                                    )
                            )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$set$pos = Commands.literal("block")
                .executes(LeashDataCommand::setBlockMaxDistance)
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        // 设置最大距离
                        .then(Commands.literal("maxDistance")
                                .executes(context -> setBlockMaxDistance(
                                        context,
                                        BlockPosArgument.getBlockPos(context, "pos")
                                        )
                                )
                                .then(Commands.argument("distance", DoubleArgumentType.doubleArg(LeashConfigManager.MAX_DISTANCE_MIN_VALUE, LeashConfigManager.MAX_DISTANCE_MAX_VALUE))
                                        .executes(context -> setBlockMaxDistance(
                                                context,
                                                BlockPosArgument.getBlockPos(context, "pos"),
                                                DoubleArgumentType.getDouble(context, "distance")
                                                )
                                        )
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> setBlockMaxDistance(context,
                                                        BlockPosArgument.getBlockPos(context, "pos"),
                                                        DoubleArgumentType.getDouble(context, "distance"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks")
                                                        )
                                                )
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> setBlockMaxDistance(context,
                                                                BlockPosArgument.getBlockPos(context, "pos"),
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
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(context -> setBlockElasticDistanceScale(
                                                context,
                                                BlockPosArgument.getBlockPos(context, "pos")
                                                )
                                        )
                                    .then(Commands.argument("scale", DoubleArgumentType.doubleArg(LeashConfigManager.ELASTIC_DISTANCE_MIN_VALUE, LeashConfigManager.ELASTIC_DISTANCE_MAX_VALUE))
                                            .executes(context -> setBlockElasticDistanceScale(
                                                    context,
                                                    BlockPosArgument.getBlockPos(context, "pos"),
                                                    DoubleArgumentType.getDouble(context, "scale")
                                                    )
                                            )
                                            .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                    .executes(context -> setBlockElasticDistanceScale(
                                                            context,
                                                            BlockPosArgument.getBlockPos(context, "pos"),
                                                            DoubleArgumentType.getDouble(context, "scale"),
                                                            IntegerArgumentType.getInteger(context, "keepTicks")
                                                            )
                                                    )
                                                    .then(Commands.argument("reserved", StringArgumentType.string())
                                                            .executes(context -> setBlockElasticDistanceScale(
                                                                    context,
                                                                    BlockPosArgument.getBlockPos(context, "pos"),
                                                                    DoubleArgumentType.getDouble(context, "scale"),
                                                                    IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                    StringArgumentType.getString(context, "reserved")
                                                                    )
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
                        .then($$$set$pos).executes(LeashDataCommand::setBlockMaxDistance)
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


    private static int setMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setMaxDistance(context, null);
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, @Nullable Entity holder) throws CommandSyntaxException {
        return setMaxDistance(context, holder, -1);
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance) throws CommandSyntaxException {
        return setMaxDistance(context, holder, maxDistance, 0);
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setMaxDistance(context, holder, maxDistance, keepTicks, null);
    }

    /**
     * The constant SET_MAX_DISTANCE.
     */
    public static final String SET_MAX_DISTANCE_ = SLP_LEASH_MESSAGE_ + "set.max_distance.";
    /**
     * The constant SET_MAX_DISTANCE_SUC.
     */
    public static final String SET_MAX_DISTANCE_SUC = SET_MAX_DISTANCE_ + SUC;
    /**
     * The constant SET_MAX_DISTANCE_SUC_FAIL.
     */
    public static final String SET_MAX_DISTANCE_SUC_FAIL = SET_MAX_DISTANCE_ + SUC_FAIL;
    /**
     * The constant SET_MAX_DISTANCE_FAIL.
     */
    public static final String SET_MAX_DISTANCE_FAIL = SET_MAX_DISTANCE_ + FAIL;
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance, int keepTicks, @Nullable String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        AtomicInteger success = new AtomicInteger(-1);
        AtomicInteger failure = new AtomicInteger(-1);
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            if (holder != null) {
                boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setMaxDistance(target, holder, maxDistance == -1 ? null : maxDistance, keepTicks, reserved);
                if (isSuccessful) {
                    if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        sucEntities[success.get()] = target;
                    }
                } else {
                    if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        failEntities[failure.get()] = target;
                    }
                }
            } else {
                LeashDataInnerAPI.getLeashData(target)
                        .ifPresent(
                                leashData -> leashData.getAllHolderLeashes()
                                        .forEach(info -> {
                                                    boolean isSuccessful = leashData.setMaxDistance(info.holderUUIDOpt().orElseThrow(), null);
                                                    if (isSuccessful) {
                                                        if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            sucEntities[success.get()] = target;
                                                        }
                                                    } else {
                                                        if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            failEntities[failure.get()] = target;
                                                        }
                                                    }
                                                }
                                        )
                        );
            }

        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success.get(), successEntitiesComponent);
        generateEntityComponent(failEntities, failure.get(), failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success.get() >= 0) {
            // Successfully adjusted the  max distance of leash from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(SET_MAX_DISTANCE_SUC, successEntitiesComponent, holder == null ? Component.translatable(ALL_HOLDERS) : Command.getSLPName(holder)));
            if (failure.get() >= 0) {
                // , but failed to adjust it from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(SET_MAX_DISTANCE_SUC_FAIL, failureEntitiesComponent, holder == null ? Component.translatable(ALL_HOLDERS) : Command.getSLPName(holder)));
            } //.
            else send.append(".");
        } else {
            // Failed to adjust the max distance of leash from %s[if more than 4 items, display in abbreviated form] to %s
            send.append(Component.translatable(SET_MAX_DISTANCE_FAIL, failureEntitiesComponent, holder == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(holder)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant REMOVE_ALL_BLOCK_LEASHES.
     */
    public static final String REMOVE_ALL_BLOCK_LEASHES = SLP_LEASH_MESSAGE_ + "remove.all_block_leashes";
    private static int removeAllBlockLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAllKnots(target);
            if (++success <= MAX_SHOW_NUMBER - 1) {
                sucEntities[success] = target;
            }
        }
        MutableComponent successEntitiesComponent = Component.empty(), send = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        // Successfully removed all holders' leash to [%s]
        send.append(Component.translatable(REMOVE_ALL_BLOCK_LEASHES, successEntitiesComponent));
        source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant REMOVE_ALL_HOLDER_LEASHES.
     */
    public static final String REMOVE_ALL_HOLDER_LEASHES = SLP_LEASH_MESSAGE_ + "remove.all_holder_leashes";
    private static int removeAllHolderLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAllHolders(target);
            if (++success <= MAX_SHOW_NUMBER - 1) {
                sucEntities[success] = target;
            }
        }
        MutableComponent successEntitiesComponent = Component.empty(), send = Component.empty();
        generateEntityComponent(sucEntities, success, successEntitiesComponent);
        // Successfully removed all holders' leash to [%s]
        send.append(Component.translatable(REMOVE_ALL_HOLDER_LEASHES, successEntitiesComponent));
        source.sendSuccess(() -> send, true);
        return 0;
    }


    private static int transferFromBlock(CommandContext<CommandSourceStack> context, BlockPos fromPos, Entity to) throws CommandSyntaxException {
        return transferFromBlock(context, fromPos, to, null);
    }

    /**
     * The constant TRANSFER_FROM_BLOCK.
     */
    public static final String TRANSFER_FROM_BLOCK_ = SLP_LEASH_MESSAGE_ + "transfer.from_block.";
    /**
     * The constant TRANSFER_FROM_BLOCK_SUC.
     */
    public static final String TRANSFER_FROM_BLOCK_SUC = TRANSFER_FROM_BLOCK_ + SUC;
    /**
     * The constant TRANSFER_FROM_BLOCK_SUC_FAIL.
     */
    public static final String TRANSFER_FROM_BLOCK_SUC_FAIL = TRANSFER_FROM_BLOCK_ + SUC_FAIL;
    /**
     * The constant TRANSFER_FROM_BLOCK_FAIL.
     */
    public static final String TRANSFER_FROM_BLOCK_FAIL = TRANSFER_FROM_BLOCK_ + FAIL;
    private static int transferFromBlock(CommandContext<CommandSourceStack> context, BlockPos fromPos, Entity to, @Nullable String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        int success = -1, failure = -1;
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            boolean isSuccessful = LeashDataInnerAPI.TransferOperations.transfer(target, fromPos, to, reserved);
            if (isSuccessful) {
                if (++success <= MAX_SHOW_NUMBER - 1) {
                    sucEntities[success] = target;
                }
            } else {
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
            // Successfully transferred leash from %s[BlockPos] to %s[BlockPos/Entity] [Leashed: [if more than 4 items, display in abbreviated form]]
            send.append(Component.translatable(TRANSFER_FROM_BLOCK_SUC, Command.getSLPName(fromPos), Command.getSLPName(to), successEntitiesComponent));
            if (failure >= 0) {
                // , but failed to transfer leash from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
                send.append(Component.translatable(TRANSFER_FROM_BLOCK_SUC_FAIL, Command.getSLPName(fromPos), Command.getSLPName(to), failureEntitiesComponent));
            } //.
            else send.append(".");
        } else {
            // Failed to transfer leash from %s to %s [Leashed: [if more than 4 items, display in abbreviated form]].
            send.append(Component.translatable(TRANSFER_FROM_BLOCK_FAIL, Command.getSLPName(fromPos), Command.getSLPName(to), failureEntitiesComponent));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }



    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setElasticDistanceScale(context, null);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable Entity holder) throws CommandSyntaxException {
        return setElasticDistanceScale(context, holder, -1);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance) throws CommandSyntaxException {
        return setElasticDistanceScale(context, holder, maxDistance ,0);
    }
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setElasticDistanceScale(context, holder, maxDistance, keepTicks, null);
    }

    /**
     * The constant SET_ELASTIC_DISTANCE_SCALE.
     */
    public static final String SET_ELASTIC_DISTANCE_SCALE_ = SLP_LEASH_MESSAGE_ + "set.elastic_distance_scale.";
    /**
     * The constant SET_ELASTIC_DISTANCE_SCALE_SUC.
     */
    public static final String SET_ELASTIC_DISTANCE_SCALE_SUC = SET_ELASTIC_DISTANCE_SCALE_ + SUC;
    /**
     * The constant SET_ELASTIC_DISTANCE_SCALE_SUC_FAIL.
     */
    public static final String SET_ELASTIC_DISTANCE_SCALE_SUC_FAIL = SET_ELASTIC_DISTANCE_SCALE_ + SUC_FAIL;
    /**
     * The constant SET_ELASTIC_DISTANCE_SCALE_FAIL.
     */
    public static final String SET_ELASTIC_DISTANCE_SCALE_FAIL = SET_ELASTIC_DISTANCE_SCALE_ + FAIL;
    private static int setElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable Entity holder, double maxDistance, int keepTicks, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        AtomicInteger success = new AtomicInteger(-1);
        AtomicInteger failure = new AtomicInteger(-1);
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            if (holder != null) {
                boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setElasticDistanceScale(target, holder, maxDistance == -1 ? null : maxDistance, keepTicks, reserved);
                if (isSuccessful) {
                    if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        sucEntities[success.get()] = target;
                    }
                } else {
                    if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        failEntities[failure.get()] = target;
                    }
                }
            } else {
                LeashDataInnerAPI.getLeashData(target)
                        .ifPresent(
                                leashData -> leashData.getAllKnotLeashes()
                                        .forEach(info -> {
                                                    boolean isSuccessful = leashData.setElasticDistanceScale(info.holderUUIDOpt().orElseThrow(), null);
                                                    if (isSuccessful) {
                                                        if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            sucEntities[success.get()] = target;
                                                        }
                                                    } else {
                                                        if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            failEntities[failure.get()] = target;
                                                        }
                                                    }
                                                }
                                        )
                        );
            }

        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success.get(), successEntitiesComponent);
        generateEntityComponent(failEntities, failure.get(), failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success.get() >= 0) {
            // Successfully adjusted the elastic distance scale of leash from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(SET_ELASTIC_DISTANCE_SCALE_SUC, successEntitiesComponent, holder == null ? Component.translatable(ALL_HOLDERS) : Command.getSLPName(holder)));
            if (failure.get() >= 0) {
                // , but failed to adjust it from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(SET_ELASTIC_DISTANCE_SCALE_SUC_FAIL, failureEntitiesComponent, holder == null ? Component.translatable(ALL_HOLDERS) : Command.getSLPName(holder)));
            } //.
            else send.append(".");
        } else {
            // Failed to adjust the elastic distance scale of leash from %s[if more than 4 items, display in abbreviated form] to %s
            send.append(Component.translatable(SET_ELASTIC_DISTANCE_SCALE_FAIL, failureEntitiesComponent, holder == null ? Component.translatable(ALL_HOLDERS) : Command.getSLPName(holder)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }


    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockMaxDistance(context, null, -1);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos) throws CommandSyntaxException {
        return setBlockMaxDistance(context, blockPos, -1);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double maxDistance) throws CommandSyntaxException {
        return setBlockMaxDistance(context, blockPos, maxDistance, 0);
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double maxDistance, int keepTicks) throws CommandSyntaxException {
        return setBlockMaxDistance(context, blockPos, maxDistance, keepTicks, null);
    }

    /**
     * The constant SET_BLOCK_MAX_DISTANCE.
     */
    public static final String SET_BLOCK_MAX_DISTANCE_ = SLP_LEASH_MESSAGE_ + "set.block_max_distance.";
    /**
     * The constant SET_BLOCK_MAX_DISTANCE_SUC.
     */
    public static final String SET_BLOCK_MAX_DISTANCE_SUC = SET_BLOCK_MAX_DISTANCE_ + SUC;
    /**
     * The constant SET_BLOCK_MAX_DISTANCE_SUC_FAIL.
     */
    public static final String SET_BLOCK_MAX_DISTANCE_SUC_FAIL = SET_BLOCK_MAX_DISTANCE_ + SUC_FAIL;
    /**
     * The constant SET_BLOCK_MAX_DISTANCE_FAIL.
     */
    public static final String SET_BLOCK_MAX_DISTANCE_FAIL = SET_BLOCK_MAX_DISTANCE_ + FAIL;
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double maxDistance, int keepTicks, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        AtomicInteger success = new AtomicInteger(-1);
        AtomicInteger failure = new AtomicInteger(-1);
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            if (blockPos != null) {
                boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setMaxDistance(target, blockPos, maxDistance == -1 ? null : maxDistance, keepTicks, reserved);
                if (isSuccessful) {
                    if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        sucEntities[success.get()] = target;
                    }
                } else {
                    if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        failEntities[failure.get()] = target;
                    }
                }
            } else {
                LeashDataInnerAPI.getLeashData(target)
                        .ifPresent(
                                leashData -> leashData.getAllKnotLeashes()
                                        .forEach(info -> {
                                                    boolean isSuccessful = leashData.setMaxDistance(info.blockPosOpt().orElseThrow(), null);
                                                    if (isSuccessful) {
                                                        if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            sucEntities[success.get()] = target;
                                                        }
                                                    } else {
                                                        if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                            failEntities[failure.get()] = target;
                                                        }
                                                    }
                                                }
                                        )
                        );
            }

        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success.get(), successEntitiesComponent);
        generateEntityComponent(failEntities, failure.get(), failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success.get() >= 0) {
            // Successfully adjusted the  max distance of leash from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(SET_BLOCK_MAX_DISTANCE_SUC, successEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
            if (failure.get() >= 0) {
                // , but failed to adjust it from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(SET_BLOCK_MAX_DISTANCE_SUC_FAIL, failureEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
            } //.
            else send.append(".");
        } else {
            // Failed to adjust the max distance of leash from %s[if more than 4 items, display in abbreviated form] to %s
            send.append(Component.translatable(SET_BLOCK_MAX_DISTANCE_FAIL, failureEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }


    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, null, -1);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, blockPos, -1);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double scale) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, blockPos, scale ,0);
    }
    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double scale, int keepTicks) throws CommandSyntaxException {
        return setBlockElasticDistanceScale(context, blockPos, scale ,keepTicks, null);
    }

    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE_SCALE.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE_SCALE_ = SLP_LEASH_MESSAGE_ + "set.block_elastic_distance_scale.";
    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC = SET_BLOCK_ELASTIC_DISTANCE_SCALE_ + SUC;
    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC_FAIL.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC_FAIL = SET_BLOCK_ELASTIC_DISTANCE_SCALE_ + SUC_FAIL;
    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE_SCALE_FAIL.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE_SCALE_FAIL = SET_BLOCK_ELASTIC_DISTANCE_SCALE_ + FAIL;

    private static int setBlockElasticDistanceScale(CommandContext<CommandSourceStack> context, @Nullable BlockPos blockPos, double scale, int keepTicks,@Nullable String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        AtomicInteger success = new AtomicInteger(-1);
        AtomicInteger failure = new AtomicInteger(-1);
        Entity[] sucEntities = new Entity[MAX_SHOW_NUMBER], failEntities = new Entity[MAX_SHOW_NUMBER];
        for (Entity target : targets) {
            if (blockPos != null) {
                boolean isSuccessful = LeashDataInnerAPI.PropertyOperations.setElasticDistanceScale(target, blockPos, scale == -1 ? null : scale, keepTicks, reserved);
                if (isSuccessful) {
                    if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        sucEntities[success.get()] = target;
                    }
                } else {
                    if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                        failEntities[failure.get()] = target;
                    }
                }
            } else {
                LeashDataInnerAPI.getLeashData(target)
                        .ifPresent(
                                leashData -> leashData.getAllKnotLeashes()
                                    .forEach(info -> {
                                                boolean isSuccessful = leashData.setElasticDistanceScale(info.blockPosOpt().orElseThrow(), null);
                                                if (isSuccessful) {
                                                    if (success.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                        sucEntities[success.get()] = target;
                                                    }
                                                } else {
                                                    if (failure.incrementAndGet() <= MAX_SHOW_NUMBER - 1) {
                                                        failEntities[failure.get()] = target;
                                                    }
                                                }
                                            }
                                    )
                        );
            }

        }
        MutableComponent failureEntitiesComponent = Component.empty(), successEntitiesComponent = Component.empty();
        generateEntityComponent(sucEntities, success.get(), successEntitiesComponent);
        generateEntityComponent(failEntities, failure.get(), failureEntitiesComponent);
        MutableComponent send = Component.empty();
        if (success.get() >= 0) {
            // Successfully adjusted the elastic distance scale of leash from %s[if more than 4 items, display in abbreviated form] to %s[BlockPos]
            send.append(Component.translatable(SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC, successEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
            if (failure.get() >= 0) {
                // , but failed to adjust it from %s[if more than 4 items, display in abbreviated form] to %s
                send.append(Component.translatable(SET_BLOCK_ELASTIC_DISTANCE_SCALE_SUC_FAIL, failureEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
            } //.
            else send.append(".");
        } else {
            // Failed to adjust the elastic distance scale of leash from %s[if more than 4 items, display in abbreviated form] to %s
            send.append(Component.translatable(SET_BLOCK_ELASTIC_DISTANCE_SCALE_FAIL, failureEntitiesComponent, blockPos == null ? Component.translatable(ALL_KNOTS) : Command.getSLPName(blockPos)));
        }
        source.sendSuccess(() -> send, true);
        return 0;
    }

    /**
     * The constant LEASH_DATA_HEAD.
     */
// LeashData: [Entity]{Holder:{[Entity]}, BlockPos{[BlockPos]}} ...
    public static final String LEASH_DATA_HEAD = SLP_LEASH_MESSAGE_ + "leash_data.head";
    /**
     * The constant LEASH_DATA_ITEM.
     */
    public static final String LEASH_DATA_ITEM = SLP_LEASH_MESSAGE_ + "leash_data.item";
    /**
     * The constant LEASH_DATA_GET_.
     */
    public static final String LEASH_DATA_GET_ = SLP_LEASH_MESSAGE_ + ".get.",
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
    RESERVED    = LEASH_DATA_GET_ + "reserved",
    /**
     * The Entity.
     */
    ENTITY      = LEASH_DATA_GET_ + "entity",
    /**
     * The Knot.
     */
    KNOT        = LEASH_DATA_GET_ + "knot";
    /**
     * The constant MAX_SHOW_ENTITY.
     */
    public static final int MAX_SHOW_ENTITY = 25;
    private static int getLeashData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "targets");
        CommandSourceStack source = context.getSource();
        // LeashData: \n
        MutableComponent head = Component.translatable(LEASH_DATA_HEAD).append("\n");
        List<? extends Entity> list = targets.stream().toList();
        Component[] items = new Component[MAX_SHOW_ENTITY];
        for (int i = 0; i < list.size() ; i++) {
            if (i > MAX_SHOW_ENTITY) break;
            // [Entity]%s { Holder: { %s[Entity]{UUID,Max,Elastic,Keep,Reserved} ... 4 }, BlockPos{ %s [BlockPos]{Max,Elastic,Keep,Reserved} ... 4 } } [\n] ... 25 break;
            Collection<LeashInfo> leashes = LeashDataInnerAPI.QueryOperations.getAllLeashes(list.get(i));
            int entityCount = 0, knotCount = 0;
            Component[] holders = new Component[4], knots = new Component[4];
            for (LeashInfo leash : leashes) {
                MutableComponent info;
                MutableComponent hover = Component.empty();
                if (leash.holderIdOpt().isPresent()){

                    if (entityCount >= MAX_SHOW_NUMBER && knotCount >= MAX_SHOW_NUMBER) {
                        break;
                    } else if (entityCount >= MAX_SHOW_NUMBER) {
                        continue;
                    } else {
                        info = Component.translatable(ENTITY).withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                        knots[entityCount] = info;
                        leash.holderUUIDOpt().ifPresent(uuid -> hover
                                .append(Component.translatable(UUID).withStyle(ChatFormatting.DARK_AQUA))
                                .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                                .append(Component.literal(uuid.toString())).append("\n")
                        );
                        entityCount++; // 在这里增加计数
                    }

                } else {
                    if (knotCount >= MAX_SHOW_NUMBER && entityCount >= MAX_SHOW_NUMBER) {
                        break;
                    } else if (knotCount >= MAX_SHOW_NUMBER) {
                        continue;
                    } else {
                        info = Component.translatable(KNOT).withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                        holders[knotCount] = info;
                        leash.blockPosOpt().ifPresent(pos -> hover
                                .append(Component.translatable(BLOCK).withStyle(ChatFormatting.DARK_AQUA))
                                .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                                .append(Command.getSLPName(pos)).append("\n")
                        );
                        knotCount++; // 在这里增加计数
                    }
                }
                hover.append(Component.translatable(MAX).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.format("%.2f", leash.maxDistance())).append("\n")
                );
                hover.append(Component.translatable(ELASTIC).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.format("%.2f", leash.elasticDistanceScale())).append("\n")
                );
                hover.append(Component.translatable(KEEP).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.valueOf(leash.maxKeepLeashTicks())).append("\n")
                );
                if (!leash.reserved().isEmpty()) {
                    hover.append(Component.translatable(KEEP).withStyle(ChatFormatting.DARK_AQUA))
                            .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(String.valueOf(leash.maxKeepLeashTicks())).append("\n")
                    );
                }
            }

            MutableComponent item = Component.translatable(LEASH_DATA_ITEM, list.get(i).getName(), buildHolderComponent(holders, entityCount), buildHolderComponent(knots, knotCount));
            items[i] = item;
        }
        Component component = buildFinalComponent(items, list.size());
        source.sendSuccess(() -> head.append(component), true);
        return 0;
    }

    /**
     * The constant LEASH_INFO_HEAD.
     */
// LeashData: [Entity]{Holder:{[Entity]}, BlockPos{[BlockPos]}} ...
    public static final String LEASH_INFO_HEAD = SLP_LEASH_MESSAGE_ + "leash_info.head";
    /**
     * The constant LEASH_INFO_ITEM.
     */
    public static final String LEASH_INFO_ITEM = SLP_LEASH_MESSAGE_ + "leash_info.item";
    private static int getLeashInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Entity target = EntityArgument.getEntity(context, "target");
        CommandSourceStack source = context.getSource();
        // LeashData: \n
        MutableComponent head = Component.translatable(LEASH_INFO_HEAD).append("\n");
            // [Entity]%s { Holder: { %s[Entity]{UUID,Max,Elastic,Keep,Reserved} ... 4 }, BlockPos{ %s [BlockPos]{Max,Elastic,Keep,Reserved} ... 4 } } [\n] ... 25 break;
            Collection<LeashInfo> leashes = LeashDataInnerAPI.QueryOperations.getAllLeashes(target);
            int entityCount = 0, knotCount = 0;
            Component[] items = new Component[leashes.size()];
            for (LeashInfo leash : leashes) {
                MutableComponent info;
                MutableComponent hover = Component.empty();
                if (leash.holderIdOpt().isPresent()){
                    info = Component.translatable(ENTITY).withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                    items[entityCount] = info;
                    leash.holderUUIDOpt().ifPresent(uuid -> hover
                            .append(Component.translatable(UUID).withStyle(ChatFormatting.DARK_AQUA))
                            .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(uuid.toString())).append("\n")
                    );
                    entityCount++; // 在这里增加计数
                } else {
                    info = Component.translatable(KNOT).withStyle(s -> s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)));
                    items[knotCount] = info;
                    leash.blockPosOpt().ifPresent(pos -> hover
                            .append(Component.translatable(BLOCK).withStyle(ChatFormatting.DARK_AQUA))
                            .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                            .append(Command.getSLPName(pos)).append("\n")
                    );
                    knotCount++; // 在这里增加计数

                }
                hover.append(Component.translatable(MAX).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.format("%.2f", leash.maxDistance())).append("\n")
                        );
                hover.append(Component.translatable(ELASTIC).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.format("%.2f", leash.elasticDistanceScale())).append("\n")
                        );
                hover.append(Component.translatable(KEEP).withStyle(ChatFormatting.DARK_AQUA))
                        .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                        .append(Component.literal(String.valueOf(leash.maxKeepLeashTicks())).append("\n")
                        );
                if (!leash.reserved().isEmpty()) {
                    hover.append(Component.translatable(KEEP).withStyle(ChatFormatting.DARK_AQUA))
                            .append(Component.translatable(COLON).withStyle(ChatFormatting.GRAY))
                            .append(Component.literal(String.valueOf(leash.maxKeepLeashTicks())).append("\n")
                            );
                }
            }
            // LeashInfo: [Entity]{LeashInfo: {B/U, M, E, K, R}  ..，}
            MutableComponent item = Component.translatable(LEASH_INFO_ITEM, target.getName(), buildHolderInfoComponent(items, entityCount + knotCount));
            source.sendSuccess(() -> head.append(item), true);
            return 0;
    }

    private static @NotNull Component buildFinalComponent(Component @NotNull [] items, int count) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < Math.min(items.length, count); i++) {
            if (items[i] != null) {
                result.append(items[i]);
                if (i < Math.min(items.length, count) - 1) {
                    result.append(Component.literal("\n"));
                }
            }
        }
        if (count > MAX_SHOW_ENTITY - 1) {
            result.append(Component.translatable(ABBREVIATION));
        }

        return result;
    }
    // 辅助方法：构建holder组件
    private static @NotNull Component buildHolderComponent(Component @NotNull [] holders, int count) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < Math.min(holders.length, count); i++) {
            if (holders[i] != null) {
                result.append(holders[i]);
                if (i < Math.min(holders.length, count) - 1) {
                    result.append(Component.literal(", "));
                }
            }
        }
        if (count > MAX_SHOW_NUMBER - 1) {
            result.append(Component.translatable(ABBREVIATION));
        }

        return result;
    }
    private static @NotNull Component buildHolderInfoComponent(Component @NotNull [] holders, int count) {
        MutableComponent result = Component.empty();
        for (int i = 0; i < Math.min(holders.length, count); i++) {
            if (holders[i] != null) {
                result.append(holders[i]);
                if (i < Math.min(holders.length, count) - 1) {
                    result.append(Component.literal(", "));
                }
            }
        }
        return result;
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
                    if (SLPGameruleRegistry.getGameruleBoolValue(level, CreateSuperLeashKnotEntityIfAbsent.ID) && SuperLeashKnotEntity.isSupportBlock(level.getBlockState(pos)))
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
            if (entities[i] == null) break;
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