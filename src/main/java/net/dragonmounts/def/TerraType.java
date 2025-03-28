package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class TerraType extends DragonType {
    public TerraType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.MESA) && dragon.posY > level.getHeight() + 8 && dragon.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = 0; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.FALLING_DUST,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0,
                        4
                );
            }
        }
    }
}
