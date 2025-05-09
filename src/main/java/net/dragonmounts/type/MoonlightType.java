package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.MoonlightBreath;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class MoonlightType extends DragonType {
    public MoonlightType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() && !level.isDaytime() && dragon.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING)) {
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2F;
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
        return new MoonlightBreath(dragon, 0.7F);
    }
}
