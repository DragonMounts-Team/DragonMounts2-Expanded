/*
 ** 2013 October 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.helper;

import com.google.common.collect.ImmutableMap;
import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.util.ClientServerSynchronisedTickCount;
import net.dragonmounts.util.LogUtil;
import net.dragonmounts.util.math.MathX;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

import static net.dragonmounts.entity.helper.DragonLifeStage.getLifeStageFromTickCount;
import static net.dragonmounts.util.EntityUtil.replaceAttributeModifier;
import static net.minecraft.entity.SharedMonsterAttributes.*;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonLifeStageHelper extends DragonHelper {
    public static final UUID DRAGON_AEG_MODIFIER_ID = UUID.fromString("856d4ba4-9ffe-4a52-8606-890bb9be538b");
    public static final Map<DragonLifeStage, BreathPower> BREATHNODE_POWER_BY_STAGE =
            ImmutableMap.<DragonLifeStage, BreathPower>builder()
                    .put(DragonLifeStage.EGG, BreathPower.SMALL)           // dummy
                    .put(DragonLifeStage.HATCHLING, BreathPower.SMALL)     // dummy
                    .put(DragonLifeStage.INFANT, BreathPower.SMALL)        // dummy
                    .put(DragonLifeStage.PREJUVENILE, BreathPower.SMALL)
                    .put(DragonLifeStage.JUVENILE, BreathPower.MEDIUM)
                    .put(DragonLifeStage.ADULT, BreathPower.LARGE)
                    .build();
    private static final Logger L = LogManager.getLogger();
    private static final String NBT_TICKS_SINCE_CREATION = "TicksSinceCreation";
    private static final int TICKS_SINCE_CREATION_UPDATE_INTERVAL = 100;
    private static final float EGG_CRACK_THRESHOLD = 0.9f;
    private static final float EGG_WIGGLE_THRESHOLD = 0.75f;
    private static final float EGG_WIGGLE_BASE_CHANCE = 20;
    // the ticks since creation is used to control the dragon's life stage.  It is only updated by the server occasionally.
    // the client keeps a cached copy of it and uses client ticks to interpolate in the gaps.
    // when the watcher is updated from the server, the client will tick it faster or slower to resynchronise
    private final DataParameter<Integer> dataParam;
    private final ClientServerSynchronisedTickCount ticksSinceCreationClient;
    private DragonLifeStage lifeStagePrev;
    private int eggWiggleX;
    private int eggWiggleZ;
    //    private final Map<EnumDragonBreed, AtomicInteger> breedPoints = new EnumMap<>(EnumDragonBreed.class);
    private int ticksSinceCreationServer;

    public DragonLifeStageHelper(TameableDragonEntity dragon, DataParameter<Integer> dataParam) {
        super(dragon);

        this.dataParam = dataParam;
        dataWatcher.register(dataParam, ticksSinceCreationServer);

        if (dragon.world.isRemote) {
            ticksSinceCreationClient = new ClientServerSynchronisedTickCount(TICKS_SINCE_CREATION_UPDATE_INTERVAL);
            ticksSinceCreationClient.reset(ticksSinceCreationServer);
        } else {
            ticksSinceCreationClient = null;
        }
    }

    @Override
    public void applyEntityAttributes() {
        AbstractAttributeMap attributes = this.dragon.getAttributeMap();
        double scale = this.getScale();
        double factor = MathX.clamp(scale, 0.1, 1);
        replaceAttributeModifier(attributes.getAttributeInstance(MAX_HEALTH), DRAGON_AEG_MODIFIER_ID, "Dragon size modifier", factor, 1, false);
        replaceAttributeModifier(attributes.getAttributeInstance(ATTACK_DAMAGE), DRAGON_AEG_MODIFIER_ID, "Dragon size modifier", factor, 1, false);
        replaceAttributeModifier(attributes.getAttributeInstance(ARMOR), DRAGON_AEG_MODIFIER_ID, "Dragon size modifier", MathX.clamp(scale, 0.1, 1.2), 1, false);
    }

    /**
     * Generates some egg shell particles and a breaking sound.
     */
    public void playEggCrackEffect() {
        // dragon.world.playEvent(2001, dragon.getPosition(), Block.getIdFromBlock(BlockDragonBreedEgg.DRAGON_BREED_EGG));
        this.playEvent(dragon.getPosition());
    }

    public void playEvent(BlockPos blockPosIn) {
        dragon.world.playSound(null, blockPosIn, DMSounds.DRAGON_HATCHING, SoundCategory.BLOCKS, +1.0F, 1.0F);
    }

    public int getEggWiggleX() {
        return eggWiggleX;
    }

//    public DragonLifeStage getLifeStageP() {
//        int age = getTicksSinceCreation();
//        return DragonLifeStage.getLifeStageFromTickCount(age);
//    }

    public int getEggWiggleZ() {
        return eggWiggleZ;
    }

    /**
     * Returns the current life stage of the dragon.
     *
     * @return current life stage
     */
    public DragonLifeStage getLifeStage() {
        int age = getTicksSinceCreation();
        return getLifeStageFromTickCount(age);
    }

    /**
     * Sets a new life stage for the dragon.
     *
     * @param lifeStage
     */
    public final void setLifeStage(DragonLifeStage lifeStage) {
        L.trace("setLifeStage({})", lifeStage);
        if (dragon.world.isRemote) {
            L.error("setLifeStage called on Client");
        } else {
            ticksSinceCreationServer = lifeStage.boundaryTick - lifeStage.durationTicks;
            dataWatcher.set(dataParam, ticksSinceCreationServer);
        }
        updateLifeStage();
    }

    public int getTicksSinceCreation() {
        if (dragon.world.isRemote) {
            return ticksSinceCreationClient.getCurrentTickCount();
        } else {
            return ticksSinceCreationServer;
        }
    }

    public void setTicksSinceCreation(int ticksSinceCreation) {
        if (dragon.world.isRemote) {
            ticksSinceCreationClient.updateFromServer(ticksSinceCreationServer);
        } else {
            ticksSinceCreationServer = ticksSinceCreation;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_TICKS_SINCE_CREATION, getTicksSinceCreation());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        int ticksRead = nbt.getInteger(NBT_TICKS_SINCE_CREATION);
        ticksRead = DragonLifeStage.clipTickCountToValid(ticksRead);
        ticksSinceCreationServer = ticksRead;
        dataWatcher.set(dataParam, ticksSinceCreationServer);
        float health = this.dragon.getHealth();
        DragonLifeStage stage = getLifeStageFromTickCount(ticksRead);
        this.onNewLifeStage(stage, this.lifeStagePrev);
        this.lifeStagePrev = stage;
        this.dragon.setHealth(health);
    }

    /**
     * Returns the size multiplier for the current age.
     *
     * @return size
     */
    public float getScale() {
        return DragonLifeStage.getScaleFromTickCount(getTicksSinceCreation());
    }

    /**
     * Transforms the dragon to an egg (item form)
     */
    public void transformToEgg() {
        if (dragon.getHealth() <= 0) {
            // no can do
            return;
        }

        L.debug("transforming to egg");

        float volume = 3;
        float pitch = 1;
        dragon.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, volume, pitch);

        if (dragon.isSaddled()) {
            dragon.dropItem(Items.SADDLE, 1);
        }

        dragon.entityDropItem(new ItemStack(dragon.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG)), 0);

        dragon.setDead();
    }

    /**
     * Called when the dragon enters a new life stage.
     */
    private void onNewLifeStage(DragonLifeStage lifeStage, DragonLifeStage prevLifeStage) {
        L.trace("onNewLifeStage({},{})", prevLifeStage, lifeStage);

        if (dragon.world.isRemote) {
            // play particle and sound effects when the dragon hatches
            if (prevLifeStage != null && prevLifeStage.isEgg() && !lifeStage.isBaby()) {
                playEggCrackEffect();
                dragon.world.playSound(dragon.posX, dragon.posY, dragon.posZ, DMSounds.DRAGON_HATCHED, SoundCategory.BLOCKS, 4, 1, false);
            }
        } else {
            // update AI
            dragon.getBrain().updateAITasks();
            // update attribute modifier
            applyEntityAttributes();

            // heal dragon to updated full health
            dragon.setHealth(dragon.getMaxHealth());
            if (lifeStage.isEgg()) {
                dragon.variantHelper.resetPoints(null);
            }
        }
        dragon.onLifeStageChange(lifeStage);
    }

    @Override
    public void onLivingUpdate() {
        this.ageUp(1);
    }

    public EntityDataManager getDataWatcher() {
        return dataWatcher;
    }

    private void updateLifeStage() {
        // trigger event when a new life stage was reached
        DragonLifeStage lifeStage = getLifeStage();
        if (lifeStagePrev != lifeStage) {
            onNewLifeStage(lifeStage, lifeStagePrev);
            lifeStagePrev = lifeStage;
        }
    }

    private void updateEgg() {
        if (!isEgg()) {
            return;
        }

        // animate egg wiggle based on the time the eggs take to hatch
        float progress = DragonLifeStage.getStageProgressFromTickCount(getTicksSinceCreation());

        // wait until the egg is nearly hatched
        if (progress > EGG_WIGGLE_THRESHOLD) {
            float wiggleChance = (progress - EGG_WIGGLE_THRESHOLD) / EGG_WIGGLE_BASE_CHANCE * (1 - EGG_WIGGLE_THRESHOLD);

            if (eggWiggleX > 0) {
                eggWiggleX--;
            } else if (rand.nextFloat() < wiggleChance) {
                eggWiggleX = rand.nextBoolean() ? 10 : 20;
                if (progress > EGG_CRACK_THRESHOLD) {
                    playEggCrackEffect();
                }
            }

            if (eggWiggleZ > 0) {
                eggWiggleZ--;
            } else if (rand.nextFloat() < wiggleChance) {
                eggWiggleZ = rand.nextBoolean() ? 10 : 20;
                if (progress > EGG_CRACK_THRESHOLD) {
                    playEggCrackEffect();
                }
            }
        }

        // spawn generic particles
        double px = dragon.posX + (rand.nextDouble() - 0.3);
        double py = dragon.posY + (rand.nextDouble() - 0.3);
        double pz = dragon.posZ + (rand.nextDouble() - 0.3);
        double ox = (rand.nextDouble() - 0.3) * 2;
        double oy = (rand.nextDouble() - 0.3) * 2;
        double oz = (rand.nextDouble() - 0.3) * 2;
        dragon.world.spawnParticle(this.getEggParticle(), px, py, pz, ox, oy, oz);

    }

    protected EnumParticleTypes getEggParticle() {
        return this.dragon.getVariant().type.eggParticle;
    }

    @Override
    public void onDeath() {
        if (this.dragon.world.isRemote && this.isEgg()) {
            this.playEggCrackEffect();
        }
    }

    public boolean isEgg() {
        return getLifeStage().isEgg();
    }

    public boolean isFullyGrown() {
        return getLifeStage().isFullyGrown();
    }

    /**
     * @return whether this life stage act like a vanilla baby
     */
    public boolean isBaby() {
        return getLifeStage().isBaby();
    }

    public boolean isJuvenile() {
        return getLifeStage().isJuvenile();
    }

    public boolean isOldEnough(DragonLifeStage stage) {
        return getLifeStage().isOldEnough(stage);
    }

    public BreathPower getBreathPower() {
        BreathPower power = BREATHNODE_POWER_BY_STAGE.get(getLifeStage());
        if (power == null) {
            LogUtil.once(Level.ERROR, "Illegal lifestage in getBreathPower():" + getLifeStage());
            power = BreathPower.SMALL;
        }
        return power;
    }

    public void ageUp(int ticks) {
        // if the dragon is not an adult or paused, update its growth ticks
        if (dragon.world.isRemote) {
            ticksSinceCreationClient.updateFromServer(dataWatcher.get(dataParam));
            if (!isFullyGrown()) ticksSinceCreationClient.tick();
        } else {
            if (!isFullyGrown() && !dragon.isGrowthPaused()) {
                ticksSinceCreationServer += ticks;
                if (ticks > TICKS_SINCE_CREATION_UPDATE_INTERVAL || ticksSinceCreationServer % TICKS_SINCE_CREATION_UPDATE_INTERVAL == 0)
                    dataWatcher.set(dataParam, ticksSinceCreationServer);
            }
        }
        updateLifeStage();
        updateEgg();
        dragon.setScalePublic(getScale());
    }
}