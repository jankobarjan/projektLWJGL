/*package pl.kobarjan.Input;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import java.nio.DoubleBuffer;

public class MouseInput extends GLFWCursorPosCallback{

    Vector2f rotVector;
    Vector2f currentPos;
    Vector2f previousPos;
    DoubleBuffer xpos;
    DoubleBuffer ypos;
    float mouseSensetive;

    public MouseInput(float mouseSensetive) {
        rotVector = new Vector2f();
        currentPos = new Vector2f();
        previousPos = new Vector2f(0,0);
        this.mouseSensetive = mouseSensetive;
        xpos = BufferUtils.createDoubleBuffer(1);
        ypos = BufferUtils.createDoubleBuffer(1);

    }

    @Override
    public void invoke(long window, double xpos, double ypos) {
        this.xpos = xpos;
    }

    public Vector2f getRotVector() {
        return rotVector;
    }
}*/
