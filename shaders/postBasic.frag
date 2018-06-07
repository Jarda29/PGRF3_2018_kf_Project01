#version 330
in vec2 texCoord;
uniform sampler2D textureID;
out vec4 outColor;

void main() {
    vec3 color = texture2D(textureID, texCoord).rgb;
    outColor = vec4(color,1.0);
}
