package pl.kobarjan.util;

import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation,float angle,float rx,float ry,float rz,float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation.x,translation.y,translation.z);
        matrix.rotate(angle, rx,ry,rz);
        matrix.scale(scale,scale,scale);
        return matrix;
    }
}
