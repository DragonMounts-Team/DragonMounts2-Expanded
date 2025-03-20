package net.dragonmounts.entity.helper;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.Interpolation;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DragonHeadLocator<T extends TameableDragonEntity> implements ITickable {
    public static final int NECK_SEGMENTS = 7;
    public final T dragon;
    public final Segment[] neckSegments = DMUtils.makeArray(new Segment[NECK_SEGMENTS], Segment::new);
    public final Segment head = new Segment();
    // entity parameters
    protected float lookYaw;
    protected float lookPitch;
    protected float speed = 1.0F;
    // timing vars
    protected float anim;
    protected float animBase;
    protected float ground = 1.0F;
    protected float flutter;
    protected float walk;
    protected float sit;
    private boolean wingsDown;

    public DragonHeadLocator(T dragon) {
        this.dragon = dragon;
    }

    public void setLook(float lookYaw, float lookPitch) {
        // don't twist the neck
        this.lookYaw = MathHelper.clamp(lookYaw, (float) -120, (float) 120);
        this.lookPitch = MathHelper.clamp(lookPitch, (float) -90, (float) 90);
    }

    @Override
    public void update() {
        // server side impl:
        TameableDragonEntity dragon = this.dragon;
        // don't move anything during death sequence
        if (dragon.deathTime > 0) return;
        boolean flying = dragon.isFlying();
        boolean onGround = !flying || dragon.isEgg();
        boolean sitting = dragon.isSitting();
        float speedMax = 0.05f;
        float speedEnt = (float) (dragon.motionX * dragon.motionX + dragon.motionZ * dragon.motionZ);
        float speedMulti = MathX.clamp(speedEnt / speedMax);

        // update main animation timer, depend timing speed on movement
        this.anim += onGround
                ? 0.035F
                : 0.070F - speedMulti * 0.035F;
        this.animBase = this.anim * MathX.PI_F * 2;
        // update ground transition
        this.ground = MathX.clamp(onGround ? this.ground * 0.95F + 0.08F : this.ground - 0.1F);
        // update flutter transition
        this.flutter = MathX.clamp(!onGround && (
                dragon.collided || dragon.motionY > -0.1 || speedEnt < speedMax
        ) ? this.flutter + 0.1F : this.flutter - 0.1F);
        // update walking transition
        this.walk = MathX.clamp(sitting ? this.walk - 0.1F : this.walk + 0.1F);
        // update sitting transition
        this.sit = MathX.clamp((sitting ? this.sit + 0.1F : this.sit - 0.1F) * 0.95F);
        // update speed transition
        this.speed = MathX.clamp(onGround ||
                speedEnt > speedMax ||
                dragon.getAltitude() < dragon.height * 2 ||
                dragon.getPassengers().size() > 1 ||
                dragon.isUnHovered()
                ? this.speed + 0.05F
                : this.speed - 0.05F
        );
        // check if the wings are moving down and trigger the event
        boolean wingsDown = MathHelper.sin(this.animBase - 1.0F) > 0.0F;
        if (flying && wingsDown && !this.wingsDown && this.flutter != 0 && !dragon.isInWater()) {
            // play wing sounds
            dragon.playSound(dragon.getWingsSound(), 0.8F + (dragon.getScale() - this.speed), 1, false);
        }
        this.wingsDown = wingsDown;
        this.calculateHeadAndNeck();
    }

    /**
     * Calculate the position, rotation angles, and scale of the head and all segments in the neck
     */
    public void calculateHeadAndNeck() {
        TameableDragonEntity dragon = this.dragon;
        Segment head = this.head;
        Segment segment = this.neckSegments[0];
        segment.posX = 0.0F;
        segment.posY = 14.0F;
        segment.posZ = -8.0F;
        float lastRotY = segment.rotX = segment.rotY = segment.rotZ = 0.0F;
        float rotXFactor = 0.15F
                // basic up/down movement
                * MathX.lerp(1.0F, 0.2F, this.sit)
                // reduce rotation when on ground
                * MathX.slerp(1.0F, 0.5F, this.walk);
        if (dragon.isUsingBreathWeapon()) {
            rotXFactor *= MathX.lerp(0.2F, 1.0F, this.flutter);
        }
        float healthFactor = dragon.getHealth() / dragon.getMaxHealth() * this.ground;
        float speed = this.speed;
        float rotYFactor = MathX.toRadians(this.lookYaw) * speed;
        float base = this.animBase;
        for (int i = 0; i < NECK_SEGMENTS; ) {
            float posX = segment.posX, posY = segment.posY, posZ = segment.posZ;
            float vertMulti = (i + 1) / (float) NECK_SEGMENTS;
            float rotX = segment.rotX = MathHelper.cos(i * 0.45F + base)
                    * rotXFactor
                    // flex neck down when hovering
                    + (1 - speed) * vertMulti
                    // lower neck on low health
                    - MathX.lerp(0.0F, MathHelper.sin(vertMulti * MathX.PI_F * 0.9F) * 0.63F, healthFactor);
            // use looking yaw
            lastRotY = segment.rotY = rotYFactor * vertMulti;
            // update size (scale)
            segment.scaleX = segment.scaleY = MathX.lerp(1.6f, 1, vertMulti);
            segment.scaleZ = 0.6F;
            segment = (++i < NECK_SEGMENTS) ? this.neckSegments[i] : head;
            // move next segment behind the current one
            float neckSize = 0.6F * DragonModel.NECK_SIZE - 1.4F;
            float factor = MathHelper.cos(rotX) * neckSize;
            segment.posX = posX - MathHelper.sin(lastRotY) * factor;
            segment.posY = posY + MathHelper.sin(rotX) * neckSize;
            segment.posZ = posZ - MathHelper.cos(lastRotY) * factor;
        }
        //final float HEAD_TILT_DURING_BREATH = -0.1F;
        head.rotX = MathX.toRadians(this.lookPitch) + (1 - speed); // + breath * HEAD_TILT_DURING_BREATH
        head.rotY = lastRotY;
        head.rotZ = 0.0F;
    }

    /**
     * Calculate the position of the dragon's throat
     * Must have previously called calculateHeadAndNeck()
     *
     * @return the world [x,y,z] of the throat
     */
    public Vec3d getThroatPosition() {
        TameableDragonEntity dragon = this.dragon;
        Segment head = this.head;
        float scale = dragon.getScale();
        final float ADULT_SCALE_FACTOR = 0.1F;//TODO: use DragonType or something else
        final float BODY_X_SCALE = -ADULT_SCALE_FACTOR * scale;
        final float BODY_Y_SCALE = -ADULT_SCALE_FACTOR * scale;
        final float BODY_Z_SCALE = ADULT_SCALE_FACTOR * scale;

        final float headScale = scale * getRelativeHeadSize(scale) * ADULT_SCALE_FACTOR;

        // the head offset plus the headLocation.rotationPoint is the origin of the head, i.e. the point about which the
        //   head rotates, relative to the origin of the body (getPositionEyes)
        final float HEAD_Z_OFFSET = -15;

        final float THROAT_Y_OFFSET = 2;
        final float THROAT_Z_OFFSET = -8;

        final float centerY = -6 * BODY_Y_SCALE;
        final float centerZ = 19 * BODY_Z_SCALE;

        // offset of the throat position relative to the head origin- rotate and pitch to match head
        return new Vec3d(0, THROAT_Y_OFFSET * headScale, THROAT_Z_OFFSET * headScale)
                .rotatePitch(head.rotX)
                .rotateYaw(-head.rotY)
                .add(
                        head.posX * BODY_X_SCALE,
                        head.posY * BODY_Y_SCALE + centerY,
                        (head.posZ + -15) * BODY_Z_SCALE + centerZ
                ).rotatePitch(-MathX.toRadians(this.getPitch()))//rotate body
                .subtract(0, centerY, centerZ)
                .rotateYaw(MathX.PI_F + MathX.toRadians(-dragon.renderYawOffset))
                .add(dragon.posX, dragon.posY + dragon.getEyeHeight(), dragon.posZ);
    }

    /**
     * Baby dragon has a relatively larger head compared to its body size (makes it look cuter)
     */
    public float getRelativeHeadSize(float scale) {
        return 1.6F * MathHelper.clamp(scale, 0.2F, 1.0F) + 0.96F; // 0.96F = 1.6F * 0.6F
    }

    public float getPitch() {
        return Interpolation.smoothStep(60, 0, this.speed);
    }
}
