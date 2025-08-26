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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.CapabilityRemainder;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.content.entity.SuperLeashEntity;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;
import top.r3944realms.superleadrope.content.item.SuperLeadRopeItem;
import top.r3944realms.superleadrope.core.leash.LeashInteractHandler;
import top.r3944realms.superleadrope.core.leash.LeashSyncManager;

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
        public static void onPlayerHitOnBlock (PlayerInteractEvent.RightClickBlock event) {
            Level level = event.getLevel();
            if (level.isClientSide) {
                return ;
            }
            BlockPos blockPos = event.getHitVec().getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);
            if (SuperLeashKnotEntity.isSupportBlock(blockState)) {
                SuperLeadRopeItem.bindToBlock(event.getEntity(), level, blockPos, event.getItemStack());
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
            LeashInteractHandler.onEntityInteract(event); //处理实体互动
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

    }
}
