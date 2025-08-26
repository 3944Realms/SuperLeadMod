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

package top.r3944realms.superleadrope.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.client.renderer.SLPRenderType;
import top.r3944realms.superleadrope.client.renderer.resolver.SuperLeashStateResolver;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;
import top.r3944realms.superleadrope.content.capability.CapabilityHandler;
import top.r3944realms.superleadrope.content.capability.LeashDataImpl;
import top.r3944realms.superleadrope.content.entity.SuperLeashEntity;

import java.util.List;

public class SuperLeashRenderer extends EntityRenderer<SuperLeashEntity> {
    // 渲染参数
    private static final int SEGMENTS = 24; // 绳子分段数
    private static final float BASE_THICKNESS = 0.08f; // 基础线宽
    private static final float CRITICAL_PULSE_SPEED = 0.2f; // 临界状态脉冲速度

    public SuperLeashRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull SuperLeashEntity entity, float entityYaw, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Entity controlled = entity.getControlled();
        if (!LeashDataImpl.isLeashable(controlled)) return;

        // 创建渲染状态
        List<SuperLeashRenderState> superLeashRenderStates = SuperLeashStateResolver.resolveAll(
                controlled,
                (LeashDataImpl) controlled.getCapability(CapabilityHandler.LEASH_DATA_CAP).orElseThrow(NullPointerException::new),
                partialTick
        );

        // 渲染绳子
        superLeashRenderStates.forEach(s -> renderLeash(s, poseStack, buffer, packedLight, partialTick));
    }

    private void renderLeash(SuperLeashRenderState state, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, float partialTicks) {
        poseStack.pushPose();

        VertexConsumer vertexBuilder = buffer.getBuffer(SLPRenderType.leashType());
        Vec3 start = state.startPos();
        Vec3 end = state.endPos();

        // 计算绳子总长度和方向
        double length = start.distanceTo(end);
        Vec3 direction = end.subtract(start).normalize();

        // 临界状态闪烁效果
        float alpha = state.isCritical() ?
                (Mth.sin(partialTicks * CRITICAL_PULSE_SPEED) * 0.5f + 0.5f) :
                1.0f;

        // 分段渲染绳子
        for (int i = 0; i <= SEGMENTS; i++) {
            float progress = (float)i / SEGMENTS;

            // 计算当前段位置和摆动偏移
            Vec3 segmentPos = start.add(direction.scale(length * progress));
            Vec3 swingOffset = state.getSwingOffset(progress, partialTicks);

            // 应用张力拉伸效果
            if (state.stretchRatio() > 1.0f) {
                float stretchFactor = (float)Math.sin(progress * Math.PI) * 0.2f * (state.stretchRatio() - 1.0f);
                segmentPos = segmentPos.add(direction.scale(stretchFactor * length));
            }

            // 添加摆动偏移
            segmentPos = segmentPos.add(swingOffset);

            // 计算颜色渐变 (从正常色到紧张色)
            int color = interpolateColor(
                    SuperLeashRenderState.COLOR_NORMAL,
                    state.isCritical() ? SuperLeashRenderState.COLOR_CRITICAL : SuperLeashRenderState.COLOR_TENSION,
                    state.tension());

            // 添加顶点
            addVertex(vertexBuilder, poseStack, segmentPos,
                    state.thickness() * (1 - progress * 0.3f), // 末端稍细
                    color, alpha, packedLight);
        }

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer builder, PoseStack poseStack, Vec3 pos,
                           float thickness, int color, float alpha, int packedLight) {
        // 解包颜色
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        // 转换到相机空间
        Vec3 viewPos = pos.subtract(this.entityRenderDispatcher.camera.getPosition());

        // 添加顶点 (使用LINE_STRIP渲染)
        builder.vertex(poseStack.last().pose(), (float)viewPos.x, (float)viewPos.y, (float)viewPos.z)
                .color(r, g, b, alpha)
                .uv2(packedLight)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .normal(poseStack.last().normal(), 0, 1, 0)
                .endVertex();
    }

    private int interpolateColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    @Override
    public ResourceLocation getTextureLocation(@NotNull SuperLeashEntity entity) {
        return null; // 使用自定义渲染类型，不需要纹理
    }
}
