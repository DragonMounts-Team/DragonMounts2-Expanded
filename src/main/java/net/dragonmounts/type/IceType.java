package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.IceBreath;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class IceType extends DragonType {
    public static final float FOOTPRINT_CHANCE = 0.01F;

    public IceType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (level.isRemote) return;
        DragonLifeStage stage = dragon.lifeStageHelper.getLifeStage();
        if (dragon.isOverWater()) {
            EnchantmentFrostWalker.freezeNearby(dragon, level, new BlockPos(dragon), DragonLifeStage.EGG == stage ? 0 : 1);
        }
        Random random = level.rand;
        if (!dragon.isDead && dragon.posY > level.getHeight() * 1.25 && stage.isOldEnough(DragonLifeStage.PREJUVENILE) && BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.SNOWY)) {
            float s = dragon.getAdjustedSize() * 1.2f;
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
        // only apply on server adult dragons that isn't flying
        if (DragonLifeStage.ADULT != stage || dragon.isFlying()) return;

        // footprint loop, from EntitySnowman.onLivingUpdate with slight tweaks
        for (int i = 0; i < 4; ++i) {
            // place only if randomly selected
            if (random.nextFloat() > FOOTPRINT_CHANCE) continue;

            // get footprint position
            double bx = dragon.posX + (i & 1) * 0.5 - 0.25;
            double by = dragon.posY + 0.5;
            double bz = dragon.posZ + (i / 2 % 2 * 2 - 1) * 0.25;
            BlockPos pos = new BlockPos(bx, by, bz);

            // footprints can only be placed on empty space
            if (level.isAirBlock(pos) && level.getBiomeForCoordsBody(pos).getTemperature(pos) < 0.1F && Blocks.SNOW_LAYER.canPlaceBlockAt(level, pos)) {
                level.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
            }
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new IceBreath(dragon, 0.7F);
    }
}
