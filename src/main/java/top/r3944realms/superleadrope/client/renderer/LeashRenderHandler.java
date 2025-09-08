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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.renderer.resolver.SuperLeashStateResolver;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.inter.ILeashDataCapability;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

import java.util.Optional;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SuperLeadRope.MOD_ID, value = Dist.CLIENT)
public class LeashRenderHandler {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        renderAllCustomLeashes(event.getPoseStack(), event.getPartialTick());
    }

    private static void renderAllCustomLeashes(PoseStack poseStack, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        Entity cameraEntity = minecraft.getCameraEntity();

        if (level == null || cameraEntity == null) return;

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                cameraEntity.getBoundingBox().inflate(50))) {

            entity.getCapability(CapabilityHandler.LEASH_DATA_CAP).ifPresent(leashData -> {
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
            Optional<Entity> holderOpt = getHolderFromLeashInfo((ClientLevel) entity.level(), leashInfo);
            if (holderOpt.isEmpty()) return;

            Entity holder = holderOpt.get();

            Optional<SuperLeashRenderState> stateOpt = SuperLeashStateResolver.resolve(
                    holder, entity, leashInfo, partialTick
            );

            stateOpt.ifPresent(state -> {
                // ✅ 每个渲染都创建自己的 PoseStack，避免污染全局
                PoseStack localStack = new PoseStack();
                localStack.mulPoseMatrix(poseStack.last().pose());

                renderSingleLeash(state, localStack, bufferSource, partialTick);
            });

        } catch (Exception e) {
            SuperLeadRope.logger.error("Error rendering leash: {}", e.getMessage());
        }
    }

    private static Optional<Entity> getHolderFromLeashInfo(ClientLevel level, ILeashDataCapability.LeashInfo leashInfo) {
        // 根据LeashInfo类型获取持有者
        if (leashInfo.blockPosOpt().isPresent()) {
            // 拴绳结持有者
            BlockPos pos = leashInfo.blockPosOpt().get();
            SuperLeashKnotEntity knot = SuperLeashKnotEntity.getOrCreateKnot(level, pos);
            return Optional.of(knot);
        } else if (leashInfo.holderUUIDOpt().isPresent()) {
            // 实体持有者
            UUID holderUUID = leashInfo.holderUUIDOpt().get();
            // 在客户端，我们需要通过ID查找实体
            if (leashInfo.holderIdOpt().isPresent()) {
                int holderId = leashInfo.holderIdOpt().get();
                Entity holder = level.getEntity(holderId);
                if (holder != null) {
                    return Optional.of(holder);
                }
            }
            // 备用方案：通过UUID查找（可能效率较低）
            for (Entity entity : level.entitiesForRendering()) {
                if (entity.getUUID().equals(holderUUID)) {
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    private static void renderSingleLeash(SuperLeashRenderState state, PoseStack poseStack,
                                          MultiBufferSource bufferSource, float partialTick) {
        poseStack.pushPose();
        try {
            // 相机位置平移
            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // 获取缓冲区
            VertexConsumer consumer = bufferSource.getBuffer(SLPRenderType.leashType());

            // 绘制拴绳
            renderLeashAsTriangleStrip(state, consumer, poseStack, partialTick);

        } finally {
            // ✅ 保证无论发生什么都会弹栈
            poseStack.popPose();
        }
    }

    private static void renderLeashAsTriangleStrip(SuperLeashRenderState state,
                                                   VertexConsumer consumer,
                                                   PoseStack poseStack,
                                                   float partialTick) {
        Vec3 start = state.startPos();
        Vec3 end = state.endPos();
        double length = start.distanceTo(end);

        if (length < 0.001) return;

        Vec3 direction = end.subtract(start).normalize();

        // 计算垂直向量用于厚度
        Vec3 perpendicular = calculatePerpendicularVector(direction);

        Matrix4f pose = poseStack.last().pose();
        int color = state.color();
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = state.isCritical() ? 0.8f : 1.0f;

        // 分段渲染（使用三角形带）
        final int SEGMENTS = 12;
        for (int i = 0; i <= SEGMENTS; i++) {
            float progress = (float) i / SEGMENTS;

            // 计算当前段中心位置（包含摆动效果）
            Vec3 center = start.add(direction.scale(length * progress));
            Vec3 swingOffset = state.getSwingOffset(progress, partialTick);
            center = center.add(swingOffset);

            // 应用张力拉伸效果
            if (state.stretchRatio() > 1.0f) {
                float stretchFactor = (float)Math.sin(progress * Math.PI) * 0.2f * (state.stretchRatio() - 1.0f);
                center = center.add(direction.scale(stretchFactor * length));
            }

            // 计算当前厚度
            float currentThickness = state.thickness() * (1 - progress * 0.3f);

            // 添加两个顶点形成带子
            Vec3 top = center.add(perpendicular.scale(currentThickness));
            Vec3 bottom = center.subtract(perpendicular.scale(currentThickness));

            consumer.vertex(pose, (float)top.x, (float)top.y, (float)top.z)
                    .color(r, g, b, a)
                    .uv(0.0F, 0.0F)             // 固定UV，随便填
                    .uv2(0xF000F0)              // 光照，全亮
                    .normal(0.0F, 1.0F, 0.0F)   // 法线，朝上
                    .endVertex();

            consumer.vertex(pose, (float)bottom.x, (float)bottom.y, (float)bottom.z)
                    .color(r, g, b, a)
                    .uv(1.0F, 0.0F)             // 对应另一边UV
                    .uv2(0xF000F0)
                    .normal(0.0F, 1.0F, 0.0F)
                    .endVertex();
        }
    }

    private static Vec3 calculatePerpendicularVector(Vec3 direction) {
        // 计算一个与方向向量垂直的向量
        Vec3 perpendicular = new Vec3(-direction.z, 0, direction.x).normalize();
        if (perpendicular.lengthSqr() < 1.0E-7D) {
            // 如果方向是垂直的，使用另一个计算方法
            perpendicular = new Vec3(0, -direction.z, direction.y).normalize();
        }
        return perpendicular;
    }

    // 距离和视野检查（性能优化）
    private static boolean shouldRenderLeash(Entity entity, Entity holder) {
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();

        if (cameraEntity == null) return false;

        // 距离检查（只渲染50格内）
        double distanceToEntity = entity.distanceToSqr(cameraEntity);
        double distanceToHolder = holder.distanceToSqr(cameraEntity);
        return !(distanceToEntity > 50 * 50) || !(distanceToHolder > 50 * 50);
    }
}