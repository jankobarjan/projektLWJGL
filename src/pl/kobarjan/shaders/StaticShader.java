package pl.kobarjan.shaders;

import org.joml.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/pl/kobarjan/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/pl/kobarjan/shaders/fragmentShader.glsl";

    private int location_worldMatrix;
    private int location_projectionMatrix;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocation() {
        location_worldMatrix = super.getUniformLocation("worldMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    public int getUniformLocation(String uniformName) {
        return super.getUniformLocation(uniformName);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0,"position");
        super.bindAttribute(1,"textureCoords");
    }

    public void loadTransformationMatrix(Matrix4f matrix, int location) {
        super.loadMatrix(location, matrix);
    }
}