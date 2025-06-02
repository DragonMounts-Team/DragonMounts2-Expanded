package net.dragonmounts.entity.helper;

import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.EnumDifficulty;

public class DragonHungerHelper {
    public final TameableDragonEntity dragon;
    private float foodSaturationLevel = 5.0F;
    private float foodExhaustionLevel;
    private int foodTimer;

    public DragonHungerHelper(TameableDragonEntity dragon) {
        this.dragon = dragon;
    }

    public boolean shouldHeal(TameableDragonEntity dragon) {
        return dragon.getHealth() > 0.0F && dragon.getHealth() < dragon.getMaxHealth();
    }

    public void onUpdate(TameableDragonEntity dragon) {
        EnumDifficulty enumdifficulty = dragon.world.getDifficulty();
        int foodLevel = dragon.getHunger();
        if (this.foodExhaustionLevel > 4.0F) {
            this.foodExhaustionLevel -= 4.0F;

            if (this.foodSaturationLevel > 0.0F) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
            } else if (enumdifficulty != EnumDifficulty.PEACEFUL) {

                foodLevel = Math.max(foodLevel - 1, 0);
            }
        }

        if (foodSaturationLevel > 0 && shouldHeal(dragon) && foodLevel >= 150) {
            ++foodTimer;
            float f = Math.min(this.foodSaturationLevel, 6.0F);
            dragon.heal(f / 6.0F);
            this.addExhaustion(f);
            this.foodTimer = 0;
        } else if (foodLevel >= 18 && shouldHeal(dragon)) {
            ++this.foodTimer;

            if (this.foodTimer >= 80) {
                dragon.heal(1.0F);
                this.addExhaustion(6.0F);
                this.foodTimer = 0;
            }
        } else if (foodLevel <= 0) {
            ++this.foodTimer;

            if (this.foodTimer >= 80) {
                if (dragon.getHealth() > 10.0F || enumdifficulty == EnumDifficulty.HARD || dragon.getHealth() > 1.0F && enumdifficulty == EnumDifficulty.NORMAL) {
                    dragon.attackEntityFrom(DamageSource.STARVE, 1.0F);
                }

                this.foodTimer = 0;
            }
        } else {
            this.foodTimer = 0;
        }
    }

    public void readNBT(NBTTagCompound compound) {
        if (compound.hasKey("foodLevel", 99)) {
            this.foodTimer = compound.getInteger("foodTickTimer");
            this.foodSaturationLevel = compound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNBT(NBTTagCompound compound) {
        compound.setInteger("foodTickTimer", this.foodTimer);
        compound.setFloat("foodSaturationLevel", this.foodSaturationLevel);
        compound.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    public void addExhaustion(float exhaustion) {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + exhaustion, 40.0F);
    }
}
