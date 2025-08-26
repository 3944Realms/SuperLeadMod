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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import top.r3944realms.superleadrope.SuperLeadRope;
import top.r3944realms.superleadrope.client.model.SuperLeashKnotModel;
import top.r3944realms.superleadrope.client.model.geom.SLPModelLayers;
import top.r3944realms.superleadrope.content.entity.SuperLeashKnotEntity;

@OnlyIn(Dist.CLIENT)
public class SuperLeashKnotRenderer extends EntityRenderer<SuperLeashKnotEntity> {
    private static final ResourceLocation KNOT_LOCATION = new ResourceLocation(SuperLeadRope.MOD_ID, "textures/entity/super_lead_knot.png");
    private final SuperLeashKnotModel<SuperLeashKnotEntity> model;
    public SuperLeashKnotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SuperLeashKnotModel<>(context.bakeLayer(SLPModelLayers.SUPER_LEASH_KNOT));
    }

    @Override
    public void render(@NotNull SuperLeashKnotEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        // 根据实体的实际 hitbox 调整缩放
        float scaleX = entity.getBbWidth() / 0.5f;   // 默认 0.5f，对应 scale=1
        float scaleY = entity.getBbHeight() / 0.5f;  // 默认 0.5f，对应 scale=1
        float scaleZ = scaleX;                            // 宽度对称

        poseStack.scale(-1.5F * scaleX, -1.5F * scaleY, 1.5F * scaleZ);

        // 位置微调（避免浮空）
        poseStack.translate(0.0D, 0.15D * scaleY, 0.0D);

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
