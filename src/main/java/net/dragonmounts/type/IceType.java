package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.IceBreath;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.MutableBlockPosEx;
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
    public void tickClient(ClientDragonEntity dragon) {
        World level = dragon.world;
        if (!dragon.isDead &&
                dragon.posY > level.getWorldType().getCloudHeight() &&
                dragon.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE) &&
                BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.SNOWY)
        ) {
            Random random = level.rand;
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
    }

    /// @see net.minecraft.entity.monster.EntitySnowman#onLivingUpdate()
    @Override
    public void tickServer(ServerDragonEntity dragon) {
        DragonLifeStage stage = dragon.lifeStageHelper.getLifeStage();
        if (dragon.isOverWater()) {
            EnchantmentFrostWalker.freezeNearby(dragon, dragon.world, new BlockPos(dragon), DragonLifeStage.EGG == stage ? 0 : 1);
        }
        // only apply on adult dragons that isn't flying
        if (DragonLifeStage.ADULT != stage || dragon.isFlying()) return;
        World level = dragon.world;
        Random random = level.rand;
        MutableBlockPosEx pos = new MutableBlockPosEx(0, 0, 0);
        for (int i = 0; i < 4; ++i) {
            // place only if randomly selected
            if (random.nextFloat() > FOOTPRINT_CHANCE) continue;
            // footprints can only be placed on empty space
            if (level.isAirBlock(pos.with(
                    (i & 1) == 0 ? dragon.posX - 0.25 : dragon.posX + 0.25,
                    dragon.posY + 0.5,
                    i < 2 ? dragon.posZ - 0.25 : dragon.posX + 0.25
            )) && level.getBiomeForCoordsBody(pos).getTemperature(pos) < 0.1F && Blocks.SNOW_LAYER.canPlaceBlockAt(level, pos)) {
                level.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
            }
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new IceBreath(dragon, 0.7F);
    }
}
