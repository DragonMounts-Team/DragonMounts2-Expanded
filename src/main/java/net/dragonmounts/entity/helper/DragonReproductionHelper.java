/*
 ** 2013 October 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DragonReproductionHelper {
    private static final Logger L = LogManager.getLogger();
    public static final String NBT_REPRODUCTION_COUNT = "ReproductionCount";
    // old NBT keys
    public static final String NBT_REPRODUCED = "HasReproduced";
    public static final int REPRODUCTION_LIMIT = 2;
    public final TameableDragonEntity dragon;
    protected final Random rand;

    public DragonReproductionHelper(TameableDragonEntity dragon) {
        this.dragon = dragon;
        this.rand = dragon.getRNG();
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_REPRODUCTION_COUNT, this.dragon.getReproductionCount());
    }

    public void readFromNBT(NBTTagCompound nbt) {
        int count = 0;
        if (nbt.hasKey(NBT_REPRODUCTION_COUNT)) {
            count = nbt.getInteger(NBT_REPRODUCTION_COUNT);
        } else if (nbt.hasKey(NBT_REPRODUCED) && nbt.getBoolean(NBT_REPRODUCED)) {
            // convert old boolean value
            ++count;
        }
        this.dragon.setReproductionCount(count);
    }

    public void addReproduced() {
        this.dragon.setReproductionCount(this.dragon.getReproductionCount() + 1);
    }

    public boolean canReproduce() {
        return this.dragon.isTamed() && this.dragon.getReproductionCount() < REPRODUCTION_LIMIT;
    }

    public boolean canMateWith(EntityAnimal mate) {
        if (mate == this.dragon) {
            // No. Just... no.
            return false;
        } else if (!(mate instanceof TameableDragonEntity)) {
            return false;
        } else if (!canReproduce()) {
            return false;
        }

        TameableDragonEntity dragonMate = (TameableDragonEntity) mate;

        if (!dragonMate.isTamed()) {
            return false;
        } else  {
            return dragon.isInLove() && dragonMate.isInLove();
        }
    }

    public ServerDragonEntity createChild(TameableDragonEntity mate) {
        TameableDragonEntity self = this.dragon;
        if (self.world.isRemote) return null;
        ServerDragonEntity baby = new ServerDragonEntity(self.world);

        // mix the custom names in case both parents have one
        if (self.hasCustomName() && mate.hasCustomName()) {
            String p1Name = self.getCustomNameTag();
            String p2Name = mate.getCustomNameTag();
            String babyName;

            if (p1Name.contains(" ") || p2Name.contains(" ")) {
                // combine two words with space
                // "Tempor Invidunt Dolore" + "Magna"
                // = "Tempor Magna" or "Magna Tempor"
                String[] p1Names = p1Name.split(" ");
                String[] p2Names = p2Name.split(" ");

                p1Name = fixChildName(p1Names[rand.nextInt(p1Names.length)]);
                p2Name = fixChildName(p2Names[rand.nextInt(p2Names.length)]);

                babyName = rand.nextBoolean() ? p1Name + " " + p2Name : p2Name + " " + p1Name;
            } else {
                // scramble two words
                // "Eirmod" + "Voluptua"
                // = "Eirvolu" or "Volueir" or "Modptua" or "Ptuamod" or ...
                if (rand.nextBoolean()) {
                    p1Name = p1Name.substring(0, (p1Name.length() - 1) / 2);
                } else {
                    p1Name = p1Name.substring((p1Name.length() - 1) / 2);
                }

                if (rand.nextBoolean()) {
                    p2Name = p2Name.substring(0, (p2Name.length() - 1) / 2);
                } else {
                    p2Name = p2Name.substring((p2Name.length() - 1) / 2);
                }

                p2Name = fixChildName(p2Name);

                babyName = rand.nextBoolean() ? p1Name + p2Name : p2Name + p1Name;
            }

            baby.setCustomNameTag(babyName);
        }

        baby.lifeStageHelper.setLifeStage(DragonLifeStage.EGG);
        // inherit the baby's breed from its parents
        baby.variantHelper.inheritBreed(self, mate);
        // increase reproduction counter
        this.addReproduced();
        mate.reproductionHelper.addReproduced();
        return baby;
    }

    private static String fixChildName(String nameOld) {
        if (nameOld == null || nameOld.isEmpty()) {
            return nameOld;
        }

        // create all lower-case char array
        char[] chars = nameOld.toLowerCase().toCharArray();

        // convert first char to upper-case
        chars[0] = Character.toUpperCase(chars[0]);

        String nameNew = new String(chars);

        if (!nameOld.equals(nameNew)) {
            L.debug("Fixed child name {} -> {}", nameOld, nameNew);
        }

        return nameNew;
    }
}