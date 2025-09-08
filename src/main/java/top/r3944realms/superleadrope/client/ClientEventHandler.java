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

package top.r3944realms.superleadrope.client;


import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.model.SuperLeashKnotModel;
import top.r3944realms.superleadrope.client.model.geom.SLPModelLayers;
import top.r3944realms.superleadrope.client.renderer.entity.SuperLeashKnotRenderer;
import top.r3944realms.superleadrope.core.potato.EternalPotatoFacade;
import top.r3944realms.superleadrope.core.register.SLPEntityTypes;
import top.r3944realms.superleadrope.core.register.SLPItems;
import top.r3944realms.superleadrope.core.util.PotatoMode;

import static top.r3944realms.superleadrope.core.util.PotatoModeHelper.getCurrentMode;

@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
    public static class Game {
        // 未使用-注释
        @SubscribeEvent
        public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            // 清理客户端缓存数据
            EternalPotatoFacade.clear();
        }
    }
    @net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, value = Dist.CLIENT, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
    public static class Mod {
        @SubscribeEvent
        public static void onClientInitializer (FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(
                        SLPItems.SUPER_LEAD_ROPE.get(),  new ResourceLocation(SuperLeadRope.MOD_ID, "broken"),
                        (itemStack, clientLevel, livingEntity, i) -> {
                            if (!itemStack.isDamageableItem()) return 0.0F;

                            return itemStack.getDamageValue() > 1024 - 50 ? 1.0F : 0.0F; // 损坏 → 返回 1.0（触发 override）
                        }
                );
                PotatoMode mode = getCurrentMode();
                EternalPotatoFacade.init(mode, false); // 客户端
            });
        }
        @SubscribeEvent
        public static void onRegisterModelLayer (EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(SLPModelLayers.SUPER_LEASH_KNOT, SuperLeashKnotModel::createBodyLayer);
        }
        @SubscribeEvent
        public static void onRegisterRenderer (EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(SLPEntityTypes.SUPER_LEAD_KNOT.get(), SuperLeashKnotRenderer::new);
        }
    }
}
