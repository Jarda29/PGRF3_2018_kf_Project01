#version 330
in vec2 texCoord;
uniform sampler2D textureID;
uniform float time;
uniform int postProcessMode;
out vec4 outColor;

void main() {
    vec3 color;
    vec2 texcoord_t;
    switch(postProcessMode){
        case 0:
            color = texture2D(textureID, texCoord).rgb;
            outColor = vec4(color,1.0);
            return;
        case 1:
            texcoord_t = texCoord;
            texcoord_t.x += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
            color = texture2D(textureID, texcoord_t).rgb;
            outColor = vec4(color,1.0);
            return;
        case 2:
            texcoord_t = texCoord;
            texcoord_t.y += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
            color = texture2D(textureID, texcoord_t).rgb;
            outColor = vec4(color,1.0);
            return;
        case 3:
            texcoord_t = texCoord;
            texcoord_t.x += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
            texcoord_t.y += sin(texcoord_t.x * 4*2*3.14159 + time) / 100;
            color = texture2D(textureID, texcoord_t).rgb;
            outColor = vec4(color,1.0);
            return;
    }
}
