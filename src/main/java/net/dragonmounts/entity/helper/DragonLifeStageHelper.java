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

import net.dragonmounts.block.HatchableDragonEggBlock;
import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMBlocks;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.util.ClientServerSynchronisedTickCount;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.UUID;

import static net.dragonmounts.entity.DragonLifeStage.getLifeStageFromTickCount;
import static net.dragonmounts.util.EntityUtil.replaceAttributeModifier;
import static net.minecraft.entity.SharedMonsterAttributes.*;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonLifeStageHelper {
    public static AttributeModifier makeAttributeModifier(int operator, double amount) {
        return new AttributeModifier(DRAGON_AEG_MODIFIER_ID, "Dragon size modifier", amount, operator);
    }

    public static final UUID DRAGON_AEG_MODIFIER_ID = UUID.fromString("856d4ba4-9ffe-4a52-8606-890bb9be538b");
    private static final Logger L = LogManager.getLogger();
    private static final String NBT_TICKS_SINCE_CREATION = "TicksSinceCreation";
    private static final int TICKS_SINCE_CREATION_UPDATE_INTERVAL = 100;
    private static final float EGG_CRACK_THRESHOLD = 0.9f;
    private static final float EGG_WIGGLE_THRESHOLD = 0.75f;
    private static final float EGG_WIGGLE_BASE_CHANCE = 20;
    public final TameableDragonEntity dragon;
    // the ticks since creation is used to control the dragon's life stage.  It is only updated by the server occasionally.
    // the client keeps a cached copy of it and uses client ticks to interpolate in the gaps.
    // when the watcher is updated from the server, the client will tick it faster or slower to resynchronise
    private final DataParameter<Integer> dataParam;
    private final ClientServerSynchronisedTickCount ticksSinceCreationClient;
    private DragonLifeStage lifeStagePrev;
    private int eggWiggleX;
    private int eggWiggleZ;
    private int ticksSinceCreationServer;

    public DragonLifeStageHelper(TameableDragonEntity dragon, DataParameter<Integer> dataParam) {
        this.dragon = dragon;
        this.dataParam = dataParam;
        if (dragon.world.isRemote) {
            ticksSinceCreationClient = new ClientServerSynchronisedTickCount(TICKS_SINCE_CREATION_UPDATE_INTERVAL);
            ticksSinceCreationClient.reset(ticksSinceCreationServer);
        } else {
            ticksSinceCreationClient = null;
        }
    }

    public void applyEntityAttributes() {
        TameableDragonEntity dragon = this.dragon;
        AbstractAttributeMap attributes = dragon.getAttributeMap();
        float health = dragon.getHealth() / dragon.getMaxHealth();
        double scale = this.getScale();
        double factor = MathHelper.clamp(scale, 0.1, 1);
        replaceAttributeModifier(attributes.getAttributeInstance(MAX_HEALTH), makeAttributeModifier(1, factor));
        replaceAttributeModifier(attributes.getAttributeInstance(ATTACK_DAMAGE), makeAttributeModifier(1, factor));
        replaceAttributeModifier(attributes.getAttributeInstance(ARMOR), makeAttributeModifier(0, Math.max(scale, 0.1F) * DMConfig.BASE_ARMOR.value));
        dragon.setHealth(health * dragon.getMaxHealth());
    }

    public void playEggCrackEffect() {
        TameableDragonEntity dragon = this.dragon;
        dragon.world.playEvent(2001, dragon.getPosition(), Block.getIdFromBlock(
                dragon.getVariant().type.getInstance(HatchableDragonEggBlock.class, DMBlocks.ENDER_DRAGON_EGG)
        ));
    }

    public int getEggWiggleX() {
        return eggWiggleX;
    }

    public int getEggWiggleZ() {
        return eggWiggleZ;
    }

    /**
     * Returns the current life stage of the dragon.
     *
     * @return current life stage
     */
    public DragonLifeStage getLifeStage() {
        return getLifeStageFromTickCount(this.getTicksSinceCreation());
    }

    /**
     * Sets a new life stage for the dragon.
     */
    public final void setLifeStage(DragonLifeStage lifeStage) {
        L.trace("setLifeStage({})", lifeStage);
        if (dragon.world.isRemote) {
            L.error("setLifeStage called on Client");
        } else {
            ticksSinceCreationServer = lifeStage.boundaryTick - lifeStage.durationTicks;
            this.dragon.getDataManager().set(dataParam, ticksSinceCreationServer);
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
        ticksSinceCreationServer = ticksSinceCreation;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_TICKS_SINCE_CREATION, getTicksSinceCreation());
    }

    public void readFromNBT(NBTTagCompound nbt) {
        int ticksRead = nbt.getInteger(NBT_TICKS_SINCE_CREATION);
        ticksRead = DragonLifeStage.clipTickCountToValid(ticksRead);
        ticksSinceCreationServer = ticksRead;
        this.dragon.getDataManager().set(dataParam, ticksSinceCreationServer);
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
     * Called when the dragon enters a new life stage.
     */
    private void onNewLifeStage(DragonLifeStage lifeStage, DragonLifeStage prevLifeStage) {
        L.trace("onNewLifeStage({},{})", prevLifeStage, lifeStage);
        TameableDragonEntity dragon = this.dragon;
        if (dragon instanceof ServerDragonEntity) {
            // clear current navigation target
            dragon.getNavigator().clearPath();
            // update AI
            ((ServerDragonEntity) dragon).setupTasks();
            if (DragonLifeStage.EGG == lifeStage) {
                dragon.variantHelper.resetPoints(null);
            }
        } else if (DragonLifeStage.EGG == prevLifeStage && !lifeStage.isBaby()) {
            // play particle and sound effects when the dragon hatches
            dragon.world.playSound(dragon.posX, dragon.posY, dragon.posZ, DMSounds.DRAGON_EGG_SHATTER, SoundCategory.BLOCKS, 4, 1, false);
        }
        // update attribute modifier
        this.applyEntityAttributes();
        dragon.onLifeStageChange(lifeStage);
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
        TameableDragonEntity dragon = this.dragon;
        Random rand = dragon.getRNG();
        // animate egg wiggle based on the time the eggs take to hatch
        float progress = DragonLifeStage.getStageProgressFromTickCount(getTicksSinceCreation());

        // wait until the egg is nearly hatched
        if (progress > EGG_WIGGLE_THRESHOLD) {
            float wiggleChance = (progress - EGG_WIGGLE_THRESHOLD) / EGG_WIGGLE_BASE_CHANCE * (1 - EGG_WIGGLE_THRESHOLD);
            boolean mayCrack = false;
            if (eggWiggleX > 0) {
                eggWiggleX--;
            } else if (rand.nextFloat() < wiggleChance) {
                eggWiggleX = rand.nextBoolean() ? 10 : 20;
                mayCrack = true;
            }
            if (eggWiggleZ > 0) {
                eggWiggleZ--;
            } else if (rand.nextFloat() < wiggleChance) {
                eggWiggleZ = rand.nextBoolean() ? 10 : 20;
                mayCrack = true;
            }
            if (mayCrack && progress > EGG_CRACK_THRESHOLD) {
                this.playEggCrackEffect();
                dragon.world.playSound(null, dragon.getPosition(), DMSounds.DRAGON_EGG_CRACK, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

        // spawn generic particles
        double px = dragon.posX + (rand.nextDouble() - 0.3);
        double py = dragon.posY + (rand.nextDouble() - 0.3);
        double pz = dragon.posZ + (rand.nextDouble() - 0.3);
        double ox = (rand.nextDouble() - 0.3) * 2;
        double oy = (rand.nextDouble() - 0.3) * 2;
        double oz = (rand.nextDouble() - 0.3) * 2;
        dragon.world.spawnParticle(dragon.getVariant().type.eggParticle, px, py, pz, ox, oy, oz);
    }

    public boolean isEgg() {
        return this.getTicksSinceCreation() < DragonLifeStage.EGG.boundaryTick;
    }

    /**
     * @return whether this life stage act like a vanilla baby
     */
    public boolean isBaby() {
        int age = this.getTicksSinceCreation();
        return age >= DragonLifeStage.EGG.boundaryTick && age < DragonLifeStage.INFANT.boundaryTick;
    }

    public boolean isOldEnough(DragonLifeStage stage) {
        return this.getTicksSinceCreation() + stage.durationTicks >= stage.boundaryTick;
    }

    public void ageUp(int ticks) {
        TameableDragonEntity dragon = this.dragon;
        // if the dragon is not an adult or paused, update its growth ticks
        if (dragon.world.isRemote) {
            this.sync();
            if (DragonLifeStage.ADULT != this.getLifeStage()) {
                this.ticksSinceCreationClient.tick();
            }
        } else {
            if (DragonLifeStage.ADULT != this.getLifeStage() && !dragon.isGrowthPaused()) {
                ticksSinceCreationServer += ticks;
                if (ticks > TICKS_SINCE_CREATION_UPDATE_INTERVAL || ticksSinceCreationServer % TICKS_SINCE_CREATION_UPDATE_INTERVAL == 0)
                    dragon.getDataManager().set(dataParam, ticksSinceCreationServer);
            }
        }
        updateLifeStage();
        if (this.isEgg()) {
            updateEgg();
        }
        dragon.updateScale();
    }

    public void sync() {
        this.ticksSinceCreationClient.updateFromServer(this.dragon.getDataManager().get(this.dataParam));
    }
}