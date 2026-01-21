package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.MoonlightBreath;
import net.dragonmounts.init.DragonTypes;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

import static net.dragonmounts.util.LevelUtil.isDaytime;

public class MoonlightType extends DragonType {
    public MoonlightType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() && !isDaytime(level) && dragon.getLifeStage().isOldEnough(DragonLifeStage.FLEDGLING)) {
            Random random = level.rand;
            if (random.nextDouble() > dragon.getAdjustedSize()) return;
            float f = dragon.width * 1.2F + 0.75F;
            level.spawnParticle(
                    EnumParticleTypes.FIREWORKS_SPARK,
                    dragon.posX + (random.nextDouble() - 0.5) * f,
                    dragon.posY + (random.nextDouble() + 0.25) * dragon.height * 0.5F,
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

    @Override
    public void onStruckByLightning(ServerDragonEntity dragon, EntityLightningBolt bolt) {
        super.onStruckByLightning(dragon, bolt);
        convertByLightning(dragon, DragonTypes.DARK);
    }
}
