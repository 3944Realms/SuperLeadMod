/*
 *  Super Lead rope mod
 *  Copyright (C)  2025  R3944Realms
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR 阿 PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.r3944realms.superleadrope;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.api.SuperLeadRopeApi;
import top.r3944realms.superleadrope.api.event.SuperLeadRopeEvent;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashState;
import top.r3944realms.superleadrope.api.type.capabilty.LeashInfo;
import top.r3944realms.superleadrope.config.LeashCommonConfig;
import top.r3944realms.superleadrope.config.LeashConfigManager;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.CapabilityRemainder;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.content.capability.inter.IEternalPotato;
import top.r3944realms.superleadrope.content.command.LeashDataCommand;
import top.r3944realms.superleadrope.content.command.LeashStateCommand;
import top.r3944realms.superleadrope.content.command.MotionCommand;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.content.gamerule.server.TeleportWithLeashedEntities;
import top.r3944realms.superleadrope.content.item.EternalPotatoItem;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.leash.LeashInteractHandler;
import top.r3944realms.superleadrope.core.leash.LeashSyncManager;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.register.SLPGameruleRegistry;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.util.PotatoMode;
import top.r3944realms.superleadrope.core.util.PotatoModeHelper;
import top.r3944realms.superleadrope.datagen.data.SLPLangKeyValue;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;
import top.r3944realms.superleadrope.util.capability.LeashStateInnerAPI;
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

/**
 * The type Common event handler.
 */
public class CommonEventHandler {
    /**
     * The constant leashConfigManager.
     */
    public volatile static LeashConfigManager leashConfigManager;

    /**
     * The type Game.
     */
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class Game {
        /**
         * On entity join world.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide) return;
            if (LeashDataImpl.isLeashable(entity)) {
                LeashDataInnerAPI.getLeashData(entity).ifPresent(LeashSyncManager.Data::track);
                LeashStateInnerAPI.getLeashState(entity).ifPresent(LeashSyncManager.State::track);
                if (entity instanceof ServerPlayer serverPlayer) {
                    LeashSyncManager.Data.forEach(i -> {
                        if (i.isLeashedBy(serverPlayer) && i.isInDelayedLeash(serverPlayer.getUUID())) {
                            i.removeDelayedLeash(serverPlayer.getUUID());//重新加入去除延迟
                        }
                    });
                }
            }
        }

        /**
         * On entity leave world.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
            Entity entity = event.getEntity();
            if (entity.level().isClientSide) return;
            if (LeashDataImpl.isLeashable(entity)) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    LeashSyncManager.Data.forEach(i -> {
                        if(i.isLeashedBy(serverPlayer)) {
                            i.addDelayedLeash(serverPlayer); //添加延迟
                        }
                    });
                }
                LeashDataInnerAPI.getLeashData(entity).ifPresent(LeashSyncManager.Data::untrack);
                LeashStateInnerAPI.getLeashState(entity).ifPresent(LeashSyncManager.State::untrack);
            }
        }

        /**
         * On player logged in.
         *
         * @param event the event
         */
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

        /**
         * On player right hit on block.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onPlayerRightHitOnBlock(PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getLevel();
            BlockPos blockPos = event.getHitVec().getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);
            Player player = event.getEntity();
            ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (SuperLeashKnotEntity.isSupportBlock(blockState)) {
                boolean shouldConsume = SuperLeadRopeItem.bindToBlock(player, level, blockPos, event.getItemStack(), itemInHand.is(SLPItems.SUPER_LEAD_ROPE.get()));
                if (shouldConsume) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                }
            }
        }

        /**
         * Gets server level.
         *
         * @return the server level
         */
        public static ServerLevel getServerLevel() {
            return sl;
        }

        private static ServerLevel sl;

        /**
         * On server starting.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            PotatoMode mode = PotatoModeHelper.getCurrentMode();
            EternalPotatoFacade.init(mode, true); // 服务端
        }

        /**
         * On world load.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onWorldLoad(LevelEvent.Load event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                // 只在主世界挂载 SavedData
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    EternalPotatoFacade.initSavedData(serverLevel);
                    RidingSaver.setEntityProvider(serverLevel::getEntity);
                    sl = serverLevel;
                }
            }
        }

        /**
         * On world unload.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onWorldUnload(LevelEvent.Unload event) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                // 只在主世界卸载时清空
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    EternalPotatoFacade.clear();
                    sl = null;
                }
            }
        }

        /**
         * On server stopping.
         *
         * @param event the event
         */
// 服务器关闭
        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            EternalPotatoFacade.clear();
        }

        /**
         * On item drop.
         *
         * @param event the event
         */
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

        /**
         * On item pickup.
         *
         * @param event the event
         */
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

        /**
         * On entity teleport.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onEntityTeleport(EntityTeleportEvent event) {
            Entity telEntity = event.getEntity();
            Vec3 targetPos = event.getTarget();
            Level level = telEntity.level();

            if (!(level instanceof ServerLevel serverLevel)) return;

            // 获取范围内可被拴住实体
            List<Entity> entities = SuperLeadRopeApi.leashableInArea(telEntity);
            //规则关闭则禁止
            if(!SLPGameruleRegistry.getGameruleBoolValue(event.getEntity().level(), TeleportWithLeashedEntities.ID)) {
                entities.forEach(entity -> LeashDataInnerAPI.LeashOperations.detach(entity, telEntity));
                return;
            }

            for (Entity beLeashedEntity : entities) {
                // --- 保存状态快照 ---
                if (MinecraftForge.EVENT_BUS.post(new SuperLeadRopeEvent.teleportWithHolder(beLeashedEntity, telEntity, beLeashedEntity.level(), level, beLeashedEntity.position(), targetPos))) continue;
                Pose originalPose = beLeashedEntity.getPose();
                boolean originalIsSprinting = beLeashedEntity.isSprinting();
                float originalYaw = beLeashedEntity.getYRot();
                float originalPitch = beLeashedEntity.getXRot();
                Vec3 originalDeltaMovement = beLeashedEntity.getDeltaMovement();

                AtomicReference<LeashInfo> originalLeashInfo = new AtomicReference<>();
                LeashDataInnerAPI.getLeashData(beLeashedEntity).ifPresent(data -> {
                    originalLeashInfo.set(data.getLeashInfo(telEntity).orElse(null));
                    data.removeLeash(telEntity);
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

                // --- 将holder替换 ---
                LeashInfo leashInfo = Optional.ofNullable(originalLeashInfo.get())
                        .orElse(LeashInfo.EMPTY);

                LeashDataInnerAPI.LeashOperations.attachWithInfo(beLeashedEntity, telEntity, leashInfo);

                // --- 重新应用骑乘关系，仅保留白名单根载具 ---
                RidingRelationship filteredRelationship = RidingSaver.filterByWhitelistRoot(originalRidingRelationship);
                RidingApplier.applyRidingRelationship(filteredRelationship, serverLevel::getEntity);
            }
        }

        /**
         * On player clone.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            CapabilityRemainder.onPlayerClone(event);
        }
        private static int tickCounter = 0;

        /**
         * On server tick.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCounter++;

                // 每10 tick标记为脏（needsSync）
                if (tickCounter % 10 == 0) {
                    LeashSyncManager.Data.forEach(ILeashData::markForSync);
                    LeashSyncManager.State.forEach(ILeashState::markForSync);
                    leashConfigManager.broadHashPacket();
                }

                // 定期同步检查
                LeashSyncManager.Data.forEach(ILeashData::checkSync);
                LeashSyncManager.State.forEach(ILeashState::checkSync);

                // 应用物理拉力/效果
                LeashSyncManager.Data.forEach(ILeashData::applyLeashForces);
            }
        }

        /**
         * On entity attack.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onEntityAttack (AttackEntityEvent event) {
            LeashInteractHandler.onEntityLeftInteract(event.getEntity().level(), event.getTarget(), event.getEntity(), event);
        }

        /**
         * On entity interact.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onEntityInteract (PlayerInteractEvent.EntityInteract event) {
            LeashInteractHandler.onEntityRightInteract(event.getLevel(), event.getHand(), event.getTarget(), event.getEntity(), event); //处理实体互动
        }

        /**
         * Attach capability.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void attachCapability(AttachCapabilitiesEvent<?> event) {
            CapabilityHandler.attachCapability(event);
        }

        /**
         * On register command.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onRegisterCommand (RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            MotionCommand.register(dispatcher);
            LeashDataCommand.register(dispatcher);
            LeashStateCommand.register(dispatcher);
        }
    }

    /**
     * The type Mod.
     */
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, bus= net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class Mod {
        /**
         * On fml common init.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onFMLCommonInit(FMLCommonSetupEvent event) {
            event.enqueueWork(Mod::checkAndSet);
            event.enqueueWork(SLPGameruleRegistry::register);
        }

        /**
         * Register capability.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void registerCapability(RegisterCapabilitiesEvent event) {
            CapabilityHandler.registerCapability(event);
        }

        /**
         * On creative tab.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onCreativeTab (BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(SLPItems.SUPER_LEAD_ROPE);
            }
        }

        /**
         * On config reloading.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onConfigReloading(final ModConfigEvent.Reloading event) {
            if (event.getConfig().getModId().equals(SuperLeadRope.MOD_ID)) {
                SuperLeadRope.logger.debug("Config reloading detected...");
                Optional.ofNullable(leashConfigManager).ifPresent(LeashConfigManager::reloadAll);
            }
        }
        private static void checkAndSet() {
            if (leashConfigManager == null) {
                synchronized (LeashConfigManager.class) {
                    if (leashConfigManager == null) {
                        leashConfigManager = new LeashConfigManager();
                    }
                }
            }
        }

        /**
         * On config loaded.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onConfigLoaded(ModConfigEvent.Loading event) {
            if (event.getConfig().getSpec() == LeashCommonConfig.SPEC) {
                checkAndSet();
                LeashConfigManager.loading(leashConfigManager);
            }
        }

        /**
         * On config reloaded.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onConfigReloaded(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == LeashCommonConfig.SPEC) {
                checkAndSet();
                LeashConfigManager.reloading(leashConfigManager);
            }
        }
        // 忘记订阅事件是静态方法了xwx
        /**
         * On config unloaded.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onConfigUnloaded(ModConfigEvent.Unloading event) {
            if (event.getConfig().getSpec() == LeashCommonConfig.SPEC) {
                LeashConfigManager.unloading(leashConfigManager);
            }
        }
    }
}