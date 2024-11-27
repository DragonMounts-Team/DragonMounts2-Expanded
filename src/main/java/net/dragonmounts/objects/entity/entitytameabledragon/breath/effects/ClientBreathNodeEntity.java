package net.dragonmounts.objects.entity.entitytameabledragon.breath.effects;

import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public abstract class ClientBreathNodeEntity extends Entity {
    public static final float NORMAL_PARTICLE_CHANCE = 0.1F;
    public static final float SPECIAL_PARTICLE_CHANCE = 0.3F;
    public static final float MAX_ALPHA = 0.99F;
    public float scale;
    protected final BreathNode node;
    public final boolean extraRotation;

    public ClientBreathNodeEntity(
            World world,
            Vec3d position,
            Vec3d direction,
            BreathNode.Power power,
            float partialTicksHeadStart,
            boolean extraRotation
    ) {
        super(world);
        BreathNode node = new BreathNode(power);
        node.randomiseProperties(this.rand);
        Vec3d motion = node.getRandomisedStartingMotion(direction.normalize(), this.rand);
        this.setPosition(
                position.x + motion.x * partialTicksHeadStart,
                position.y + motion.y * partialTicksHeadStart,
                position.z + motion.z * partialTicksHeadStart
        );
        this.scale = (this.rand.nextFloat() * 0.7F + 0.7F) * 4.0F;
        this.node = node;
        this.extraRotation = extraRotation;
        //undo random velocity variation of vanilla EntityFX constructor
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
    }

    /**
     * call once per tick to update the EntityFX size, position, collisions, etc
     */
    @Override
    public void onUpdate() {
        BreathNode node = this.node;
        this.scale = node.getCurrentRenderDiameter();
        this.handleMovement();
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        float size = node.getCurrentAABBcollisionSize();
        EntityUtil.moveAndResize(this, this.motionX, this.motionY, this.motionZ, size, size);
        if (this.collided && this.onGround) {
            this.motionY -= 0.01F;// ensure that we hit the ground next time too
        }
        node.updateAge(this);
        if (node.isDead()) {
            this.setDead();
        }
    }

    protected void handleMovement() {
        // spawn a smoke trail after some time
        if (this.rand.nextFloat() <= NORMAL_PARTICLE_CHANCE && this.rand.nextFloat() < this.node.getLifetimeFraction()) {
            this.world.spawnParticle(this.getParticleType(), this.posX, this.posY, this.posZ, this.motionX * 0.5, this.motionY * 0.5, this.motionZ * 0.5);
        }

        // smoke / steam when hitting water.  node is responsible for aging to death
        if (this.handleWaterMovement()) {
            this.world.spawnParticle(this.getParticleType(), this.posX, this.posY, this.posZ, 0, 0, 0);
        }
    }

    protected EnumParticleTypes getParticleType() {
        return this.rand.nextFloat() <= SPECIAL_PARTICLE_CHANCE ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL;
    }

    public abstract ResourceLocation getTexture();

    public float getRenderScale() {
        return this.scale * 1.25F;
    }

    public final Random getRandom() {
        return this.rand;
    }

    /**
     * Vanilla moveEntity does a pile of unneeded calculations, and also doesn't handle resize around the centre properly,
     * so replace with a custom one
     *
     * @param dx the amount to move the entity in world inates [dx, dy, dz]
     * @param dy
     * @param dz
     */
    @Override
    public void move(MoverType mover, double dx, double dy, double dz) {
        EntityUtil.moveAndResize(this, dx, dy, dz, this.width, this.height);
    }

    @Override
    protected void entityInit() {}

    @Override
    protected void readEntityFromNBT(NBTTagCompound ignored) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {}

}
