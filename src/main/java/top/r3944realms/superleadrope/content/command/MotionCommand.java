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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.network.NetworkHandler;
import top.r3944realms.superleadrope.network.toClient.UpdatePlayerMovementPacket;

import java.util.ArrayList;
import java.util.List;

import static top.r3944realms.superleadrope.content.command.Command.*;


/**
 * The type Motion command.
 */
public class MotionCommand {
    private final static String SLP_MOTION_MESSAGE_ = SuperLeadRope.MOD_ID + ".command.motion.message.";
    /**
     * The constant MOTION_SETTER_SUCCESSFUL.
     */
    public final static String MOTION_SETTER_SUCCESSFUL = SLP_MOTION_MESSAGE_ + "setter.successful",
    /**
     * The Motion adder successful.
     */
    MOTION_ADDER_SUCCESSFUL = SLP_MOTION_MESSAGE_ + "adder.successful",
    /**
     * The Motion multiply successful.
     */
    MOTION_MULTIPLY_SUCCESSFUL = SLP_MOTION_MESSAGE_ + "multiply.successful";

    /**
     * Register.
     *
     * @param dispatcher the dispatcher
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        @Nullable List<LiteralArgumentBuilder<CommandSourceStack>> nodeList = SHOULD_USE_PREFIX ? null : new ArrayList<>();
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal(PREFIX);
        LiteralArgumentBuilder<CommandSourceStack> $$motionRoot = getLiterArgumentBuilderOfCSS("motion", !SHOULD_USE_PREFIX, nodeList);
        com.mojang.brigadier.Command<CommandSourceStack> motionVecAdder = context -> {
            CommandSourceStack source = context.getSource();
            for(Entity entity : EntityArgument.getEntities(context, "targets")){
                Vec3 motionVec = new Vec3(
                        DoubleArgumentType.getDouble(context, "vecX"),
                        DoubleArgumentType.getDouble(context, "vecY"),
                        DoubleArgumentType.getDouble(context, "vecZ")
                );
                boolean flag = entity instanceof ServerPlayer;
                if(entity instanceof ServerPlayer player) {
                    NetworkHandler.sendToPlayer(new UpdatePlayerMovementPacket(UpdatePlayerMovementPacket.Operation.ADD, motionVec.x, motionVec.y, motionVec.z), player);
                } else {
                    entity.addDeltaMovement(motionVec);
                }
                Vec3 deltaMovement = entity.getDeltaMovement();
                double vecX = deltaMovement.x, vecY = deltaMovement.y, vecZ = deltaMovement.z;
                source.sendSuccess(() ->
                        Component.translatable(
                                MOTION_ADDER_SUCCESSFUL,
                                entity.getDisplayName(),
                                flag ? vecX + motionVec.x : vecX,
                                flag ? vecY + motionVec.y : vecY,
                                flag ? vecZ + motionVec.z : vecZ
                        ), true
                );
            }
            return 0;
        };
        Command<CommandSourceStack> motionVecSetter = context -> {
            CommandSourceStack source = context.getSource();
            for(Entity entity : EntityArgument.getEntities(context, "targets")){
                Vec3 motionVec = new Vec3(
                        DoubleArgumentType.getDouble(context, "vecX"),
                        DoubleArgumentType.getDouble(context, "vecY"),
                        DoubleArgumentType.getDouble(context, "vecZ")
                );
                boolean flag = entity instanceof ServerPlayer;
                if(entity instanceof ServerPlayer player) {
                    NetworkHandler.sendToPlayer(new UpdatePlayerMovementPacket(UpdatePlayerMovementPacket.Operation.SET, motionVec.x, motionVec.y, motionVec.z), player);
                } else {
                    entity.setDeltaMovement(motionVec);
                }
                double vecX = entity.getDeltaMovement().x, vecY = entity.getDeltaMovement().y, vecZ = entity.getDeltaMovement().z;
                source.sendSuccess(() ->
                        Component.translatable(
                            MOTION_SETTER_SUCCESSFUL,
                            entity.getDisplayName(),
                            flag ? motionVec.x : vecX,
                            flag ? motionVec.y : vecY,
                            flag ? motionVec.z : vecZ
                        ), true
                );
            }
            return 0;
        };
        Command<CommandSourceStack> motionVecMultiply = context -> {
            CommandSourceStack source = context.getSource();
            for(Entity entity : EntityArgument.getEntities(context, "targets")){
                Vec3 motionFactorVec = new Vec3(
                        DoubleArgumentType.getDouble(context, "vecXFactor"),
                        DoubleArgumentType.getDouble(context, "vecYFactor"),
                        DoubleArgumentType.getDouble(context, "vecZFactor")
                );
                boolean flag = entity instanceof ServerPlayer;
                Vec3 deltaMovement = entity.getDeltaMovement();
                if(entity instanceof ServerPlayer player) {
                    NetworkHandler.sendToPlayer(new UpdatePlayerMovementPacket(UpdatePlayerMovementPacket.Operation.MULTIPLY, motionFactorVec.x, motionFactorVec.y, motionFactorVec.z), player);
                } else {
                    entity.setDeltaMovement(deltaMovement.multiply(motionFactorVec));
                }
                double vecX = deltaMovement.x, vecY = deltaMovement.y, vecZ = deltaMovement.z;
                source.sendSuccess(() ->
                        Component.translatable(
                                MOTION_MULTIPLY_SUCCESSFUL,
                                entity.getDisplayName(),
                                flag ? vecX * motionFactorVec.x : vecX,
                                flag ? vecY * motionFactorVec.y : vecY,
                                flag ? vecZ * motionFactorVec.z : vecZ
                        ), true
                );
            }
            return 0;
        };

        LiteralArgumentBuilder<CommandSourceStack> Motion = $$motionRoot.requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.literal("add")
                                .then(Commands.argument("vecX", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("vecY", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("vecZ", DoubleArgumentType.doubleArg())
                                                        .executes(motionVecAdder)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("vecX", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("vecY", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("vecZ", DoubleArgumentType.doubleArg())
                                                        .executes(motionVecSetter)
                                                )
                                        )
                                )
                        )
                        .then(Commands.literal("multiply")
                                .then(Commands.argument("vecXFactor", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("vecYFactor", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("vecZFactor", DoubleArgumentType.doubleArg())
                                                        .executes(motionVecMultiply)
                                                )
                                        )
                                )
                        )
                );
        if(SHOULD_USE_PREFIX){
            literalArgumentBuilder.then(Motion);
            dispatcher.register(literalArgumentBuilder);
        } else {
            if (nodeList != null) {
                nodeList.forEach(dispatcher::register);
            }
        }
    }
}

