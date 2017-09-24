package pl.kobarjan.entities;

import org.joml.Vector3f;
import pl.kobarjan.models.TexturedModel;

public class Entity {

    private final TexturedModel model;
    private Vector3f position;
    private float scale;
    private final Vector3f rotation;

    public Entity(TexturedModel model) {
        this.model = model;
        position = new Vector3f(0, 0, 0);
        scale = 5f;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x,float y,float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public TexturedModel getModel() {
        return model;
    }
}