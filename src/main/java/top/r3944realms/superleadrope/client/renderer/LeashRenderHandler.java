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

package top.r3944realms.superleadrope.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.renderer.resolver.SuperLeashStateResolver;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

import java.util.Optional;
import java.util.UUID;

public class LeashRenderHandler {
    public static void onRenderLevelStage(PoseStack poseStack, float partialTick) {
        renderAllCustomLeashes(poseStack, partialTick);
    }

    private static void renderAllCustomLeashes(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        Entity cameraEntity = minecraft.getCameraEntity();
        if (level == null || cameraEntity == null) return;

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // 遍历摄像机附近所有实体
        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                cameraEntity.getBoundingBox().inflate(50))) {

            entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(leashData -> {
                if(leashData instanceof ILeashDataCapability) {}
                for (ILeashDataCapability.LeashInfo leashInfo : leashData.getAllLeashes()) {
                    renderLeashFromInfo(entity, leashInfo, poseStack, bufferSource, partialTick);
                }
            });
        }
    }

    private static void renderLeashFromInfo(Entity entity, ILeashDataCapability.LeashInfo leashInfo,
                                            PoseStack poseStack, MultiBufferSource bufferSource,
                                            float partialTick) {
        try {
            Optional<Entity> holderOpt = getHolderFromLeashInfo(entity.level(), leashInfo);
            if (holderOpt.isEmpty()) return;

            Entity holder = holderOpt.get();

            // 构建渲染状态
            SuperLeashStateResolver.resolve(entity, holder, leashInfo, partialTick).ifPresent(
                    leashRenderState -> SuperLeashRenderer.renderLeash(leashRenderState, poseStack, bufferSource)
            );


        } catch (Exception e) {
            SuperLeadRope.logger.error("Failed to render leash", e);
        }
    }

    private static Optional<Entity> getHolderFromLeashInfo(Level level, ILeashDataCapability.LeashInfo leashInfo) {
        if (leashInfo.blockPosOpt().isPresent()) {
            BlockPos pos = leashInfo.blockPosOpt().get();
            return Optional.of(SuperLeashKnotEntity.getOrCreateKnot(level, pos));
        } else if (leashInfo.holderUUIDOpt().isPresent()) {
            UUID holderUUID = leashInfo.holderUUIDOpt().get();
            for (Entity e : ((ClientLevel)level).entitiesForRendering()) {
                if (e.getUUID().equals(holderUUID)) return Optional.of(e);
            }
        }
        return Optional.empty();
    }
}