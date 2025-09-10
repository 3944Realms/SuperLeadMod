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
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import top.r3944realms.superleadrope.client.renderer.state.SuperLeashRenderState;

@SuppressWarnings("UnnecessaryLocalVariable")
public class SuperLeashRenderer {

    private static final int LEASH_STEPS = 24;

    public static void renderLeash(SuperLeashRenderState state, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();

        Vec3 startWorld = state.startPos();
        Vec3 endWorld = state.endPos();

        VertexConsumer consumer = buffer.getBuffer(RenderType.leash());

        Minecraft mc = Minecraft.getInstance();
        Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
        poseStack.translate(startWorld.x - camPos.x, startWorld.y - camPos.y, startWorld.z - camPos.z);
        Matrix4f matrix = poseStack.last().pose();

        int blockLightStart = getBlockLight(BlockPos.containing(startWorld));
        int blockLightEnd   = getBlockLight(BlockPos.containing(endWorld));
        int skyLightStart   = getSkyLight(BlockPos.containing(startWorld));
        int skyLightEnd     = getSkyLight(BlockPos.containing(endWorld));

        // 差向量 + 偏移
        Offsets offsets = computeOffsets(state, startWorld, endWorld);

        // pass1: 0 → N
        for (int i = 0; i <= LEASH_STEPS; i++) {
            float f = (float)i / LEASH_STEPS;
            int packedLight = packLight(blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, f);
            float[] rgb = computeColor(state, i, false);
            addVertexPair(consumer, matrix, offsets, packedLight, rgb, i, true);
        }

        // pass2: N → 0
        for (int i = LEASH_STEPS; i >= 0; i--) {
            float f = (float)i / LEASH_STEPS;
            int packedLight = packLight(blockLightStart, blockLightEnd, skyLightStart, skyLightEnd, f);
            float[] rgb = computeColor(state, i, true);
            addVertexPair(consumer, matrix, offsets, packedLight, rgb, i, false);
        }

        poseStack.popPose();
    }



    /** 计算差向量和偏移 */
    private static Offsets computeOffsets(SuperLeashRenderState state, Vec3 start, Vec3 end) {
        float dx = (float) (end.x - start.x);
        float dy = (float) (end.y - start.y);
        float dz = (float) (end.z - start.z);

        float base = 0.05f;
        float horiz = dx * dx + dz * dz;
        float inv = (horiz > 1e-8F) ? Mth.invSqrt(horiz) * base / 2.0F : 0.0F;
        float xOffset = dz * inv;
        float zOffset = dx * inv;

        float yOffsetPass1 = base;
        float dyOffsetPass1 = base;

        // pass2 纵向不改 py，交叉通过厚度方向 ± 偏移实现
        float yOffsetPass2 = base;
        float dyOffsetPass2 = -dyOffsetPass1; // 反向交叉

        return new Offsets(dx, dy, dz, xOffset, zOffset,
                yOffsetPass1, dyOffsetPass1, yOffsetPass2, dyOffsetPass2);
    }

    // 光照计算
    private static int packLight(int blockStart, int blockEnd, int skyStart, int skyEnd, float f) {
        int block = (int) Mth.lerp(f, blockStart, blockEnd);
        int sky = (int) Mth.lerp(f, skyStart, skyEnd);
        return LightTexture.pack(block, sky);
    }

    // 颜色渐变
    private static float[] computeColor(SuperLeashRenderState state, int index, boolean reversePass) {
        float distance = (float) state.startPos().distanceTo(state.endPos());
        float ratio = Mth.clamp(distance / (state.maxDistance() * 2f), 0f, 1f);

        // 定义颜色
        float rStart = 0.42f; // 深棕 R
        float gStart = 0.31f; // 深棕 G
        float bStart = 0.18f; // 深棕 B

        float rEnd = 0.69f; // 暗红 R
        float gEnd = 0.23f; // 暗红 G
        float bEnd = 0.18f; // 暗红 B

        // 线性插值
        float r = Mth.lerp(ratio, rStart, rEnd);
        float g = Mth.lerp(ratio, gStart, gEnd);
        float b = Mth.lerp(ratio, bStart, bEnd);

        // 原版交替颜色
        float colorFactor = index % 2 == (reversePass ? 1 : 0) ? 0.7f : 1.0f;
        return new float[]{r * colorFactor, g * colorFactor, b * colorFactor};
    }

    // 顶点生成
    private static void addVertexPair(VertexConsumer consumer, Matrix4f matrix, Offsets offsets, int packedLight, float[] rgb, int index, boolean usePass1) {
        float f = (float) index / LEASH_STEPS;
        float px = offsets.dx * f;
        // py 保持曲线，用于纹理
        float py = offsets.dy > 0 ? offsets.dy * f * f : offsets.dy - offsets.dy * (1 - f) * (1 - f);
        float pz = offsets.dz * f;

        // 微摆动，避免长绳子顶点重合
        float sway = 0.01f * (float)Math.sin(f * Math.PI * 6 + index);

        float yOffset = usePass1 ? offsets.yOffsetPass1 : offsets.yOffsetPass2;
        float dyOffset = usePass1 ? offsets.dyOffsetPass1 : offsets.dyOffsetPass2;

        consumer.vertex(matrix, px - offsets.xOffset, py + dyOffset + sway, pz + offsets.zOffset)
                .color(rgb[0], rgb[1], rgb[2], 1f).uv2(packedLight).endVertex();
        consumer.vertex(matrix, px + offsets.xOffset, py + yOffset - dyOffset - sway, pz - offsets.zOffset)
                .color(rgb[0], rgb[1], rgb[2], 1f).uv2(packedLight).endVertex();
    }
    private static int getBlockLight(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 15; // 默认亮度，防止空指针
        return mc.level.getBrightness(LightLayer.BLOCK, pos);
    }

    private static int getSkyLight(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 15; // 默认亮度，防止空指针
        return mc.level.getBrightness(LightLayer.SKY, pos);
    }

    /** 横纵偏移数据 */
    private static class Offsets {
        public final float dx, dy, dz;     // 绳子差向量
        public final float xOffset, zOffset; // 横向偏移
        public final float yOffsetPass1, dyOffsetPass1; // pass1 纵向偏移
        public final float yOffsetPass2, dyOffsetPass2; // pass2 纵向偏移

        public Offsets(float dx, float dy, float dz,
                       float xOffset, float zOffset,
                       float yOffsetPass1, float dyOffsetPass1,
                       float yOffsetPass2, float dyOffsetPass2) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.xOffset = xOffset;
            this.zOffset = zOffset;
            this.yOffsetPass1 = yOffsetPass1;
            this.dyOffsetPass1 = dyOffsetPass1;
            this.yOffsetPass2 = yOffsetPass2;
            this.dyOffsetPass2 = dyOffsetPass2;
        }
    }

}