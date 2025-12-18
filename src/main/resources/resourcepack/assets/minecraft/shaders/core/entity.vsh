#version 330

#moj_import <minecraft:light.glsl>
#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1; // overlay
uniform sampler2D Sampler2; // lightmap

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
#ifdef PER_FACE_LIGHTING
out vec4 vertexPerFaceColorBack;
out vec4 vertexPerFaceColorFront;
#else
out vec4 vertexColor;
#endif
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;

out float isMannequinFlag;

#moj_import <minecraft:recape/vertex/imports.glsl>

// parameters
const vec3 DEFAULT_COLOR = vec3(1.0, 0.0, 1.0); // фиолетовый
const vec3 SNEAKING_COLOR = vec3(1.0, 1.0, 0.0); // жёлтый
const vec3 SWIMMING_COLOR = vec3(0.0, 1.0, 1.0); // бирюзовый

const vec3 THRD_DEFAULT_COLOR = vec3(0.5, 0.0, 0.5); // фиолетовый
const vec3 THRD_SNEAKING_COLOR = vec3(0.5, 0.5, 0.0); // жёлтый
const vec3 THRD_SWIMMING_COLOR = vec3(0.0, 0.5, 0.5); // бирюзовый

const float COLOR_EPS = 0.05;

const float purple_shift = 4.23;
const float yellow_shift = 3.95;
const float turquoise_shift = 3.6;

const float thrd_purple_shift = 1.2;
const float thrd_yellow_shift = 0.9;
const float thrd_turquoise_shift = 0.6;

void main() {
    vec3 localPos = Position;

    bool markerFound = false;
    float appliedShift = 0.0;

    ivec2 s0 = textureSize(Sampler0, 0);
    if (s0.x > 0 && s0.y > 0) {
        vec4 px = texelFetch(Sampler0, ivec2(0, 0), 0);
        if (px.a > 0.5) {
            vec3 dPurple = abs(px.rgb - DEFAULT_COLOR);
            vec3 dYellow = abs(px.rgb - SNEAKING_COLOR);
			vec3 dTurquoise = abs(px.rgb - SWIMMING_COLOR);
			
			vec3 dThrdPurple = abs(px.rgb - THRD_DEFAULT_COLOR);
            vec3 dThrdYellow = abs(px.rgb - THRD_SNEAKING_COLOR);
			vec3 dThrdTurquoise = abs(px.rgb - THRD_SWIMMING_COLOR);

            if (dPurple.r < COLOR_EPS && dPurple.g < COLOR_EPS && dPurple.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = purple_shift;
            } else if (dYellow.r < COLOR_EPS && dYellow.g < COLOR_EPS && dYellow.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = yellow_shift;
            } else if (dTurquoise.r < COLOR_EPS && dTurquoise.g < COLOR_EPS && dTurquoise.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = turquoise_shift;
            
			} else if (dThrdPurple.r < COLOR_EPS && dThrdPurple.g < COLOR_EPS && dThrdPurple.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = thrd_purple_shift;
            } else if (dThrdYellow.r < COLOR_EPS && dThrdYellow.g < COLOR_EPS && dThrdYellow.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = thrd_yellow_shift;
            } else if (dThrdTurquoise.r < COLOR_EPS && dThrdTurquoise.g < COLOR_EPS && dThrdTurquoise.b < COLOR_EPS) {
                markerFound = true;
                appliedShift = thrd_turquoise_shift;
            }
        }
    }

    if (markerFound) {
        localPos.y -= appliedShift;
    }

    isMannequinFlag = markerFound ? 1.0 : 0.0;

    gl_Position = ProjMat * ModelViewMat * vec4(localPos, 1.0);

    sphericalVertexDistance = fog_spherical_distance(localPos);
    cylindricalVertexDistance = fog_cylindrical_distance(localPos);

    #ifdef PER_FACE_LIGHTING
        vec2 light = minecraft_compute_light(Light0_Direction, Light1_Direction, Normal);
        vertexPerFaceColorBack = minecraft_mix_light_separate(-light, Color);
        vertexPerFaceColorFront = minecraft_mix_light_separate(light, Color);
    #elif defined(NO_CARDINAL_LIGHTING)
        vertexColor = Color;
    #else
        vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    #endif

    #ifndef EMISSIVE
        lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    #endif
    overlayColor = texelFetch(Sampler1, UV1, 0);

    texCoord0 = UV0;
    #ifdef APPLY_TEXTURE_MATRIX
        texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    #endif
}
