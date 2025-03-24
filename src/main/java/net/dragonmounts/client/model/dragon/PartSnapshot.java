package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;
import net.minecraft.client.model.ModelRenderer;

public class PartSnapshot<T extends ModelRenderer> {
    // rotation points
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;

    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;

    public void apply(T part) {
        part.rotationPointX = rotationPointX;
        part.rotationPointY = rotationPointY;
        part.rotationPointZ = rotationPointZ;
        part.rotateAngleX = rotateAngleX;
        part.rotateAngleY = rotateAngleY;
        part.rotateAngleZ = rotateAngleZ;
    }

    /**
     * @return neo if valid (not NaN), old otherwise
     */
    public static float takeIfValid(float neo, float old) {
        return Float.isNaN(neo) ? old : neo;
    }

    public static void save(Segment segment, PartSnapshot<?> snapshot) {
        snapshot.rotationPointX = takeIfValid(segment.posX, snapshot.rotationPointX);
        snapshot.rotationPointY = takeIfValid(segment.posY, snapshot.rotationPointY);
        snapshot.rotationPointZ = takeIfValid(segment.posZ, snapshot.rotationPointZ);
        snapshot.rotateAngleX = takeIfValid(segment.rotX, snapshot.rotateAngleX);
        snapshot.rotateAngleY = takeIfValid(segment.rotY, snapshot.rotateAngleY);
        snapshot.rotateAngleZ = takeIfValid(segment.rotZ, snapshot.rotateAngleZ);
    }
}
