package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelRenderer;

public class PartSnapshot<T extends ModelRenderer> {
    // rotation points
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;

    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;

    // misc meta data
    public boolean hidden;
    public boolean showModel;

    public void save(T part) {
        this.rotationPointX = part.rotationPointX;
        this.rotationPointY = part.rotationPointY;
        this.rotationPointZ = part.rotationPointZ;
        this.rotateAngleX = part.rotateAngleX;
        this.rotateAngleY = part.rotateAngleY;
        this.rotateAngleZ = part.rotateAngleZ;
        this.hidden = part.isHidden;
        this.showModel = part.showModel;
    }

    public void apply(T part) {
        part.rotationPointX = rotationPointX;
        part.rotationPointY = rotationPointY;
        part.rotationPointZ = rotationPointZ;
        part.rotateAngleX = rotateAngleX;
        part.rotateAngleY = rotateAngleY;
        part.rotateAngleZ = rotateAngleZ;
        part.isHidden = hidden;
        part.showModel = showModel;
    }
}
