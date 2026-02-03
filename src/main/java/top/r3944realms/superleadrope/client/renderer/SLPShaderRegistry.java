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

package top.r3944realms.superleadrope.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import top.r3944realms.superleadrope.SuperLeadRope;

import java.io.IOException;

/**
 * The type Slp shader registry.
 */
public class SLPShaderRegistry {
    private static final ResourceLocation RL_SUPER_ROPE = new ResourceLocation(SuperLeadRope.MOD_ID, "super_leash");
    /**
     * The constant ROPE_SHADER.
     */
    public static ShaderInstance ROPE_SHADER;

    /**
     * Register shaders.
     *
     * @param event the event
     * @throws IOException the io exception
     */
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        RL_SUPER_ROPE,
                        DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP
                ),
                shader -> ROPE_SHADER = shader
        );
    }
}
