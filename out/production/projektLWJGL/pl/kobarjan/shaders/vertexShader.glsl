#version 140

in vec3 position;
in vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 worldMatrix;
uniform mat4 projectionMatrix;

void main(void) {

    gl_Position = projectionMatrix * worldMatrix * vec4(position.x,position.y,position.y,1.0);
    pass_textureCoords = textureCoords;

}