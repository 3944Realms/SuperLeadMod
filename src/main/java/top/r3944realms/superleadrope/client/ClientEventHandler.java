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

package top.r3944realms.superleadrope.client;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.api.type.capabilty.ILeashData;
import top.r3944realms.superleadrope.client.model.SuperLeashKnotModel;
import top.r3944realms.superleadrope.client.model.geom.SLPModelLayers;
import top.r3944realms.superleadrope.client.renderer.LeashRenderHandler;
import top.r3944realms.superleadrope.client.renderer.SLPShaderRegistry;
import top.r3944realms.superleadrope.client.renderer.entity.SuperLeashKnotRenderer;
import top.r3944realms.superleadrope.content.capability.impi.LeashDataImpl;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.util.PotatoMode;
import top.r3944realms.superleadrope.util.capability.LeashDataInnerAPI;

import java.io.IOException;

import static top.r3944realms.superleadrope.core.util.PotatoModeHelper.getCurrentMode;

/**
 * The type Client event handler.
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    /**
     * The type Game.
     */
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class Game {
        /**
         * On level renderer.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onLevelRenderer (RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                return;
            }
            LeashRenderHandler.onRenderLevelStage(event.getPoseStack(), event.getPartialTick());
        }
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player instanceof LocalPlayer player && player.equals(Minecraft.getInstance().player)) {
                LeashDataInnerAPI
                        .getLeashData(player)
                        .ifPresent(ILeashData::applyLeashForcesClientPlayer);
            }
        }

        /**
         * On player logged out.
         *
         * @param event the event
         */
// 未使用-注释
        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            // 清理客户端缓存数据
            EternalPotatoFacade.clear();
        }
    }

    /**
     * The type Mod.
     */
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class Mod {
        /**
         * On client initializer.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onClientInitializer (FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(
                        SLPItems.SUPER_LEAD_ROPE.get(),  new ResourceLocation(SuperLeadRope.MOD_ID, "broken"),
                        (itemStack, clientLevel, livingEntity, i) -> {
                            if (!itemStack.isDamageableItem()) return 0.0F;

                            return itemStack.getDamageValue() > 1024 - 33 ? 1.0F : 0.0F; // 损坏 → 返回 1.0（触发 override）
                        }
                );
                PotatoMode mode = getCurrentMode();
                EternalPotatoFacade.init(mode, false); // 客户端
            });
        }

        /**
         * On register model layer.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onRegisterModelLayer (EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(SLPModelLayers.SUPER_LEASH_KNOT, SuperLeashKnotModel::createBodyLayer);
        }

        /**
         * On register renderer.
         *
         * @param event the event
         */
        @SubscribeEvent
        public static void onRegisterRenderer (EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SLPEntityTypes.SUPER_LEAD_KNOT.get(), SuperLeashKnotRenderer::new);
        }

        /**
         * On register shaders.
         *
         * @param event the event
         * @throws IOException the io exception
         */
        @SubscribeEvent
        public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
            SLPShaderRegistry.registerShaders(event);
        }

    }
}
