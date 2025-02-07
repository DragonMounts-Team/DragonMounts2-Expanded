package net.dragonmounts.entity;

import net.dragonmounts.init.CarriageTypes;
import net.dragonmounts.registry.CarriageType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * used by dragons to be able to carry more than one passenger and not being able to control the dragon
 * 1. Carriage is placed on ground from item form like boat or minecarts, and when it hits a non player entity and is small enough i.e. pig the entity mounts the carriage
 * 2. rider will mount the dragon and control the dragon to mount the carriage, if the dragon hits the carriage, the carriage will ride the dragon with it the entity riding it(if there is any) like a pig
 *    the dragon can carry 2 carriages placed behind the players back (planned to add 2 more near the sides)
 * 3. Carriage can only be mounted up to 4 leaving one more room for the player to control the dragon.
 * 4. up to 2 other players can mount the dragon behind but will need carriages for the sides (It would be weird to see a player sitting and floating at the side without a carriage that acts like a saddle),
 *  so far players can't interact with the carriage at the side since the dragons hitbox is soo big blocking the carriage hitbox for playerInteract to be called
 * 5. dismount the carriage by making the dragon sit
 */
public class CarriageEntity extends Entity {
    private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(CarriageEntity.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(CarriageEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(CarriageEntity.class, DataSerializers.VARINT);
    private static final DataParameter<CarriageType> TYPE = EntityDataManager.createKey(CarriageEntity.class, CarriageType.SERIALIZER);
    public static float defaultMaxSpeedAirLateral = 0.4f;
    public static float defaultMaxSpeedAirVertical = -1f;
    public static double defaultDragAir = 0.94999998807907104D;
    protected float maxSpeedAirLateral = defaultMaxSpeedAirLateral;
    protected float maxSpeedAirVertical = defaultMaxSpeedAirVertical;
    protected double dragAir = defaultDragAir;
    private boolean isInReverse;

    private int lerpSteps;
    private double boatPitch;
    private double lerpY;
    private double lerpZ;
    private double boatYaw;
    private double lerpXRot;

    public CarriageEntity(World level) {
        super(level);
        this.preventEntitySpawning = true;
        this.setSize(1.0F, 0.5F);
    }

    public CarriageEntity(World level, Vec3d pos) {
        this(level);
        this.setPosition(this.prevPosX = pos.x, this.prevPosY = pos.y, this.prevPosZ = pos.z);
        this.motionX = this.motionY = this.motionZ = 0.0F;
    }

    @Override
    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    public Entity getControllingPassenger() {
        List<Entity> list = this.getPassengers();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity entity) {
        return entity.canBePushed() ? entity.getEntityBoundingBox() : null;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed() {
        return !this.isRiding();
    }

    /**
     * Returns the collision bounding box for this entity
     */
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead || !this.isRiding() || !this.isBeingRidden();
    }

    public float getMaxSpeedAirVertical() {
        return maxSpeedAirVertical;
    }

    /**
     * Get's the maximum speed for a minecart
     */
    protected double getMaximumSpeed() {
        return 0.4D;
    }

    public float getMaxSpeedAirLateral() {
        return maxSpeedAirLateral;
    }

    public double getDragAir() {
        return dragAir;
    }

    public void setDragAir(double value) {
        dragAir = value;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tickLerp();

        if (this.getTimeSinceHit() > 0) {
            this.setTimeSinceHit(this.getTimeSinceHit() - 1);
        }

        if (world.isRemote) {

        } else {
            if (!this.hasNoGravity()) {
                this.motionY -= 0.03999999910593033D;
            }
        }

        if (this.getRidingEntity() != null) {
            this.setEntityInvulnerable(true);
        }

        double d0 = onGround ? this.getMaximumSpeed() : getMaxSpeedAirLateral();
        this.motionX = MathHelper.clamp(this.motionX, -d0, d0);
        this.motionZ = MathHelper.clamp(this.motionZ, -d0, d0);

        double moveY = motionY;
        if (getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical()) {
            moveY = getMaxSpeedAirVertical();
            if (Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f) {
                moveY = 0.15f;
                motionY = moveY;
            }
        } else if (isInWater()) {
            moveY = getMaxSpeedAirVertical();
            if (Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f) {
                moveY = 0.0000006f;
                motionY = moveY;
            }
        }

        if (this.onGround) {
            this.motionX *= 0.5D;
            this.motionY *= 1.0D;
            this.motionZ *= 0.5D;
        }

        this.move(MoverType.SELF, this.motionX, moveY, this.motionZ);

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        if (this.posY < -64.0D) {
            this.outOfWorld();
        }

        if (!this.world.isRemote && this.world instanceof WorldServer) {
            this.world.profiler.startSection("portal");
            MinecraftServer minecraftserver = this.world.getMinecraftServer();
            int i = this.getMaxInPortalTime();

            if (this.inPortal) {
                if (minecraftserver.getAllowNether()) {
                    if (!this.isRiding() && this.portalCounter++ >= i) {
                        this.portalCounter = i;
                        this.timeUntilPortal = this.getPortalCooldown();
                        int j;

                        if (this.world.provider.getDimensionType().getId() == -1) {
                            j = 0;
                        } else {
                            j = -1;
                        }

                        this.changeDimension(j);
                    }

                    this.inPortal = false;
                }
            } else {
                if (this.portalCounter > 0) {
                    this.portalCounter -= 4;
                }

                if (this.portalCounter < 0) {
                    this.portalCounter = 0;
                }
            }

            if (this.timeUntilPortal > 0) {
                --this.timeUntilPortal;
            }

            this.world.profiler.endSection();
        }

        double d3 = MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);

        if (d3 < -170.0D || d3 >= 170.0D) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
        }

        this.setRotation(this.rotationYaw, this.rotationPitch);

        this.doBlockCollisions();
        List<Entity> list = world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), EntitySelectors.getTeamCollisionPredicate(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);

            for (Entity entity : list) {
                if (!entity.isPassenger(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width + 0.7 && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer)) {
                        entity.startRiding(this);
                        // } else {
                        //    this.applyEntityCollision(entity);
                    }
                }
            }
        }
    }

    private void tickLerp() {
        if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
            double d0 = this.posX + (this.boatPitch - this.posX) / this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.boatYaw - this.rotationYaw);
            this.rotationYaw = (float) (this.rotationYaw + d3 / this.lerpSteps);
            this.rotationPitch = (float) (this.rotationPitch + (this.lerpXRot - this.rotationPitch) / this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }


    // Forge: Fix MC-119811 by instantly completing lerp on board
    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.lerpSteps > 0) {
            this.lerpSteps = 0;
            this.posX = this.boatPitch;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float) this.boatYaw;
            this.rotationPitch = (float) this.lerpXRot;
        }
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.boatPitch = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.boatYaw = yaw;
        this.lerpXRot = pitch;
        this.lerpSteps = 10;
    }

    @Override
    public void updatePassenger(Entity passenger) {
        float f = 0.0F;
        float f1 = (float) ((this.isDead ? 0.009999999776482582D : this.getMountedYOffset()) + passenger.getYOffset());
        Vec3d vec3d = (new Vec3d(f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
        passenger.setPosition(this.posX + vec3d.x, this.posY + f1, this.posZ + vec3d.z);
        if (!(passenger instanceof EntityPlayer)) {
            EntityUtil.clampYaw(passenger, this.rotationYaw, 105.0F);
        }
    }

    /**
     * Applies a velocity to the entities, to push them away from eachother.
     */
    public void applyEntityCollision(Entity entityIn) {
        if (!this.world.isRemote) {
            if (!entityIn.noClip && !this.noClip && canBePushed()) {
                if (!this.isPassenger(entityIn)) {
                    double d0 = entityIn.posX - this.posX;
                    double d1 = entityIn.posZ - this.posZ;
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 >= 9.999999747378752E-5D) {
                        d2 = MathHelper.sqrt(d2);
                        d0 = d0 / d2;
                        d1 = d1 / d2;
                        double d3 = 1.0D / d2;

                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d3 *= (1.0F - this.entityCollisionReduction) * 0.10000000149011612D * 0.5D;

                        d0 *= d3;
                        d1 *= d3;

                        if (entityIn instanceof CarriageEntity) {
                            double d4 = entityIn.posX - this.posX;
                            double d5 = entityIn.posZ - this.posZ;
                            Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                            Vec3d vec3d1 = (new Vec3d(MathHelper.cos(this.rotationYaw * 0.017453292F), 0.0D, MathHelper.sin(this.rotationYaw * 0.017453292F))).normalize();
                            double d6 = Math.abs(vec3d.dotProduct(vec3d1));

                            if (d6 < 0.800000011920929D) {
                                return;
                            }

                            double d7 = entityIn.motionX + this.motionX;
                            double d8 = entityIn.motionZ + this.motionZ;


                            d7 = d7 / 2.0D;
                            d8 = d8 / 2.0D;
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                            entityIn.motionX *= 0.20000000298023224D;
                            entityIn.motionZ *= 0.20000000298023224D;
                            entityIn.addVelocity(d7 + d0, 0.0D, d8 + d1);

                        } else {
                            this.addVelocity(-d0, 0.0D, -d1);
                            entityIn.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into
     * account.
     */
    @Override
    public EnumFacing getAdjustedHorizontalFacing() {
        return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    @SideOnly(Side.CLIENT)
    public void performHurtAnimation() {
        this.setForwardDirection(-this.getForwardDirection());
        this.setTimeSinceHit(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote && !this.isDead) {
            if (source.getTrueSource() != null && !this.isPassenger(source.getTrueSource()) && !this.isEntityInvulnerable(source)) {
                this.setForwardDirection(-this.getForwardDirection());
                this.setTimeSinceHit(10);
                this.setDamage(this.getDamage() + amount * 10.0F);
                this.markVelocityChanged();
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) source.getTrueSource()).capabilities.isCreativeMode;
                if (flag || this.getDamage() > 40.0F) {
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(this.getType().getItemStack(this), 0.0F);
                    }
                    this.setDead();
                }
            }
        }
        return false;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            return false;
        } else {
            if (!this.world.isRemote) {
                player.startRiding(this);
            }
            return true;
        }
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(DAMAGE, 0.0F);
        this.dataManager.register(FORWARD_DIRECTION, 1);
        this.dataManager.register(TIME_SINCE_HIT, 2);
        this.dataManager.register(TYPE, CarriageTypes.OAK);
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public float getDamage() {
        return this.dataManager.get(DAMAGE);
    }

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public void setDamage(float damage) {
        this.dataManager.set(DAMAGE, damage);
    }

    /**
     * Gets the time since the last hit.
     */
    public int getTimeSinceHit() {
        return this.dataManager.get(TIME_SINCE_HIT);
    }

    /**
     * Sets the time to count down from since the last time entity was hit.
     */
    public void setTimeSinceHit(int timeSinceHit) {
        this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
    }

    /**
     * Gets the forward direction of the entity.
     */
    public int getForwardDirection() {
        return this.dataManager.get(FORWARD_DIRECTION);
    }

    /**
     * Sets the forward direction of the entity.
     */
    public void setForwardDirection(int forwardDirection) {
        this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
    }

    public CarriageType getType() {
        return this.dataManager.get(TYPE);
    }

    public void setType(CarriageType type) {
        this.dataManager.set(TYPE, type);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setFloat("damage", this.getDamage());
        compound.setInteger("forward", this.getForwardDirection());
        compound.setInteger("timesincehit", this.getTimeSinceHit());
        compound.setString("Type", this.getType().getSerializedName().toString());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        this.setDamage(compound.getFloat("damage"));
        this.setForwardDirection(compound.getInteger("forward"));
        this.setTimeSinceHit(compound.getInteger("timesincehit"));
        if (compound.hasKey("Type", 8)) {
            this.setType(CarriageType.byName(compound.getString("Type")));
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
    public void fall(float distance, float damageMultiplier) {
        // ignore fall damage if the entity can fly
        if (!isBeingRidden()) {
            super.fall(distance, damageMultiplier);
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return this.getType().getItemStack(this);
    }
}
