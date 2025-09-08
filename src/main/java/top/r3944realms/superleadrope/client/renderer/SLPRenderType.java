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

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.RenderType;

public class SLPRenderType extends RenderType {
    public SLPRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }
    static RenderType SUPER_LEASH;
    public static final VertexFormat POSITION_COLOR_LIGHTMAP_NORMAL;
    public static RenderType leashType() {
        return SUPER_LEASH;
    }
    static {
        POSITION_COLOR_LIGHTMAP_NORMAL = new VertexFormat(
                ImmutableMap.<String, VertexFormatElement>builder()
                        .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
                        .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
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
                        .setShaderState(RENDERTYPE_LEASH_SHADER) // 使用实体着色器
                        .setTextureState(NO_TEXTURE) // 无纹理
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP) // 启用光照
                        .setCullState(NO_CULL) // 双面渲染
                        .createCompositeState(false)
        );
    }
}
