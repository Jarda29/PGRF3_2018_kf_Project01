#version 330
in vec3 worldPos; //pozice bodu na povrchu telesa ve scene
in vec3 worldNormal; //normala ve scene
in vec3 vertColor;
in vec2 texCoord;
out vec4 outColor;
uniform sampler2D textureID;
uniform vec3 lightPos; //ve scene
uniform vec3 eyePos; //ve scene
void main() {
    vec3 Drgb = texture(textureID, texCoord).rgb;
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
	outColor = vec4(Irgb, 1);
} 
