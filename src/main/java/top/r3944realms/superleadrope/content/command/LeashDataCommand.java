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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.CommonEventHandler;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
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
    public static final String SLP_LEASH_MESSAGE_ = SuperLeadRope.MOD_ID + ".command.leash.message.";
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
                .executes(LeashDataCommand::addLeash)
                .then(Commands.argument("maxDistance", DoubleArgumentType.doubleArg(1.0, 256.0))
                        .executes(context -> addLeash(context,
                                DoubleArgumentType.getDouble(context, "maxDistance")))
                        .then(Commands.argument("elasticDistanceScale", DoubleArgumentType.doubleArg(1.0, 128.0))
                                .executes(context -> addLeash(context,
                                        DoubleArgumentType.getDouble(context, "maxDistance"),
                                        DoubleArgumentType.getDouble(context, "elasticDistanceScale")))
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> addLeash(context,
                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                IntegerArgumentType.getInteger(context, "keepTicks")))
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> addLeash(context,
                                                        DoubleArgumentType.getDouble(context, "maxDistance"),
                                                        DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")))
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$add$pos = Commands.literal("block")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(LeashDataCommand::addBlockLeash)
                        .then(Commands.argument("maxDistance", DoubleArgumentType.doubleArg(1.0, 256.0))
                                .executes(context -> addBlockLeash(context,
                                        DoubleArgumentType.getDouble(context, "maxDistance")))
                                .then(Commands.argument("elasticDistanceScale", DoubleArgumentType.doubleArg(1.0, 128.0))
                                        .executes(context -> addBlockLeash(context,
                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"), 0, ""))
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> addBlockLeash(context,
                                                        DoubleArgumentType.getDouble(context, "maxDistance"),
                                                        DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                        IntegerArgumentType.getInteger(context, "keepTicks")))
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> addBlockLeash(context,
                                                                DoubleArgumentType.getDouble(context, "maxDistance"),
                                                                DoubleArgumentType.getDouble(context, "elasticDistanceScale"),
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")))
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$add = Commands.literal("addApplyEntity")
                .then(Commands.argument("target", EntityArgument.entities())
                        // 实体拴绳
                        .then($$$add$holder)

                        // 方块拴绳
                        .then($$$add$pos)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$remove = Commands.literal("removeApplyEntity")
                .then(Commands.argument("target", EntityArgument.entities())
                        // 移除特定实体拴绳
                        .then(Commands.argument("holder", EntityArgument.entity())
                                .executes(LeashDataCommand::removeLeash)
                        )

                        // 移除方块拴绳
                        .then(Commands.literal("block")
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(LeashDataCommand::removeBlockLeash)
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
                .then(Commands.argument("target", EntityArgument.entities())
                        // 实体到实体转移
                        .then(Commands.argument("from", EntityArgument.entity())
                                .then(Commands.argument("to", EntityArgument.entity())
                                        .executes(LeashDataCommand::transferLeash)
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> transferLeash(context,
                                                        StringArgumentType.getString(context, "reserved")))
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
                        .then(Commands.argument("distance", DoubleArgumentType.doubleArg(1.0, 256.0))
                                .executes(LeashDataCommand::setMaxDistance)
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> setMaxDistance(context,
                                                IntegerArgumentType.getInteger(context, "keepTicks")))
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> setMaxDistance(context,
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")))
                                        )
                                )
                        )
                )

                // 设置弹性距离
                .then(Commands.literal("elasticDistanceScale")
                        .then(Commands.argument("distance", DoubleArgumentType.doubleArg(1.0, 128.0))
                                .executes(context -> setElasticDistance(context, 0, ""))
                                .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                        .executes(context -> setElasticDistance(context,
                                                IntegerArgumentType.getInteger(context, "keepTicks"), ""))
                                        .then(Commands.argument("reserved", StringArgumentType.string())
                                                .executes(context -> setElasticDistance(context,
                                                        IntegerArgumentType.getInteger(context, "keepTicks"),
                                                        StringArgumentType.getString(context, "reserved")))
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$set$pos = Commands.literal("block")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        // 设置最大距离
                        .then(Commands.literal("maxDistance")
                                .then(Commands.argument("distance", DoubleArgumentType.doubleArg(1.0, 256.0))
                                        .executes(LeashDataCommand::setBlockMaxDistance)
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> setBlockMaxDistance(context,
                                                        IntegerArgumentType.getInteger(context, "keepTicks")))
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> setBlockMaxDistance(context,
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")))
                                                )
                                        )
                                )
                        )

                        // 设置弹性距离
                        .then(Commands.literal("elasticDistanceScale")
                                .then(Commands.argument("distance", DoubleArgumentType.doubleArg(1.0, 128.0))
                                        .executes(LeashDataCommand::setBlockElasticDistance)
                                        .then(Commands.argument("keepTicks", IntegerArgumentType.integer(0))
                                                .executes(context -> setBlockElasticDistance(context,
                                                        IntegerArgumentType.getInteger(context, "keepTicks")))
                                                .then(Commands.argument("reserved", StringArgumentType.string())
                                                        .executes(context -> setBlockElasticDistance(context,
                                                                IntegerArgumentType.getInteger(context, "keepTicks"),
                                                                StringArgumentType.getString(context, "reserved")))
                                                )
                                        )
                                )
                        )
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$set = Commands.literal("setApplyEntity")
                .then(Commands.argument("target", EntityArgument.entities())
                        // 实体拴绳设置
                        .then($$$set$holder)

                        // 方块拴绳设置
                        .then($$$set$pos)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$applayForces = Commands.literal("applyForces")
                .then(Commands.argument("target", EntityArgument.entities())
                        .executes(LeashDataCommand::applyForces)
                );
        LiteralArgumentBuilder<CommandSourceStack> $$$get = Commands.literal("get")
                .then(Commands.argument("target", EntityArgument.entities())
                        .executes(LeashDataCommand::getLeashData)
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
    public static final String SET_MAX_DISTANCE = SLP_LEASH_MESSAGE_ + "set_apply_entity.max_distance";
    private static int setMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setMaxDistance(context, CommonEventHandler.leashConfigManager.getMaxLeashLength(), "");
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance) throws CommandSyntaxException {
        return setMaxDistance(context, maxDistance, "");
    }
    private static int setMaxDistance(CommandContext<CommandSourceStack> context, double maxDistance, String reserved) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        Entity holder = EntityArgument.getEntity(context, "holder");
        for (Entity target : targets) {

        }
        return -1;
    }

    /**
     * The constant REMOVE_ALL_BLOCK_LEASHES.
     */
    public static final String REMOVE_ALL_BLOCK_LEASHES = SLP_LEASH_MESSAGE_ + "remove_apply_entity.all_block_leashes";
    private static int removeAllBlockLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return -1;
    }

    /**
     * The constant REMOVE_ALL_HOLDER_LEASHES.
     */
    public static final String REMOVE_ALL_HOLDER_LEASHES = SLP_LEASH_MESSAGE_ + "remove_apply_entity.all_holder_leashes";
    private static int removeAllHolderLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
        return -1;
    }

    /**
     * The constant SET_ELASTIC_DISTANCE.
     */
    public static final String SET_ELASTIC_DISTANCE = SLP_LEASH_MESSAGE_ + "set_apply_entity.elastic_distance";
    private static int setElasticDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setElasticDistance(context, 0 ,"");
    }
    private static int setElasticDistance(CommandContext<CommandSourceStack> context, int keepTicks) throws CommandSyntaxException {
        return setElasticDistance(context, keepTicks ,"");
    }
    private static int setElasticDistance(CommandContext<CommandSourceStack> context, int keepTicks, String reserved) throws CommandSyntaxException {
        return -1;
    }

    /**
     * The constant SET_BLOCK_MAX_DISTANCE.
     */
    public static final String SET_BLOCK_MAX_DISTANCE = SLP_LEASH_MESSAGE_ + "set_apply_entity.block_max_distance";
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockMaxDistance(context, 0 ,"");
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, int keepTicks) throws CommandSyntaxException {
        return setBlockMaxDistance(context, keepTicks ,"");
    }
    private static int setBlockMaxDistance(CommandContext<CommandSourceStack> context, int keepTicks, String reserved) throws CommandSyntaxException {
        return -1;
    }

    /**
     * The constant SET_BLOCK_ELASTIC_DISTANCE.
     */
    public static final String SET_BLOCK_ELASTIC_DISTANCE = SLP_LEASH_MESSAGE_ + "set_apply_entity.block_elastic_distance";
    private static int setBlockElasticDistance(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return setBlockElasticDistance(context, 0 ,"");
    }
    private static int setBlockElasticDistance(CommandContext<CommandSourceStack> context, int keepTicks) throws CommandSyntaxException {
        return setBlockElasticDistance(context, keepTicks ,"");
    }
    private static int setBlockElasticDistance(CommandContext<CommandSourceStack> context, int keepTicks, String reserved) throws CommandSyntaxException {
        return -1;
    }

    // ==================== 命令执行方法 ====================

    private static int getLeashData(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        CommandSourceStack source = context.getSource();

        for (Entity target : targets) {
            Collection<LeashInfo> leashes = LeashDataInnerAPI.QueryOperations.getAllLeashes(target);
            // +++

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
        }

        return targets.size();
    }
    private static int addLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return addLeash(context,  CommonEventHandler.leashConfigManager.getMaxLeashLength(), CommonEventHandler.leashConfigManager.getElasticDistanceScale(), 0, "");
    }

    private static int addLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance) throws CommandSyntaxException {
        return addLeash(context, maxDistance, CommonEventHandler.leashConfigManager.getElasticDistanceScale(), 0, "");
    }

    private static int addLeash(CommandContext<CommandSourceStack> context,
                                double maxDistance, double elasticDistance) throws CommandSyntaxException {
        return addLeash(context, maxDistance, elasticDistance, 0, "");
    }
    private static int addLeash(CommandContext<CommandSourceStack> context,
                                double maxDistance, double elasticDistance, int keepTicks) throws CommandSyntaxException {
        return addLeash(context, maxDistance, elasticDistance, keepTicks, "");
    }
    private static int addLeash(CommandContext<CommandSourceStack> context,
                                double maxDistance, double elasticDistance, int keepTicks, String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        Entity holder = EntityArgument.getEntity(context, "holder");
        CommandSourceStack source = context.getSource();
        List<Entity> successful = new ArrayList<>(), failed = new ArrayList<>();
        for (Entity target : targets) {
            if(LeashDataInnerAPI.LeashOperations.attach(target, holder, maxDistance, elasticDistance, keepTicks, reserved)) {
                successful.add(target);
            } else failed.add(target);
        }
//        todo: source.sendSuccess(() -> Component.translatable(/*成功{}，失败{}*/), true);
        return successful.size();
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return addBlockLeash(context, CommonEventHandler.leashConfigManager.getMaxLeashLength(), CommonEventHandler.leashConfigManager.getElasticDistanceScale(), 0, "");
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance) throws CommandSyntaxException {
        return addBlockLeash(context, maxDistance, CommonEventHandler.leashConfigManager.getElasticDistanceScale(), 0, "");
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance, double elasticDistance, int keepTicks) throws CommandSyntaxException {
        return addBlockLeash(context, maxDistance, elasticDistance, keepTicks, "");
    }
    private static int addBlockLeash(CommandContext<CommandSourceStack> context,
                                     double maxDistance, double elasticDistance, int keepTicks, String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
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
//           todo: source.sendFailure(Component.translatable(/*失败，目标上无拴绳结*/));
            return -1;
        }
        List<Entity> successful = new ArrayList<>(), failed = new ArrayList<>();
        for (Entity target : targets) {
            if(LeashDataInnerAPI.LeashOperations.attach(target, knotEntity, maxDistance, elasticDistance, keepTicks, reserved)) {
                successful.add(target);
            } else failed.add(target);
        }
//        todo: source.sendSuccess(() -> Component.translatable(/*成功{}，失败{}*/), true);
        return successful.size();
    }

    private static int removeLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        Entity holder = EntityArgument.getEntity(context, "holder");
        CommandSourceStack source = context.getSource();
        int successCount = 0;
        /*
        Removed leash from %s[more than 4只显示前4个]  held by %s[如果有失败则加上, But no leash found form %s  on %s[more than 4 只显示前4个]]
         */
        for (Entity target : targets) {
            boolean success = LeashDataInnerAPI.LeashOperations.detach(target, holder);

            if (success) {
                successCount++;
                source.sendSuccess(() -> Component.literal("Removed leash from " + target.getName().getString() +
                        " held by " + holder.getName().getString()), false);
            } else {
                source.sendFailure(Component.literal("No leash found for " + holder.getName().getString() +
                        " on " + target.getName().getString()));
            }
        }

        return successCount;
    }

    private static int removeBlockLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        BlockPos pos = BlockPosArgument.getBlockPos(context, "pos");
        CommandSourceStack source = context.getSource();
        int successCount = 0;

        for (Entity target : targets) {
            boolean success = LeashDataInnerAPI.LeashOperations.detach(target, pos);

            if (success) {
                successCount++;
                source.sendSuccess(() -> Component.literal("Removed block leash from " + target.getName().getString() +
                        " at " + pos.toShortString()), false);
            } else {
                source.sendFailure(Component.literal("No block leash found at " + pos.toShortString() +
                        " on " + target.getName().getString()));
            }
        }

        return successCount;
    }

    private static int removeAllLeashes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        CommandSourceStack source = context.getSource();

        for (Entity target : targets) {
            LeashDataInnerAPI.LeashOperations.detachAll(target);
            source.sendSuccess(() -> Component.literal("Removed all leashes from " + target.getName().getString()), false);
        }

        return targets.size();
    }

    private static int transferLeash(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return transferLeash(context, "");
    }
    private static int transferLeash(CommandContext<CommandSourceStack> context, String reserved)
            throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        Entity from = EntityArgument.getEntity(context, "from");
        Entity to = EntityArgument.getEntity(context, "to");
        CommandSourceStack source = context.getSource();
        int successCount = 0;

        for (Entity target : targets) {
            boolean success = reserved.isEmpty() ?
                    LeashDataInnerAPI.TransferOperations.transfer(target, from, to) :
                    LeashDataInnerAPI.TransferOperations.transfer(target, from, to, reserved);

            if (success) {
                successCount++;
                source.sendSuccess(() -> Component.literal("Transferred leash from " + from.getName().getString() +
                        " to " + to.getName().getString() + " for " + target.getName().getString()), false);
            } else {
                source.sendFailure(Component.literal("Failed to transfer leash for " + target.getName().getString()));
            }
        }

        return successCount;
    }

    private static int applyForces(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(context, "target");
        CommandSourceStack source = context.getSource();

        for (Entity target : targets) {
            LeashDataInnerAPI.PhysicsOperations.applyForces(target);
            source.sendSuccess(() -> Component.literal("Applied leash forces to " + target.getName().getString()), false);
        }

        return targets.size();
    }
}