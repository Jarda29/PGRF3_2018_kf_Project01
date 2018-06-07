#version 330
in vec2 texCoord;
uniform sampler2D textureID;
uniform float time;
uniform int postProcessMode;
out vec4 outColor;

vec3 defaultMode(){
    return texture2D(textureID, texCoord).rgb;
}
vec3 movingX() {
     vec2 texcoord_t= texCoord;
     texcoord_t.x += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
     return texture2D(textureID, texcoord_t).rgb;
}
vec3 movingY() {
    vec2 texcoord_t = texCoord;
    texcoord_t.y += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
    return texture2D(textureID, texcoord_t).rgb;
}
vec3 movingXY(){
    vec2 texcoord_t = texCoord;
    texcoord_t.x += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
    texcoord_t.y += sin(texcoord_t.x * 4*2*3.14159 + time) / 100;
    return texture2D(textureID, texcoord_t).rgb;
}
vec3 blur(){
    float delta = 3.0/512.0;
    vec3 color = 4.0 * texture(textureID, texCoord).rgb;
    color += 2.0 * texture(textureID, fract(texCoord+vec2(delta, 0.0))).rgb;
    color += 2.0 * texture(textureID, fract(texCoord+vec2(-delta, 0.0))).rgb;
    color += 2.0 * texture(textureID, fract(texCoord+vec2(0.0, -delta))).rgb;
    color += 2.0 * texture(textureID, fract(texCoord+vec2(0.0, delta))).rgb;
    color += texture(textureID, fract(texCoord+vec2(delta, delta))).rgb;
    color += texture(textureID, fract(texCoord+vec2(delta, -delta))).rgb;
    color += texture(textureID, fract(texCoord+vec2(-delta, -delta))).rgb;
    color += texture(textureID, fract(texCoord+vec2(-delta, delta))).rgb;
    return color;
}
vec3 grey() {
    vec3 color = texture(textureID, texCoord).rgb;
    return vec3(0.299*color.r+0.587*color.b+0.114*color.g);
}

vec3 blurMoving(){
    vec2 texcoord_t = texCoord;
    texcoord_t.x += sin(texcoord_t.y * 4*2*3.14159 + time) / 100;
    texcoord_t.y += sin(texcoord_t.x * 4*2*3.14159 + time) / 100;

    float delta = 3.0/512.0;
    vec3 color = 4.0 * texture(textureID, texcoord_t).rgb;
    color += 2.0 * texture(textureID, fract(texcoord_t+vec2(delta, 0.0))).rgb;
    color += 2.0 * texture(textureID, fract(texcoord_t+vec2(-delta, 0.0))).rgb;
    color += 2.0 * texture(textureID, fract(texcoord_t+vec2(0.0, -delta))).rgb;
    color += 2.0 * texture(textureID, fract(texcoord_t+vec2(0.0, delta))).rgb;
    color += texture(textureID, fract(texcoord_t+vec2(delta, delta))).rgb;
    color += texture(textureID, fract(texcoord_t+vec2(delta, -delta))).rgb;
    color += texture(textureID, fract(texcoord_t+vec2(-delta, -delta))).rgb;
    color += texture(textureID, fract(texcoord_t+vec2(-delta, delta))).rgb;
    return color;
}

void main() {
    switch(postProcessMode){
        case 0:
            outColor = vec4(defaultMode(),1.0);
            return;
        case 1:
            outColor = vec4(movingX(),1.0);
            return;
        case 2:
            outColor = vec4(movingY(),1.0);
            return;
        case 3:
            outColor = vec4(movingXY(),1.0);
            return;
        case 4:
            outColor = vec4(blur()/16.0,1.0);
            return;
        case 5:
            outColor = vec4(blurMoving()/16.0,1.0);
            return;
        case 6:
            outColor = vec4(grey(), 1.0);
            return;
    }
}
