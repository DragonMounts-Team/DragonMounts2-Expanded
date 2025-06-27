package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.Segment;
import net.dragonmounts.util.math.Interpolation;
import net.dragonmounts.util.math.MathX;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static net.dragonmounts.entity.DragonModelContracts.NECK_SEGMENTS;
import static net.dragonmounts.entity.DragonModelContracts.NECK_SIZE;

public class DragonHeadLocator<T extends TameableDragonEntity> implements ITickable {
    public final T dragon;
    public final Segment[] neckSegments = DMUtils.fillArray(new Segment[NECK_SEGMENTS], Segment::new);
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
        float speedMax = 0.05f;
        float speedEnt = (float) (dragon.motionX * dragon.motionX + dragon.motionZ * dragon.motionZ);
        float speedMulti = MathX.clamp(speedEnt / speedMax);
        // update main animation timer, depend timing speed on movement
        boolean flying = dragon.isFlying();
        this.anim += flying ? 0.070F - speedMulti * 0.035F : 0.035F;
        this.animBase = this.anim * MathX.PI_F * 2;
        // update ground transition
        this.ground = MathX.clamp(flying ? this.ground - 0.1F : this.ground * 0.95F + 0.08F);
        // update flutter transition
        this.flutter = MathX.clamp(flying && (speedEnt < speedMax || dragon.motionY > -0.1)
                ? this.flutter + 0.1F
                : this.flutter - 0.1F
        );
        // update walking and sitting transition
        if (dragon.isSitting()) {
            this.walk = MathX.clamp(this.walk - 0.1F);
            this.sit = MathX.clamp((this.sit + 0.1F) * 0.95F);
        } else {
            this.walk = MathX.clamp(this.walk + 0.1F);
            this.sit = MathX.clamp((this.sit - 0.1F) * 0.95F);
        }
        // update speed transition
        this.speed = MathX.clamp(!flying ||
                speedEnt > speedMax ||
                dragon.isUnHovered() ||
                dragon.getPassengers().size() > 1 ||
                dragon.getAltitude() < dragon.height * 2
                ? this.speed + 0.05F
                : this.speed - 0.05F
        );
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
                * Interpolation.smoothLinear(1.0F, 0.5F, this.walk);
        if (!dragon.isUsingBreathWeapon()) {
            rotXFactor *= MathX.lerp(0.2F, 1.0F, this.flutter);
        }
        float healthFactor = dragon.getHealth() / dragon.getMaxHealth() * this.ground;
        float speed = this.speed;
        float rotYFactor = MathX.toRadians(this.lookYaw) * speed;
        float speedFactor = 1.0F - speed;
        float base = this.animBase;
        for (int i = 0; i < NECK_SEGMENTS; ) {
            float posX = segment.posX, posY = segment.posY, posZ = segment.posZ;
            float vertMulti = (i + 1) / (float) NECK_SEGMENTS;
            float rotX = segment.rotX = MathHelper.cos(i * 0.45F + base)
                    * rotXFactor
                    // flex neck down when hovering
                    + speedFactor * vertMulti
                    // lower neck on low health
                    - MathX.lerp(0.0F, MathHelper.sin(vertMulti * MathX.PI_F * 0.9F) * 0.63F, healthFactor);
            // use looking yaw
            lastRotY = segment.rotY = rotYFactor * vertMulti;
            // update size (scale)
            segment.scaleX = segment.scaleY = MathX.lerp(1.6F, 1.0F, vertMulti);
            segment.scaleZ = 0.6F;
            segment = (++i < NECK_SEGMENTS) ? this.neckSegments[i] : head;
            // move next segment behind the current one
            float neckSize = 0.6F * NECK_SIZE - 1.4F;
            float factor = MathHelper.cos(rotX) * neckSize;
            segment.posX = posX - MathHelper.sin(lastRotY) * factor;
            segment.posY = posY + MathHelper.sin(rotX) * neckSize;
            segment.posZ = posZ - MathHelper.cos(lastRotY) * factor;
        }
        //final float HEAD_TILT_DURING_BREATH = -0.1F;
        head.rotX = MathX.toRadians(this.lookPitch) + speedFactor; // + breath * HEAD_TILT_DURING_BREATH
        head.rotY = lastRotY;
        head.rotZ = 0.0F;
    }

    public Vec3d getHeadRelativeOffset(float x, float y, float z) {
        Segment head = this.head;
        TameableDragonEntity dragon = this.dragon;
        final float scale = dragon.getAdjustedSize();
        final float modelScale = scale * MathX.MOJANG_MODEL_SCALE;
        return new Vec3d(x * modelScale, y * modelScale, -z * modelScale)
                .rotatePitch(head.rotX)
                .rotateYaw(-head.rotY)
                .add(-head.posX * modelScale, -head.posY * modelScale, head.posZ * modelScale)
                .rotatePitch(-MathX.toRadians(this.getPitch()))
                .add(0.0, 0.0, -1.5F * scale)
                .rotateYaw(MathX.PI_F - MathX.toRadians(dragon.renderYawOffset))
                .add(dragon.posX, dragon.posY + scale * (this.getModelOffsetY() + MathX.MOJANG_MODEL_OFFSET_Y), dragon.posZ);
    }

    public final float getModelOffsetY() {
        return 1.5F - 0.6F * this.sit;
    }

    public float getPitch() {
        return Interpolation.smoothLinear(60, 0, this.speed);
    }
}
