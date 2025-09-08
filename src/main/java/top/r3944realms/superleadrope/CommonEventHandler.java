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
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.CapabilityRemainder;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.leash.LeashInteractHandler;
import top.r3944realms.superleadrope.core.leash.LeashSyncManager;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.util.PotatoMode;
import top.r3944realms.superleadrope.core.util.PotatoModeHelper;
import top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue;
import top.r3944realms.superleadrope.util.model.RidingRelationship;
import top.r3944realms.superleadrope.util.riding.RidingApplier;
import top.r3944realms.superleadrope.util.riding.RidingDismounts;
import top.r3944realms.superleadrope.util.riding.RidingFinder;
import top.r3944realms.superleadrope.util.riding.RidingSaver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player)) return; // 只处理服务端

            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof EternalPotatoItem) {
                    stack.getCapability(CapabilityHandler.ETERNAL_POTATO_CAP).ifPresent(cap -> {
                        // 物品已绑定主人时，恢复服务端上下文
                        if (cap.getOwnerUUID() != null && cap.getOwnerUUID().equals(player.getUUID())) {
                            cap.bindSyncContext(player);        // 只绑定服务端玩家
                            cap.syncToClient(player);          // 由服务端发包给客户端
                        }
                    });
                }
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
        public static void onServerStarting(ServerStartingEvent event) {
            PotatoMode mode = PotatoModeHelper.getCurrentMode();
            EternalPotatoFacade.init(mode, true); // 服务端
        }
        @SubscribeEvent
        public static void onWorldLoad(LevelEvent.Load event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                // 只在主世界挂载 SavedData
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    EternalPotatoFacade.initSavedData(serverLevel);
                    RidingSaver.setEntityProvider(serverLevel::getEntity);
                }
            }
        }

        @SubscribeEvent
        public static void onWorldUnload(LevelEvent.Unload event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                // 只在主世界卸载时清空
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    EternalPotatoFacade.clear();
                }
            }
        }

        // 服务器关闭
        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            EternalPotatoFacade.clear();
        }
        @SubscribeEvent
        public static void onItemDrop(ItemTossEvent event) {
            Player player = event.getPlayer();
            ItemEntity entityItem = event.getEntity();
            ItemStack stack = entityItem.getItem();

            if (!(stack.getItem() instanceof EternalPotatoItem)) return;
            if (player.level().isClientSide) return; // 只处理服务端

            UUID uuid = EternalPotatoItem.getOrCreateItemUUID(stack);
            IEternalPotato cap = EternalPotatoFacade.getOrCreate(uuid);

            if (player instanceof ServerPlayer serverPlayer) {
                cap.bindSyncContext(serverPlayer); // 服务端绑定
            }

            EternalPotatoItem.ensureItemInInventory(player, stack);

            // 移除地面实体
            BlockPos spawnPos = entityItem.level().getSharedSpawnPos();
            entityItem.setPos(spawnPos.getX(), -394, spawnPos.getZ());

            // 处罚逻辑
            if (cap.getOwnerUUID() != null
                    && cap.getOwnerUUID().equals(player.getUUID())
                    && !player.getAbilities().instabuild) {
                cap.beginInit();
                cap.setPendingPunishments(cap.getPendingPunishments() + 2);
                cap.endInit();
                player.sendSystemMessage(
                        net.minecraft.network.chat.Component.translatable(SLPLangKeyValue.EP_CANNOT_DROP.getKey(), 2)
                );
            }
        }
        @SubscribeEvent
        public static void onItemPickup(@NotNull PlayerEvent.ItemPickupEvent event) {
            Player player = event.getEntity();
            ItemStack stack = event.getStack();

            if (player.level().isClientSide) return;       // 客户端不处理
            if (!(stack.getItem() instanceof EternalPotatoItem)) return;
            if (player.getAbilities().instabuild) return;

            UUID uuid = EternalPotatoItem.getOrCreateItemUUID(stack);
            IEternalPotato cap = EternalPotatoFacade.getOrCreate(uuid);

            if (player instanceof ServerPlayer serverPlayer) {
                cap.bindSyncContext(serverPlayer);         // 服务端绑定
            }

            // 如果玩家不是主人，显示提示
            if (cap.getOwnerUUID() != null && !cap.getOwnerUUID().equals(player.getUUID())) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable(SLPLangKeyValue.EP_PICKUP_NOT_OWNER.getKey()),
                        true
                );
            }
        }

        @SubscribeEvent
        public static void onEntityTeleport(EntityTeleportEvent event) {
            Entity telEntity = event.getEntity();
            Vec3 targetPos = event.getTarget();
            Level level = telEntity.level();

            if (!(level instanceof ServerLevel serverLevel)) return;

            // 获取范围内可被拴住实体
            List<Entity> entities = LeashDataImpl.leashableInArea(telEntity);

            for (Entity beLeashedEntity : entities) {
                // --- 保存状态快照 ---
                Pose originalPose = beLeashedEntity.getPose();
                boolean originalIsSprinting = beLeashedEntity.isSprinting();
                float originalYaw = beLeashedEntity.getYRot();
                float originalPitch = beLeashedEntity.getXRot();
                Vec3 originalDeltaMovement = beLeashedEntity.getDeltaMovement();

                AtomicReference<ILeashDataCapability.LeashInfo> originalLeashInfo = new AtomicReference<>();
                beLeashedEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(cap -> {
                    originalLeashInfo.set(cap.getLeashInfo(telEntity).orElse(null));
                    cap.removeLeash(telEntity);
                });

                // --- 保存骑乘关系（可修改列表） ---
                RidingRelationship originalRidingRelationship = RidingSaver.save(beLeashedEntity, true);

                // --- 解除骑乘 ---
                List<Entity> allPassengers = RidingFinder.getEntityFromRidingShip(originalRidingRelationship, serverLevel::getEntity);
                RidingDismounts.dismountEntities(allPassengers);

                // --- 传送实体及乘客 ---
                for (Entity entity : allPassengers) {
                    if (entity.level() != serverLevel) {
                        entity.teleportTo(serverLevel, targetPos.x, targetPos.y, targetPos.z,
                                Collections.emptySet(), originalYaw, originalPitch);
                    } else if (entity instanceof ServerPlayer player) {
                        player.connection.teleport(targetPos.x, targetPos.y, targetPos.z,
                                originalYaw, originalPitch, Collections.emptySet());
                    } else {
                        entity.teleportTo(serverLevel, targetPos.x, targetPos.y, targetPos.z,
                                Collections.emptySet(), originalYaw, originalPitch);
                    }
                }

                // --- 恢复状态 ---
                for (Entity entity : allPassengers) {
                    entity.setDeltaMovement(originalDeltaMovement);
                    entity.setSprinting(originalIsSprinting);
                    entity.setPose(originalPose);
                }

                // --- 恢复拴绳 ---
                ILeashDataCapability.LeashInfo leashInfo = Optional.ofNullable(originalLeashInfo.get())
                        .map(info -> info.transferHolder(telEntity))
                        .orElse(ILeashDataCapability.LeashInfo.EMPTY);

                beLeashedEntity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(
                        cap -> cap.addLeash(telEntity, leashInfo)
                );

                // --- 重新应用骑乘关系，仅保留白名单根载具 ---
                RidingRelationship filteredRelationship = RidingSaver.filterByWhitelistRoot(originalRidingRelationship);
                RidingApplier.applyRidingRelationship(filteredRelationship, serverLevel::getEntity);
            }
        }
        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            CapabilityRemainder.onPlayerClone(event);
        }
        private static int tickCounter = 0;
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCounter++;

                // 每10 tick标记为脏（needsSync）
                if (tickCounter % 10 == 0) {
                    LeashSyncManager.forEach(ILeashDataCapability::markForSync);
                }

                // 定期同步检查
                LeashSyncManager.forEach(ILeashDataCapability::checkSync);

                // 应用物理拉力/效果
                LeashSyncManager.forEach(ILeashDataCapability::applyLeashForces);
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
