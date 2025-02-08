package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.HydroBreathFX;
import net.dragonmounts.entity.breath.impl.WaterBreath;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class WaterType extends DragonType {
    public WaterType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        if (dragon.isInWater()) {
            EntityUtil.addOrResetEffect(dragon, MobEffects.WATER_BREATHING, 200, 0, false, false, 21);
        }
        if (dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            World level = dragon.world;
            Random random = level.rand;
            float s = dragon.getScale() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = -2; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.DRIP_WATER,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY - 1 + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }

    @Nullable
    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new WaterBreath(dragon, 0.7F);
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.water;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new HydroBreathFX(world, position, direction, power, partialTicks));
    }
}
