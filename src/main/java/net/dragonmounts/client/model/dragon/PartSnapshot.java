package net.dragonmounts.client.model.dragon;

public class PartSnapshot<T extends ModelPart> {
    // scale multiplier
    public float renderScaleX = 1;
    public float renderScaleY = 1;
    public float renderScaleZ = 1;

    // rotation points
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;

    // rotation angles
    public float preRotateAngleX;
    public float preRotateAngleY;
    public float preRotateAngleZ;

    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;

    // misc meta data
    public boolean hidden;
    public boolean showModel;


    public void save(T part) {
        this.renderScaleX = part.renderScaleX;
        this.renderScaleY = part.renderScaleY;
        this.renderScaleZ = part.renderScaleZ;
        this.rotationPointX = part.rotationPointX;
        this.rotationPointY = part.rotationPointY;
        this.rotationPointZ = part.rotationPointZ;
        this.preRotateAngleX = part.preRotateAngleX;
        this.preRotateAngleY = part.preRotateAngleY;
        this.preRotateAngleZ = part.preRotateAngleZ;
        this.rotateAngleX = part.rotateAngleX;
        this.rotateAngleY = part.rotateAngleY;
        this.rotateAngleZ = part.rotateAngleZ;
        this.hidden = part.isHidden;
        this.showModel = part.showModel;
    }

    public void apply(T part) {
        part.renderScaleX = renderScaleX;
        part.renderScaleY = renderScaleY;
        part.renderScaleZ = renderScaleZ;
        part.rotationPointX = rotationPointX;
        part.rotationPointY = rotationPointY;
        part.rotationPointZ = rotationPointZ;
        part.preRotateAngleX = preRotateAngleX;
        part.preRotateAngleY = preRotateAngleY;
        part.preRotateAngleZ = preRotateAngleZ;
        part.rotateAngleX = rotateAngleX;
        part.rotateAngleY = rotateAngleY;
        part.rotateAngleZ = rotateAngleZ;
        part.isHidden = hidden;
        part.showModel = showModel;
    }
}
