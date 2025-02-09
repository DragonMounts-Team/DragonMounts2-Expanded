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

import com.google.common.base.Optional;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsConfig;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.client.gui.GuiHandler;
import net.dragonmounts.client.model.dragon.anim.DragonAnimator;
import net.dragonmounts.entity.ai.ground.EntityAIDragonSit;
import net.dragonmounts.entity.ai.path.PathNavigateFlying;
import net.dragonmounts.entity.breath.DragonBreathHelper;
import net.dragonmounts.entity.helper.*;
import net.dragonmounts.init.*;
import net.dragonmounts.inventory.DragonInventory;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.item.DragonEssenceItem;
import net.dragonmounts.item.DragonSpawnEggItem;
import net.dragonmounts.network.CDragonBreathPacket;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.ItemUtil;
import net.dragonmounts.util.MutableBlockPosEx;
import net.dragonmounts.util.math.MathX;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.BiomeDictionary;
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

import static net.dragonmounts.util.EntityUtil.replaceAttributeModifier;
import static net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE;
import static net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE;

/**
 * Here be dragons
 */
public class TameableDragonEntity extends EntityTameable implements IEntityAdditionalSpawnData {
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
    private static final DataParameter<Boolean> DATA_FLYING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GROWTH_PAUSED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> DATA_BREATHING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GOING_DOWN = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> ALLOW_OTHERPLAYERS = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> BOOSTING = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HOVER_CANCELLED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> Y_LOCKED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FOLLOW_YAW = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Optional<UUID>> DATA_BREEDER = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    private static final DataParameter<DragonVariant> DATA_VARIANT = EntityDataManager.createKey(TameableDragonEntity.class, DragonVariant.SERIALIZER);
    private static final DataParameter<Integer> DATA_REPRO_COUNT = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> HUNGER = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DATA_TICKS_SINCE_CREATION = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DATA_SHEARED = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.BOOLEAN);
    //private static final DataParameter<Boolean> SLEEP = EntityDataManager.createKey(EntityTameableDragon.class, DataSerializers.BOOLEAN);
    private static final DataParameter<String> DATA_BREATH_WEAPON_TARGET = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> DATA_BREATH_WEAPON_MODE = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.VARINT);
    private static final DataParameter<ItemStack> DATA_ARMOR = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<ItemStack> DATA_CHEST = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<ItemStack> DATA_SADDLE = EntityDataManager.createKey(TameableDragonEntity.class, DataSerializers.ITEM_STACK);
    // server/client delegates
    private final Map<Class<?>, DragonHelper> helpers = new Reference2ObjectOpenHashMap<>();
    // client-only delegates
    private final DragonBodyHelper dragonBodyHelper = new DragonBodyHelper(this);
    public EntityEnderCrystal healingEnderCrystal;
    public final DragonInventory inventory = new DragonInventory(this);
    public final DragonVariantHelper variantHelper = new DragonVariantHelper(this);
    public int inAirTicks;
    public int roarTicks;
    protected int ticksSinceLastAttack;
    private boolean isUsingBreathWeapon;
    private boolean isGoingDown;
    private boolean isUnhovered;
    private boolean yLocked;
    private boolean followYaw;
    public final DragonAnimator animator;
    private boolean armored;
    private boolean chested;
    private boolean saddled;
    protected int shearCooldown;
    public TameableDragonEntity(World world) {
        super(world);

        // enables walking over blocks
        stepHeight = (float) DragonMountsConfig.stepHeight;

        // create entity delegates
        addHelper(new DragonLifeStageHelper(this, DATA_TICKS_SINCE_CREATION));
        addHelper(new DragonReproductionHelper(this, DATA_BREEDER, DATA_REPRO_COUNT));
        addHelper(new DragonBreathHelper(this, DATA_BREATH_WEAPON_TARGET, DATA_BREATH_WEAPON_MODE));
        addHelper(new DragonHungerHelper(this));
        if (!world.isRemote) addHelper(new DragonBrain(this));

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
        manager.register(GOING_DOWN, false);
        manager.register(Y_LOCKED, false);
        manager.register(HOVER_CANCELLED, false);
        manager.register(ALLOW_OTHERPLAYERS, false);
        manager.register(BOOSTING, false);
        manager.register(DATA_SHEARED, false);
        //        manager.register(SLEEP, false); //unused as of now
        manager.register(FOLLOW_YAW, true);
        manager.register(DATA_BREATH_WEAPON_TARGET, "");
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
        attributes.getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(DragonMountsConfig.BASE_HEALTH);
        attributes.getAttributeInstance(SWIM_SPEED).setBaseValue(5.0);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Sheared", this.shearCooldown);
        nbt.setBoolean("Breathing", this.isUsingBreathWeapon());
        //nbt.setBoolean("projectile", this.isUsingAltBreathWeapon());
        nbt.setBoolean("unhovered", this.isUnHovered());
        nbt.setBoolean("followyaw", this.followYaw());
        nbt.setInteger("AgeTicks", this.getLifeStageHelper().getTicksSinceCreation());
        nbt.setInteger("hunger", this.getHunger());
        nbt.setBoolean("boosting", this.boosting());
        nbt.setBoolean("ylocked", this.isYLocked());
        nbt.setBoolean("growthpause", this.isGrowthPaused());
        nbt.setBoolean("AllowOtherPlayers", this.allowedOtherPlayers());
        nbt.setString(DragonVariant.DATA_PARAMETER_KEY, this.getVariant().getSerializedName());
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
        this.setSheared(nbt.getInteger("Sheared"));
        this.setHunger(nbt.getInteger("hunger"));
        this.setGrowthPaused(nbt.getBoolean("growthpause"));
        this.setUsingBreathWeapon(nbt.getBoolean("Breathing"));
        //this.setUsingProjectile(nbt.getBoolean("projectile"));
        this.getLifeStageHelper().setTicksSinceCreation(nbt.getInteger("AgeTicks"));
        this.setUnHovered(nbt.getBoolean("unhovered"));
        this.setYLocked(nbt.getBoolean("ylocked"));
        this.setFollowYaw(nbt.getBoolean("followyaw"));
        this.setBoosting(nbt.getBoolean("boosting"));
        //        this.setSleeping(nbt.getBoolean("sleeping")); //unused as of now
        this.setToAllowedOtherPlayers(nbt.getBoolean("AllowOtherPlayers"));
        this.inventory.readAdditionalData(nbt);
        if (nbt.getBoolean("DataFix$IsForest")) {
            boolean male = this.rand.nextBoolean();
            Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(this.world.getBiome(this.getPosition()));
            if (types.contains(BiomeDictionary.Type.SAVANNA) || types.contains(BiomeDictionary.Type.DRY) || types.contains(BiomeDictionary.Type.MESA) || types.contains(BiomeDictionary.Type.SANDY)) {
                this.setVariant(male ? DragonVariants.FOREST_DRY_MALE : DragonVariants.FOREST_DRY_FEMALE);
            } else if (types.contains(BiomeDictionary.Type.COLD) || types.contains(BiomeDictionary.Type.MOUNTAIN)) {
                this.setVariant(male ? DragonVariants.FOREST_TAIGA_MALE : DragonVariants.FOREST_TAIGA_FEMALE);
            } else {
                this.setVariant(male ? DragonVariants.FOREST_MALE : DragonVariants.FOREST_FEMALE);
            }
        } else if (nbt.hasKey(DragonVariant.DATA_PARAMETER_KEY)) {
            this.setVariant(DragonVariant.byName(nbt.getString(DragonVariant.DATA_PARAMETER_KEY)));
        }
        this.variantHelper.readFromNBT(nbt);
        helpers.values().forEach(helper -> helper.readFromNBT(nbt));
    }

    public boolean boosting() {
        return dataManager.get(BOOSTING);
    }

    public void setBoosting(boolean allow) {
        dataManager.set(BOOSTING, allow);
    }

    // public boolean isSleeping() {
    //  return dataManager.get(SLEEP);
    // }

    // public void setSleeping(boolean sleeping) {
    //   dataManager.set(SLEEP, sleeping);
    // }

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

    @SideOnly(Side.CLIENT)
    public void updateKeys() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player == this.getRidingEntity()) {
            DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonBreathPacket(
                    this.getEntityId(),
                    DMKeyBindings.KEY_BREATH.isKeyDown()
            ));
        }
    }

    @Override
    public void onLivingUpdate() {
        boolean isServer = !this.world.isRemote;
        this.variantHelper.onLivingUpdate();
        helpers.values().forEach(DragonHelper::onLivingUpdate);
        this.getVariant().type.tick(this);
        if (isServer) {
            animator.setMovement(0, 0); // dummy
            animator.setLook(
                    getRotationYawHead() - renderYawOffset, // netYawHead
                    rotationPitch
            );
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

                //TODO: reuse?
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
            this.updateKeys();
        }

        if (ticksSinceLastAttack >= 0) { // used for jaw animation
            ++ticksSinceLastAttack;
            if (ticksSinceLastAttack > 1000) {
                ticksSinceLastAttack = -1; // reset at arbitrary large value
            }
        }

        if (this.rand.nextFloat() < 0.001F && !this.isEgg()) {
            this.roar();
        } else if (roarTicks >= 0) {
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
        if (this.shearCooldown > 0) {
            this.setSheared(this.shearCooldown - 1);
        }
        if (!isDead) {
            if (this.healingEnderCrystal != null) {
                if (this.healingEnderCrystal.isDead) {
                    this.healingEnderCrystal = null;
                } else if (isServer && this.ticksExisted % 10 == 0) {
                    this.heal(1.0F);
                    EntityUtil.addOrResetEffect(this, MobEffects.STRENGTH, 300, 0, false, false, 201);
                }
            }

            if (this.rand.nextInt(10) == 0) {
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

        this.doBlockCollisions();

        if (!isServer) {
            EnumParticleTypes sneeze = this.getVariant().type.sneezeParticle;
            if (sneeze != null && rand.nextInt(700) == 0 && !this.isUsingBreathWeapon() && this.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
                Vec3d throatPos = this.getAnimator().getThroatPosition();
                double throatPosX = throatPos.x;
                double throatPosY = throatPos.y;
                double throatPosZ = throatPos.z;
                int floorY = MathHelper.floor(throatPosY);
                MutableBlockPosEx pos = new MutableBlockPosEx(MathHelper.floor(throatPosX), floorY, MathHelper.floor(throatPosZ));
                for (int i = -1; i < 2; ++i) {
                    world.spawnParticle(sneeze, throatPosX, throatPosY + i, throatPosZ, 0, 0.3, 0);
                    world.playSound(null, pos.withY(floorY + i), DMSounds.DRAGON_SNEEZE, SoundCategory.NEUTRAL, 0.8F, 1);
                }
            }
        }
        super.onLivingUpdate();
        if (this.getControllingPlayer() == null && !this.isFlying() && this.isSitting()) {
            this.removePassengers();
        }
    }

    @Override
    protected void collideWithEntity(Entity other) {
        if (other instanceof CarriageEntity &&
                !this.world.isRemote &&
                this.isSaddled() &&
                !other.isRiding() &&
                this.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE) &&
                !other.isPassenger(this)
        ) {
            List<Entity> passengers = this.getPassengers();
            if (passengers.isEmpty() || passengers.size() < (passengers.get(0) instanceof EntityPlayer ? 5 : 4)) {
                other.startRiding(this);
                return;
            }
        }
        super.collideWithEntity(other);
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
            DragonEssenceItem item = this.getVariant().type.getInstance(DragonEssenceItem.class, null);
            if (item == null) return;
            BlockPos pos = this.getPosition();
            this.world.setBlockState(pos, DMBlocks.DRAGON_CORE.getDefaultState(), 3);
            TileEntity tile = this.world.getTileEntity(pos);
            if (tile instanceof DragonCoreBlockEntity) {
                ((DragonCoreBlockEntity) tile).setInventorySlotContents(0, item.saveEntity(this));
            }
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
    public String getName() {
        if (this.hasCustomName()) return this.getCustomNameTag();
        String name = EntityList.getEntityString(this);
        name = name == null ? "entity.dragonmounts.dragon" : "entity." + name;
        return I18n.translateToLocalFormatted(name, I18n.translateToLocal(this.getVariant().type.translationKey));
    }

    public void roar() {
        if (!isDead && !isUsingBreathWeapon()) {
            SoundEvent sound = this.getVariant().type.getRoarSound(this);
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
        return this.isEgg() ? DMSounds.DRAGON_HATCHED : DMSounds.ENTITY_DRAGON_DEATH;//TODO: update DragonType.behavior
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    public SoundEvent getLivingSound() {
        return isEgg() || isUsingBreathWeapon() ? null : this.getVariant().type.getLivingSound(this);
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
    public SoundEvent getHurtSound(DamageSource src) {
        return isEgg()
                ? DMSounds.DRAGON_HATCHING
                : SoundEvents.ENTITY_ENDERDRAGON_HURT;//TODO: update DragonType.behavior
    }

    public SoundEvent getWingsSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_FLAP;//TODO: update DragonType.behavior
    }

    public SoundEvent getStepSound() {
        return DMSounds.ENTITY_DRAGON_STEP;//TODO: update DragonType.behavior
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
            float volume = 0.8f + (getScale() - speed);
            playSound(getWingsSound(), volume, 1, false);
        }
    }

    /**
     * Plays step sound at given x, y, z for the entity
     */
    public void playStepSound(BlockPos entityPos, Block block) {
        // no sounds for eggs or underwater action
        if (isEgg() || isInWater() || isOverWater() || isFlying() || isSitting()) return;

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
    public void playSound(@Nullable SoundEvent sound, float volume, float pitch) {
        if (sound == null) return;
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
        float eyeHeight = height * 0.85F;
        if (isSitting()) {
            eyeHeight *= 0.8f;
        }
        return eyeHeight;
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
        this.isImmuneToFire = isImmuneToFire;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource src) {
        Entity srcEnt = src.getImmediateSource();
        if (srcEnt != null) {
            // ignore own damage
            if (srcEnt == this) {
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
        if (this.isBaby() && isJumping) return false; // ?
        Entity attacker = source.getTrueSource();
        // ignore damage from riders
        if (attacker != null) {
            if (this.isPassengerBroadly(attacker)) return false;
        }
        if (source != DamageSource.IN_WALL) {
            // don't just sit there!
            this.getAISit().setSitting(false);
        }
        if (super.attackEntityFrom(source, damage)) {
            if (!this.world.isRemote && this.rand.nextFloat() < 0.25F && !this.isEgg()) {
                this.roar();
            }
            return true;
        }
        return false;
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
    public boolean shouldAttackEntity(@Nonnull EntityLivingBase target, @Nullable EntityLivingBase owner) {
        return target instanceof EntityTameable ? !(((EntityTameable) target).getOwner() instanceof EntityPlayer) : !this.isOwner(target);
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
        return mate instanceof TameableDragonEntity ? this.getReproductionHelper().createChild((TameableDragonEntity) mate) : null;
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
        List<Entity> passengers = this.getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    /**
     * Biased method of getControllingPassenger so that only players can control the dragon
     *
     * @return player on the front
     */
    @Nullable
    public EntityPlayer getControllingPlayer() {
        List<Entity> passengers = this.getPassengers();
        if (passengers.isEmpty()) return null;
        Entity entity = passengers.get(0);
        return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
    }

    public boolean isPassengerBroadly(Entity entity) {
        for (Entity rider : this.getPassengers()) {
            if (rider == entity || (
                    rider instanceof CarriageEntity && rider.getPassengers().contains(entity)
            )) return true;
        }
        return false;
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
            if (vehicle instanceof EntityPlayer) {
                this.updateRiding((EntityPlayer) vehicle);
            }
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
        int index = riding.getPassengers().indexOf(this);
        if (index == -1) return;
        boolean flying = riding.isElytraFlying();
        float radius = (index == 2 ? 0F : 0.4F) + (flying ? 2 : 0);
        float angle = (0.01745329251F * riding.renderYawOffset) + (index == 1 ? -90 : index == 0 ? 90 : 0);
        double extraX = radius * MathHelper.sin(angle + (float) Math.PI);
        double extraZ = radius * MathHelper.cos(angle);
        double extraY = (riding.isSneaking() ? 1.1D : 1.4D) + (index == 2 ? 0.4D : 0D);
        this.rotationYaw = riding.rotationYaw;
        this.prevRotationYaw = riding.prevRotationYaw;
        this.renderYawOffset = riding.renderYawOffset;
        this.rotationYawHead = riding.rotationYawHead;
        this.prevRotationYawHead = riding.prevRotationYawHead;
        this.rotationPitch = riding.rotationPitch;
        this.prevRotationPitch = riding.prevRotationPitch;
        this.setPosition(riding.posX + extraX, riding.posY + extraY, riding.posZ + extraZ);
        this.setFlying(flying || !riding.onGround &&
                !riding.capabilities.isFlying &&
                riding.posY - this.world.getHeight(MathHelper.floor(this.posX), MathHelper.floor(this.posZ)) > 2.0
        );
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
     * This code is called when the passenger is riding on the dragon
     *
     * @param passenger
     */
    @Override
    public void updatePassenger(Entity passenger) {
        List<Entity> passengers = getPassengers();
        int index = passengers.indexOf(passenger);
        if (index == -1) return;
        //getBreed().getAdultModelRenderScaleFactor() * getScale();
        Vec3d position = this.getVariant().type.locatePassenger(index, this.isSitting(), this.getScale())
                .rotateYaw((float) Math.toRadians(-renderYawOffset))
                .add(this.posX, this.posY + passenger.getYOffset(), this.posZ);
        passenger.setPosition(position.x, position.y, position.z);

        // fix rider rotation
        if (passenger instanceof EntityPlayer) {
            if (passenger == getControllingPlayer()) {
                passenger.prevRotationPitch = passenger.rotationPitch;
                passenger.prevRotationYaw = passenger.rotationYaw;
                ((EntityPlayer) passenger).renderYawOffset = renderYawOffset;
            }
        } else {
            EntityUtil.clampYaw(passenger, this.rotationYaw, 105.0F);
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
        return !isTamed() && !isEgg() || !isBaby() ? this.getVariant().type.lootTable : null;
    }

    public boolean isSheared() {
        return this.dataManager.get(DATA_SHEARED);
    }

    public void setSheared(int cooldown) {
        this.shearCooldown = cooldown;
        this.dataManager.set(DATA_SHEARED, cooldown > 0);
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
        } else if (current == DragonTypes.WATER) {
            this.setVariant(DragonTypes.STORM.variants.draw(this.rand, null));
            this.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 2, 1);
            this.playSound(SoundEvents.BLOCK_END_PORTAL_SPAWN, 2, 1);
        }
        addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 35 * 20));
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
        this.playSound(this.getEatSound(), 1f, 0.75f);
        this.setHunger(this.getHunger() + level);
        this.getLifeStageHelper().ageUp(growth);
        if (this.world.isRemote) {
            this.resetFeedTimer();
            Vec3d pos = this.getAnimator().getThroatPosition();
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

    public static boolean isInertItem(ItemStack stack) {
        return stack.isEmpty() || (stack.getItemUseAction() == EnumAction.NONE &&
                !DMItems.DRAGON_INTERACTABLE.contains(stack.getItem()) &&
                !stack.hasCapability(DMCapabilities.DRAGON_FOOD, null)
        );
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
                world.setBlockState(this.getPosition(), this.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG).getDefaultState());
                setDead();
                return true;
            }
            return false;
        }
        // prevent doing any interactions when a hatchling rides you, the hitbox could block the player's raytraceresult for rightclick
        if (player.isPassenger(this)) return false;

        ItemStack stack = player.getHeldItem(hand);

        final Relation relation = Relation.checkRelation(this, player);
        final boolean isTrusted = relation != Relation.STRANGER;

        /*
         * Dragon Riding The Player
         */
        if (isTrusted && !isSitting() && this.isBaby() && !player.isSneaking() && player.getPassengers().size() < 2 && isInertItem(stack)) {
            this.setAttackTarget(null);
            this.getNavigator().clearPath();
            this.getAISit().setSitting(false);
            this.startRiding(player, true);
            return true;
        }

        boolean isServer = !this.world.isRemote;
        if (!this.isSheared() && this.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            IHardShears shears = stack.getCapability(DMCapabilities.HARD_SHEARS, null);
            if (shears != null) {
                if (isServer) {
                    int cooldown = shears.onShear(stack, player, this);
                    if (cooldown != 0) {
                        this.setSheared(cooldown);
                        this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                        this.playSound(DMSounds.ENTITY_DRAGON_GROWL, 1.0F, 1.0F);
                        if (!isTrusted) {
                            this.setAttackTarget(player);
                        }
                        return true;
                    }
                } else return true;
            }
        }
        if (isTrusted && isInertItem(stack)) {
            /*
             * Sit
             */
            if (this.onGround && !stack.isEmpty() && ItemUtil.anyMatches(stack, OreDictionary.getOreID("stickWood"), OreDictionary.getOreID("bone"))) {
                if (isServer) {
                    this.getAISit().setSitting(!this.isSitting());
                    this.getNavigator().clearPath();
                }
                return true;
            }
            /*
             * GUI
             */
            if (player.isSneaking()) {
                // Dragon Inventory
                this.openGUI(player, GuiHandler.GUI_DRAGON);
                return true;
            }
            if (!this.isBaby()) {
                /*
                 * Player Riding the Dragon
                 */
                if (this.isSaddled()) {
                    List<Entity> passengers = this.getPassengers();
                    if (passengers.size() < 3) {
                        if (isServer) {
                            this.setRidingPlayer(player);
                        }
                        return true;
                    }
                } else if (!stack.isEmpty() && stack.getItem() == Items.SADDLE) {
                    if (isServer) {
                        if (player.capabilities.isCreativeMode) {
                            ItemStack saddle = stack.copy();
                            saddle.setCount(1);
                            this.setSaddle(saddle);
                        } else {
                            this.setSaddle(stack.splitStack(1));
                        }
                    }
                    return true;
                }
            }
        }

        /*
         * Consume
         */
        if (stack.isEmpty()) return false;
        IDragonFood food = stack.getCapability(DMCapabilities.DRAGON_FOOD, null);
        return food != null && food.tryFeed(this, player, relation, stack, hand);
    }

    /**
     * Credits: AlexThe 666 Ice and Fire
     */
    public void openGUI(EntityPlayer playerEntity, int guiId) {
        if (!this.world.isRemote && (!this.isPassenger(playerEntity))) {
            playerEntity.openGui(DragonMounts.getInstance(), guiId, this.world, this.getEntityId(), 0, 0);
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

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        this.inventory.writeSpawnData(buffer);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        this.inventory.readSpawnData(buffer);
        this.getLifeStageHelper().sync();
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

    @Nonnull
    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return new ItemStack(this.isEgg()
                ? Item.getItemFromBlock(this.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG))
                : this.getVariant().type.getInstance(DragonSpawnEggItem.class, DMItems.ENDER_DRAGON_SPAWN_EGG)
        );
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
        super.onInitialSpawn(difficulty, data);
        this.variantHelper.onVariantChanged(this.getVariant());
        return data;
    }
}

