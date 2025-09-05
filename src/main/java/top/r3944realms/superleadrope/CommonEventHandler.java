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

package top.r3944realms.superleadrope;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.CapabilityRemainder;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashEntity;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.leash.LeashInteractHandler;
import top.r3944realms.superleadrope.core.leash.LeashSyncManager;
import top.r3944realms.superleadrope.core.register.SLPItems;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommonEventHandler {
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class Game {
        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide) return;
            if (entity instanceof LivingEntity || entity instanceof Boat || entity instanceof Minecart) {
                entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(LeashSyncManager::track);
                entity.level().addFreshEntity(new SuperLeashEntity(entity.level(), entity));
            }
        }

        @SubscribeEvent
        public static void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide) return;
            if (entity instanceof LivingEntity || entity instanceof Boat || entity instanceof Minecart) {
                entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(LeashSyncManager::untrack);
            }
        }
        @SubscribeEvent
        public static void onPlayerRightHitOnBlock(PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getLevel();
            if (level.isClientSide) {
                return;
            }
            BlockPos blockPos = event.getHitVec().getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);
            Player player = event.getEntity();
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (SuperLeashKnotEntity.isSupportBlock(blockState)) {
                boolean shouldConsume = SuperLeadRopeItem.bindToBlock(player, level, blockPos, event.getItemStack(), itemInHand.is(SLPItems.SUPER_LEAD_ROPE.get()));
                if (shouldConsume) {
                    event.setCancellationResult(InteractionResult.CONSUME);
                    event.setCanceled(true);
                }
            }
        }
        @SubscribeEvent
        public static void onEntityTeleport(EntityTeleportEvent event) {
            Entity telEntity = event.getEntity();
            Vec3 targetPos = event.getTarget();
            Level level = telEntity.level();
            if (level instanceof ServerLevel serverLevel) {
                List<Entity> entities = LeashDataImpl.leashableInArea(telEntity);
                // 为每个实体创建状态快照
                entities.forEach(beLeashedEntity -> {
                    // 保存原来的旋转角度

                    Pose originalPose = beLeashedEntity.getPose();
                    boolean originalIsSprinting = beLeashedEntity.isSprinting();
                    float originalYaw = beLeashedEntity.getYRot();
                    float originalPitch = beLeashedEntity.getXRot();
                    Vec3 originalDeltaMovement = beLeashedEntity.getDeltaMovement();
                    AtomicReference<ILeashDataCapability.LeashInfo> originalLeashInfo = new AtomicReference<>();
                    beLeashedEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP)
                            .ifPresent(cap -> {
                                originalLeashInfo.set(cap.getLeashInfo(telEntity).orElse(null));
                                cap.removeLeash(telEntity);
                            });

                    if (beLeashedEntity.level() == telEntity.level()) {
                        // 使用空集合表示所有值都是绝对的
                        if (beLeashedEntity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.teleport(
                                    targetPos.x, targetPos.y, targetPos.z,
                                    originalYaw, originalPitch,
                                    Collections.emptySet() // 所有值都是绝对的
                            );
                        } else {
                            beLeashedEntity.teleportTo(
                                    serverLevel,
                                    targetPos.x,
                                    targetPos.y,
                                    targetPos.z,
                                    Collections.emptySet(),
                                    originalYaw, originalPitch
                            );
                        }
                    } else {
                        beLeashedEntity.teleportTo(
                                serverLevel,
                                targetPos.x,
                                targetPos.y,
                                targetPos.z,
                                Collections.emptySet(),
                                originalYaw, originalPitch
                        );
                    }
                    beLeashedEntity.setDeltaMovement(originalDeltaMovement);
                    beLeashedEntity.setSprinting(originalIsSprinting);
                    beLeashedEntity.setPose(originalPose);
                    ILeashDataCapability.LeashInfo leashInfoOrDefault = Optional.ofNullable(originalLeashInfo.get()).map(i -> i.transferHolder(telEntity)).orElse(ILeashDataCapability.LeashInfo.EMPTY);
                    beLeashedEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(
                            cap -> cap.addLeash(telEntity, leashInfoOrDefault)
                    );
                });
            }
        }
        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            CapabilityRemainder.onPlayerClone(event);
        }

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                LeashSyncManager.forEach((i -> ((LeashDataImpl) i).checkSync()));
            }
        }

        @SubscribeEvent
        public static void onEntityInteract (PlayerInteractEvent.EntityInteract event) {
            LeashInteractHandler.onEntityInteract(event.getLevel(), event.getHand(), event.getTarget(), event.getEntity(), event); //处理实体互动
        }

        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<?> event) {
            CapabilityHandler.attachCapability(event);
        }
    }
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, bus= net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void onCommonInit (FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
            });
        }
        @SubscribeEvent
        public static void registerCapability(RegisterCapabilitiesEvent event) {
            CapabilityHandler.registerCapability(event);
        }
        @SubscribeEvent
        public static void onCreativeTab (BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(SLPItems.SUPER_LEAD_ROPE);
            }
        }

    }
}
