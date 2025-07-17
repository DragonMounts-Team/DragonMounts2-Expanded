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

import net.dragonmounts.config.DMConfig;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
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
    /// old NBT key
    public static final String NBT_REPRODUCED = "HasReproduced";
    public final ServerDragonEntity dragon;
    private int reproduced = 0;

    public DragonReproductionHelper(ServerDragonEntity dragon) {
        this.dragon = dragon;
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_REPRODUCTION_COUNT, this.reproduced);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey(NBT_REPRODUCTION_COUNT)) {
            this.reproduced = nbt.getInteger(NBT_REPRODUCTION_COUNT);
        } else if (nbt.hasKey(NBT_REPRODUCED) && nbt.getBoolean(NBT_REPRODUCED)) {
            // convert old boolean value
            this.reproduced = 1;
        }
    }

    public boolean canMateWith(EntityAnimal other) {
        if (other == this.dragon ||
                !(other instanceof ServerDragonEntity) ||
                this.reproduced >= DMConfig.REPRODUCTION_LIMIT.value ||
                !this.dragon.isTamed() ||
                !this.dragon.isInLove()
        ) return false;
        ServerDragonEntity mate = (ServerDragonEntity) other;
        return mate.isInLove() && mate.isTamed() && !mate.isSitting();
    }

    public ServerDragonEntity createChild(ServerDragonEntity mate) {
        ServerDragonEntity self = this.dragon;
        ServerDragonEntity baby = new ServerDragonEntity(self.world);

        // mix the custom names in case both parents have one
        if (self.hasCustomName() && mate.hasCustomName()) {
            Random random = baby.getRNG();
            String p1Name = self.getCustomNameTag();
            String p2Name = mate.getCustomNameTag();
            String babyName;

            if (p1Name.contains(" ") || p2Name.contains(" ")) {
                // combine two words with space
                // "Tempor Invidunt Dolore" + "Magna"
                // = "Tempor Magna" or "Magna Tempor"
                String[] p1Names = p1Name.split(" ");
                String[] p2Names = p2Name.split(" ");

                p1Name = fixChildName(p1Names[random.nextInt(p1Names.length)]);
                p2Name = fixChildName(p2Names[random.nextInt(p2Names.length)]);

                babyName = random.nextBoolean() ? p1Name + " " + p2Name : p2Name + " " + p1Name;
            } else {
                // scramble two words
                // "Eirmod" + "Voluptua"
                // = "Eirvolu" or "Volueir" or "Modptua" or "Ptuamod" or ...
                if (random.nextBoolean()) {
                    p1Name = p1Name.substring(0, (p1Name.length() - 1) / 2);
                } else {
                    p1Name = p1Name.substring((p1Name.length() - 1) / 2);
                }

                if (random.nextBoolean()) {
                    p2Name = p2Name.substring(0, (p2Name.length() - 1) / 2);
                } else {
                    p2Name = p2Name.substring((p2Name.length() - 1) / 2);
                }

                p2Name = fixChildName(p2Name);

                babyName = random.nextBoolean() ? p1Name + p2Name : p2Name + p1Name;
            }

            baby.setCustomNameTag(babyName);
        }

        baby.lifeStageHelper.setLifeStage(DragonLifeStage.EGG);
        // inherit the baby's breed from its parents
        baby.variantHelper.inheritBreed(self, mate);
        // increase reproduction counter
        ++this.reproduced;
        ++mate.reproductionHelper.reproduced;
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

    public int getReproductionCount() {
        return this.reproduced;
    }
}