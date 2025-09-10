#version 150
#moj_import <fog.glsl>

in vec2 texCoord;       // interpolated texture UV0
in vec3 fragNormal;     // normal (if you want lighting later)
in float vertexDistance; // distance for fog
in vec4 vertexColor;    // per-vertex color (ARGB)
in vec2 lightmapUV;     // normalized UV from vertex shader

uniform sampler2D Sampler2;         // rope texture
uniform sampler2D LightmapSampler;  // lightmap texture
uniform float AlphaMultiplier;

// Fog parameters
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

out vec4 FragColor;

vec3 applyLight(vec3 color, vec2 uv) {
    vec4 light = texture(LightmapSampler, uv);
    return color * light.rgb;
}

void main() {
    // 1. Sample base rope texture
    vec4 texColor = texture(Sampler2, texCoord);

    // 2. Multiply with vertex color
    vec4 baseColor = texColor * vertexColor;

    // 3. Apply lightmap
    vec3 litColor = applyLight(baseColor.rgb, lightmapUV);

    // 4. Alpha (from texture * vertex alpha * multiplier)
    float alpha = baseColor.a * AlphaMultiplier;

    // 5. Final color
    vec4 color = vec4(litColor, alpha);

    // 6. Apply vanilla fog
    FragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
