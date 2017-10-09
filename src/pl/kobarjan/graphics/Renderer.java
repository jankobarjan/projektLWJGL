package pl.kobarjan.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import pl.kobarjan.entities.Entity;
import pl.kobarjan.models.TexturedModel;
import pl.kobarjan.shaders.StaticShader;

public class Renderer {

    Transformation transformation = new Transformation();

    public void prepare() {
        GL11.glClearColor(0,0,0,1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    public void render(Entity[] entitys, StaticShader shader, Camera camera) {
        shader.loadTransformationMatrix(transformation.getProjectionMatrix(90, 0.01f,1000,1280,720), shader.getUniformLocation("projectionMatrix"));
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        for (Entity entity : entitys) {
            TexturedModel model = entity.getModel();
            shader.loadTransformationMatrix(transformation.getModelViewMatrix(entity, viewMatrix), shader.getUniformLocation("worldMatrix"));
            model.render();
        }
    }
}