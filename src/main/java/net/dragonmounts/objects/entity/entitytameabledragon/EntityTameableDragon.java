/*
c ** 2012 August 13
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.objects.entity.entitytameabledragon;

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.client.gui.GuiHandler;
import net.dragonmounts.client.model.dragon.anim.DragonAnimator;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.init.DragonVariants;
import net.dragonmounts.inits.ModItems;
import net.dragonmounts.inits.ModKeys;
import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.item.DragonScalesItem;
import net.dragonmounts.network.MessageDragonBreath;
import net.dragonmounts.network.MessageDragonExtras;
import net.dragonmounts.objects.entity.entitycarriage.EntityCarriage;
import net.dragonmounts.objects.entity.entitytameabledragon.ai.ground.EntityAIDragonSit;
import net.dragonmounts.objects.entity.entitytameabledragon.ai.path.PathNavigateFlying;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.DragonBreathHelper;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.DragonBreathHelperP;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.DragonBreedForest;
import net.dragonmounts.objects.entity.entitytameabledragon.breeds.EnumDragonBreed;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.*;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.DMUtils;
import net.dragonmounts.util.LootTableLocation;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE;
import static net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE;

/**
 * Here be dragons
 */
public class EntityTameableDragon extends EntityTameable implements IShearable, IEntityAdditionalSpawnData {
    // base attributes
    public static final double BASE_GROUND_SPEED = 0.4;
    public static final double BASE_AIR_SPEED = 0.9;
    public static final IAttribute MOVEMENT_SPEED_AIR = new RangedAttribute(null, "generic.movementSpeedAir", 0.9, 0.0, Double.MAX_VALUE).setDescription("Movement Speed Air").setShouldWatch(true);
    public static final double BASE_DAMAGE = DragonMountsConfig.BASE_DAMAGE;
    public static final double BASE_ARMOR = DragonMountsConfig.ARMOR;
    public static final double BASE_TOUGHNESS = 30.0D;
    public static final float RESISTANCE = 10.0f;
    public static final double BASE_FOLLOW_RANGE = 70;
    public static final double BASE_FOLLOW_RANGE_FLYING = BASE_FOLLOW_RANGE * 2;
    public static final int HOME_RADIUS = 64;
    public static final double IN_AIR_THRESH = 10;
    private static final Logger L = LogManager.getLogger();

    // data value IDs
    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GROWTH_PAUSED = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_BREATHING = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_ALT_BREATHING = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALLOW_OTHERPLAYERS = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BOOSTING = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_MALE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVER_CANCELLED = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Y_LOCKED = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALT_TEXTURE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOW_YAW = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> DATA_BREEDER = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<EnumDragonBreed> DATA_BREED = EntityDataManager.createKey(EntityTameableDragon.class, EnumDragonBreed.SERIALIZER);
    private static final DataParameter<DragonVariant> DATA_VARIANT = EntityDataManager.createKey(EntityTameableDragon.class, DragonVariant.SERIALIZER);
    private static final DataParameter<DragonBreedForest.SubType> FOREST_TEXTURES = EntityDataManager.createKey(EntityTameableDragon.class, DragonBreedForest.SubType.SERIALIZER);
    private static final DataParameter<Integer> DATA_REPRO_COUNT = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DATA_TICKS_SINCE_CREATION = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.VARINT);
    private static final DataParameter<Byte> DRAGON_SCALES = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> HAS_ADJUCATOR_STONE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_ELDER_STONE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    //private static final DataParameter<Boolean> SLEEP = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> DATA_BREATH_WEAPON_TARGET = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.STRING);
    private static final DataParameter<Integer> DATA_BREATH_WEAPON_MODE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> DATA_ARMOR = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<ItemStack> DATA_CHEST = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<ItemStack> DATA_SADDLE = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.ITEM_STACK);
    @Deprecated
    public int ticksShear;
    // server/client delegates
    private final Map<Class<?>, DragonHelper> helpers = new HashMap<>();
    // client-only delegates
    private final DragonBodyHelper dragonBodyHelper = new DragonBodyHelper(this);
    public EntityEnderCrystal healingEnderCrystal;
    public final DragonInventory inventory = new DragonInventory(this);
    public final DragonVariantHelper variantHelper = new DragonVariantHelper(this);
    public int inAirTicks;
    public int roarTicks;
    protected int ticksSinceLastAttack;
    private boolean isUsingBreathWeapon;
    private boolean altBreathing;
    private boolean isGoingDown;
    private boolean isUnhovered;
    private boolean yLocked;
    private boolean followYaw;
    private DragonAnimator animator;
    private double airSpeedVertical = 0;
    private boolean armored;
    private boolean chested;
    private boolean saddled;

    public EntityTameableDragon(World world) {
        super(world);

        // enables walking over blocks
        stepHeight = 1;

        // create entity delegates
        addHelper(new DragonLifeStageHelper(this, DATA_TICKS_SINCE_CREATION));
        addHelper(new DragonReproductionHelper(this, DATA_BREEDER, DATA_REPRO_COUNT));
        addHelper(new DragonBreathHelper(this, DATA_BREATH_WEAPON_TARGET, DATA_BREATH_WEAPON_MODE));
        addHelper(new DragonHungerHelper(this));
        if (isServer()) addHelper(new DragonBrain(this));

        // set base size
        setSize(4.8F, 4.2F);//TODO: use DragonType or something else

        // init helpers
        moveHelper = new DragonMoveHelper(this);
        aiSit = new EntityAIDragonSit(this);
        helpers.values().forEach(DragonHelper::applyEntityAttributes);
        animator = new DragonAnimator(this);
    }

    @Override
    protected float updateDistance(float f1, float f2) {
        dragonBodyHelper.updateRenderAngles();
        return f2;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        EntityDataManager manager = this.dataManager;
        manager.register(DATA_FLYING, false);
        manager.register(GROWTH_PAUSED, false);
        manager.register(DATA_BREATHING, false);
        manager.register(DATA_ALT_BREATHING, false);
        manager.register(GOING_DOWN, false);
        manager.register(Y_LOCKED, false);
        manager.register(HOVER_CANCELLED, false);
        manager.register(ALT_TEXTURE, false);
        manager.register(HAS_ELDER_STONE, false);
        manager.register(HAS_ADJUCATOR_STONE, false);
        manager.register(ALLOW_OTHERPLAYERS, false);
        manager.register(BOOSTING, false);
        manager.register(DRAGON_SCALES, (byte) 0);
        manager.register(IS_MALE, getRNG().nextBoolean());
        //        manager.register(SLEEP, false); //unused as of now
        manager.register(FOLLOW_YAW, true);
        manager.register(DATA_BREATH_WEAPON_TARGET, "");
        manager.register(FOREST_TEXTURES, DragonBreedForest.SubType.NONE);
        manager.register(DATA_BREATH_WEAPON_MODE, 0);
        manager.register(HUNGER, 100);
        manager.register(DATA_ARMOR, ItemStack.EMPTY);
        manager.register(DATA_CHEST, ItemStack.EMPTY);
        manager.register(DATA_SADDLE, ItemStack.EMPTY);
        manager.register(DATA_VARIANT, DragonVariants.ENDER_FEMALE);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        AbstractAttributeMap attributes = this.getAttributeMap();
        attributes.registerAttribute(MOVEMENT_SPEED_AIR);
        attributes.registerAttribute(ATTACK_DAMAGE);
        attributes.getAttributeInstance(MOVEMENT_SPEED_AIR).setBaseValue(BASE_AIR_SPEED);
        attributes.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BASE_GROUND_SPEED);
        attributes.getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(BASE_DAMAGE);
        attributes.getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(BASE_FOLLOW_RANGE);
        attributes.getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(RESISTANCE);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR).setBaseValue(BASE_ARMOR);
        attributes.getAttributeInstance(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(BASE_TOUGHNESS);
        attributes.getAttributeInstance(SWIM_SPEED).setBaseValue(5.0);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("Sheared", this.isSheared());
        nbt.setBoolean("Breathing", this.isUsingBreathWeapon());
        nbt.setBoolean("projectile", this.isUsingAltBreathWeapon());
        nbt.setBoolean("IsMale", this.isMale());
        nbt.setBoolean("unhovered", this.isUnHovered());
        nbt.setBoolean("followyaw", this.followYaw());
        nbt.setInteger("AgeTicks", this.getLifeStageHelper().getTicksSinceCreation());
        nbt.setInteger("hunger", this.getHunger());
        nbt.setBoolean("boosting", this.boosting());
        nbt.setBoolean("ylocked", this.isYLocked());
        nbt.setBoolean("Elder", this.canBeElder());
        nbt.setBoolean("Adjucator", this.canBeAdjucator());
        nbt.setBoolean("growthpause", this.isGrowthPaused());
        nbt.setBoolean("AllowOtherPlayers", this.allowedOtherPlayers());
        nbt.setString("Variant", this.getVariant().getSerializedName());
        //        nbt.setBoolean("sleeping", this.isSleeping()); //unused as of now
        this.inventory.saveAdditionalData(nbt);
        this.variantHelper.writeToNBT(nbt);
        helpers.values().forEach(helper -> helper.writeToNBT(nbt));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
        this.setHunger(nbt.getInteger("hunger"));
        this.setGrowthPaused(nbt.getBoolean("growthpause"));
        this.setUsingBreathWeapon(nbt.getBoolean("Breathing"));
        this.setUsingProjectile(nbt.getBoolean("projectile"));
        this.getLifeStageHelper().setTicksSinceCreation(nbt.getInteger("AgeTicks"));
        this.setMale(nbt.getBoolean("IsMale"));
        this.setUnHovered(nbt.getBoolean("unhovered"));
        this.setYLocked(nbt.getBoolean("ylocked"));
        this.setFollowYaw(nbt.getBoolean("followyaw"));
        this.setBoosting(nbt.getBoolean("boosting"));
        //        this.setSleeping(nbt.getBoolean("sleeping")); //unused as of now
        this.setCanBeElder(nbt.getBoolean("Elder"));
        this.setCanBeAdjucator(nbt.getBoolean("Adjucator"));
        this.setToAllowedOtherPlayers(nbt.getBoolean("AllowOtherPlayers"));
        this.inventory.readAdditionalData(nbt);
        if (nbt.hasKey("Variant")) {
            this.setVariant(DragonVariant.byName(nbt.getString("Variant")));
        }
        this.variantHelper.readFromNBT(nbt);
        helpers.values().forEach(helper -> helper.readFromNBT(nbt));
    }

    /**
     * Returns relative speed multiplier for the vertical flying speed.
     *
     * @return relative vertical speed multiplier
     */
    public double getMoveSpeedAirVert() {
        return this.airSpeedVertical;
    }

    public boolean boosting() {
        return dataManager.get(BOOSTING);
    }

    public void setBoosting(boolean allow) {
        dataManager.set(BOOSTING, allow);
    }

    public boolean canBeAdjucator() {
        return dataManager.get(HAS_ADJUCATOR_STONE);
    }

    public void setCanBeAdjucator(boolean male) {
        dataManager.set(HAS_ADJUCATOR_STONE, male);
    }

    public boolean canBeElder() {
        return dataManager.get(HAS_ELDER_STONE);
    }

    public void setCanBeElder(boolean male) {
        dataManager.set(HAS_ELDER_STONE, male);
    }

    // public boolean isSleeping() {
    //  return dataManager.get(SLEEP);
    // }

    // public void setSleeping(boolean sleeping) {
    //   dataManager.set(SLEEP, sleeping);
    // }

    /**
     * Gets the gender since booleans return only 2 values (true or false) true == MALE, false == FEMALE
     * 2 genders only dont call me sexist and dont talk to me about political correctness
     */
    public boolean isMale() {
        return dataManager.get(IS_MALE);
    }

    public void setMale(boolean male) {
        dataManager.set(IS_MALE, male);
    }

    /**
     * set in commands
     */
    public void setOppositeGender() {
        this.setMale(!this.isMale());
    }

    public boolean isGrowthPaused() {
        return dataManager.get(GROWTH_PAUSED);
    }

    public void setGrowthPaused(boolean paused) {
        dataManager.set(GROWTH_PAUSED, paused);
    }

    public boolean canFly() {
        // eggs can't fly
        return !isEgg() && !isBaby();
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
        L.trace("setFlying({})", flying);
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
        if (!this.isOldEnoughToBreathe()) {
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
    public boolean isUsingAltBreathWeapon() {
        if (world.isRemote) {
            boolean usingBreathWeapon = this.dataManager.get(DATA_ALT_BREATHING);
            this.altBreathing = usingBreathWeapon;
            return usingBreathWeapon;
        }
        return altBreathing;
    }

    /**
     * Set the breathing flag of the entity.
     */
    public void setUsingAltBreathWeapon(boolean altBreathing) {
        if (!this.isOldEnoughToBreathe()) {
            altBreathing = false;
        }
        this.dataManager.set(DATA_ALT_BREATHING, altBreathing);
        if (!world.isRemote) {
            this.altBreathing = altBreathing;
        }
    }

    /**
     * Set the breathing flag of the entity.
     */
    public void setUsingProjectile(boolean altBreathing) {
        this.dataManager.set(DATA_ALT_BREATHING, altBreathing);
        if (!world.isRemote) {
            this.altBreathing = altBreathing;
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

    public boolean altTextures() {
        return this.dataManager.get(ALT_TEXTURE);
    }

    public void setAltTextures(boolean alt) {
        dataManager.set(ALT_TEXTURE, alt);
    }

    public DragonBreedForest.SubType getForestType() {
        return dataManager.get(FOREST_TEXTURES);
    }

    public void setForestType(DragonBreedForest.SubType forest) {
        dataManager.set(FOREST_TEXTURES, forest);
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

    public int getTicksSinceLastAttack() {
        return ticksSinceLastAttack;
    }

    /**
     * returns the pitch of the dragon's body
     */
    public float getBodyPitch() {
        return getAnimator().getBodyPitch();
    }

    /**
     * Returns the distance to the ground while the entity is flying.
     */
    public double getAltitude() {
        BlockPos groundPos = world.getHeight(getPosition());
        return posY - groundPos.getY();
    }

    /**
     * Causes this entity to lift off if it can fly.
     */
    public void liftOff() {
        L.trace("liftOff");
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
        for (double y = -3.0; y <= -1.0; ++y) {
            for (double xz = -2.0; xz < 3.0; ++xz) {
                if (isBlockSolid(posX + xz, posY + y, posZ + xz)) return true;
            }
        }
        return false;
    }

    /*
     * Called in onSolidGround()
     */
    private boolean isBlockSolid(double xcoord, double ycoord, double zcoord) {
        BlockPos pos = new BlockPos(xcoord, ycoord, zcoord);
        IBlockState state = world.getBlockState(pos);
        return state.getMaterial().isSolid() || (this.getControllingPlayer() != null && state.getMaterial().isLiquid());
    }

    @Override
    public void onEntityUpdate() {
        if (getRNG().nextInt(1500) == 1 && !isEgg()) roar();
        super.onEntityUpdate();
    }

    @SideOnly(Side.CLIENT)
    public void updateKeys() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && (hasControllingPlayer(mc.player) || mc.player.equals(this.getRidingEntity()))) {
            boolean isBoosting = ModKeys.BOOST.isKeyDown();
            boolean isDown = ModKeys.DOWN.isKeyDown();
            boolean projectile = ModKeys.KEY_PROJECTILE.isPressed();
            boolean unhover = ModKeys.KEY_HOVERCANCEL.isPressed();
            boolean followyaw = ModKeys.FOLLOW_YAW.isPressed();
            boolean locky = ModKeys.KEY_LOCKEDY.isPressed();
            boolean isBreathing = ModKeys.KEY_BREATH.isKeyDown();
            DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonBreath(getEntityId(), isBreathing));
            DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonExtras(getEntityId(), unhover, followyaw, locky, isBoosting, isDown));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
//        EntityPlayer rider = getControllingPlayer();
//        if (rider != null) {
//            if (entityIsJumping(rider)) {
//                motionY += 0.4;
//            } else if (isGoingDown()) {
//                motionY -= 0.4;
//            }
//        }
        if (world.isRemote) {
            this.updateKeys();
        }
    }

    @Override
    public void onLivingUpdate() {
        this.variantHelper.onLivingUpdate();
        helpers.values().forEach(DragonHelper::onLivingUpdate);
        this.getVariant().type.behavior.tick(this);

        if (isServer()) {
            final float DUMMY_MOVETIME = 0;
            final float DUMMY_MOVESPEED = 0;
            animator.setMovement(DUMMY_MOVETIME, DUMMY_MOVESPEED);
            float netYawHead = getRotationYawHead() - renderYawOffset;
            animator.setLook(netYawHead, rotationPitch);
            animator.tickingUpdate();
            animator.animate();

            // set home position near owner when tamed
            if (isTamed()) {
                Entity owner = getOwner();
                if (owner != null) {
                    setHomePosAndDistance(owner.getPosition(), HOME_RADIUS);
                }
            }

            // delay flying state for 10 ticks (0.5s)
            if (onSolidGround()) {
                inAirTicks = 0;
            } else {
                inAirTicks++;
            }

            boolean flying = inAirTicks > IN_AIR_THRESH && canFly() && (!isInWater() && !isInLava() || getControllingPlayer() != null);
            if (flying != isFlying()) {

                // notify client
                setFlying(flying);

                // clear tasks (needs to be done before switching the navigator!)
                //			getBrain().clearTasks();

                // update AI follow range (needs to be updated before creating
                // new PathNavigate!)
                getEntityAttribute(FOLLOW_RANGE).setBaseValue(getDragonSpeed());

                // update pathfinding method
                if (flying) {
                    navigator = new PathNavigateFlying(this, world);
                } else {
                    navigator = new PathNavigateGround(this, world);
                }

                // tasks need to be updated after switching modes
                getBrain().updateAITasks();

            }

        } else {
            animator.tickingUpdate();
        }

        if (ticksSinceLastAttack >= 0) { // used for jaw animation
            ++ticksSinceLastAttack;
            if (ticksSinceLastAttack > 1000) {
                ticksSinceLastAttack = -1; // reset at arbitrary large value
            }
        }

        if (roarTicks >= 0) {
            ++roarTicks;
            if (roarTicks > 1000) {
                roarTicks = -1;
            }
        }


        if (this.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase ridingEntity = (EntityLivingBase) this.getRidingEntity();
            if (ridingEntity.isElytraFlying()) {
                this.setUnHovered(true);
            }
        }
        if (this.ticksExisted % (DragonMountsConfig.hungerDecrement) == 1) {
            if (this.getHunger() > 0) {
                this.setHunger(this.getHunger() - 1);
            }
        }
        if (ticksShear <= 0) {
            setSheared(false);
        }

        if (ticksShear >= 0) {
            ticksShear--;
        }

        if (!isDead) {
            if (this.healingEnderCrystal != null) {
                if (this.healingEnderCrystal.isDead) {
                    this.healingEnderCrystal = null;
                } else if (this.ticksExisted % 10 == 0) {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.setHealth(this.getHealth() + 1.0F);
                    }

                    addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 15 * 20));
                }
            }

            if (this.rand.nextInt(10) == 0) {
                List<EntityEnderCrystal> list = this.world.getEntitiesWithinAABB(EntityEnderCrystal.class, this.getEntityBoundingBox().grow(32.0D));
                EntityEnderCrystal target = null;
                double min = Double.MAX_VALUE;

                for (EntityEnderCrystal crystal : list) {
                    double distance = crystal.getDistanceSq(this);
                    if (distance < min) {
                        min = distance;
                        target = crystal;
                    }
                }

                this.healingEnderCrystal = target;
            }
        }

        doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(0.2, -0.01, 0.2), EntitySelectors.getTeamCollisionPredicate(this));

        if (!list.isEmpty() && this.isSaddled()) {
            boolean onServer = !this.world.isRemote;

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                if (!entity.isPassenger(this) && !entity.isRiding() && entity instanceof EntityCarriage) {
                    if (onServer && this.getPassengers().size() < 5 && !entity.isRiding() && !isBaby() && (isJuvenile() || isAdult())) {
                        entity.startRiding(this);
                    } else {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }

        if (getControllingPlayer() == null && !isFlying() && isSitting()) {
            removePassengers();
        }
        EnumParticleTypes sneeze = this.getVariant().type.sneezeParticle;

        if (sneeze != null && rand.nextInt(700) == 0 && !this.isUsingBreathWeapon() && !isBaby() && !isEgg()) {
            for (int i = -1; i < 2; i++) {
                if (world.isRemote) {
                    double throatPosX = (this.getAnimator().getThroatPosition().x);
                    double throatPosY = (this.getAnimator().getThroatPosition().y + i);
                    double throatPosZ = (this.getAnimator().getThroatPosition().z);
                    world.spawnParticle(sneeze, throatPosX, throatPosY, throatPosZ, 0, 0.3, 0);
                    world.playSound(null, new BlockPos(throatPosX, throatPosY, throatPosZ), ModSounds.DRAGON_SNEEZE, SoundCategory.NEUTRAL, 0.8F, 1);
                }
            }
        }
        super.onLivingUpdate();
    }

    public void spawnBodyParticle(EnumParticleTypes type) {
        double ox, oy, oz;
        float s = this.getScale() * 1.2f;

        switch (type) {
            case EXPLOSION_NORMAL:
                ox = rand.nextGaussian() * s;
                oy = rand.nextGaussian() * s;
                oz = rand.nextGaussian() * s;
                break;

            case CLOUD:
                ox = (rand.nextDouble() - 0.5) * 0.1;
                oy = rand.nextDouble() * 0.2;
                oz = (rand.nextDouble() - 0.5) * 0.1;
                break;

            case REDSTONE:
                ox = 0.8;
                oy = 0;
                oz = 0.8;
                break;

            default:
                ox = 0;
                oy = 0;
                oz = 0;
        }

        // use generic random box spawning
        double x = this.posX + (rand.nextDouble() - 0.5) * this.width * s;
        double y = this.posY + (rand.nextDouble() - 0.5) * this.height * s;
        double z = this.posZ + (rand.nextDouble() - 0.5) * this.width * s;

        this.world.spawnParticle(type, x, y, z, ox, oy, oz);
    }

    public void spawnBodyParticles(EnumParticleTypes type, int baseAmount) {
        int amount = (int) (baseAmount * this.getScale());
        for (int i = 0; i < amount; i++) {
            spawnBodyParticle(type);
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource src) {
        super.onDeath(src);
        if (this.world.isRemote) return;
        if (isTamed()) {
            /*ItemStack stack = new ItemStack(dragonEssence());
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeToNBT(nbt);
            stack.setTagCompound(nbt);
            BlockPos pos = this.getPosition();
            this.world.setBlockState(pos, DMBlocks.DRAGON_CORE.getDefaultState(), 1);
            TileEntity tile = this.world.getTileEntity(pos);
            if (tile instanceof DragonCoreBlockEntity) {
                ((DragonCoreBlockEntity) tile).setInventorySlotContents(0, stack);
            }TODO: DragonType.getInstance*/
        } else if (!isEgg()) {
            this.inventory.dropAllItems();
        }
    }

    /**
     * Handles entity death timer, experience orb and particle creation
     */
    @Override
    protected void onDeathUpdate() {
        helpers.values().forEach(DragonHelper::onDeathUpdate);

        // unmount any riding entities
        removePassengers();

        // freeze at place
        motionX = motionY = motionZ = 0;
        rotationYaw = prevRotationYaw;
        rotationYawHead = prevRotationYawHead;

        if (isEgg() || ++deathTime > getMaxDeathTime()) setDead();// actually delete entity after the time is up

        if (this.world.isRemote && deathTime < getMaxDeathTime() - 20)
            spawnBodyParticles(EnumParticleTypes.CLOUD, 4);
    }

    @Override
    public void setDead() {
        helpers.values().forEach(DragonHelper::onDeath);
        super.setDead();
    }

    @Override
    public ITextComponent getDisplayName() {
        // return custom name if set
        if (this.hasCustomName()) return new TextComponentString(this.getCustomNameTag());
        // return default breed name otherwise
        return new TextComponentTranslation(this.makeTranslationKey());
    }

    public String makeTranslationKey() {
        return DMUtils.makeDescriptionId("entity.dragon", this.getVariant().type.identifier);
    }

    public void roar() {
        if (!isDead && !isUsingBreathWeapon()) {
            SoundEvent sound = this.getVariant().type.behavior.getRoarSound(this);
            if (sound == null) return;
            this.roarTicks = 0;
            world.playSound(posX, posY, posZ, sound, SoundCategory.NEUTRAL, MathX.clamp(getScale(), 0.3F, 0.6F), getSoundPitch(), true);
            // sound volume should be between 0 - 1, and scale is also 0 - 1
        }
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
    protected SoundEvent getDeathSound() {
        return this.isEgg() ? ModSounds.DRAGON_HATCHED : ModSounds.ENTITY_DRAGON_DEATH;//TODO: update DragonType.behavior
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    public SoundEvent getLivingSound() {
        return isEgg() || isUsingBreathWeapon() ? null : this.getVariant().type.behavior.getLivingSound(this);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    public SoundEvent getHurtSound(DamageSource src) {
        return isEgg()
                ? ModSounds.DRAGON_HATCHING
                : SoundEvents.ENTITY_ENDERDRAGON_HURT;//TODO: update DragonType.behavior
    }

    public SoundEvent getWingsSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_FLAP;//TODO: update DragonType.behavior
    }

    public SoundEvent getStepSound() {
        return ModSounds.ENTITY_DRAGON_STEP;//TODO: update DragonType.behavior
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;//TODO: update DragonType.behavior
    }

    public SoundEvent getAttackSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;//TODO: update DragonType.behavior
    }

    /**
     * Plays living's sound at its position
     */
    public void playLivingSound() {
        SoundEvent sound = getLivingSound();
        if (sound == null && !isEgg() && isUsingBreathWeapon()) {
            return;
        }

        playSound(sound, 0.8f, 1);
    }

    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    public int getTalkInterval() {
        return 240;
    }

    /**
     * Client side method for wing animations. Plays wing flapping sounds.
     *
     * @param speed wing animation playback speed
     */
    public void onWingsDown(float speed) {
        if (!isInWater() && isFlying()) {
            // play wing sounds
            float pitch = (1);
            float volume = 0.8f + (getScale() - speed);
            playSound(getWingsSound(), volume, pitch, false);
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    public void playStepSound(BlockPos entityPos, Block block) {
        // no sounds for eggs or underwater action
        if (isEgg() || isInWater() || isOverWater()) return;

        if (isFlying() || isSitting()) return;

        SoundEvent stepSound;
        // baby has quiet steps, larger have stomping sound
        if (isBaby()) {
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
    public void playSound(SoundEvent sound, float volume, float pitch) {
        playSound(sound, volume, pitch, false);
    }

    /**
     * Returns the volume for a sound to play.
     */
    public float getVolume(SoundEvent sound) {
        return MathX.clamp(getScale(), 0.8F, 1.4F);
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
        return 1;
    }

    /**
     * Gets the pitch of living sounds in living entities.
     */
    @Override
    protected float getSoundPitch() {
        // note: unused, managed in playSound()
        return 1;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEFINED;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.925F;// Vanilla: 0.8F
    }

    public void tamedFor(EntityPlayer player, boolean successful) {
        if (successful) {
            setTamed(true);
            navigator.clearPath(); // replacement for setPathToEntity(null);
            setAttackTarget(null);
            setOwnerId(player.getUniqueID());
            playTameEffect(true);
            world.setEntityState(this, (byte) 7);
        } else {
            playTameEffect(false);
            world.setEntityState(this, (byte) 6);
        }
    }

    public boolean isTamedFor(EntityPlayer player) {
        return isTamed() && isOwner(player);
    }

    /**
     * Checks if the parameter is an item which this animal can be fed to breed it
     * (wheat, carrots or seeds depending on the animal type)
     */
    @Override
    public boolean isBreedingItem(ItemStack item) {
        return Items.FISH == item.getItem();//TODO: update DragonType.behavior
    }

    /**
     * Returns the height level of the eyes. Used for looking at other entities.
     */
    @Override
    public float getEyeHeight() {
        float eyeHeight = height * 0.85F;

        if (isSitting()) {
            eyeHeight *= 0.8f;
        }

        if (isEgg()) {
            eyeHeight = 1.3f;
        }

        return eyeHeight;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this
     * one.
     * May not be necessary since we also override updatePassenger()
     */
    @Override
    public double getMountedYOffset() {
        //final int DEFAULT_PASSENGER_NUMBER = 0;
        return this.getVariant().type.behavior.locatePassenger(0, this.isSitting()).y * getScale();
    }

    /**
     * Returns render size modifier for the shadow
     */
    @Override
    public float getRenderSizeModifier() {
        return getScale() / (isChild() ? 0.5F : 1.0F);
//  0.5 isChild() correction is required due to the code in Render::renderShadow which shrinks the shadow for a child
//    if (entityIn instanceof EntityLiving)
//    {
//      EntityLiving entityliving = (EntityLiving)entityIn;
//      f *= entityliving.getRenderSizeModifier();
//
//      if (entityliving.isChild())
//      {
//        f *= 0.5F;
//      }
//    }

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
     * Determines if an entity can be despawned, used on idle far away entities
     */
    @Override
    protected boolean canDespawn() {
        return false;
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    @Override
    public boolean isOnLadder() {
        // this better doesn't happen...
        return false;
    }

    /**
     * Returns the entity's health relative to the maximum health.
     *
     * @return health normalized between 0 and 1
     */
    public double getHealthRelative() {
        return getHealth() / (double) getMaxHealth();
    }

    public int getDeathTime() {
        return deathTime;
    }

    public int getMaxDeathTime() {
        return 120;
    }

    public void setImmuneToFire(boolean isImmuneToFire) {
        L.trace("setImmuneToFire({})", isImmuneToFire);
        this.isImmuneToFire = isImmuneToFire;
    }

    public void setAttackDamage(double damage) {
        L.trace("setAttackDamage({})", damage);
        getEntityAttribute(ATTACK_DAMAGE).setBaseValue(damage);
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource src) {
        Entity srcEnt = src.getImmediateSource();
        if (srcEnt != null) {
            // ignore own damage
            if (srcEnt == this) {
                return true;
            }

            // ignore damage from riders
            if (isPassenger(srcEnt)) {
                return true;
            }
        }

        // don't drown as egg
        if (src == DamageSource.DROWN && isEgg()) {
            return true;
        }

        return this.getVariant().type.isInvulnerableTo(src);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity sourceEntity = source.getTrueSource();

        if (source != DamageSource.IN_WALL) {
            // don't just sit there!
            this.getAISit().setSitting(false);
        }

        if (this.isBeingRidden() && sourceEntity != null && this.isPassenger(sourceEntity) && damage < 1) {
            return false;
        }

        if (getRidingCarriage() != null && getRidingCarriage().isPassenger(sourceEntity)) {
            return false;
        }

        if (!world.isRemote && sourceEntity != null && this.getRNG().nextInt(4) == 0 && !isEgg()) {
            this.roar();
        }

        if (isBaby() && isJumping) {
            return false;
        }

        if (sourceEntity != null && sourceEntity.isPassenger(this)) {
            return false;
        }

        return super.attackEntityFrom(source, damage);
    }

    /**
     * Called when an entity attacks
     */
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean attacked = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float) getEntityAttribute(ATTACK_DAMAGE).getAttributeValue());

        if (attacked) {
            applyEnchantments(this, entityIn);
        }

        if (this.getVariant().type == DragonTypes.WITHER) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
        }

        return attacked;
    }

    @Override
    public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
        if (target.isChild()) return super.shouldAttackEntity(target, owner);
        if (target instanceof EntityTameable) {
            if (((EntityTameable) target).isTamed()) {
                return false;
            }
        } else if (target instanceof EntityPlayer && this.isTamedFor((EntityPlayer) target)) {
            return false;
        }
        return super.shouldAttackEntity(target, owner);
    }

    /**
     * Used to get the hand in which to swing and play the hand swinging animation
     * when attacking In this case the dragon's jaw
     */
    @Override
    public void swingArm(EnumHand hand) {
        // play eating sound
        playSound(getAttackSound(), 1, 0.7f);

        // play attack animation
        if (world instanceof WorldServer) {
            ((WorldServer) world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, 0));
        }

        ticksSinceLastAttack = 0;
    }

    /**
     * Return whether this entity should be rendered as on fire.
     */
    @Override
    public boolean canRenderOnFire() {
        return super.canRenderOnFire() && !this.getVariant().type.isInvulnerableTo(DamageSource.IN_FIRE);
    }

    /**
     * Returns true if the mob is currently able to mate with the specified mob.
     */
    @Override
    public boolean canMateWith(EntityAnimal mate) {
        return getReproductionHelper().canMateWith(mate);
    }

    /**
     * This function is used when two same-species animals in 'love mode' breed to
     * generate the new baby animal.
     */
    @Override
    public EntityAgeable createChild(EntityAgeable mate) {
        EntityTameableDragon parent1 = this;
        EntityTameableDragon parent2 = (EntityTameableDragon) mate;

        if (parent1.isMale() && !parent2.isMale() || !parent1.isMale() && parent2.isMale()) {
            return getReproductionHelper().createChild(parent1.isMale() ? mate : parent1);
        } else {
            return null;
        }
    }

    private void addHelper(DragonHelper helper) {
        L.trace("addHelper({})", helper.getClass().getName());
        helpers.put(helper.getClass(), helper);
    }

    @SuppressWarnings("unchecked")
    private <T extends DragonHelper> T getHelper(Class<T> clazz) {
        return (T) helpers.get(clazz);
    }

    public DragonLifeStageHelper getLifeStageHelper() {
        return getHelper(DragonLifeStageHelper.class);
    }

    public DragonReproductionHelper getReproductionHelper() {
        return getHelper(DragonReproductionHelper.class);
    }

    public DragonBreathHelper getBreathHelper() {
        return getHelper(DragonBreathHelper.class);
    }

    public DragonBreathHelperP getBreathHelperP() {  // enable compilation only
        throw new UnsupportedOperationException();
        //return getHelper(DragonBreathHelperP.class);
    }

    public DragonAnimator getAnimator() {
        return animator;
    }

    public DragonBrain getBrain() {
        return getHelper(DragonBrain.class);
    }

    public double getDragonSpeed() {
        return isFlying() ? BASE_FOLLOW_RANGE_FLYING : BASE_FOLLOW_RANGE;
    }

    public void updateIntendedRideRotation(EntityPlayer rider) {
        if (isUsingBreathWeapon()) this.rotationYawHead = this.renderYawOffset;
        boolean hasRider = this.hasControllingPlayer(rider);
        if (hasRider && rider.moveStrafing == 0) {
            this.rotationYaw = rider.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = rider.rotationPitch;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            this.renderYawOffset = this.rotationYaw;
        }
    }
//
//    public boolean canBeSteered() {
//        return true;
//    }

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
        return this.getPassengers().isEmpty() ? null : getPassengers().get(0);
    }

    /**
     * Biased method of getControllingPassenger so that only players can control the dragon
     *
     * @return player on the front
     */
    @Nullable
    public EntityPlayer getControllingPlayer() {
        Entity entity = this.getPassengers().isEmpty() ? null : getPassengers().get(0);
        if (entity instanceof EntityPlayer) {
            return (EntityPlayer) entity;
        } else {
            return null;
        }
    }

    /**
     * gets the passengers and check if there is any carriages
     *
     * @return a riding carriage
     */
    @Nullable
    public Entity getRidingCarriage() {
        List<Entity> entity = this.getPassengers().isEmpty() ? null : this.getPassengers();
        if (entity instanceof EntityCarriage) {
            return (EntityCarriage) entity;
        } else {
            return null;
        }
    }

    /**
     * Gets a controlling player along with clientside
     *
     * @param player
     * @return
     */
    public boolean hasControllingPlayer(EntityPlayer player) {
        return player.equals(this.getControllingPlayer());
    }

    /**
     * sets the riding player alongide with its rotationYaw and rotationPitch
     *
     * @param player
     */
    public void setRidingPlayer(EntityPlayer player) {
        L.trace("setRidingPlayer({})", player.getName());
        player.rotationYaw = rotationYaw;
        player.rotationPitch = rotationPitch;
        player.startRiding(this);
    }

    public boolean isRidingAboveGround(Entity entityBeingRidden) {
        int groundPos = world.getHeight(getPosition()).getY();
        double altitude = entityBeingRidden.posY - groundPos;
        return altitude > 2.0;
    }

    @Override
    public void updateRidden() {
        Entity vehicle = this.getRidingEntity();
        if (this.isDead || vehicle != null && vehicle.isSneaking() || !isBaby()) {
            this.dismountRidingEntity();
        } else {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            this.onUpdate();
            if (vehicle instanceof EntityPlayer) this.updateRiding((EntityPlayer) vehicle);
        }
    }

    @Override
    public void dismountRidingEntity() {
        super.dismountRidingEntity();
        this.setUsingBreathWeapon(false);
    }

    /**
     * This code is called when the dragon is riding on the shoulder of the player. Credits: Ice and Fire
     *
     * @param riding
     */
    public void updateRiding(@Nonnull EntityPlayer riding) {
        if (riding.isPassenger(this)) {
            int index = riding.getPassengers().indexOf(this);
            boolean flying = riding.isElytraFlying();
            float radius = (index == 2 ? 0F : 0.4F) + (flying ? 2 : 0);
            float angle = (0.01745329251F * riding.renderYawOffset) + (index == 1 ? -90 : index == 0 ? 90 : 0);
            double extraX = radius * MathHelper.sin((float) (Math.PI + angle));
            double extraZ = radius * MathHelper.cos(angle);
            double extraY = (!riding.isSneaking() ? 1.4D : 1.1D) + (index == 2 ? 0.4D : 0D);
            this.rotationYaw = riding.rotationYaw;
            this.prevRotationYaw = riding.prevRotationYaw;
            this.renderYawOffset = riding.renderYawOffset;
            this.rotationYawHead = riding.rotationYawHead;
            this.prevRotationYawHead = riding.prevRotationYawHead;
            this.rotationPitch = riding.rotationPitch;
            this.prevRotationPitch = riding.prevRotationPitch;
            this.setPosition(riding.posX + extraX, riding.posY + extraY, riding.posZ + extraZ);
            this.setFlying(isRidingAboveGround(riding) && !riding.capabilities.isFlying && !riding.onGround || flying);
        }
    }

    /**
     * Arrays Starts at 0, hope a new coder wont be confused with < and <=
     *
     * @param passenger
     * @return
     */
    protected boolean canFitPassenger(Entity passenger) {
        return this.getPassengers().size() < 5;
    }

    /**
     * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
     */
    protected void applyYawToEntity(Entity entityToUpdate) {
        entityToUpdate.setRenderYawOffset(this.rotationYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    /**
     * This code is called when the passenger is riding on the dragon
     *
     * @param passenger
     */
    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            List<Entity> passengers = getPassengers();
            int passengerNumber = passengers.indexOf(passenger);
            if (passengerNumber < 0) {  // should never happen!
                DragonMounts.loggerLimit.error_once("Logic error- passenger not found");
                return;
            }

            Vec3d mountedPositionOffset = this.getVariant().type.behavior.locatePassenger(passengerNumber, this.isSitting());

            double dragonScaling = getScale(); //getBreed().getAdultModelRenderScaleFactor() * getScale();

            mountedPositionOffset = mountedPositionOffset.scale(dragonScaling);
            mountedPositionOffset = mountedPositionOffset.rotateYaw((float) Math.toRadians(-renderYawOffset)); // oops
            mountedPositionOffset = mountedPositionOffset.add(0, passenger.getYOffset(), 0);

            if (!(passenger instanceof EntityPlayer)) {
                passenger.rotationYaw = this.rotationYaw;
                passenger.setRotationYawHead(passenger.getRotationYawHead() + this.rotationYaw);
                this.applyYawToEntity(passenger);
            }
            Vec3d passengerPosition = mountedPositionOffset.add(this.posX, this.posY, this.posZ);
            passenger.setPosition(passengerPosition.x, passengerPosition.y, passengerPosition.z);

            // fix rider rotation
            if (passenger == getControllingPlayer()) {
                EntityPlayer rider = getControllingPlayer();
                rider.prevRotationPitch = rider.rotationPitch;
                rider.prevRotationYaw = rider.rotationYaw;
                rider.renderYawOffset = renderYawOffset;
            }
        }
    }

    /**
     * Public wrapper for protected final setScale(), used by DragonLifeStageHelper.
     *
     * @param scale
     */
    public void setScalePublic(float scale) {
        double posXTmp = posX;
        double posYTmp = posY;
        double posZTmp = posZ;
        boolean onGroundTmp = onGround;

        setScale(scale);

        // workaround for a vanilla bug; the position is apparently not set correcty
        // after changing the entity size, causing asynchronous server/client
        // positioning
        setPosition(posXTmp, posYTmp, posZTmp);

        // otherwise, setScale stops the dragon from landing while it is growing
        onGround = onGroundTmp;
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
        return isAdult() ? 0 : -1;
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
        return getLifeStageHelper().getScale();
    }

    public boolean isEgg() {
        return getLifeStageHelper().isEgg();
    }

    public boolean isBaby() {
        return getLifeStageHelper().isBaby();
    }

    public boolean isOldEnoughToBreathe() {
        return getLifeStageHelper().isOldEnough(DragonLifeStage.INFANT);
    }

    public boolean isJuvenile() {
        return getLifeStageHelper().isJuvenile();
    }

    public boolean isAdult() {
        return getLifeStageHelper().isFullyGrown();
    }

    @Override
    public boolean isChild() {
        return getLifeStageHelper().isBaby();
    }

    /**
     * Checks if this entity is running on a client.
     * <p>
     * Required since MCP's isClientWorld returns the exact opposite...
     *
     * @return true if the entity runs on a client or false if it runs on a server
     */
    @Deprecated
    public final boolean isClient() {
        return world.isRemote;
    }

    /**
     * Checks if this entity is running on a server.
     *
     * @return true if the entity runs on a server or false if it runs on a client
     */
    @Deprecated
    public final boolean isServer() {
        return !world.isRemote;
    }

    @Override
    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    public boolean canBeLeashedTo(EntityPlayer player) {
        return true;
    }

    public int getHunger() {
        return dataManager.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.dataManager.set(HUNGER, Math.min(100, hunger));
    }

    @Override
    protected ResourceLocation getLootTable() {
        if (!isTamed() && !isEgg() || !isBaby()) {
            return this.getVariant().type.getInstance(LootTableLocation.class, null);
        } else {
            return null;
        }
    }

    public boolean isSheared() {
        return (this.dataManager.get(DRAGON_SCALES) & 16) != 0;
    }

    /**
     * make a dragon sheared if set to true
     */
    public void setSheared(boolean sheared) {
        byte b0 = this.dataManager.get(DRAGON_SCALES);

        if (sheared) {
            dataManager.set(DRAGON_SCALES, (byte) (b0 | 16));
        } else {
            dataManager.set(DRAGON_SCALES, (byte) (b0 & -17));
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
        return item != null && item.getItem() == DMItems.diamond_shears && (isJuvenile() || isAdult()) && ticksShear <= 0;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack stack, IBlockAccess world, BlockPos pos, int fortune) {
        //TODO: getDragonType
        Item item = DragonTypes.ENDER.getInstance(DragonScalesItem.class, null);
        if (item == null) return Collections.emptyList();
        this.setSheared(true);
        ticksShear = 3000;

        playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        playSound(ModSounds.ENTITY_DRAGON_GROWL, 1.0F, 1.0F);

        return Collections.singletonList(new ItemStack(item, 2 + this.rand.nextInt(3)));
    }

    /**
     * Called when a lightning bolt hits the entity.
     */
    @Override
    public void onStruckByLightning(EntityLightningBolt bolt) {
        DragonType current = this.getVariant().type;
        super.onStruckByLightning(bolt);
        if (current == DragonTypes.SKELETON) {
            this.setVariant(DragonTypes.WITHER.variants.draw(this.rand, null));
            this.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2, 1);
            this.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 2, 1);
        }

        if (current == DragonTypes.WATER) {
            this.setVariant(DragonTypes.STORM.variants.draw(this.rand, null));
            this.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2, 1);
            this.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 2, 1);
        }

        addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 35 * 20));
    }

    @Nullable
    public String checkAccess(EntityPlayer player) {
        if (!this.isTamed() && !isEgg()) {
            return "dragon.notTamed";
        } else if (!this.allowedOtherPlayers() && !this.isTamedFor(player) && this.isTamed()) {
            return "dragon.locked";
        } else return null;
    }

    /**
     * is the dragon allowed to be controlled by other players? Uses dragon is not owned status messages
     *
     * @param player
     * @return
     */
    public boolean isAllowed(EntityPlayer player) {
        String warn = this.checkAccess(player);
        if (warn == null) return true;
        player.sendStatusMessage(new TextComponentTranslation(warn), true);
        return false;
    }

    private void eatEvent(ItemStack stack) {
        playSound(this.getEatSound(), 1f, 0.75f);
        double motionX = this.getRNG().nextGaussian() * 0.07D;
        double motionY = this.getRNG().nextGaussian() * 0.07D;
        double motionZ = this.getRNG().nextGaussian() * 0.07D;
        Vec3d pos = this.getAnimator().getThroatPosition();

        if (world.isRemote) {
            world.spawnParticle(EnumParticleTypes.ITEM_CRACK, pos.x, pos.y, pos.z, motionX, motionY, motionZ, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
        }
    }

    public static boolean isInertItem(ItemStack stack) {
        if (stack.isEmpty()) return true;
        if (stack.getItemUseAction() != EnumAction.NONE) return false;
        Item item = stack.getItem();
        if (ModItems.DRAGON_INTERACTABLE.contains(item)) return false;
        return !(item instanceof ItemFood);
    }

    /**
     * Called when a player right clicks a mob. e.g. gets milk from a cow, gets into the saddle on a pig, ride a dragon.
     */
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.isEgg()) {
            /*
             * Turning it to block
             */
            if (player.isSneaking()) {
                world.playSound(player, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.PLAYERS, 0.5F, 1);
                //world.setBlockState(getPosition(), BlockDragonBreedEgg.DRAGON_BREED_EGG.getStateFromMeta(getBreedType().getMeta()));TODO: use DragonType
                setDead();
                return true;
            }
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);

        final String warn = checkAccess(player);
        final boolean allow = warn == null;

        /*
         * Dragon Riding The Player
         */
        if (allow && !isSitting() && this.isBaby() && !player.isSneaking() && player.getPassengers().size() < 2 && isInertItem(stack)) {
            this.setAttackTarget(null);
            this.getNavigator().clearPath();
            this.getAISit().setSitting(false);
            this.startRiding(player, true);
            return true;
        }

        // prevent doing any interactions when a hatchling rides you, the hitbox could block the player's raytraceresult for rightclick
        if (player.isPassenger(this)) return false;

        if (this.isServer()) {
            if (allow && isInertItem(stack)) {
                /*
                 * Sit
                 */
                if (this.onGround && !stack.isEmpty() && DMUtils.anyMatches(stack, OreDictionary.getOreID("stickWood"), OreDictionary.getOreID("bone"))) {
                    L.debug("sit");
                    this.getAISit().setSitting(!this.isSitting());
                    this.getNavigator().clearPath();
                }

                /*
                 * GUI
                 */
                if (player.isSneaking()) {
                    // Dragon Inventory
                    this.openGUI(player, GuiHandler.GUI_DRAGON);
                } else if (!this.isBaby()) {
                    /*
                     * Player Riding the Dragon
                     */
                    if (this.isSaddled()) {
                        if (this.getPassengers().size() < 3) {
                            this.setRidingPlayer(player);
                        }
                    } else if (!stack.isEmpty() && stack.getItem() == Items.SADDLE) {
                        if (player.capabilities.isCreativeMode) {
                            ItemStack saddle = stack.copy();
                            saddle.setCount(1);
                            this.setSaddle(saddle);
                        } else {
                            this.setSaddle(stack.splitStack(1));
                        }
                        player.swingArm(hand);
                    }
                }
            }
        }

        /*
         * Consume
         */
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (!(item instanceof ItemFood)) return false;
        boolean isTamed = this.isTamed();
        if (isTamed && this.getHunger() >= 100) return false;
        ItemFood food = (ItemFood) item;
        boolean isAquaticFood;
        if (food.isWolfsFavoriteMeat()) {
            if (this.consumeFood(player, stack, food)) return true;
            isAquaticFood = ModItems.isAquaticFood(stack);
        } else {
            isAquaticFood = ModItems.isAquaticFood(stack);
            if (isAquaticFood && this.consumeFood(player, stack, food)) return true;
        }
        if (!allow) {
            player.sendStatusMessage(new TextComponentTranslation(warn), true);
            return false;
        }
        if (isGrowthPaused()) {
            // Continue growth
            if (Items.CARROT == food) {//TODO: Re-implement dragon food
                setGrowthPaused(false);
                eatEvent(stack);
                playSound(SoundEvents.ENTITY_PLAYER_BURP, 1, 0.8F);
                consumeItemFromStack(player, stack);
                return true;
            }
        } else {
            // Stop Growth
            if (Items.POISONOUS_POTATO == food) {
                setGrowthPaused(true);
                eatEvent(stack);
                playSound(SoundEvents.ENTITY_PLAYER_BURP, 1f, 0.8F);
                player.sendStatusMessage(new TextComponentTranslation("dragon.growth.paused"), true);
                consumeItemFromStack(player, stack);
                return true;
            }
        }
        // Mate
        if ((isAquaticFood || Items.FISH == food) && isTamed && this.isAdult() && !this.isInLove()) {
            setInLove(player);
            eatEvent(stack);
            consumeItemFromStack(player, stack);
            return true;
        }
        return false;
    }

    public boolean consumeFood(EntityPlayer player, ItemStack stack, ItemFood food) {
        // Taming
        if (!this.isTamed()) {
            consumeItemFromStack(player, stack);
            eatEvent(stack);
            tamedFor(player, this.getRNG().nextInt(4) == 0);
            return true;
        }

        //  hunger
        if (this.getHunger() < 100) {
            consumeItemFromStack(player, stack);
            eatEvent(stack);
            setHunger(this.getHunger() + food.getHealAmount(stack) * 2);
            return true;
        }
        return false;
    }

    /**
     * Credits: AlexThe 666 Ice and Fire
     */
    public void openGUI(EntityPlayer playerEntity, int guiId) {
        if (!this.world.isRemote && (!this.isPassenger(playerEntity))) {
            playerEntity.openGui(DragonMounts.instance, guiId, this.world, this.getEntityId(), 0, 0);
        }
    }

    @Override
    public boolean isPotionApplicable(PotionEffect effect) {
        PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, effect);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult() == Event.Result.DEFAULT ? effect.getPotion() != MobEffects.WEAKNESS : event.getResult() == Event.Result.ALLOW;
    }

    public final void onLifeStageChange(DragonLifeStage stage) {
        if (stage.isEgg()) {
            this.setSize(3.5F, 4.0F);
        } else {
            //Pair<Float, Float> size = this.getBreed().getAdultEntitySize();TODO: use DragonType or something else
            this.setSize(4.8F, 4.2F);
        }
    }

    public final boolean isFirstUpdate() {
        return this.firstUpdate;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        this.inventory.writeSpawnData(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.inventory.readSpawnData(buffer);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (DATA_VARIANT.equals(key)) {
            this.variantHelper.onVariantChanged(this.getVariant());
        } else if (DATA_CHEST.equals(key)) {
            ItemStack stack = this.getChest();
            boolean chested = !stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST);
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

