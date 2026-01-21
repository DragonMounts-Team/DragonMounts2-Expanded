package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

import static net.dragonmounts.util.LevelUtil.isDaytime;

public class SunlightType extends DragonType {
    public SunlightType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() && isDaytime(level) && dragon.getLifeStage().isOldEnough(DragonLifeStage.FLEDGLING)) {
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2f;
            float h = dragon.height * 0.5F;
            float f = dragon.width * 1.2F + 0.75F;
            for (int i = -2; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.CRIT,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY + (random.nextDouble() + 0.25) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }
}
