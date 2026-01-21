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
public class ScalablePart extends ModelRenderer {
    public float renderScaleX = 1;
    public float renderScaleY = 1;
    public float renderScaleZ = 1;
    protected boolean compiled;
    protected int displayList;

    public ScalablePart(ModelBase base, String name) {
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

    public static class Snapshot extends PartSnapshot<ScalablePart> {
        public float renderScaleX;
        public float renderScaleY;
        public float renderScaleZ;

        public Snapshot(float scaleX, float scaleY, float scaleZ) {
            this.renderScaleX = scaleX;
            this.renderScaleY = scaleY;
            this.renderScaleZ = scaleZ;
        }

        @Override
        public void apply(ScalablePart part) {
            super.apply(part);
            part.renderScaleX = this.renderScaleX;
            part.renderScaleY = this.renderScaleY;
            part.renderScaleZ = this.renderScaleZ;
        }
    }
}
