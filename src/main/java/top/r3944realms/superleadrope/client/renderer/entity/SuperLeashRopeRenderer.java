/*
 *  Super Lead rope mod
 *  Copyright (C)  2026  R3944Realms
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
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.content.entity.SuperLeashRopeEntity;
import top.r3944realms.superleadrope.core.register.SLPItems;

/**
 * The type Super leash rope renderer.
 */
public class SuperLeashRopeRenderer extends EntityRenderer<SuperLeashRopeEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            SuperLeadRope.MOD_ID, "textures/entity/super_lead_knot.png"
    );

    private final Minecraft minecraft = Minecraft.getInstance();

    /**
     * Instantiates a new Super leash rope renderer.
     *
     * @param context the context
     */
    public SuperLeashRopeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull SuperLeashRopeEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        // 获取拴绳物品
        ItemStack ropeItem = entity.getRopeItem();
        if (ropeItem.isEmpty()) {
            ropeItem = new ItemStack(SLPItems.SUPER_LEAD_ROPE.get());
        }

        // 获取旋转角度（从实体数据）
        float rotation = entity.getRotation();
        float rotationRadians = (float) Math.toRadians(rotation);

        // 获取实体的俯仰和偏航
        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        // 计算摆动的物理效果
        float swingAmount = 0.0f;
        if (!entity.isReturning() && !entity.hasTarget()) {
            // 飞行动画的摆动
            swingAmount = (float) Math.sin((entity.tickCount + partialTicks) * 0.3f) * 15.0f;
        }

        // 应用旋转和位置变换
        poseStack.translate(0.0D, 0.1D, 0.0D);

        // 应用偏航旋转（左右）
        poseStack.mulPose(Axis.YP.rotationDegrees(-yRot));

        // 应用俯仰旋转（上下）
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        // 应用自旋转
        poseStack.mulPose(Axis.YP.rotation(rotationRadians));

        // 应用摆动效果
        if (swingAmount != 0) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(swingAmount));
        }

        // 调整大小
        float scale = 1.2f; // 稍微放大以更清晰可见
        poseStack.scale(scale, scale, scale);

        // 渲染物品
        this.minecraft.getItemRenderer().renderStatic(
                ropeItem,
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SuperLeashRopeEntity entity) {
        return TEXTURE;
    }
}
