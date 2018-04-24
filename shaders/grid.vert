#version 330
in vec2 inPosition;
out vec3 vertColor;
out vec2 texCoord;
out vec3 worldPos; //pozice bodu na povrchu telesa ve scene
out vec3 worldNormal; //normala ve scene
uniform mat4 mat;

const float PI = 3.1415927;

vec3 ellipsoid(vec2 param){
    float s = param.x * 2 * PI;
    float t = param.y * PI;

    return vec3(
        sin(t) * cos(s),
        2 * sin(t) * sin(s),
        cos(t)
    );
}

vec3 surface(vec2 param) {
    vec3 result;
    result.x = param.y;
    result.y = param.x;
    result.z = 0;
    return result;
}

vec3 surfaceNormal(vec2 param) {
//    vec3 tx = vec3(0,1,0);//parcialni derivace podle param.x;
//    vec3 ty = vec3(1,0,0);//parcialni derivace podle param.y;
    float delta = 1e-5;
    vec3 tx = (ellipsoid(param + vec2(delta, 0))
                - ellipsoid(param - vec2(delta, 0))) / (2 * delta);
    vec3 ty = (ellipsoid(param + vec2(0, delta))
                - ellipsoid(param - vec2(0, delta))) / (2 * delta);
    return cross(ty, tx);
}

void main() {
    vec3 position = ellipsoid(inPosition);
    vec3 normal = surfaceNormal(inPosition);
	gl_Position = mat * vec4(position, 1.0);
	vertColor = normal * 0.5 + 0.5;
	worldPos = position;
	worldNormal = normal;
	texCoord = inPosition;
} 
