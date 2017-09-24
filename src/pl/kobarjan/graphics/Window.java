package pl.kobarjan.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;
import pl.kobarjan.Input.MouseInput;
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
    Entity entity;
    Entity[] entities;
    MouseInput mouseInput;
    Vector3f cameraInc;
    Camera camera;

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

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        glfwSetWindowPos(window,
                (vidMode.width()-1280)/2,
                (vidMode.height()-720)/2
        );

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(1);
        glfwSetKeyCallback(window, keyCallback);

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

        mouseInput = new MouseInput();
        mouseInput.init(window);

        camera = new Camera();

        entity.setPosition(0,3,-9);
        //entity.setRotation(0,0,0);
        //entity.setScale();
    }
    private void input() {
        mouseInput.input(window);

    }
    private void update() {
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplayVec();
            camera.moveRotation(rotVec.x * 1.05f, rotVec.y * 1.05f, 0);
        }
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

            sync(30);
        }
    }
    private long variableYieldTime, lastTime;

    /**
     * An accurate sync method that adapts automatically
     * to the system it runs on to provide reliable results.
     *
     * @param fps The desired frame rate, in frames per second
     * @author kappa (On the LWJGL Forums)
     */
    public void sync(int fps) {
        if (fps <= 0) return;

        long sleepTime = 1000000000 / fps; // nanoseconds to sleep this frame
        // yieldTime + remainder micro & nano seconds if smaller than sleepTime
        long yieldTime = Math.min(sleepTime, variableYieldTime + sleepTime % (1000*1000));
        long overSleep = 0; // time the sync goes over by

        try {
            while (true) {
                long t = System.nanoTime() - lastTime;

                if (t < sleepTime - yieldTime) {
                    Thread.sleep(1);
                }else if (t < sleepTime) {
                    // burn the last few CPU cycles to ensure accuracy
                    Thread.yield();
                }else {
                    overSleep = t - sleepTime;
                    break; // exit while loop
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            lastTime = System.nanoTime() - Math.min(overSleep, sleepTime);

            // auto tune the time sync should yield
            if (overSleep > variableYieldTime) {
                // increase by 200 microseconds (1/5 a ms)
                variableYieldTime = Math.min(variableYieldTime + 200*1000, sleepTime);
            }
            else if (overSleep < variableYieldTime - 200*1000) {
                // decrease by 2 microseconds
                variableYieldTime = Math.max(variableYieldTime - 2*1000, 0);
            }
        }
    }
    private void cleanUp() {
        MemoryUtil.memFree(width);
        MemoryUtil.memFree(height);

        shader.cleanUp();
        loader.cleanUp();

        glfwDestroyWindow(window);
        keyCallback.free();

        glfwTerminate();
        errorCallback.free();
    }

    @Override
    public void run() {

    }
}