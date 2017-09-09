package pl.kobarjan.graphics;

import org.joml.Math;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import pl.kobarjan.entities.Entity;
import pl.kobarjan.models.RawModel;
import pl.kobarjan.models.TexturedModel;
import pl.kobarjan.shaders.StaticShader;
import pl.kobarjan.textures.ModelTexture;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

public class Window {



    private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private static GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
        }
    };

    public Window(){
        long window;

        float[] vertices = {
                -0.5f, 0.5f, 0,
                -0.5f, -0.5f, 0,
                0.5f, -0.5f, 0,
                0.5f, 0.5f, 0f
        };

        int[] indices = {
                0,1,3,
                3,1,2
        };

        float[] textureCoords = {
                0,0,
                0,1,
                1,1,
                1,0
        };

        glfwSetErrorCallback(errorCallback);
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(1280,720,"Window", 0, 0);
        if(window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create window");
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window,
                (vidMode.width()-1280)/2,
                (vidMode.height()-720)/2
        );

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(1);

        glfwSetKeyCallback(window, keyCallback);

        Loader loader = new Loader();
        Renderer renderer = new Renderer();

        StaticShader shader = new StaticShader();

        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        RawModel model = loader.loadToVAO(vertices,textureCoords, indices);
        ModelTexture texture = new ModelTexture(loader.loadTexture("Untitled"));
        TexturedModel texturedModel = new TexturedModel(model,texture);

        Entity entity = new Entity(texturedModel, new Vector3f(0,0,0),0,0,0,1,0);

        while(!glfwWindowShouldClose(window)) {

            entity.increasePosition(0.02f,0,0);
            entity.increaseRotation(1,0,0, (float) Math.toRadians(1.0));

            float ratio;

            glfwGetFramebufferSize(window, width,height);
            ratio = width.get() / (float) height.get();

            width.rewind();
            height.rewind();

            glViewport(0,0,width.get(),height.get());
            glClear(GL_COLOR_BUFFER_BIT);

            renderer.prepare();
            shader.start();
            renderer.render(entity,shader);
            shader.stop();

            glfwSwapBuffers(window);
            glfwPollEvents();

            width.flip();
            height.flip();
        }
        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);

        shader.cleanUp();
        loader.cleanUp();

        glfwDestroyWindow(window);
        keyCallback.free();

        glfwTerminate();
        errorCallback.free();

    }
}