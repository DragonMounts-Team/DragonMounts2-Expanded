/*
c ** 2012 August 13
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity;

import io.netty.buffer.ByteBuf;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.helper.*;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static net.dragonmounts.util.EntityUtil.replaceAttributeModifier;

public abstract class TameableDragonEntity extends EntityTameable implements IEntityAdditionalSpawnData {
    public static TameableDragonEntity construct(World level) {
        return level.isRemote ? new ClientDragonEntity(level) : new ServerDragonEntity(level);
    }

    // base attributes
    public static final int HOME_RADIUS = 64;
    public static final double IN_AIR_THRESH = 10;
    // flags
    public static final byte DO_ATTACK = 66;
    public static final byte DO_ROAR = 67;

    // data value IDs
    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GROWTH_PAUSED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_BREATHING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALLOW_OTHERPLAYERS = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BOOSTING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVER_CANCELLED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Y_LOCKED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOW_YAW = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<DragonVariant> DATA_VARIANT = EntityDataManager.createKey(TameableDragonEntity.class, DragonVariant.SERIALIZER);
    private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DATA_TICKS_SINCE_CREATION = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> DATA_CAN_SHEAR = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> DATA_CAN_COLLECT_BREATH = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> DATA_ARMOR = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> DATA_CHEST = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> DATA_SADDLE = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    public final DragonInventory inventory = new DragonInventory(this);
    public final DragonVariantHelper variantHelper = new DragonVariantHelper(this);
    public final DragonLifeStageHelper lifeStageHelper = new DragonLifeStageHelper(this, DATA_TICKS_SINCE_CREATION);
    public final DragonBreathHelper<?> breathHelper = this.createBreathHelper();
    // public final DragonHungerHelper hungerHelper = new DragonHungerHelper(this);
    public EntityEnderCrystal healingEnderCrystal;
    public int inAirTicks;
    private boolean isUsingBreathWeapon;
    private boolean isGoingDown;
    private boolean isUnhovered;
    private boolean yLocked;
    private boolean followYaw;
    private boolean armored;
    private boolean chested;
    private boolean saddled;
    private Entity controllerCache;

    public TameableDragonEntity(World world) {
        super(world);
        this.moveHelper = new DragonMoveHelper(this);
        this.lifeStageHelper.applyEntityAttributes();
    }

    @Override
    protected @Nonnull EntityBodyHelper createBodyHelper() {
        return new DragonBodyHelper(this);
    }

    protected abstract DragonBreathHelper<?> createBreathHelper();

    public abstract Vec3d getThroatPosition();

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager manager = this.dataManager;
        manager.register(DATA_FLYING, false);
        manager.register(GROWTH_PAUSED, false);
        manager.register(DATA_BREATHING, false);
        manager.register(GOING_DOWN, false);
        manager.register(Y_LOCKED, false);
        manager.register(HOVER_CANCELLED, false);
        manager.register(ALLOW_OTHERPLAYERS, false);
        manager.register(BOOSTING, false);
        manager.register(DATA_CAN_SHEAR, true);
        manager.register(DATA_CAN_COLLECT_BREATH, true);
        manager.register(FOLLOW_YAW, true);
        manager.register(HUNGER, 100);
        manager.register(DATA_ARMOR, ItemStack.EMPTY);
        manager.register(DATA_CHEST, ItemStack.EMPTY);
        manager.register(DATA_SADDLE, ItemStack.EMPTY);
        manager.register(DATA_VARIANT, DragonVariants.ENDER_FEMALE);
        manager.register(DATA_TICKS_SINCE_CREATION, 0);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        AbstractAttributeMap attributes = this.getAttributeMap();
        attributes.registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
        attributes.registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        attributes.getAttributeInstance(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(DMConfig.BASE_FLYING_SPEED.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(DMConfig.BASE_MOVEMENT_SPEED.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(DMConfig.BASE_DAMAGE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(DMConfig.BASE_FOLLOW_RANGE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(DMConfig.BASE_KNOCKBACK_RESISTANCE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR).setBaseValue(DMConfig.BASE_ARMOR.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(DMConfig.BASE_ARMOR_TOUGHNESS.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(DMConfig.BASE_HEALTH.value);
        attributes.getAttributeInstance(SWIM_SPEED).setBaseValue(DMConfig.BASE_SWIMMING_SPEED.value);
    }

    public boolean boosting() {
        return dataManager.get(BOOSTING);
    }

    public void setBoosting(boolean allow) {
        dataManager.set(BOOSTING, allow);
    }

    public boolean isGrowthPaused() {
        return dataManager.get(GROWTH_PAUSED);
    }

    public void setGrowthPaused(boolean paused) {
        dataManager.set(GROWTH_PAUSED, paused);
    }

    public boolean canFly() {
        return this.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE);
    }

    /**
     * Returns true if the entity is flying.
     */
    public boolean isFlying() {
        return dataManager.get(DATA_FLYING);
    }

    /**
     * f Set the flying flag of the entity.
     */
    public void setFlying(boolean flying) {
        dataManager.set(DATA_FLYING, flying);
    }

    /**
     * Returns true if the entity is breathing.
     */
    public boolean isUsingBreathWeapon() {
        if (this.deathTime > 0) return false;
        if (world.isRemote) {
            boolean usingBreathWeapon = this.dataManager.get(DATA_BREATHING);
            this.isUsingBreathWeapon = usingBreathWeapon;
            return usingBreathWeapon;
        }
        return isUsingBreathWeapon;
    }

    /**
     * Set the breathing flag of the entity.
     */
    public void setUsingBreathWeapon(boolean usingBreathWeapon) {
        if (!this.isOldEnoughToBreathe() || !this.breathHelper.canBreathe()) {
            usingBreathWeapon = false;
        }
        this.dataManager.set(DATA_BREATHING, usingBreathWeapon);
        if (!world.isRemote) {
            this.isUsingBreathWeapon = usingBreathWeapon;
        }
    }

    /**
     * Returns true if the entity is breathing.
     */
    public boolean isGoingDown() {
        if (world.isRemote) {
            boolean isGoingDown = this.dataManager.get(GOING_DOWN);
            this.isGoingDown = isGoingDown;
            return isGoingDown;
        }
        return this.isGoingDown;
    }

    /**
     * Set the breathing flag of the entity.
     */
    public void setGoingDown(boolean goingdown) {
        this.dataManager.set(GOING_DOWN, goingdown);
        if (!world.isRemote) {
            this.isGoingDown = goingdown;
        }
    }

    public boolean allowedOtherPlayers() {
        return this.dataManager.get(ALLOW_OTHERPLAYERS);
    }

    public void setToAllowedOtherPlayers(boolean allow) {
        dataManager.set(ALLOW_OTHERPLAYERS, allow);
    }

    public boolean isYLocked() {
        if (world.isRemote) {
            boolean yLocked = dataManager.get(Y_LOCKED);
            this.yLocked = yLocked;
            return yLocked;
        }
        return yLocked;
    }

    public void setYLocked(boolean yLocked) {
        dataManager.set(Y_LOCKED, yLocked);
        if (!world.isRemote) {
            this.yLocked = yLocked;
        }
    }

    public boolean isUnHovered() {
        if (world.isRemote) {
            boolean isUnhovered = dataManager.get(HOVER_CANCELLED);
            this.isUnhovered = isUnhovered;
            return isUnhovered;
        }
        return isUnhovered;
    }

    public void setUnHovered(boolean isUnhovered) {
        dataManager.set(HOVER_CANCELLED, isUnhovered);
        if (!world.isRemote) {
            this.isUnhovered = isUnhovered;
        }
    }

    public boolean followYaw() {
        if (world.isRemote) {
            boolean folowYaw = dataManager.get(FOLLOW_YAW);
            this.followYaw = folowYaw;
            return folowYaw;
        }
        return followYaw;
    }

    public void setFollowYaw(boolean folowYaw) {
        dataManager.set(FOLLOW_YAW, folowYaw);
        if (!world.isRemote) {
            this.followYaw = folowYaw;
        }
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    @Override
    public void fall(float distance, float damageMultiplier) {
        // ignore fall damage if the entity can fly
        if (!canFly()) {
            super.fall(distance, damageMultiplier);
        }
    }

    /**
     * Returns the distance to the ground while the entity is flying.
     */
    public double getAltitude() {
        return this.posY - this.world.getHeight(MathHelper.floor(this.posX), MathHelper.floor(this.posZ));
    }

    /**
     * Causes this entity to lift off if it can fly.
     */
    public void liftOff() {
        if (canFly()) {
            boolean flag = isBeingRidden() || (isInWater() && isInLava());
            // stronger jump for an easier lift-off
            motionY += flag ? 0.7 : 6;
            inAirTicks += flag ? 3 : 4;
            jump();
        }
    }

    @Override
    protected float getJumpUpwardsMotion() {
        // stronger jumps for easier lift-offs
        return canFly() ? 1 : super.getJumpUpwardsMotion();
    }

    /**
     * Checks if the blocks below the dragons hitbox is present and solid
     */
    public boolean onSolidGround() {
        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        AxisAlignedBB box = this.getEntityBoundingBox();
        World level = this.world;
        int startX = MathHelper.ceil(box.minX), endX = MathHelper.floor(box.maxX);
        int startZ = MathHelper.ceil(box.minZ), endZ = MathHelper.floor(box.maxZ);
        boolean hasController = this.getControllingPlayer() != null;
        for (int endY = MathHelper.ceil(box.minY), y = endY - 3; y <= endY; ++y) {
            for (int x = startX; x <= endX; ++x) {
                for (int z = startZ; z <= endZ; ++z) {
                    Material material = level.getBlockState(pos.with(x, y, z)).getMaterial();
                    if (material.isSolid() || (hasController && material.isLiquid())) return true;
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public String getName() {
        if (this.hasCustomName()) return this.getCustomNameTag();
        String name = EntityList.getEntityString(this);
        return "dragonmounts.dragon".equals(name) ? (this.isEgg()
                ? I18n.translateToLocalFormatted("entity.dragonmounts.dragon_egg", I18n.translateToLocal(this.getVariant().type.translationKey))
                : I18n.translateToLocalFormatted("entity.dragonmounts.dragon", I18n.translateToLocal(this.getVariant().type.translationKey))
        ) : I18n.translateToLocal("entity." + name + ".name");
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.getVariant().type.getDeathSound(this);
    }

    @Override
    public SoundEvent getAmbientSound() {
        return this.isEgg() || this.isUsingBreathWeapon() ? null : this.getVariant().type.getLivingSound(this);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource src) {
        return isEgg()
                ? DMSounds.DRAGON_EGG_CRACK
                : SoundEvents.ENTITY_ENDERDRAGON_HURT;
    }

    public SoundEvent getWingsSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_FLAP;
    }

    public SoundEvent getStepSound() {
        return DMSounds.DRAGON_STEP;
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval() {
        return 240;
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    public void playStepSound(BlockPos entityPos, Block block) {
        // no sounds for eggs or underwater action
        if (isEgg() || isInWater() || isOverWater() || isFlying() || isSitting()) return;

        SoundEvent stepSound;
        // baby has quiet steps, larger have stomping sound
        if (isChild()) {
            SoundType soundType;
            // override sound type if the top block is snowy
            if (world.getBlockState(entityPos.up()).getBlock() == Blocks.SNOW_LAYER)
                soundType = Blocks.SNOW_LAYER.getSoundType();
            else
                soundType = block.getSoundType();
            stepSound = soundType.getStepSound();
        } else {
            stepSound = getStepSound();
        }
        playSound(stepSound, 0.2f, 1f, false);
    }

    public void playSound(SoundEvent sound, float volume, float pitch, boolean local) {
        if (sound == null || isSilent()) {
            return;
        }

        volume *= getVolume(sound);
        pitch *= getSoundPitch();

        if (local) world.playSound(posX, posY, posZ, sound, getSoundCategory(), volume, pitch, false);
        else world.playSound(null, posX, posY, posZ, sound, getSoundCategory(), volume, pitch);
    }

    @Override
    public void playSound(@Nullable SoundEvent sound, float volume, float pitch) {
        if (sound == null) return;
        playSound(sound, volume, pitch, false);
    }

    /**
     * Returns the volume for a sound to play.
     */
    public float getVolume(SoundEvent sound) {
        return MathHelper.clamp(getScale(), 0.8F, 1.4F);
    }

    /**
     * Returns the sound this mob makes on swimming.
     *
     * @TheRPGAdenturer: disabled due to its annoyance while swimming underwater it
     * played too many times
     */
    @Override
    protected SoundEvent getSwimSound() {
        return null;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    @Override
    protected float getSoundVolume() {
        // note: unused, managed in playSound()
        return 0.8F;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    @Override
    protected float getSoundPitch() {
        // note: unused, managed in playSound()
        return 1;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.925F;// Vanilla: 0.8F
    }

    public void tame(EntityPlayer player) {
        this.setTamedBy(player);
        this.navigator.clearPath();
        this.setAttackTarget(null);
        this.playTameEffect(true);
        this.world.setEntityState(this, (byte) 7);
    }

    public boolean tryTame(EntityPlayer player, float probability) {
        if (probability == 0) return false;
        if (this.rand.nextFloat() < probability) {
            this.tame(player);
            return true;
        }
        this.playTameEffect(false);
        this.world.setEntityState(this, (byte) 6);
        return false;
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it
     * (wheat, carrots or seeds depending on the animal type)
     */
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        IDragonFood food = stack.getCapability(DMCapabilities.DRAGON_FOOD, null);
        return food != null && food.isBreedingItem(this, stack);
    }

    /**
     * Returns the height level of the eyes. Used for looking at other entities.
     */
    @Override
    public float getEyeHeight() {
        if (this.isEgg()) return 1.3F;
        // 0.8F * 0.85F = 0.68F
        return this.isSitting() ? this.height * 0.68F : this.height * 0.85F;
    }

    /**
     * Returns render size modifier for the shadow
     * See {@code net.minecraft.client.renderer.entity.Render#renderShadow(Entity, double, double, double, float, float)}
     */
    @Override
    public float getRenderSizeModifier() {
        return this.isChild() ? 2.0F * this.getScale() : this.getScale();
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when
     * colliding.
     */
    @Override
    public boolean canBePushed() {
        return super.canBePushed() && isEgg();
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    @Override
    public boolean isOnLadder() {
        // this better doesn't happen...
        return false;
    }

    public int getMaxDeathTime() {
        return 120;
    }

    public void setImmuneToFire(boolean isImmuneToFire) {
        this.isImmuneToFire = isImmuneToFire;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource src) {
        if (src.getImmediateSource() == this) {
            // ignore own damage
            return true;
        }

        // don't drown as egg
        if (src == DamageSource.DROWN && isEgg()) {
            return true;
        }

        return this.getVariant().type.isInvulnerableTo(src);
    }

    /**
     * Called when an entity attacks
     */
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean attacked = entityIn.attackEntityFrom(
                DamageSource.causeMobDamage(this),
                (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()
        );

        if (attacked) {
            applyEnchantments(this, entityIn);
        }

        if (this.getVariant().type == DragonTypes.WITHER) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
        }

        return attacked;
    }

    @Override
    public boolean shouldAttackEntity(@Nonnull EntityLivingBase target, @Nullable EntityLivingBase owner) {
        if (target instanceof EntityTameable) {
            EntityTameable other = (EntityTameable) target;
            return !(
                    other instanceof TameableDragonEntity && ((TameableDragonEntity) other).isEgg()
            ) && (!other.isTamed() || other.getOwner() != owner);
        } else if (target instanceof EntityPlayer) {
            return !(owner instanceof EntityPlayer) || ((EntityPlayer) owner).canAttackPlayer((EntityPlayer) target);
        } else if (this.isTamed() && target instanceof AbstractHorse) {
            return !((AbstractHorse) target).isTame();
        }
        return true;
    }

    public void updateIntendedRideRotation(EntityPlayer rider) {
        if (isUsingBreathWeapon()) this.rotationYawHead = this.renderYawOffset;
        if (rider.equals(this.getControllingPlayer()) && rider.moveStrafing == 0) {
            this.rotationYaw = rider.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = rider.rotationPitch;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
        }
    }

    @Override
    public boolean canBeSteered() {
        //   must always return false or the vanilla movement code interferes
        //   with DragonMoveHelper
        return false;
    }

    @Override
    public void travel(float strafe, float forward, float vertical) {
        // disable method while flying, the movement is done entirely by
        // moveEntity() and this one just makes the dragon to fall slowly when
        if (!isFlying()) {
            super.travel(strafe, forward, vertical);
        }

    }

    @Nullable
    public Entity getControllingPassenger() {
        return this.controllerCache;
    }

    /**
     * Biased method of getControllingPassenger so that only players can control the dragon
     *
     * @return player on the front
     */
    @Nullable
    public EntityPlayer getControllingPlayer() {
        return this.controllerCache instanceof EntityPlayer ? (EntityPlayer) this.controllerCache : null;
    }

    @Override
    protected void addPassenger(@Nonnull Entity passenger) {
        super.addPassenger(passenger);
        List<Entity> passengers = this.getPassengers();
        this.controllerCache = passengers.isEmpty() ? null : passengers.get(0);
    }

    @Override
    protected void removePassenger(@Nonnull Entity passenger) {
        super.removePassenger(passenger);
        List<Entity> passengers = this.getPassengers();
        this.controllerCache = passengers.isEmpty() ? null : passengers.get(0);
    }

    public boolean isPassengerBroadly(Entity entity) {
        for (Entity rider : this.getPassengers()) {
            if (rider == entity || (
                    rider instanceof CarriageEntity && rider.getPassengers().contains(entity)
            )) return true;
        }
        return false;
    }

    @Override
    public void updateRidden() {
        Entity vehicle = this.getRidingEntity();
        if (this.isDead || vehicle != null && vehicle.isSneaking() || !isChild()) {
            this.dismountRidingEntity();
            return;
        }
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.onUpdate();
        if (vehicle instanceof EntityPlayer) {
            // called when the dragon is riding on the shoulder of the player. Credits: Ice and Fire
            EntityPlayer player = (EntityPlayer) vehicle;
            int index = player.getPassengers().indexOf(this);
            if (index == -1) return;
            boolean flying = player.isElytraFlying();
            float radius = (index == 2 ? 0F : 0.4F) + (flying ? 2 : 0);
            float angle = (0.01745329251F * player.renderYawOffset) + (index == 1 ? -90 : index == 0 ? 90 : 0);
            double extraX = radius * MathHelper.sin(angle + MathX.PI_F);
            double extraZ = radius * MathHelper.cos(angle);
            double extraY = (player.isSneaking() ? 1.1D : 1.4D) + (index == 2 ? 0.4D : 0D);
            this.rotationYaw = player.rotationYaw;
            this.prevRotationYaw = player.prevRotationYaw;
            this.renderYawOffset = player.renderYawOffset;
            this.rotationYawHead = player.rotationYawHead;
            this.prevRotationYawHead = player.prevRotationYawHead;
            this.rotationPitch = player.rotationPitch;
            this.prevRotationPitch = player.prevRotationPitch;
            this.setPosition(player.posX + extraX, player.posY + extraY, player.posZ + extraZ);
            this.setFlying(flying || !player.onGround &&
                    !player.capabilities.isFlying &&
                    player.posY - this.world.getHeight(MathHelper.floor(this.posX), MathHelper.floor(this.posZ)) > 2.0
            );
        }
    }

    @Override
    public void dismountRidingEntity() {
        super.dismountRidingEntity();
        this.setUsingBreathWeapon(false);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 5;
    }

    /**
     * Called when the passenger is riding on the dragon
     */
    @Override
    public void updatePassenger(Entity passenger) {
        List<Entity> passengers = getPassengers();
        int index = passengers.indexOf(passenger);
        if (index == -1) return;
        //getBreed().getAdultModelRenderScaleFactor() * getScale();
        Vec3d position = this.getVariant().type.locatePassenger(index, this.isSitting(), this.getScale())
                .rotateYaw(MathX.toRadians(-renderYawOffset))
                .add(this.posX, this.posY + passenger.getYOffset(), this.posZ);
        passenger.setPosition(position.x, position.y, position.z);

        // fix rider rotation
        if (index == 0 && passenger instanceof EntityPlayer) {
                passenger.prevRotationPitch = passenger.rotationPitch;
                passenger.prevRotationYaw = passenger.rotationYaw;
                ((EntityPlayer) passenger).renderYawOffset = renderYawOffset;
        } else {
            EntityUtil.clampYaw(passenger, this.rotationYaw, 105.0F);
        }
    }

    /**
     * Public wrapper for protected final setScale(), used by DragonLifeStageHelper.
     */
    public void applyScale(float scale) {
        boolean onGround = this.onGround;
        this.stepHeight = 0.5F + scale * (float) DMConfig.BASE_STEP_HEIGHT.value;
        float width = this.width;
        this.setScale(scale);
        // workaround for a vanilla bug; the position is apparently not set correcty
        // after changing the entity size, causing asynchronous server/client
        // positioning
        if (this.world.isRemote && this.width > width && !this.firstUpdate) {
            this.move(MoverType.SELF, width - this.width, 0.0D, width - this.width);
        }
        this.onGround = onGround;
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's
     * incremented on each tick, if it's positive, it get's decremented each tick.
     * Don't confuse this with EntityLiving.getAge. With a negative value the Entity
     * is considered a child.
     */
    @Override
    public int getGrowingAge() {
        // adapter for vanilla code to enable breeding interaction
        return DragonLifeStage.ADULT == this.lifeStageHelper.getLifeStage() ? 0 : -1;
    }

    /**
     * The age value may be negative or positive or zero. If it's negative, it get's
     * incremented on each tick, if it's positive, it get's decremented each tick.
     * With a negative value the Entity is considered a child.
     */
    @Override
    public void setGrowingAge(int age) {
        // managed by DragonLifeStageHelper, so this is a no-op
    }

    /**
     * Sets the scale for an ageable entity according to the boolean parameter,
     * which says if it's a child.
     */
    @Override
    public void setScaleForAge(boolean child) {
        // managed by DragonLifeStageHelper, so this is a no-op
    }

    /**
     * Returns the size multiplier for the current age.
     *
     * @return scale
     */
    public float getScale() {
        return this.lifeStageHelper.getScale();
    }

    public boolean isEgg() {
        return this.lifeStageHelper.isEgg();
    }

    public boolean isOldEnoughToBreathe() {
        return this.lifeStageHelper.isOldEnough(DragonLifeStage.INFANT);
    }

    @Override
    public boolean isChild() {
        return this.lifeStageHelper.getLifeStage().isBaby();
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    public int getHunger() {
        return dataManager.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.dataManager.set(HUNGER, Math.min(100, hunger));
    }

    @Override
    protected ResourceLocation getLootTable() {
        return this.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE)
                ? this.getVariant().type.lootTable
                : null;
    }

    public boolean canShare() {
        return this.dataManager.get(DATA_CAN_SHEAR);
    }

    public boolean canCollectBreath() {
        return this.dataManager.get(DATA_CAN_COLLECT_BREATH);
    }

    public void resetFeedTimer() {
        if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
        }
    }

    /**
     * @see EntityAnimal#ageUp(int, boolean)
     */
    public void consumeFood(ItemStack stack, int level, int growth) {
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1f, 0.75f);
        this.setHunger(this.getHunger() + level);
        this.lifeStageHelper.ageUp(growth);
        if (this.world.isRemote) {
            this.resetFeedTimer();
            Vec3d pos = this.getThroatPosition();
            Random random = this.rand;
            this.world.spawnParticle(
                    EnumParticleTypes.ITEM_CRACK,
                    pos.x,
                    pos.y,
                    pos.z,
                    random.nextGaussian() * 0.07D,
                    random.nextGaussian() * 0.07D,
                    random.nextGaussian() * 0.07D,
                    Item.getIdFromItem(stack.getItem()),
                    stack.getMetadata()
            );
        }
    }

    protected void findCrystal() {
        if (this.motionX < 0.6F && this.motionZ < 0.6F && this.rand.nextInt(10) == 0) {
            EntityEnderCrystal target = null;
            double min = Double.MAX_VALUE;
            for (EntityEnderCrystal crystal : this.world.getEntitiesWithinAABB(
                    EntityEnderCrystal.class,
                    this.getEntityBoundingBox().grow(32.0D)
            )) {
                double distance = crystal.getDistanceSq(this);
                if (distance < min) {
                    min = distance;
                    target = crystal;
                }
            }
            this.healingEnderCrystal = target;
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, effect);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? effect.getPotion() != MobEffects.WEAKNESS : event.getResult() == Event.Result.ALLOW;
    }

    public final void onLifeStageChange(DragonLifeStage stage) {
        if (DragonLifeStage.EGG == stage) {
            this.setSize(3.5F, 4.0F);
        } else {
            //Pair<Float, Float> size = this.getBreed().getAdultEntitySize();TODO: use DragonType or something else
            this.setSize(4.8F, 4.2F);
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        this.inventory.writeSpawnData(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.inventory.readSpawnData(buffer);
        this.lifeStageHelper.sync();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (DATA_VARIANT.equals(key)) {
            this.variantHelper.onVariantChanged(this.getVariant());
            if (!this.firstUpdate && this.world.isRemote) {
                Random random = this.rand;
                World level = this.world;
                for (int i = 0, count = 2 + (int) (this.getScale() * 18); i < count; ++i) {
                    level.spawnParticle(
                            EnumParticleTypes.CLOUD,
                            this.posX + (random.nextDouble() - 0.5D) * this.width,
                            this.posY + random.nextDouble() * this.height,
                            this.posZ + (random.nextDouble() - 0.5D) * this.width,
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }
            }
        } else if (DATA_CHEST.equals(key)) {
            ItemStack stack = this.getChest();
            boolean chested = DragonInventory.isValidChest(stack);
            if (!this.firstUpdate && chested && !this.chested) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.PLAYERS, 1F, 1F, false);
            } else if (!chested && this.chested) {
                this.inventory.dropItemsInChest();
            }
            this.chested = chested;
        } else if (DATA_ARMOR.equals(key)) {
            ItemStack stack = this.getArmor();
            boolean armored = !stack.isEmpty() && stack.getItem() instanceof DragonArmorItem;
            if (!this.firstUpdate && armored && !this.armored) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_HORSE_ARMOR, SoundCategory.PLAYERS, 1F, 1F, false);
            }
            IAttributeInstance attribute = this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR);
            if (armored) {
                replaceAttributeModifier(
                        attribute,
                        DragonArmorItem.MODIFIER_UUID,
                        "Dragon Armor Bonus",
                        ((DragonArmorItem) stack.getItem()).protection,
                        0,
                        false
                );
            } else if (attribute != null) {
                attribute.removeModifier(DragonArmorItem.MODIFIER_UUID);
            }
            this.armored = armored;
        } else if (DATA_SADDLE.equals(key)) {
            ItemStack stack = this.getSaddle();
            boolean saddled = !stack.isEmpty() && stack.getItem() == Items.SADDLE;
            if (!this.firstUpdate && saddled && !this.saddled) {
                this.world.playSound(this.posX, this.posY, this.posZ, SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.PLAYERS, 1F, 1F, false);
            }
            this.saddled = saddled;
        }
    }

    public void setArmor(ItemStack armor) {
        this.dataManager.set(DATA_ARMOR, armor);
    }

    public void setChest(ItemStack chest) {
        this.dataManager.set(DATA_CHEST, chest);
    }

    public void setSaddle(ItemStack saddle) {
        this.dataManager.set(DATA_SADDLE, saddle);
    }

    public ItemStack getArmor() {
        return this.dataManager.get(DATA_ARMOR);
    }

    public boolean isArmored() {
        return this.armored;
    }

    public ItemStack getChest() {
        return this.dataManager.get(DATA_CHEST);
    }

    public boolean isChested() {
        return this.chested;
    }

    public ItemStack getSaddle() {
        return this.dataManager.get(DATA_SADDLE);
    }

    public boolean isSaddled() {
        return this.saddled;
    }

    public DragonVariant getVariant() {
        return this.dataManager.get(DATA_VARIANT);
    }

    public void setVariant(DragonVariant variant) {
        this.dataManager.set(DATA_VARIANT, variant);
    }
}

