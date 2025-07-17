package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.AetherBreathFX;
import net.dragonmounts.entity.breath.impl.AetherBreath;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class MoonlightType extends DragonType {
    public MoonlightType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() && !level.isDaytime() && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = level.rand;
            float s = dragon.getScale() * 1.2f;
            float f = (dragon.width - 0.65F) * s;
            level.spawnParticle(
                    EnumParticleTypes.FIREWORKS_SPARK,
                    dragon.posX + (random.nextDouble() - 0.5) * f,
                    dragon.posY + (random.nextDouble() - 0.5) * dragon.height * s,
                    dragon.posZ + (random.nextDouble() - 0.5) * f,
                    0,
                    0,
                    0
            );
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new AetherBreath(dragon, 0.7F);
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new AetherBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.ice;// why?
    }
}
