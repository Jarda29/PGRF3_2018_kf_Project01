#version 330
in vec3 worldPos; //pozice bodu na povrchu telesa ve scene
in vec3 worldNormal; //normala ve scene
in vec3 vertColor;
in vec2 texCoord;
out vec4 outColor;
uniform sampler2D textureBase;
uniform vec3 lightPos; //ve scene
uniform vec3 eyePos; //ve scene

uniform int lightMode;
uniform int colorMode;

vec3 color(){
    vec3 result;
    switch(colorMode){
        case 0:
            result = vec3(0.8,0.9,0.3);
            break;
        case 1:
            result = vec3(0.2,0.2,1);
            break;
        case 2:
            result = worldNormal * 0.5 + 0.5;
            break;
        case 3:
            result = texture(textureBase, texCoord).rgb;
            break;
    }
    return result;
}

vec4 ambientOnly(){
    vec3 Drgb = color();
    vec3 Argb = vec3(0.4);
    vec3 Irgb = Argb * Drgb;
	return vec4(Irgb, 1);
}

vec4 ambientAndDifuse(){
    vec3 Drgb = color();
    vec3 Argb = vec3(0.4);
    vec3 Lrgb = vec3(0.8,0.8,1);
    vec3 normal = normalize(worldNormal);
    vec3 lightVec = normalize(lightPos - worldPos);
    vec3 eyeVec = normalize(eyePos - worldPos);
    float d = max(dot(lightVec, normal), 0);
    vec3 Irgb = Argb * Drgb + Lrgb * Drgb * d;
	return vec4(Irgb, 1);
}

vec4 blinnPhong(){
    vec3 Drgb = color();
    vec3 Srgb = vec3(1);
    vec3 Argb = vec3(0.4);
    vec3 Lrgb = vec3(0.8,0.8,1);
    vec3 normal = normalize(worldNormal);
    vec3 lightVec = normalize(lightPos - worldPos);
    vec3 eyeVec = normalize(eyePos - worldPos);
    vec3 r = -reflect(lightVec, normal);
    float d = max(dot(lightVec, normal), 0);
    float s = pow(max(dot(r, eyeVec), 0), 90);
    vec3 Irgb = Argb * Drgb + Lrgb * Drgb * d + Lrgb * Srgb * s; //finalni vysledek
	return vec4(Irgb, 1);
}


vec4 light(){
    vec4 col;
    switch(lightMode){
        case 0:
          col = ambientOnly();
          break;
        case 1:
            col = ambientAndDifuse();
            break;
        case 2:
            col = blinnPhong();
            break;
    }
    return col;
}


void main() {
	outColor = light();
}