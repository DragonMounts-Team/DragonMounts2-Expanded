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
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.helper.DragonBodyHelper;
import net.dragonmounts.entity.helper.DragonVariantHelper;
import net.dragonmounts.init.*;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.network.SSyncDragonAgePacket;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
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
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.dragonmounts.entity.DragonLifeStage.makeModifier;
import static net.dragonmounts.util.ByteBufferUtil.readVarInt;
import static net.dragonmounts.util.ByteBufferUtil.writeVarInt;
import static net.dragonmounts.util.EntityUtil.*;
import static net.minecraft.entity.SharedMonsterAttributes.*;

public abstract class TameableDragonEntity extends EntityTameable implements IEntityAdditionalSpawnData {
    public static TameableDragonEntity construct(World level) {
        return level.isRemote ? new ClientDragonEntity(level) : new ServerDragonEntity(level);
    }

    // base attributes
    public static final int HOME_RADIUS = 64;
    public static final double IN_AIR_THRESH = 10;
    public static final float EGG_CRACK_THRESHOLD = 0.9F;
    public static final float EGG_WOBBLE_THRESHOLD = 0.75F;
    public static final float EGG_WOBBLE_BASE_CHANCE = 0.05F;
    // flags
    public static final byte DO_ATTACK = 66;
    public static final byte DO_ROAR = 67;
    // limits
    public static final float MIN_SCALE = 0.04F;
    public static final float MAX_SCALE = 2.00F;
    public static final String SERIALIZATION_KEY_AGE_COMPAT = "TicksSinceCreation";
    public static final String SERIALIZATION_KEY_FROM_SPAWNER = "FromSpawner";
    // data value IDs
    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GROWTH_PAUSED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_BREATHING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALLOW_OTHERPLAYERS = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BOOSTING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVER_DISABLED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Y_LOCKED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOW_YAW = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<DragonVariant> DATA_VARIANT = EntityDataManager.createKey(TameableDragonEntity.class, DragonVariant.SERIALIZER);
    private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    protected static final DataParameter<Boolean> DATA_CAN_SHEAR = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Boolean> DATA_CAN_COLLECT_BREATH = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<ItemStack> DATA_ARMOR = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> DATA_CHEST = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<ItemStack> DATA_SADDLE = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    protected static final DataParameter<Float> DATA_BODY_SIZE = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.FLOAT);
    public final DragonInventory inventory = new DragonInventory(this);
    public final DragonVariantHelper variantHelper = new DragonVariantHelper(this);
    public final DragonBreathHelper<?> breathHelper = this.createBreathHelper();
    // public final DragonHungerHelper hungerHelper = new DragonHungerHelper(this);
    public EntityEnderCrystal healingEnderCrystal;
    protected DragonLifeStage stage;
    protected int inAirTicks;
    protected float amplitude;
    protected float amplitudeO;
    protected float wobbleAxis;
    protected int wobbling;
    private boolean isUsingBreathWeapon;
    private boolean isGoingDown;
    private boolean isUnhovered;
    private boolean yLocked;
    private boolean followYaw;
    private boolean armored;
    private boolean chested;
    private boolean saddled;
    private Entity controllerCache;
    private @Nonnull List<Entity> riderCache = Collections.emptyList();

    public TameableDragonEntity(World world) {
        super(world);
        this.setLifeStage(DragonLifeStage.EGG, true, false);
    }

    @Override
    protected @Nonnull EntityBodyHelper createBodyHelper() {
        return new DragonBodyHelper(this);
    }

    protected abstract DragonBreathHelper<?> createBreathHelper();

    /// must call super {@link EntityTameable#onLivingUpdate()}
    protected abstract void tickAsEgg();

    public abstract Vec3d getHeadRelativeOffset(float x, float y, float z);

    public abstract void setLifeStage(DragonLifeStage stage, boolean reset, boolean sync);

    public final DragonLifeStage getLifeStage() {
        return this.stage;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager manager = this.dataManager;
        manager.register(DATA_FLYING, false);
        manager.register(GROWTH_PAUSED, false);
        manager.register(DATA_BREATHING, false);
        manager.register(GOING_DOWN, false);
        manager.register(Y_LOCKED, false);
        manager.register(HOVER_DISABLED, false);
        manager.register(ALLOW_OTHERPLAYERS, false);
        manager.register(BOOSTING, false);
        manager.register(DATA_CAN_SHEAR, true);
        manager.register(DATA_CAN_COLLECT_BREATH, true);
        manager.register(FOLLOW_YAW, true);
        manager.register(HUNGER, 100);
        manager.register(DATA_ARMOR, ItemStack.EMPTY);
        manager.register(DATA_CHEST, ItemStack.EMPTY);
        manager.register(DATA_SADDLE, ItemStack.EMPTY);
        manager.register(DATA_BODY_SIZE, (float) DMConfig.BASE_BODY_SIZE.value);
        manager.register(DATA_VARIANT, DragonVariants.ENDER_FEMALE);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        AbstractAttributeMap attributes = this.getAttributeMap();
        attributes.registerAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(DMConfig.BASE_FLYING_SPEED.value);
        attributes.registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(DMConfig.BASE_DAMAGE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(DMConfig.BASE_MOVEMENT_SPEED.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(DMConfig.BASE_FOLLOW_RANGE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(DMConfig.BASE_KNOCKBACK_RESISTANCE.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR).setBaseValue(DMConfig.BASE_ARMOR.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(DMConfig.BASE_ARMOR_TOUGHNESS.value);
        attributes.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(DMConfig.BASE_HEALTH.value);
        attributes.getAttributeInstance(SWIM_SPEED).setBaseValue(DMConfig.BASE_SWIMMING_SPEED.value);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(DragonLifeStage.SERIALIZATION_KEY)) {
            this.setLifeStage(DragonLifeStage.byName(nbt.getString(DragonLifeStage.SERIALIZATION_KEY)), false, false);
        } else if (nbt.hasKey(SERIALIZATION_KEY_AGE_COMPAT)) {
            SSyncDragonAgePacket record = SSyncDragonAgePacket.fromTotalTicks(nbt.getInteger(SERIALIZATION_KEY_AGE_COMPAT));
            NBTBase saved = nbt.getTag("Age");
            nbt.removeTag("Age");
            this.setLifeStage(record.stage, true, false);
            int age = this.growingAge;
            this.growingAge = 0;
            super.readEntityFromNBT(nbt);
            this.growingAge = age;
            this.ageUp(record.age, false);
            if (saved != null) {
                nbt.setTag("Age", saved);
            }
            return;
        }
        super.readEntityFromNBT(nbt);
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
        return this.stage.isOldEnough(DragonLifeStage.FLEDGLING);
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
            boolean isUnhovered = dataManager.get(HOVER_DISABLED);
            this.isUnhovered = isUnhovered;
            return isUnhovered;
        }
        return isUnhovered;
    }

    public void setUnHovered(boolean isUnhovered) {
        dataManager.set(HOVER_DISABLED, isUnhovered);
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

    @Override
    protected float getJumpUpwardsMotion() {
        // stronger jumps for easier lift-offs
        return canFly() ? 1 : super.getJumpUpwardsMotion();
    }

    /**
     * Checks if the blocks below the dragons hitbox is present and solid
     */
    public boolean isNearGround() {
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
    @Override
    public int getTalkInterval() {
        return 240;
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    @Override
    public void playStepSound(BlockPos pos, Block block) {
        // no sounds for eggs or underwater action
        if (isEgg() || isInWater() || isOverWater() || isFlying() || isSitting()) return;

        SoundEvent stepSound;
        // baby has quiet steps, larger have stomping sound
        if (isChild()) {
            // override sound type if the top block is snowy
            stepSound = (world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER
                    ? Blocks.SNOW_LAYER
                    : block
            ).getSoundType().getStepSound();
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
        return MathHelper.clamp(this.getAgingScale(), 0.8F, 1.4F);
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
        this.getAISit().setSitting(true);
        this.navigator.clearPath();
        this.setAttackTarget(null);
        this.playTameEffect(true);
        this.world.setEntityState(this, (byte) 7);
    }

    public boolean tryTame(EntityPlayer player, float probability) {
        if (probability == 0) return false;
        if (this.rand.nextFloat() < probability && !ForgeEventFactory.onAnimalTame(this, player)) {
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
        return DragonFoods.getFood(stack).isBreedingItem(this, stack);
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
        return this.isChild() ? 2.0F * this.getAdjustedSize() : this.getAdjustedSize();
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

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity target) {
        this.setLastAttackedEntity(target);
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        if (target instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) target).getCreatureAttribute());
            if (this.getVariant().type == DragonTypes.WITHER) {
                addOrMergeEffect((EntityLivingBase) target, MobEffects.WITHER, 2000, 0, false, true);
            }
        }
        if (target.attackEntityFrom(DamageSource.causeMobDamage(this), damage)) {
            this.applyEnchantments(this, target);
            return true;
        }
        return false;
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
        return this.getControllingPassenger() instanceof EntityPlayer;
    }

    protected void tickRidden(EntityPlayer player, boolean forward) {
        if (this.getVariant().type == DragonTypes.WATER && player.isInWater()) {
            EntityUtil.addOrResetEffect(player, MobEffects.WATER_BREATHING, 200, 0, true, true, 21);
        }
        float rotX;
        if (forward || this.isUsingBreathWeapon()) {
            rotX = player.rotationPitch * 0.75F;
        } else if (this.followYaw()) {
            rotX = 0.0F;
        } else {
            this.rotationPitch = 0.0F;
            return;
        }
        this.setRotation(
                this.rotationYaw + MathHelper.wrapDegrees(player.rotationYaw - this.rotationYaw) * 0.08F,
                rotX
        );
        this.prevRotationYaw = this.rotationYawHead = this.rotationYaw;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        Entity controller = this.getControllingPassenger();
        if (controller instanceof EntityPlayer && !this.dead) {
            this.moveStrafing = this.moveVertical = this.moveForward = 0.0F;
            EntityPlayer player = (EntityPlayer) controller;
            if (this.onGround) {
                vertical = 0.0F;
                strafe = player.moveStrafing * 0.5F;
                forward = player.moveForward < 0.0F ? player.moveForward * 0.25F : player.moveForward;
            } else {
                strafe = player.moveStrafing * 0.75F;
                vertical = player.isJumping ? 0.5F : this.isGoingDown() ? -0.5F : 0.0F;
                if (player.moveForward == 0.0F) {
                    forward = 0.0F;
                } else if (this.isYLocked()) {
                    forward = player.moveForward < 0.0F ? 0.5F : 1.0F;
                } else {
                    float facing = MathX.toRadians(player.rotationPitch), upward = -MathHelper.sin(facing);
                    forward = MathHelper.cos(facing);
                    if (player.moveForward < 0.0F) {
                        forward *= -0.5F;
                        upward *= -0.5F;
                    }
                    vertical += upward;
                }
            }
            this.tickRidden(player, forward != 0.0F);
            if (this.isFlying()) {
                float speed = 0.25F * (float) this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).getAttributeValue();
                if (this.boosting()) {
                    speed *= 1.5F;
                }
                this.setAIMoveSpeed(speed);
                this.moveRelative(strafe, vertical, forward, speed);
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                this.motionX *= 0.91F;
                this.motionY *= 0.91F;
                this.motionZ *= 0.91F;
            } else {
                float speed = 0.75F * (float) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue();
                this.setAIMoveSpeed(this.boosting() ? speed * 1.5F + 0.125F : speed);
                super.travel(strafe, vertical, forward);
            }
        } else {
            super.travel(strafe, vertical, forward);
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

    public List<Entity> getCachedPassengers() {
        return this.riderCache;
    }

    private void refreshPassengerCache() {
        List<Entity> passengers = this.riderCache = Collections.unmodifiableList(super.getPassengers());
        if (passengers.isEmpty()) {
            this.controllerCache = null;
        } else {
            Entity rider = passengers.get(0);
            this.controllerCache = rider instanceof CarriageEntity ? null : rider;
        }
    }

    @Override
    protected void addPassenger(@Nonnull Entity passenger) {
        super.addPassenger(passenger);
        this.refreshPassengerCache();
    }

    @Override
    protected void removePassenger(@Nonnull Entity passenger) {
        super.removePassenger(passenger);
        this.refreshPassengerCache();
    }

    public boolean isPassengerBroadly(Entity entity) {
        for (Entity rider : this.getCachedPassengers()) {
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
    public boolean canFitPassenger(Entity passenger) {
        List<Entity> passengers = this.getCachedPassengers();
        boolean hasController = this.getControllingPassenger() instanceof EntityPlayer;
        return passenger instanceof EntityPlayer
                ? !hasController
                : !this.isSitting() && passenger instanceof CarriageEntity && passengers.size() < (hasController ? 5 : 4);
    }

    /**
     * Called when the passenger is riding on the dragon
     */
    @Override
    public void updatePassenger(Entity passenger) {
        int index = this.getCachedPassengers().indexOf(passenger);
        if (index == -1) return;
        Vec3d position = this.getVariant().type.locatePassenger(
                this.getControllingPassenger() instanceof EntityPlayer ? index : index + 1,
                this.isSitting(),
                this.getAdjustedSize() * MathX.MOJANG_MODEL_SCALE
        ).rotateYaw(MathX.toRadians(-renderYawOffset));
        if (passenger instanceof EntityPlayer) {
            passenger.setPosition(position.x + this.posX, position.y + this.posY - 0.6, position.z + this.posZ);
            if (index == 0) {
                passenger.prevRotationPitch = passenger.rotationPitch;
                passenger.prevRotationYaw = passenger.rotationYaw;
                ((EntityPlayer) passenger).renderYawOffset = renderYawOffset;
            }
        } else {
            passenger.setPosition(position.x + this.posX, position.y + this.posY, position.z + this.posZ);
        }
    }

    /**
     * Public wrapper for protected final setScale(), used by DragonLifeStageHelper.
     */
    public void updateScale() {
        boolean onGround = this.onGround;
        float scale = this.getAgingScale();
        this.stepHeight = 0.5F + scale * (float) DMConfig.BASE_STEP_HEIGHT.value;
        float width = this.width;
        this.setScale(MathHelper.clamp(scale * this.dataManager.get(DATA_BODY_SIZE), MIN_SCALE, MAX_SCALE));
        // workaround for a vanilla bug; the position is apparently not set correctly
        // after changing the entity size, causing asynchronous server/client
        // positioning
        if (this.world.isRemote && this.width > width && !this.firstUpdate) {
            this.move(MoverType.SELF, width - this.width, 0.0D, width - this.width);
        }
        this.onGround = onGround;
    }

    public void refreshForcedAgeTimer() {
        if (this.forcedAgeTimer <= 0) {
            this.forcedAgeTimer = 40;
        }
    }

    @Override
    public void ageUp(int amount, boolean forced) {
        int old = this.growingAge;
        // Notice:                             ↓↓                             ↓↓              ↓↓                  ↓↓
        if (!this.isGrowthPaused() && (old < 0 && (this.growingAge += amount) >= 0 || old > 0 && (this.growingAge -= amount) <= 0)) {
            this.onGrowingAdult();
            if (forced) {
                this.forcedAge += old;
                this.refreshForcedAgeTimer();
            }
        }
    }

    @Override
    public void setScaleForAge(boolean child) {
        this.updateScale();
    }

    @Override
    public final int getGrowingAge() {
        return this.growingAge;
    }

    @Override
    protected void onGrowingAdult() {
        this.setLifeStage(DragonLifeStage.byId(this.stage.ordinal() + 1), true, false);
    }

    protected void refreshAge() {
        switch (this.stage) {
            case HATCHLING:
            case INFANT:
                this.growingAge = -this.stage.duration.getAsInt();
                return;
            case FLEDGLING:
            case JUVENILE:
                this.growingAge = this.stage.duration.getAsInt();
                return;
            case EGG:
            default:
                this.growingAge = 0;
        }
    }

    protected void applyStage(DragonLifeStage stage) {
        float health = this.getHealth() / this.getMaxHealth();
        float scale = stage.getAverageScale();
        AttributeModifier modifier = makeModifier(1, MathHelper.clamp(scale, 0.1, 1));
        AbstractAttributeMap attributes = this.getAttributeMap();
        replaceAttributeModifier(attributes.getAttributeInstance(MAX_HEALTH), modifier);
        replaceAttributeModifier(attributes.getAttributeInstance(ATTACK_DAMAGE), modifier);
        replaceAttributeModifier(attributes.getAttributeInstance(ARMOR), makeModifier(0, Math.max(scale, 0.1F) * DMConfig.BASE_ARMOR.value));
        if (DragonLifeStage.EGG == stage) {
            this.setSize(3.5F, 4.0F);
        } else {
            this.setSize(3.0F, 2.5F);
        }
        if (this.world.isRemote) return;
        this.setHealth(health * this.getMaxHealth());
    }

    /**
     * Returns the body size multiplier for the current age.
     *
     * @return body size
     */
    public float getAdjustedSize() {
        return MathHelper.clamp(this.getAgingScale() * this.dataManager.get(DATA_BODY_SIZE), MIN_SCALE, MAX_SCALE);
    }

    public float getAgingScale() {
        return this.stage.getScale(this.growingAge);
    }

    public boolean isEgg() {
        return DragonLifeStage.EGG == this.stage;
    }

    public boolean isOldEnoughToBreathe() {
        return this.stage.isOldEnough(DragonLifeStage.INFANT);
    }

    @Override
    public boolean isChild() {
        return this.stage.isBaby();
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
        return this.stage.isOldEnough(DragonLifeStage.FLEDGLING) ? this.getVariant().type.lootTable : null;
    }

    public boolean canShare() {
        return this.dataManager.get(DATA_CAN_SHEAR);
    }

    public boolean canCollectBreath() {
        return this.dataManager.get(DATA_CAN_COLLECT_BREATH);
    }

    /**
     * @see EntityAnimal#ageUp(int, boolean)
     */
    public void consumeFood(ItemStack stack, int level, int growth) {
        this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1f, 0.75f);
        this.setHunger(this.getHunger() + level);
        this.ageUp(growth, true);
        if (this.world.isRemote) {
            Vec3d pos = this.getHeadRelativeOffset(0.0F, -5.0F, 22.0F);
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
            this.healingEnderCrystal = findNearestEntityWithinAABB(this, EntityEnderCrystal.class, this.getEntityBoundingBox().grow(32.0), null);
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, effect);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? effect.getPotion() != MobEffects.WEAKNESS : event.getResult() == Event.Result.ALLOW;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        writeVarInt(buffer, this.stage.ordinal());
        writeVarInt(buffer, this.growingAge);
        this.inventory.writeSpawnData(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.setLifeStage(DragonLifeStage.byId(readVarInt(buffer)), false, false);
        this.setGrowingAge(readVarInt(buffer));
        this.inventory.readSpawnData(buffer);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (DATA_VARIANT.equals(key)) {
            this.variantHelper.onVariantChanged(this.getVariant());
            if (!this.firstUpdate && this.world.isRemote) {
                Random random = this.rand;
                World level = this.world;
                for (int i = 0, count = 2 + (int) (this.getAgingScale() * 18); i < count; ++i) {
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
                this.world.playSound(this.posX, this.posY, this.posZ, DMSounds.DRAGON_CHEST, SoundCategory.PLAYERS, 1F, 1F, false);
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
                replaceAttributeModifier(attribute, new AttributeModifier(
                        DragonArmorItem.MODIFIER_UUID,
                        "Dragon Armor Bonus",
                        ((DragonArmorItem) stack.getItem()).protection,
                        0
                ));
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
        } else if (DATA_BODY_SIZE.equals(key)) {
            this.updateScale();
        }
    }

    public void playEggCrackEffect() {
        this.world.playEvent(2001, this.getPosition(), Block.getIdFromBlock(
                this.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG)
        ));
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
        if (variant != null) {
            this.dataManager.set(DATA_VARIANT, variant);
        }
    }

    public final void convertTo(DragonType type) {
        DragonVariant variant = this.getVariant();
        if (variant.type != type) {
            this.setVariant(type.variants.draw(this.rand, variant));
        }
    }

    public final void overrideType(DragonType type) {
        this.setVariant(type.variants.draw(this.rand, null));
    }
}

