/*
 ** 2012 Januar 21
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.client.model.dragon;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.breath.BreathState;
import net.dragonmounts.entity.helper.DragonHeadLocator;
import net.dragonmounts.util.CircularBuffer;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.Interpolation;
import net.dragonmounts.util.math.LinearInterpolation;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;

import static net.dragonmounts.client.model.dragon.WingPart.WING_FINGERS;

/**
 * Animation control class to put useless reptiles in motion.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonAnimator extends DragonHeadLocator<ClientDragonEntity> {
    public static final int JAW_OPENING_TIME_FOR_ATTACK = 5;
    public static final int JAW_OPENING_TIME_FOR_ROAR = 20;
    public final Segment[] tailSegments = DMUtils.fillArray(new Segment[TAIL_SEGMENTS], Segment::new);

    // entity parameters
    private float moveTime;
    private float moveSpeed;
    private double prevRenderYawOffset;
    private double yawAbs;

    // timing vars
    private float cycleOfs;
    private float bite;
    private float breath;
    private float roar;
    public int ticksSinceLastAttack = JAW_OPENING_TIME_FOR_ATTACK;
    public int ticksSinceLastRoar = JAW_OPENING_TIME_FOR_ROAR;

    // timing interp vars
    private final LinearInterpolation animTimer = new LinearInterpolation(0.0F);
    private final LinearInterpolation.Clamped groundTimer = new LinearInterpolation.Clamped(1.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped FlutterTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped walkTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped sitTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped biteTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped breathTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped speedTimer = new LinearInterpolation.Clamped(1.0F, 0.0F, 1.0F);
    private final LinearInterpolation.Clamped roarTimer = new LinearInterpolation.Clamped(0.0F, 0.0F, 1.0F);

    // trails
    public final CircularBuffer yawTrail = new CircularBuffer(16);
    public final CircularBuffer pitchTrail = new CircularBuffer(16);

    // model flags
    private boolean onGround;
    public boolean saddled;

    private final float[] wingFingerRotateY;

    // animation parameters
    private final float[] wingArmFlutter = new float[3];
    private final float[] wingForearmFlutter = new float[3];
    private final float[] wingArmGlide = new float[3];
    private final float[] wingForearmGlide = new float[3];
    private final float[] wingArmGround = new float[3];
    private final float[] wingForearmGround = new float[3];

    // X rotation angles for ground
    // 1st dim - front, hind
    // 2nd dim - thigh, crus, foot, toe
    private static final float[][] xGroundStand = {{0.8f, -1.5f, 1.3f, 0}, {-0.3f, 1.5f, -0.2f, 0},};
    private static final float[][] xGroundSit = {{0.3f, -1.8f, 1.8f, 0}, {-0.8f, 1.8f, -0.9f, 0},};

    // X rotation angles for walking
    // 1st dim - front, hind
    // 2nd dim - animation keyframe
    // 3rd dim - thigh, crus, foot, toe
    private static final float[][][] GROUND_WALKING_FRAMES = {{
            {0.4f, -1.4f, 1.3f, 0}, // move down and forward
            {1.2f, -1.6f, 1.3f, 0}, // move back
            {0.9f, -2.1f, 1.8f, 0.6f} // move up and forward
    }, {
            {0.1f, 1.2f, -0.5f, 0}, // move back
            {-0.3f, 2.1f, -0.9f, 0.6f}, // move up and forward
            {-0.7f, 1.4f, -0.2f, 0} // move down and forward
    }};

    // final X rotation angles for walking
    private final float[] xGroundWalk2 = {0, 0, 0, 0};

    // Y rotation angles for ground, thigh only
    private static final float[] yGroundStand = {-0.25f, 0.25f};
    private static final float[] yGroundSit = {0.1f, 0.35f};
    private static final float[] yGroundWalk = {-0.1f, 0.1f};

    // Y rotation angles for air, thigh only
    private static final float[] yAirAll = {-0.1f, 0.1f};

    // X rotation angles for air
    // 1st dim - front, hind
    // 2nd dim - thigh, crus, foot, toe
    private final float[][] xAirAll = {{0, 0, 0, 0}, {0, 0, 0, 0}};

    private float wingArmRotateAngleX;
    private float wingArmRotateAngleY;
    private float wingArmRotateAngleZ;
    private float wingForearmRotateAngleX;
    private float wingForearmRotateAngleY;
    private float wingForearmRotateAngleZ;

    private static final int TAIL_SEGMENTS = 12;
    // interpolate between folded and unfolded wing angles
    private static final float[] FOLD_FINGER_ROT = new float[]{2.7f, 2.8f, 2.9f, 3.0f};
    private static final float[] UNFOLD_FINGER_ROT = new float[]{0.1f, 0.9f, 1.7f, 2.5f};

    public DragonAnimator(ClientDragonEntity dragon) {
        super(dragon);
        wingFingerRotateY = new float[WING_FINGERS];
        yawTrail.fill(0.0F);
        pitchTrail.fill(0.0F);
    }

    public void setMovement(float moveTime, float moveSpeed) {
        this.moveTime = moveTime;
        this.moveSpeed = moveSpeed;
    }

    /**
     * Updates the dragon component parts - position, angles, scale. Called
     * every frame.
     */
    public void animate(DragonModel model) {
        float partialTicks = model.partialTicks;
        anim = animTimer.get(partialTicks);
        ground = groundTimer.get(partialTicks);
        flutter = FlutterTimer.get(partialTicks);
        walk = walkTimer.get(partialTicks);
        sit = sitTimer.get(partialTicks);
        bite = biteTimer.get(partialTicks);
        breath = breathTimer.get(partialTicks);
        speed = speedTimer.get(partialTicks);
        roar = roarTimer.get(partialTicks);

        animBase = anim * MathX.PI_F * 2;
        float baseOffset = MathHelper.sin(animBase - 1) + 1;
        cycleOfs = (baseOffset * baseOffset + baseOffset * 2) * 0.05F
                // reduce up/down amplitude
                * MathX.lerp(0.5f, 1, flutter) * MathX.lerp(1, 0.5f, ground);

        // animate head and neck
        this.calculateHeadAndNeck();
        model.head.setupAnim(this);
        model.neck.setupAnim(this);

        // animate tail
        this.animTail(partialTicks);

        this.animWings();
        this.animLegs(model);

        // update offsets
        model.offsetX = 0.0F;
        model.offsetY = -1.5F + (sit * 0.6F);
        model.offsetZ = -1.5F;

        // update pitch
        model.pitch = this.getPitch();
    }

    /**
     * Updates the animation state. Called on every tick.
     */
    @Override
    public void update() {
        ClientDragonEntity dragon = this.dragon;
        if (!dragon.isEgg()) {
            this.onGround = !dragon.isFlying();
        }

        // don't move anything during death sequence
        if (dragon.getHealth() <= 0) {
            animTimer.sync();
            groundTimer.sync();
            FlutterTimer.sync();
            biteTimer.sync();
            walkTimer.sync();
            sitTimer.sync();
            roarTimer.sync();
            return;
        }

        this.saddled = dragon.isSaddled();

        float speedMax = 0.05f;
        float speedEnt = (float) (dragon.motionX * dragon.motionX + dragon.motionZ * dragon.motionZ);
        float speedMulti = MathX.clamp(speedEnt / speedMax);

        // update main animation timer
        float animAdd = 0.035f;

        // depend timing speed on movement
        if (!onGround) {
            animAdd += (1 - speedMulti) * animAdd;
        }

        animTimer.add(animAdd);

        // update ground transition
        float groundVal = groundTimer.get();
        if (onGround) {
            groundVal *= 0.95f;
            groundVal += 0.08f;
        } else {
            groundVal -= 0.1f;
        }
        groundTimer.set(groundVal);

        // update Flutter transition
        boolean FlutterFlag = !onGround && (dragon.collided
                || dragon.motionY > -0.1 || speedEnt < speedMax);
        FlutterTimer.add(FlutterFlag ? 0.1f : -0.1f);

        boolean sitting = dragon.isSitting();

        // update sitting transition
        sitTimer.set((sitting ? sitTimer.get() + 0.1F : sitTimer.get() - 0.1F) * 0.95F);

        // update walking transition
        walkTimer.add(moveSpeed > 0.1 && !sitting ? 0.1F : -0.1F);


        // update bite opening transition and breath transitions
        BreathState breathState = dragon.breathHelper.getCurrentBreathState();
        switch (breathState) {
            case IDLE: {  // breath is idle, handle bite attack
                biteTimer.add(this.ticksSinceLastAttack < JAW_OPENING_TIME_FOR_ATTACK ? 0.2F : -0.2F);
                breathTimer.set(0.0F);
                roarTimer.add(this.ticksSinceLastRoar < JAW_OPENING_TIME_FOR_ROAR ? 0.2F : -0.2F);
                break;
            }
            case STARTING: {
                biteTimer.set(0.0F);
                breathTimer.set(dragon.breathHelper.getBreathStateFractionComplete());
                break;
            }
            case STOPPING: {
                float breathStateFractionComplete = dragon.breathHelper.getBreathStateFractionComplete();
                breathTimer.set(1.0F - breathStateFractionComplete);
                break;
            }
            case SUSTAIN: {
                breathTimer.set(1.0F);
                break;
            }
            default: {
                LogUtil.once(Level.ERROR, "unexpected breathstate:" + breathState);
                return;
            }
        }

        // update speed transition
        boolean speedFlag = speedEnt > speedMax ||
                onGround ||
                dragon.getAltitude() < dragon.height * 2 ||
                dragon.getPassengers().size() > 1 ||
                dragon.isUnHovered();
        float speedValue = 0.05f;
        speedTimer.add(speedFlag ? speedValue : -speedValue);

        // update trailers
        double yawDiff = dragon.renderYawOffset - prevRenderYawOffset;
        prevRenderYawOffset = dragon.renderYawOffset;

        // filter out 360 degrees wrapping
        if (yawDiff < 180 && yawDiff > -180) {
            yawAbs += yawDiff;
        }

        //yTrail.update(entity.posY - entity.getYOffset());
        yawTrail.update((float) yawAbs);
        pitchTrail.update(getBodyPitch(0.0F));

        if (this.ticksSinceLastAttack < JAW_OPENING_TIME_FOR_ATTACK) {
            ++this.ticksSinceLastAttack;
        }
        if (this.ticksSinceLastRoar < JAW_OPENING_TIME_FOR_ROAR) {
            ++this.ticksSinceLastRoar;
        }
    }

    public float getFlutterTime() {
        return flutter;
    }

    public float getWalkTime() {
        return walk;
    }

    protected void animWings() {
        // move wings slower while sitting
        float aSpeed = sit > 0 ? 0.6f : 1;
        float base = animBase;
        // animation speeds
        float a1 = base * aSpeed * 0.35f;
        float a2 = base * aSpeed * 0.5f;
        float a3 = base * aSpeed * 0.75f;

        if (ground < 1) {
            // Hovering
            wingArmFlutter[0] = 0.125f - MathHelper.cos(base) * 0.2f;
            wingArmFlutter[1] = 0.25f;
            wingArmFlutter[2] = (MathHelper.sin(base) + 0.125f) * 0.8f;

            wingForearmFlutter[0] = 0;
            wingForearmFlutter[1] = -wingArmFlutter[1] * 2;
            wingForearmFlutter[2] = -(MathHelper.sin(base + 2) + 0.5f) * 0.75f;

            // gliding
            wingArmGlide[0] = -0.25f - MathHelper.cos(base * 2) * MathHelper.cos(base * 1.5f) * 0.04f;
            wingArmGlide[1] = 0.25f;
            wingArmGlide[2] = 0.35f + MathHelper.sin(base) * 0.05f;

            wingForearmGlide[0] = 0;
            wingForearmGlide[1] = -wingArmGlide[1] * 2;
            wingForearmGlide[2] = -0.25f + (MathHelper.sin(base + 2) + 0.5f) * 0.05f;
        }

        if (ground > 0) {
            // standing
            wingArmGround[0] = 0;
            wingArmGround[1] = 1.4f - MathHelper.sin(a1) * MathHelper.sin(a2) * 0.02f;
            wingArmGround[2] = 0.8f + MathHelper.sin(a2) * MathHelper.sin(a3) * 0.05f;

            // walking
            wingArmGround[1] += MathHelper.sin(moveTime * 0.5f) * 0.02f * walk;
            wingArmGround[2] += MathHelper.cos(moveTime * 0.5f) * 0.05f * walk;

            wingForearmGround[0] = 0;
            wingForearmGround[1] = -wingArmGround[1] * 2;
            wingForearmGround[2] = 0;
        }

        float[] wingRot = new float[3];
        float[] armRot = new float[3];
        // interpolate between Fluttering and gliding
        MathX.slerpArrays(wingArmGlide, wingArmFlutter, wingRot, flutter);
        MathX.slerpArrays(wingForearmGlide, wingForearmFlutter, armRot, flutter);

        // interpolate between flying and grounded
        MathX.slerpArrays(wingRot, wingArmGround, wingRot, ground);
        MathX.slerpArrays(armRot, wingForearmGround, armRot, ground);

        // apply angles
        wingArmRotateAngleX = wingRot[0];
        wingArmRotateAngleY = wingRot[1] * MathHelper.cos(1 - speed);
        wingArmRotateAngleZ = wingRot[2];
        wingForearmRotateAngleX = armRot[0];
        wingForearmRotateAngleY = armRot[1];
        wingForearmRotateAngleZ = armRot[2];

        // set wing finger angles
        float rotYOfs = MathHelper.sin(a1) * MathHelper.sin(a2) * 0.03f;
        float rotYMulti = 1;

        for (int i = 0; i < WING_FINGERS; ++i) {
            wingFingerRotateY[i] = Interpolation.smoothLinear(UNFOLD_FINGER_ROT[i], FOLD_FINGER_ROT[i] + rotYOfs * rotYMulti, ground);
            rotYMulti -= 0.2f;
        }
    }

    protected void animTail(float partialTicks) {
        Segment segment = this.tailSegments[0];
        segment.posX = 0;
        segment.posY = 16;
        segment.posZ = 62;
        segment.rotX = segment.rotY = segment.rotZ = 0.0F;
        float sit = this.sit;
        float base = this.animBase;
        float flutterFactor = 0.04F * MathX.lerp(0.3F, 1.0F, this.flutter);
        float ground = this.ground;
        float speedFactor = 2.0F - 2.0F * this.speed;
        float rotYStand = 0;
        float rotXAir = 0;
        for (int i = 0; i < TAIL_SEGMENTS; ++i) {
            float vertMulti = (i + 1) / (float) TAIL_SEGMENTS;

            // idle
            float amp = 0.1f + i / (TAIL_SEGMENTS * 2f);

            rotYStand = (rotYStand + MathHelper.sin(i * 0.45f + base * 0.5f)) * amp * 0.4f;
            float rotX = ((
                    i - TAIL_SEGMENTS * 0.6F) * -amp * 0.4F +
                    (MathHelper.sin(base * 0.2F) * MathHelper.sin(base * 0.37F) * 0.4F * amp - 0.1F) * (1 - sit)
            ) * (sit * -0.2F + 1.0F); // sit = 0.8 * stand
            // interpolate between sitting and standing
            float rotY = MathX.lerp(
                    rotYStand,
                    MathHelper.sin(vertMulti * MathX.PI_F) * MathX.PI_F * 1.2F - 0.5F, // curl to the left
                    sit
            );
            rotXAir -= MathHelper.sin(i * 0.45F + base) * flutterFactor;

            // body movement
            float limit = 80 * vertMulti;
            float yawOfs = yawTrail.getClamped(partialTicks, 0, i + 1, limit) * 2;
            float pitchOfs = pitchTrail.getClamped(partialTicks, 0, i + 1, limit) * 2;

            // interpolate between flying and grounded
            rotX = segment.rotX = MathX.lerp(rotXAir, rotX, ground)
                    + MathX.toRadians(pitchOfs)
                    - speedFactor * vertMulti;
            rotY = segment.rotY = MathX.lerp(0, rotY, ground) + MathX.toRadians(180 - yawOfs);

            // update scale
            float scale = segment.scaleX = segment.scaleY = segment.scaleZ = MathX.lerp(1.5F, 0.3F, vertMulti);
            // move next segment behind the current one
            if (i + 1 == TAIL_SEGMENTS) return;
            float posX = segment.posX, posY = segment.posY, posZ = segment.posZ;
            float tailSize = DragonModel.TAIL_SIZE * scale - 0.7F;
            float cosFactor = MathHelper.cos(rotX) * tailSize;
            segment = this.tailSegments[i + 1];
            segment.posX = posX - MathHelper.sin(rotY) * cosFactor;
            segment.posY = posY + MathHelper.sin(rotX) * tailSize;
            segment.posZ = posZ - MathHelper.cos(rotY) * cosFactor;
        }
    }

    protected void animLegs(DragonModel model) {
        // dangling legs for flying
        float move = this.moveTime * 0.2F;
        if (ground < 1) {
            float footAirOfs = cycleOfs * 0.1f;
            float footAirX = 0.75f + footAirOfs;

            xAirAll[0][0] = 1.3f + footAirOfs;
            xAirAll[0][1] = -(0.7f * speed + 0.1f + footAirOfs);
            xAirAll[0][2] = footAirX;
            xAirAll[0][3] = footAirX * 0.5f;

            xAirAll[1][0] = footAirOfs + 0.6f;
            xAirAll[1][1] = footAirOfs + 0.8f;
            xAirAll[1][2] = footAirX;
            xAirAll[1][3] = footAirX * 0.5f;
        }

        this.animLeg(model.foreLeg, move, 0, false);
        this.animLeg(model.hindLeg, move, 1, false);
        this.animLeg(model.foreLeg, move, 0, true);
        this.animLeg(model.hindLeg, move, 1, true);
    }

    /**
     * @param index fore: 0, hind: 1
     */
    protected void animLeg(LegPart leg, float move, int index, boolean left) {
        // final X rotation angles for air
        float[] xAir = xAirAll[index];
        // interpolate between sitting and standing
        float[] rot = new float[4];
        MathX.slerpArrays(xGroundStand[index], xGroundSit[index], rot, this.sit);

        // align the toes so they're always horizontal on the ground
        rot[3] = -(rot[0] + rot[1] + rot[2]);

        // apply walking cycle
        float walk = this.walk;
        if (walk > 0) {
            // interpolate between the keyframes, based on the cycle
            Interpolation.splineArrays(move, left, xGroundWalk2, GROUND_WALKING_FRAMES[index]);
            // align the toes so they're always horizontal on the ground
            xGroundWalk2[3] -= xGroundWalk2[0] + xGroundWalk2[1] + xGroundWalk2[2];

            MathX.slerpArrays(rot, xGroundWalk2, rot, walk);
        }

        float yAir = yAirAll[index];
        float yGround;

        // interpolate between sitting and standing
        yGround = Interpolation.smoothLinear(yGroundStand[index], yGroundSit[index], this.sit);

        // interpolate between standing and walking
        yGround = Interpolation.smoothLinear(yGround, yGroundWalk[index], walk);

        // interpolate between flying and grounded
        float ground = this.ground;
        leg.rotateAngleY = Interpolation.smoothLinear(yAir, yGround, ground);
        leg.rotateAngleX = Interpolation.smoothLinear(xAir[0], rot[0], ground);
        leg.shank.rotateAngleX = Interpolation.smoothLinear(xAir[1], rot[1], ground);
        leg.foot.rotateAngleX = Interpolation.smoothLinear(xAir[2], rot[2], ground);
        leg.toe.rotateAngleX = Interpolation.smoothLinear(xAir[3], rot[3], ground);
        (left ? leg.left : leg.right).save(leg);
    }

    @Deprecated
    public float getBodyPitch(float pt) {
        return this.getPitch();
       /* float pitchMovingMax = 90;
        float pitchMoving = MathX.clamp(yTrail.get(pt, 5, 0) * 10, -pitchMovingMax, pitchMovingMax);
        float pitchHoverMax = 60;
        boolean unhover = dragon.dragonInv.getStackInSlot(33) != null || dragon.dragonInv.getStackInSlot(34) != null
                || dragon.getPassengers().size() > 1 || dragon.isUnHovered() || dragon.boosting();
        return Interpolation.smoothStep(pitchHoverMax, unhover ? 0 : pitchMoving, speed);*/
    }

    public float getJawRotateAngleX() {
        final float BITE_ANGLE = 0.72F;
        final float ROAR_ANGLE = 0.58F;
        final float BREATH_ANGLE = 0.67F;
        return (bite * BITE_ANGLE + breath * BREATH_ANGLE + roar * ROAR_ANGLE) + (1 - MathHelper.sin(animBase)) * 0.1f * flutter;
    }

    public float getSpeed() {
        return speed;
    }

    public float getWingFingerRotateY(int index) {
        return wingFingerRotateY[index];
    }

    public float getWingArmRotateAngleX() {
        return wingArmRotateAngleX;
    }

    public float getWingArmRotateAngleY() {
        return wingArmRotateAngleY;
    }

    public float getWingArmRotateAngleZ() {
        return wingArmRotateAngleZ;
    }

    public float getWingForearmRotateAngleX() {
        return wingForearmRotateAngleX;
    }

    public float getWingForearmRotateAngleY() {
        return wingForearmRotateAngleY;
    }

    public float getWingForearmRotateAngleZ() {
        return wingForearmRotateAngleZ;
    }
}