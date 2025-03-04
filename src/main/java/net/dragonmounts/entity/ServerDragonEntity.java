package net.dragonmounts.entity;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.capability.DMCapabilities;
import net.dragonmounts.capability.IDragonFood;
import net.dragonmounts.capability.IHardShears;
import net.dragonmounts.client.gui.GuiHandler;
import net.dragonmounts.entity.ai.*;
import net.dragonmounts.entity.ai.air.EntityAIDragonFlight;
import net.dragonmounts.entity.ai.air.EntityAIDragonFollowOwnerElytraFlying;
import net.dragonmounts.entity.ai.ground.*;
import net.dragonmounts.entity.ai.path.PathNavigateFlying;
import net.dragonmounts.entity.breath.impl.ServerBreathHelper;
import net.dragonmounts.entity.helper.DragonHeadLocator;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.*;
import net.dragonmounts.item.DragonEssenceItem;
import net.dragonmounts.item.DragonSpawnEggItem;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.ClassPredicate;
import net.dragonmounts.util.EntityUtil;
import net.dragonmounts.util.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE;

public class ServerDragonEntity extends TameableDragonEntity {
    public static final UUID FLYING_RANGE_UUID = UUID.fromString("26C6E399-2FB2-534B-8A0D-843015DA2C1F");
    public static final AttributeModifier FLYING_RANGE_MODIFIER = new AttributeModifier(FLYING_RANGE_UUID, "Flying range bonus", 2.0D, Constants.AttributeModifierOperation.MULTIPLY).setSaved(false);
    public final DragonHeadLocator<ServerDragonEntity> headLocator = new DragonHeadLocator<>(this);

    public ServerDragonEntity(World level) {
        super(level);
    }

    @Override
    protected ServerBreathHelper createBreathHelper() {
        return new ServerBreathHelper(this);
    }

    @Override
    public Vec3d getThroatPosition() {
        return this.headLocator.getThroatPosition();
    }

    @Override
    protected void initEntityAI() {
        this.aiSit = new EntityAIDragonSit(this);
        // mutex 1: generic targeting
        EntityAITasks targets = this.targetTasks;
        targets.addTask(2, new EntityAIOwnerHurtByTarget(this)); // mutex 1
        targets.addTask(3, new EntityAIOwnerHurtTarget(this)); // mutex 1
        targets.addTask(4, new EntityAIDragonHurtByTarget(this)); // mutex 1
        targets.addTask(5, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 10, false, true, IMob.VISIBLE_MOB_SELECTOR));
        targets.addTask(6, new EntityAIDragonHunt(this, false, new ClassPredicate<>(
                EntitySheep.class,
                EntityPig.class,
                EntityChicken.class,
                EntityRabbit.class,
                EntityLlama.class
        ))); // mutex 1
    }

    /**
     * do not use {@link #initEntityAI()} because initialization is not completed at that time
     */
    public void setupTasks() {
        // eggs don't have any tasks
        EntityAITasks tasks = this.tasks;
        if (!this.firstUpdate) {
            Iterator<EntityAITasks.EntityAITaskEntry> iterator = tasks.taskEntries.iterator();
            while (iterator.hasNext()) {
                EntityAITasks.EntityAITaskEntry entry = iterator.next();
                if (entry.using) {
                    entry.using = false;
                    entry.action.resetTask();
                    tasks.executingTaskEntries.remove(entry);
                }
                iterator.remove();
            }
        }
        DragonLifeStage stage = this.lifeStageHelper.getLifeStage();
        if (DragonLifeStage.EGG == stage) return;

        // mutex 1: movement
        // mutex 2: looking
        // mutex 4: special state
        tasks.addTask(0, new EntityAIDragonCatchOwner(this)); // mutex all
        tasks.addTask(1, new EntityAIDragonPlayerControl(this)); // mutex all
        tasks.addTask(2, this.getAISit()); // mutex 4+1
        tasks.addTask(2, new EntityAISwimming(this)); // mutex 4
        tasks.addTask(3, new EntityAIDragonFlight(this, 1)); // mutex 1
        tasks.addTask(5, new EntityAIDragonAttack(this, 1)); // mutex 2+1
        tasks.addTask(6, new EntityAIDragonFollowOwnerElytraFlying(this)); // mutex 2+1
        tasks.addTask(7, new EntityAIDragonFollowOwner(this, 1.25, 20, 16)); // mutex 2+1
        tasks.addTask(9, new EntityAIMoveTowardsRestriction(this, 1)); // mutex 1
        tasks.addTask(11, new EntityAIWander(this, 1)); // mutex 1
        tasks.addTask(12, new EntityAIDragonWatchIdle(this)); // mutex 2
        tasks.addTask(12, new EntityAIDragonWatchLiving(this, 16, 0.05f)); // mutex 2
        if (stage.isBaby()) {
            tasks.addTask(4, new EntityAILeapAtTarget(this, 0.7F)); // mutex 1
            tasks.addTask(7, new DragonTemptGoal(this, 0.75)); // mutex 2+1
            tasks.addTask(8, new EntityAIDragonFollowParent(this, 1.4f));
        } else if (DragonLifeStage.ADULT == stage) {
            tasks.addTask(6, new EntityAIDragonMate(this, 0.6)); // mutex 2+1
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("Sheared", this.shearCooldown);
        nbt.setBoolean("Breathing", this.isUsingBreathWeapon());
        //nbt.setBoolean("projectile", this.isUsingAltBreathWeapon());
        nbt.setBoolean("unhovered", this.isUnHovered());
        nbt.setBoolean("followyaw", this.followYaw());
        nbt.setInteger("hunger", this.getHunger());
        nbt.setBoolean("boosting", this.boosting());
        nbt.setBoolean("ylocked", this.isYLocked());
        nbt.setBoolean("growthpause", this.isGrowthPaused());
        nbt.setBoolean("AllowOtherPlayers", this.allowedOtherPlayers());
        nbt.setBoolean("FollowOwner", this.followOwner);
        nbt.setString(DragonVariant.DATA_PARAMETER_KEY, this.getVariant().getSerializedName());
        //        nbt.setBoolean("sleeping", this.isSleeping()); //unused as of now
        this.inventory.saveAdditionalData(nbt);
        this.lifeStageHelper.writeToNBT(nbt);
        this.variantHelper.writeToNBT(nbt);
        this.reproductionHelper.writeToNBT(nbt);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.setSheared(nbt.getInteger("Sheared"));
        this.setHunger(nbt.getInteger("hunger"));
        this.setGrowthPaused(nbt.getBoolean("growthpause"));
        this.setUsingBreathWeapon(nbt.getBoolean("Breathing"));
        //this.setUsingProjectile(nbt.getBoolean("projectile"));
        this.setUnHovered(nbt.getBoolean("unhovered"));
        this.setYLocked(nbt.getBoolean("ylocked"));
        this.setFollowYaw(nbt.getBoolean("followyaw"));
        this.setBoosting(nbt.getBoolean("boosting"));
        //        this.setSleeping(nbt.getBoolean("sleeping")); //unused as of now
        this.setToAllowedOtherPlayers(nbt.getBoolean("AllowOtherPlayers"));
        this.followOwner = nbt.getBoolean("FollowOwner");
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
        this.lifeStageHelper.readFromNBT(nbt);
        this.reproductionHelper.readFromNBT(nbt);
    }

    @Override
    public void onLivingUpdate() {
        this.variantHelper.update();
        this.lifeStageHelper.ageUp(1);
        this.breathHelper.update();
        this.getVariant().type.tick(this);
        this.headLocator.setLook(
                this.rotationYawHead - this.renderYawOffset, // netYawHead
                this.rotationPitch
        );
        this.headLocator.update();

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
            // update AI follow range (needs to be updated before creating
            // new PathNavigate!)
            // update pathfinding method

            //TODO: reuse?
            if (flying) {
                this.getEntityAttribute(FOLLOW_RANGE).applyModifier(FLYING_RANGE_MODIFIER);
                this.navigator = new PathNavigateFlying(this, world);
            } else {
                this.getEntityAttribute(FOLLOW_RANGE).removeModifier(FLYING_RANGE_MODIFIER);
                PathNavigateGround navigator = new PathNavigateGround(this, world);
                this.navigator = navigator;
                navigator.setCanSwim(true);
                navigator.setEnterDoors(this.isChild());
            }
        }
        this.roar(0.001F);


        if (this.getRidingEntity() instanceof EntityLivingBase && ((EntityLivingBase) this.getRidingEntity()).isElytraFlying()) {
            this.setUnHovered(true);
        }
        // TODO: config
        if (this.ticksExisted % 6000 == 1) {
            if (this.getHunger() > 0) {
                this.setHunger(this.getHunger() - 1);
            }
        }
        if (this.shearCooldown > 0) {
            this.setSheared(this.shearCooldown - 1);
        }
        if (!this.isDead) {
            if (this.healingEnderCrystal != null) {
                if (this.healingEnderCrystal.isDead) {
                    this.healingEnderCrystal = null;
                } else if (this.ticksExisted % 10 == 0) {
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
        super.onLivingUpdate();
        if (this.getControllingPlayer() == null && !this.isFlying() && this.isSitting()) {
            this.removePassengers();
        }
    }


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
        if (isTrusted && !isSitting() && this.isChild() && !player.isSneaking() && player.getPassengers().size() < 2 && isInertItem(stack)) {
            this.setAttackTarget(null);
            this.startRiding(player, true);
            return true;
        }

        boolean isServer = true;
        if (!this.isSheared() && this.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE)) {
            IHardShears shears = stack.getCapability(DMCapabilities.HARD_SHEARS, null);
            if (shears != null) {
                int cooldown = shears.onShear(stack, player, this);
                if (cooldown != 0) {
                    this.setSheared(cooldown);
                    this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                    this.playSound(DMSounds.DRAGON_PURR, 1.0F, 1.0F);
                    if (!isTrusted) {
                        this.setAttackTarget(player);
                    }
                    return true;
                }
            }
        }
        if (isTrusted && isInertItem(stack)) {
            /*
             * Sit
             */
            if (this.onGround && !stack.isEmpty() && ItemUtil.anyMatches(stack, OreDictionary.getOreID("stickWood"), OreDictionary.getOreID("bone"))) {
                this.getAISit().setSitting(!this.isSitting());
                this.getNavigator().clearPath();
                return true;
            }
            /*
             * GUI
             */
            if (player.isSneaking() && !this.isPassenger(player)) {
                // Dragon Inventory
                player.openGui(DragonMounts.getInstance(), GuiHandler.GUI_DRAGON, this.world, this.getEntityId(), 0, 0);
                return true;
            }
            if (!this.isChild()) {
                /*
                 * Player Riding the Dragon
                 */
                if (this.isSaddled()) {
                    if (this.getPassengers().size() < 3) {
                        player.rotationYaw = rotationYaw;
                        player.rotationPitch = rotationPitch;
                        player.startRiding(this);
                        return true;
                    }
                } else if (!stack.isEmpty() && stack.getItem() == Items.SADDLE) {
                    if (player.capabilities.isCreativeMode) {
                        ItemStack saddle = stack.copy();
                        saddle.setCount(1);
                        this.setSaddle(saddle);
                    } else {
                        this.setSaddle(stack.splitStack(1));
                    }
                    return true;
                }
            }
        }
        if (stack.isEmpty()) return false;
        IDragonFood food = stack.getCapability(DMCapabilities.DRAGON_FOOD, null);
        return food != null && food.tryFeed(this, player, relation, stack, hand);
    }

    @Override
    protected void onDeathUpdate() {
        // unmount any riding entities
        removePassengers();

        // freeze at place
        motionX = motionY = motionZ = 0;
        rotationYaw = prevRotationYaw;
        rotationYawHead = prevRotationYawHead;

        if (isEgg() || ++deathTime > getMaxDeathTime()) setDead();// actually delete entity after the time is up
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
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

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (this.isChild() && isJumping) return false; // ?
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
            this.roar(0.25F);
            return true;
        }
        return false;
    }

    @Override
    protected void collideWithEntity(Entity other) {
        if (other instanceof CarriageEntity &&
                this.isSaddled() &&
                !other.isRiding() &&
                this.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE) &&
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

    public void roar(float chance) {
        if (!this.isDead && this.rand.nextFloat() < chance && !this.isEgg() && !this.isUsingBreathWeapon()) {
            this.world.setEntityState(this, DO_ROAR);
        }
    }
}
