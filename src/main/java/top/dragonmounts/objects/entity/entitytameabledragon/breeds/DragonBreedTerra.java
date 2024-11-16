package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class DragonBreedTerra extends DragonBreed {

    DragonBreedTerra() {
        super("terra", 0Xa56c21);

        setHabitatBiome(Biomes.MESA);
        setHabitatBiome(Biomes.MESA_ROCK);
        setHabitatBiome(Biomes.MESA_CLEAR_ROCK);
        setHabitatBiome(Biomes.MUTATED_MESA_CLEAR_ROCK);
        setHabitatBiome(Biomes.MUTATED_MESA_ROCK);
        setHabitatBlock(Blocks.HARDENED_CLAY);
        setHabitatBlock(Blocks.SAND);
        setHabitatBlock(Blocks.SANDSTONE);
        setHabitatBlock(Blocks.SANDSTONE_STAIRS);
        setHabitatBlock(Blocks.RED_SANDSTONE);
        setHabitatBlock(Blocks.RED_SANDSTONE_STAIRS);

    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDisable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDeath(EntityTameableDragon dragon) {
    }

    @Override
    public void onLivingUpdate(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.MESA) && dragon.posY > level.getHeight() + 8 && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = this.rand;
            float s = dragon.getScale() * 1.2f;
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
                        (dragon.isMale() ? 3 : 5)
                );
            }
        }
    }

    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.TERRA;
    }
}
