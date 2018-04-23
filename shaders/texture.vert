#version 330
in vec3 inPosition;
in vec3 inNormal;
in vec2 inTextureCoordinates;
out vec3 vertColor;
out vec2 texCoord;
out vec3 worldPos; //pozice bodu na povrchu telesa ve scene
out vec3 worldNormal; //normala ve scene
uniform mat4 mat;
void main() {
	gl_Position = mat * vec4(inPosition, 1.0);
	vertColor = inNormal * 0.5 + 0.5;
	worldPos = inPosition;
	worldNormal = inNormal;
	texCoord = inTextureCoordinates;
} 
