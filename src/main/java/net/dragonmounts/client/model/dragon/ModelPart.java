/*
 ** 2012 Februar 10
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;

import static org.lwjgl.opengl.GL11.*;

/**
 * Extended model renderer with some helpful extra methods.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ModelPart extends ModelRenderer {
    public float renderScaleX = 1;
    public float renderScaleY = 1;
    public float renderScaleZ = 1;
    protected boolean compiled;
    protected int displayList;

    public ModelPart(ModelBase base, String name) {
        super(base, name);
    }

    protected void compileDisplayList(float scale) {
        BufferBuilder vb = Tessellator.getInstance().getBuffer();
        displayList = GLAllocation.generateDisplayLists(1);
        glNewList(displayList, GL_COMPILE);
        for (ModelBox cube : this.cubeList) {
            cube.render(vb, scale);
        }
        glEndList();
        compiled = true;
    }

    @Override
    public void render(float scale) {
        renderWithRotation(scale);
    }

    @Override
    public void renderWithRotation(float scale) {
        // skip if hidden
        if (isHidden || !showModel) {
            return;
        }

        // compile if required
        if (!compiled) {
            compileDisplayList(scale);
        }

        GlStateManager.pushMatrix();

        postRender(scale);

        // call display list
        GlStateManager.callList(displayList);

        // render child models
        if (childModels != null) {
            for (ModelRenderer child : this.childModels) {
                child.render(scale);
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void postRender(float scale) {
        // skip if hidden
        if (isHidden || !showModel) {
            return;
        }
        // translate
        GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

        // rotate
        if (rotateAngleZ != 0) {
            GlStateManager.rotate(MathX.toDegrees(rotateAngleZ), 0, 0, 1);
        }
        if (rotateAngleY != 0) {
            GlStateManager.rotate(MathX.toDegrees(rotateAngleY), 0, 1, 0);
        }
        if (rotateAngleX != 0) {
            GlStateManager.rotate(MathX.toDegrees(rotateAngleX), 1, 0, 0);
        }

        // scale
        if (renderScaleX != 1 || renderScaleY != 1 || renderScaleZ != 1) {
            GlStateManager.scale(renderScaleX, renderScaleY, renderScaleZ);
        }
    }

    public static class ScalableSnapshot extends PartSnapshot<ModelPart> {
        public float renderScaleX = 1;
        public float renderScaleY = 1;
        public float renderScaleZ = 1;

        @Override
        public void apply(ModelPart part) {
            super.apply(part);
            part.renderScaleX = this.renderScaleX;
            part.renderScaleY = this.renderScaleY;
            part.renderScaleZ = this.renderScaleZ;
        }

        public static void saveScalable(Segment segment, ScalableSnapshot snapshot) {
            save(segment, snapshot);
            snapshot.renderScaleX = takeIfValid(segment.scaleX, snapshot.renderScaleX);
            snapshot.renderScaleY = takeIfValid(segment.scaleY, snapshot.renderScaleY);
            snapshot.renderScaleZ = takeIfValid(segment.scaleZ, snapshot.renderScaleZ);
        }
    }
}
