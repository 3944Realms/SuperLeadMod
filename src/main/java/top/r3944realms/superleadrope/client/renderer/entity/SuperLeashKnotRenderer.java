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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.model.SuperLeashKnotModel;
import top.r3944realms.superleadrope.client.model.geom.SLPModelLayers;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

/**
 * The type Super leash knot renderer.
 */
@OnlyIn(Dist.CLIENT)
public class SuperLeashKnotRenderer extends EntityRenderer<SuperLeashKnotEntity> {
    private static final ResourceLocation KNOT_LOCATION = new ResourceLocation(SuperLeadRope.MOD_ID, "textures/entity/super_lead_knot.png");
    private final SuperLeashKnotModel<SuperLeashKnotEntity> model;

    /**
     * Instantiates a new Super leash knot renderer.
     *
     * @param context the context
     */
    public SuperLeashKnotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SuperLeashKnotModel<>(context.bakeLayer(SLPModelLayers.SUPER_LEASH_KNOT));
    }

    @Override
    public void render(@NotNull SuperLeashKnotEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        AABB box = entity.getBoundingBox();
        float boxWidth = (float) box.getXsize();
        float boxHeight = (float) box.getYsize();

        // 模型原始尺寸（像素 → 方块）
        float modelWidthBlocks = 6.0F / 16.0F;
        float modelHeightBlocks = 8.0F / 16.0F;

        // 缩放比例
        float scaleX = boxWidth / modelWidthBlocks;
        float scaleY = boxHeight / modelHeightBlocks;
        float scaleZ = scaleX;

        poseStack.pushPose();

        // 先缩放
        poseStack.scale(scaleX, scaleY, scaleZ);

        // 再平移：把模型抬到碰撞箱底部
        poseStack.translate(0.0D, boxHeight / scaleY, 0.0D);

        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(KNOT_LOCATION));

        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SuperLeashKnotEntity superLeashKnotEntity) {
        return KNOT_LOCATION;
    }

}
