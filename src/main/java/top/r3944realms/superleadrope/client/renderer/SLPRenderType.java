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

package top.r3944realms.superleadrope.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import top.r3944realms.superleadrope.SuperLeadRope;

/**
 * The type Slp render type.
 */
public class SLPRenderType extends RenderType {
    /**
     * Instantiates a new Slp render type.
     *
     * @param name             the name
     * @param format           the format
     * @param mode             the mode
     * @param bufferSize       the buffer size
     * @param affectsCrumbling the affects crumbling
     * @param sortOnUpload     the sort on upload
     * @param setupState       the setup state
     * @param clearState       the clear state
     */
    public SLPRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    /**
     * The Super leash.
     */
    static RenderType SUPER_LEASH;
    /**
     * The constant POSITION_COLOR_LIGHTMAP_NORMAL.
     */
    public static final VertexFormat POSITION_COLOR_LIGHTMAP_NORMAL;

    /**
     * Leash type render type.
     *
     * @return the render type
     */
    public static RenderType leashType() {
        return SUPER_LEASH;
    }
    static {
        POSITION_COLOR_LIGHTMAP_NORMAL = new VertexFormat(
                ImmutableMap.<String, VertexFormatElement>builder()
                        .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
                        .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
                        .put("UV0", DefaultVertexFormat.ELEMENT_UV0)      // 纹理坐标
                        .put("UV2", DefaultVertexFormat.ELEMENT_UV2)       // 光照
                        .put("Normal", DefaultVertexFormat.ELEMENT_NORMAL) // 法线
                        .put("Padding", DefaultVertexFormat.ELEMENT_PADDING)
                        .build()
        );
        SUPER_LEASH = RenderType.create("super_leash",
                POSITION_COLOR_LIGHTMAP_NORMAL, // 注意：需要 NORMAL（法线）信息！
                VertexFormat.Mode.TRIANGLE_STRIP, // 改为三角形带模式
                256,
                false, // not used for crumbling
                false, // sortOnUpload
                CompositeState.builder()
                        .setShaderState(new ShaderStateShard(() -> SLPShaderRegistry.ROPE_SHADER)) // 使用实体着色器
                        .setTextureState(new TextureStateShard(new ResourceLocation(SuperLeadRope.MOD_ID, "textures/rope/rope_leash.png"), false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP) // 启用光照
                        .setCullState(NO_CULL) // 双面渲染
                        .createCompositeState(false)
        );
    }
}
