/*
 ** 2011 December 10
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */

package net.dragonmounts.client.model.dragon;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.render.dragon.DragonRenderMode;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * Generic model for all winged tetrapod dragons.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @Modifier James Miller <TheRPGAdventurer.>
 */
public class DragonModel extends ModelBase {
    // model parts
    public final HeadPart head;
    public final ISegmentedPart neck;
    public final ISegmentedPart tail;
    public final LegPart foreLeg;
    public final LegPart hindLeg;
    public final BodyPart body;
    public final WingPart wing;
    public final ModelRenderer chest;
    public final ModelRenderer saddle;

    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float pitch;
    public float partialTicks;

    public DragonModel(IModelFactory factory) {
        factory.defineTextures(this, this::setTextureOffset);
        this.head = factory.makeHead(this);
        this.neck = factory.makeNeck(this);
        this.body = factory.makeBody(this);
        this.body.addChild(this.chest = factory.makeChest(this));
        this.chest.showModel = false;
        this.body.addChild(this.saddle = factory.makeSaddle(this));
        this.saddle.showModel = false;
        this.wing = factory.makeWing(this);
        this.tail = factory.makeTail(this);
        this.foreLeg = factory.makeForeLeg(this);
        this.hindLeg = factory.makeHindLeg(this);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float moveTime, float moveSpeed, float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (entity instanceof ClientDragonEntity) {
            ClientDragonEntity dragon = (ClientDragonEntity) entity;
            DragonAnimator animator = dragon.animator;
            animator.setMovement(limbSwing);
            animator.setLook(netHeadYaw, headPitch);
            animator.animate(this);
            // updateFromAnimator body parts
            this.body.setupAnim(animator);
            this.wing.setupAnim(animator);
            this.tail.setupSegments(animator.tailSegments);
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        this.render(DragonRenderMode.DRAGON, entity, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
    }

    public void render(DragonRenderMode mode, Entity dragon, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(offsetX, offsetY, offsetZ);
        GlStateManager.rotate(-pitch, 1, 0, 0);
        mode.render(this, scale);
        GlStateManager.popMatrix();
    }

    public void renderWings(float scale) {
        scale *= 1.1F;
        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        this.wing.render(scale);
        // mirror following wing
        GlStateManager.scale(-1, 1, 1);
        // switch to back face culling
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        this.wing.render(scale);
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
    }

    public void renderLegs(float scale) {
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        LegPart fore = this.foreLeg, hind = this.hindLeg;
        fore.left.apply(fore);
        fore.render(scale);
        hind.left.apply(hind);
        hind.render(scale);
        // mirror following legs
        GlStateManager.scale(-1, 1, 1);
        // switch to front face culling
        GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
        fore.right.apply(fore);
        fore.render(scale);
        hind.right.apply(hind);
        hind.render(scale);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
    }
}