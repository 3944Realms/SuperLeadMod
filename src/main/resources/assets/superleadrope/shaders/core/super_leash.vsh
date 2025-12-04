#version 150
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV2; // Vertex lightmap UV (float)
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform sampler2D Sampler2;
uniform vec4 ColorModulator;
uniform int FogShape;
uniform float Time;        // Game time, controls rope swing
uniform vec3 HolderPos;    // Rope holder position
uniform vec3 EntityPos;    // Tied entity position
uniform float MaxDistance; // Maximum rope length

out vec2 texCoord;
out vec3 fragNormal;
out float vertexDistance;
out vec4 vertexColor;
out vec2 lightmapUV;      // Normalized UV for lightmap

// Catenary function (rope sag)
float cosh_p(float x) { return (exp(x) + exp(-x)) * 0.5; }

void main() {
    float progress = UV0.x;

    // ----------------------------
    // Rope sag
    // ----------------------------
    float a = 0.15;
    float sag = a * (cosh_p(progress * 2.0 - 1.0) - 1.0);

    // ----------------------------
    // Rope sway (enhanced)
    // ----------------------------
    float sway = sin(progress * 3.14159 + Time) * 0.05 +   // Main sway
    sin(progress * 5.0 + Time * 1.5) * 0.03 + // Secondary frequency
    sin(progress * 8.0 + Time * 2.0) * 0.01;  // High-frequency micro sway

    vec3 ropeDir = normalize(vec3(0.0, 1.0, 0.0));
    vec3 swayDir = normalize(cross(ropeDir, Normal));
    vec3 offset = swayDir * sway + vec3(0.0, -sag, 0.0);

    // ----------------------------
    // Final world position
    // ----------------------------
    vec4 worldPos = vec4(Position + offset, 1.0);
    gl_Position = ProjMat * ModelViewMat * worldPos;

    // ----------------------------
    // Diagonal UV sampling + texture repeat
    // ----------------------------
    float repeatFactor = 4.0; // Texture repeat count, adjustable
    vec2 diagUV = vec2(progress, progress) * repeatFactor;
    texCoord = diagUV;

    // ----------------------------
    // Normal and fog
    // ----------------------------
    fragNormal = normalize((ModelViewMat * vec4(Normal, 0.0)).xyz);
    vertexDistance = fog_distance(ModelViewMat, Position + offset, FogShape);

    // ----------------------------
    // Rope stretch ratio (enhanced)
    // ----------------------------
    float distance = length(HolderPos - EntityPos);
    float ratio = clamp(distance / MaxDistance * 5.0, 0.0, 1.0); // Amplify short distance
    float eased = pow(ratio, 0.5); // Non-linear easing, visible at short distances

    // Dynamic color (brown → red) + noise
    vec3 colorNormal = vec3(0.55, 0.27, 0.07);
    vec3 colorCritical = vec3(1.0, 0.0, 0.0);
    float noise = fract(sin(dot(Position.xy ,vec2(12.9898,78.233))) * 43758.5453);
    vec3 dynamicColor = mix(colorNormal, colorCritical, eased) + noise * 0.03;

    // ----------------------------
    // Texture sampling and modulation
    // ----------------------------
    vec4 texColor = texture(Sampler2, diagUV);
    vertexColor = vec4(dynamicColor, texColor.a) * ColorModulator * texColor;

    // ----------------------------
    // Lightmap
    // ----------------------------
    lightmapUV = UV2 / 512.0; // Normalization for 512x512 texture
}