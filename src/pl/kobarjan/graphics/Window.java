package pl.kobarjan.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
//import pl.kobarjan.Input.MouseInput;
import pl.kobarjan.entities.Entity;
import pl.kobarjan.models.RawModel;
import pl.kobarjan.models.TexturedModel;
import pl.kobarjan.shaders.StaticShader;
import pl.kobarjan.textures.ModelTexture;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;

public class Window implements Runnable {

    long window;

    float[] vertices = {
            // VO
            -0.5f,  0.5f,  0.5f,
            // V1
            -0.5f, -0.5f,  0.5f,
            // V2
            0.5f, -0.5f,  0.5f,
            // V3
            0.5f,  0.5f,  0.5f,
            // V4
            -0.5f,  0.5f, -0.5f,
            // V5
            0.5f,  0.5f, -0.5f,
            // V6
            -0.5f, -0.5f, -0.5f,
            // V7
            0.5f, -0.5f, -0.5f,
    };

    int[] indices = {
            // Front face
            0, 1, 3, 3, 1, 2,
            // Top Face
            4, 0, 3, 5, 4, 3,
            // Right face
            3, 2, 7, 5, 3, 7,
            // Left face
            6, 1, 0, 6, 0, 4,
            // Bottom face
            2, 1, 6, 2, 6, 7,
            // Back face
            7, 6, 4, 7, 4, 5,
    };

    float[] textureCoords = {
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
            0.5f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f,
            0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.5f,
    };

    float speed = 0.002f;

    Renderer renderer;
    Loader loader;
    StaticShader shader;
    IntBuffer width;
    IntBuffer height;
    RawModel model;
    ModelTexture texture;
    TexturedModel texturedModel;
    Vector2f rotVec;
    Entity entity;
    Entity[] entities;
    Camera camera;
    double xPos = 0;
    double yPos = 0;
    double previousX = 1;
    double previousY = 1;
    //MouseInput mouseInput;

    private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);


    public Window(){
        init();
        loop();
        cleanUp();
    }
    private void init() {
        glfwSetErrorCallback(errorCallback);
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        window = glfwCreateWindow(1280,720,"Window", 0, 0);
        if(window == 0) {
            glfwTerminate();
            throw new RuntimeException("Failed to create window");
        }

        //mouseInput = new MouseInput(1.5f);
        //glfwSetCursorPosCallback(window, mouseInput);


        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window,
                (vidMode.width()-1280)/2,
                (vidMode.height()-720)/2
        );

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        GLFWCursorPosCallback cursorPosCallback;
        glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback() {

                    @Override
                    public void invoke(long window, double xpos, double ypos) {
                        xPos = xpos;
                        yPos = ypos;
                    }
                });

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);
        GLFWKeyCallback keyCallback;
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                    glfwSetWindowShouldClose(window, true);
                }
                if(key == GLFW_KEY_W && action != GLFW_RELEASE) {
                    camera.movePosition(0, (float) (camera.getPosition().y + 0.01),0);
                }
                if(key == GLFW_KEY_S && action != GLFW_RELEASE) {
                    camera.movePosition(0, (float) (camera.getPosition().y + -0.01),0);
                }
                if(key == GLFW_KEY_A && action != GLFW_RELEASE) {
                    camera.movePosition((float) (camera.getPosition().x + -0.01), 0,0);
                }
                if(key == GLFW_KEY_D && action != GLFW_RELEASE) {
                    camera.movePosition((float) (camera.getPosition().x + 0.01), 0,0);
                }



            }
        });
        rotVec = new Vector2f();

        loader = new Loader();
        renderer = new Renderer();
        shader = new StaticShader();

        width = MemoryUtil.memAllocInt(1);
        height = MemoryUtil.memAllocInt(1);

        model = loader.loadToVAO(vertices,textureCoords, indices);
        texture = new ModelTexture(loader.loadTexture("Untitled"));
        texturedModel = new TexturedModel(model,texture);

        entity = new Entity(texturedModel);

        entities = new Entity[]{entity};

        camera = new Camera();

        entity.setPosition(0,0,0);
        //entity.setRotation(0,0,0);
        //entity.setScale();
    }
    private void input() {
        rotVec.x = (float) (xPos - previousX);
        rotVec.y = (float) (yPos - previousY);
        System.out.println("CursorPos: " + rotVec.x + " , " + rotVec.y);
        previousX = xPos;
        previousY = yPos;
        //rotVec = mouseInput.getRotVector();
        camera.moveRotation((float)  Math.toRadians(rotVec.y),(float)   Math.toRadians(rotVec.x), 0);
    }

    private void loop() {
        while(!glfwWindowShouldClose(window)) {


            //entity.setPosition(entity.getPosition().x + speed,0,0);
            //entity.setPosition(0,entity.getPosition().y + speed,0);
            //entity.setPosition(0,0,entity.getPosition().z + speed);

            input();

            glfwGetFramebufferSize(window, width,height);

            width.rewind();
            height.rewind();

            glViewport(0,0,width.get(),height.get());
            glClear(GL_COLOR_BUFFER_BIT);

            renderer.prepare();
            shader.start();
            renderer.render(entities,shader, camera);
            shader.stop();

            glfwSwapBuffers(window);
            glfwPollEvents();

            width.flip();
            height.flip();
        }
    }
    private void cleanUp() {
        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);

        shader.cleanUp();
        loader.cleanUp();

        glfwDestroyWindow(window);

        glfwTerminate();
        errorCallback.free();
    }

    @Override
    public void run() {

    }
}