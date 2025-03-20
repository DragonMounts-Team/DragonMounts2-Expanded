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
import net.dragonmounts.client.variant.VariantAppearance;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.Interpolation;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import static net.dragonmounts.client.ClientUtil.withRotation;

/**
 * Generic model for all winged tetrapod dragons.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @Modifier James Miller <TheRPGAdventurer.>
 */
public class DragonModel extends ModelBase {

    // model constants
    public static final int NECK_SIZE=10;
    public static final int TAIL_SIZE=10;
    public static final int VERTS_TAIL=12;
    public static final int HEAD_OFS=-16;

    // model parts
    public ModelPart head;
    public final NeckPart neck;
    public final TailPart tail;
    public final LegPart foreLeg;
    public final LegPart hindLeg;
    public ModelRenderer jaw;
    public ModelRenderer body;
    public ModelRenderer back;
    public ModelRenderer chest;
    public ModelRenderer saddle;
    public ModelRenderer wingArm;
    public ModelRenderer wingForearm;
    public final ModelRenderer[] wingFinger = new ModelRenderer[4];

    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public float pitch;
    public float partialTicks;
    public final VariantAppearance appearance;

    // final X rotation angles for ground
    private final float[] xGround = {0, 0, 0, 0};

    // X rotation angles for ground
    // 1st dim - front, hind
    // 2nd dim - thigh, crus, foot, toe
    private final float[][] xGroundStand = {{0.8f, -1.5f, 1.3f, 0}, {-0.3f, 1.5f, -0.2f, 0},};
    private final float[][] xGroundSit = {{0.3f, -1.8f, 1.8f, 0}, {-0.8f, 1.8f, -0.9f, 0},};

    // X rotation angles for walking
    // 1st dim - animation keyframe
    // 2nd dim - front, hind
    // 3rd dim - thigh, crus, foot, toe
    private final float[][][] xGroundWalk = {{{0.4f, -1.4f, 1.3f, 0}, // move down and forward
            {0.1f, 1.2f, -0.5f, 0} // move back
    }, {{1.2f, -1.6f, 1.3f, 0}, // move back
            {-0.3f, 2.1f, -0.9f, 0.6f} // move up and forward
    }, {{0.9f, -2.1f, 1.8f, 0.6f}, // move up and forward
            {-0.7f, 1.4f, -0.2f, 0} // move down and forward
    }};

    // final X rotation angles for walking
    private final float[] xGroundWalk2 = {0, 0, 0, 0};

    // Y rotation angles for ground, thigh only
    private final float[] yGroundStand = {-0.25f, 0.25f};
    private final float[] yGroundSit = {0.1f, 0.35f};
    private final float[] yGroundWalk = {-0.1f, 0.1f};

    // X rotation angles for air
    // 1st dim - front, hind
    // 2nd dim - thigh, crus, foot, toe
    private final float[][] xAirAll = {{0, 0, 0, 0}, {0, 0, 0, 0}};

    // Y rotation angles for air, thigh only
    private final float[] yAirAll = {-0.1f, 0.1f};

    public DragonModel(VariantAppearance appearance, IModelFactory factory) {
        textureWidth=256;
        textureHeight=256;

        this.appearance = appearance;

        setTextureOffset("body.body", 0, 0);
        setTextureOffset("body.scale", 0, 32);
        setTextureOffset("saddle.cushion", 184, 98);
        setTextureOffset("saddle.front", 214, 120);
        setTextureOffset("saddle.back", 214, 120);
        setTextureOffset("saddle.tie", 220, 100);
        setTextureOffset("saddle.metal", 224, 132);
        setTextureOffset("chest.left", 192, 132);
        setTextureOffset("chest.right", 224, 132);
        setTextureOffset("head.nostril", 48, 0);
        setTextureOffset("head.mainhead", 0, 0);
        setTextureOffset("head.upperjaw", 56, 88);
        setTextureOffset("head.lowerjaw", 0, 88);
        setTextureOffset("head.horn", 28, 32);
        setTextureOffset("foreLeg.thigh", 112, 0);
        setTextureOffset("foreLeg.shank", 148, 0);
        setTextureOffset("foreLeg.foot", 210, 0);
        setTextureOffset("foreLeg.toe", 176, 0);
        setTextureOffset("hindLeg.thigh", 112, 29);
        setTextureOffset("hindLeg.shank", 152, 29);
        setTextureOffset("hindLeg.foot", 180, 29);
        setTextureOffset("hindLeg.toe", 215, 29);
        setTextureOffset("neck.box", 112, 88);
        setTextureOffset("neck.scale", 0, 0);
        setTextureOffset("tail.box", 152, 88);
        setTextureOffset("tail.scale", 0, 0);
        setTextureOffset("tail.horn", 0, 117);
        setTextureOffset("wingarm.bone", 0, 152);
        setTextureOffset("wingarm.skin", 116, 232);
        setTextureOffset("wingfinger.bone", 0, 172);
        setTextureOffset("wingfinger.shortskin", -32, 224);
        setTextureOffset("wingfinger.skin", -49, 176);
        setTextureOffset("wingforearm.bone", 0, 164);

        buildBody();
        this.neck = factory.makeNeck(this);
        buildHead();
        this.tail = factory.makeTail(this);
        buildWing();
        this.foreLeg = factory.makeForeLeg(this);
        this.hindLeg = factory.makeHindLeg(this);
    }

    private void buildHead() {
        head = new ModelPart(this, "head");
        head.addBox("upperjaw",  -6, -1,   -8 + HEAD_OFS, 12,  5, 16);
        head.addBox("mainhead", -8, -8,    6 + HEAD_OFS, 16, 16, 16); // 6
        head.addBox("nostril",   -5, -3,   -6 + HEAD_OFS,  2,  2,  4);
        head.mirror = true;
        head.addBox("nostril",    3,  -3,  -6 + HEAD_OFS,  2,  2,  4);

        buildHorn(false);
        buildHorn(true);

        this.head.addChild(this.jaw = new ModelRenderer(this, "head")
                .addBox("lowerjaw", -6, 0, -16, 12, 4, 16)
        );
        this.jaw.setRotationPoint(0, 4, 8 + HEAD_OFS);
    }

    private void buildHorn(boolean mirror) {
        int hornThick=3;
        int hornLength=12;

        float hornOfs=-(hornThick / 2f);

        float hornPosX = -5;
        float hornPosY = -8;
        float hornPosZ = 0;

        float hornRotX=MathX.toRadians(30);
        float hornRotY=MathX.toRadians(-30);
        float hornRotZ=0;

        if (mirror) {
            hornPosX*=-1;
            hornRotY*=-1;
        }

        head.mirror=mirror;
        ModelRenderer horn;
        this.head.addChild(horn = new ModelRenderer(this, "head")
                .addBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength)
        );
        withRotation(horn, hornRotX, hornRotY, hornRotZ).setRotationPoint(hornPosX, hornPosY, hornPosZ);
    }

    private void buildBody() {
        this.body = new ModelRenderer(this, "body");
        this.body.addBox("body", -12, 0, -16, 24, 24, 64)
                .addBox("scale", -1, -6, 10, 2, 6, 12)
                .addBox("scale", -1, -6, 30, 2, 6, 12)
                .setRotationPoint(0, 4, 8);
        this.body.addChild(this.back = new ModelRenderer(this, "body")
                .addBox("scale", -1, -6, -10, 2, 6, 12)
        );
        this.body.addChild(this.saddle = new ModelRenderer(this, "saddle")
                .addBox("cushion", -7, -2, -15, 15, 3, 20)
                .addBox("tie", 12, 0, -14, 1, 14, 2) // left
                .addBox("tie", -13, 0, -14, 1, 10, 2) // right
                .addBox("metal", 12, 14, -15, 1, 5, 4) // left
                .addBox("metal", -13, 10, -15, 1, 5, 4) // right
                .addBox("front", -3, -3, -14, 6, 1, 2)
                .addBox("back", -6, -4, 2, 13, 2, 2)
        );
        this.saddle.showModel = false;
        this.body.addChild(this.chest = new ModelRenderer(this, "chest")
                .addBox("left", 12, 0, 21, 4, 12, 12)
                .addBox("right", -16, 0, 21, 4, 12, 12)
        );
        this.chest.showModel = false;
    }

    private void buildWing() {
        wingArm=new ModelPart(this, "wingarm");
        wingArm.setRotationPoint(-10, 5, 4);
        wingArm.addBox("bone", -28, -3, -3, 28, 6, 6);
        wingArm.addBox("skin", -28, 0, 2, 28, 0, 24);

        wingForearm = new ModelRenderer(this, "wingforearm");
        wingForearm.setRotationPoint(-28, 0, 0);
        wingForearm.addBox("bone", -48, -2, -2, 48, 4, 4);
        wingArm.addChild(wingForearm);

        wingFinger[0]=buildWingFinger(false);
        wingFinger[1]=buildWingFinger(false);
        wingFinger[2]=buildWingFinger(false);
        wingFinger[3]=buildWingFinger(true);
    }

    private ModelRenderer buildWingFinger(boolean small) {
        ModelRenderer finger = new ModelRenderer(this, "wingfinger");
        finger.setRotationPoint(-47, 0, 0);
        finger.addBox("bone", -70, -1, -1, 70, 2, 2);
        if (small) {
            finger.addBox("shortskin", -70, 0, 1, 70, 0, 32);
        } else {
            finger.addBox("skin", -70, 0, 1, 70, 0, 48);
        }
        wingForearm.addChild(finger);
        return finger;
    }

    /**
     * Applies the animations on the model. Called every frame before the model
     * is rendered.
     */
    private void updateFromAnimator(DragonAnimator animator) {
        // update offsets
        offsetX = animator.getModelOffsetX();
        offsetY = animator.getModelOffsetY();
        offsetZ = animator.getModelOffsetZ();

        // update pitch
        pitch = animator.getBodyPitch(this.partialTicks);

        // updateFromAnimator body parts
        animHeadAndNeck(animator);
        animTail(animator);
        animWings(animator);
        animLegs(animator);
    }

    protected void animHeadAndNeck(DragonAnimator animator) {
        Segment[] segments = animator.neckSegments;
        NeckPart neck = this.neck;
        for (int i = 0; i < segments.length; ++i) {
            neck.applySegment(segments[i]);
            // hide the first and every second scale
            neck.scale.isHidden = (i & 1) == 1 || i == 0;
            neck.save(i);
        }
        this.head.applySegment(animator.head);
        jaw.rotateAngleX = animator.getJawRotateAngleX();
    }

    protected void animWings(DragonAnimator animator) {
        // apply angles
        wingArm.rotateAngleX = animator.getWingArmRotateAngleX();
        wingArm.rotateAngleY = animator.getWingArmRotateAngleY();
        wingArm.rotateAngleZ = animator.getWingArmRotateAngleZ();
        wingForearm.rotateAngleX = animator.getWingForearmRotateAngleX();
        wingForearm.rotateAngleY = animator.getWingForearmRotateAngleY();
        wingForearm.rotateAngleZ = animator.getWingForearmRotateAngleZ();

        // set wing finger angles
        for (int i=0; i < wingFinger.length; i++) {
            wingFinger[i].rotateAngleX = animator.getWingFingerRotateX(i);
            wingFinger[i].rotateAngleY = animator.getWingFingerRotateY(i);
        }

    }

    protected void animTail(DragonAnimator animator) {
        Segment[] segments = animator.tailSegments;
        final int TAIL_SEGMENTS = segments.length;
        TailPart tail = this.tail;
        for (int i = 0; i < segments.length; ++i) {
            // display horns near the tip
            tail.leftHorn.isHidden = tail.rightHorn.isHidden = !(i > TAIL_SEGMENTS - 7 && i < TAIL_SEGMENTS - 3);
            tail.applySegment(segments[i]);
            tail.save(i);
        }
    }

    // left this in DragonModel because it isn't really needed by the server and is difficult to move.
    protected void animLegs(DragonAnimator animator) {
        // dangling legs for flying
        float ground = animator.getGroundTime();
        float speed = animator.getSpeed();
        float walk = animator.getWalkTime();
        float sit = animator.getSitTime();
        float cycleOfs = animator.getCycleOfs();
        float move = animator.getMoveTime() * 0.2F;
        if (ground < 1) {
            float footAirOfs=cycleOfs * 0.1f;
            float footAirX=0.75f + cycleOfs * 0.1f;

            xAirAll[0][0]=1.3f + footAirOfs;
            xAirAll[0][1]=-(0.7f * speed + 0.1f + footAirOfs);
            xAirAll[0][2]=footAirX;
            xAirAll[0][3]=footAirX * 0.5f;

            xAirAll[1][0]=footAirOfs + 0.6f;
            xAirAll[1][1]=footAirOfs + 0.8f;
            xAirAll[1][2]=footAirX;
            xAirAll[1][3]=footAirX * 0.5f;
        }

        this.animLeg(this.foreLeg, ground, walk, sit, move, 0, false);
        this.animLeg(this.hindLeg, ground, walk, sit, move, 1, false);
        this.animLeg(this.foreLeg, ground, walk, sit, move, 0, true);
        this.animLeg(this.hindLeg, ground, walk, sit, move, 1, true);
    }

    /**
     * @param index fore: 0, hind: 1
     */
    protected void animLeg(LegPart leg, float ground, float walk, float sit, float move, int index, boolean left) {
        //leg.rotationPointZ = index == 0 ? 4 : 46;
        // final X rotation angles for air
        float[] xAir = xAirAll[index];
        // interpolate between sitting and standing
        MathX.slerpArrays(xGroundStand[index], xGroundSit[index], xGround, sit);

        // align the toes so they're always horizontal on the ground
        xGround[3] = -(xGround[0] + xGround[1] + xGround[2]);

        // apply walking cycle
        if (walk > 0) {
            // interpolate between the keyframes, based on the cycle
            Interpolation.splineArrays(move, left, xGroundWalk2, xGroundWalk[0][index], xGroundWalk[1][index], xGroundWalk[2][index]);
            // align the toes so they're always horizontal on the ground
            xGroundWalk2[3] -= xGroundWalk2[0] + xGroundWalk2[1] + xGroundWalk2[2];

            MathX.slerpArrays(xGround, xGroundWalk2, xGround, walk);
        }

        float yAir = yAirAll[index];
        float yGround;

        // interpolate between sitting and standing
        yGround = MathX.slerp(yGroundStand[index], yGroundSit[index], sit);

        // interpolate between standing and walking
        yGround = MathX.slerp(yGround, yGroundWalk[index], walk);

        // interpolate between flying and grounded
        leg.rotateAngleY = MathX.slerp(yAir, yGround, ground);
        leg.rotateAngleX = MathX.slerp(xAir[0], xGround[0], ground);
        leg.shank.rotateAngleX = MathX.slerp(xAir[1], xGround[1], ground);
        leg.foot.rotateAngleX = MathX.slerp(xAir[2], xGround[2], ground);
        leg.toe.rotateAngleX = MathX.slerp(xAir[3], xGround[3], ground);
        (left ? leg.left : leg.right).save(leg);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float moveTime, float moveSpeed, float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
        if (entity instanceof ClientDragonEntity) {
            DragonAnimator animator = ((ClientDragonEntity) entity).animator;
            animator.setMovement(limbSwing, limbSwingAmount);
            animator.setLook(netHeadYaw, headPitch);
            animator.animate(this.partialTicks);
        }
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entity, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        this.render(DragonRenderMode.DRAGON, (ClientDragonEntity) entity, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
    }

    public void render(DragonRenderMode mode, ClientDragonEntity dragon, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        // update flags
        back.isHidden = dragon.isSaddled();
        boolean flag = this.appearance.hasSideTailScale(dragon);
        TailPart tail = this.tail;
        tail.middleScale.showModel = !flag;
        tail.leftScale.showModel = tail.rightScale.showModel = flag;
        tail.leftHorn.showModel = tail.rightHorn.showModel = this.appearance.hasTailHorns(dragon);
        this.updateFromAnimator(dragon.animator);
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
        wingArm.render(scale);
        // mirror following wing
        GlStateManager.scale(-1, 1, 1);
        // switch to back face culling
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        wingArm.render(scale);
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