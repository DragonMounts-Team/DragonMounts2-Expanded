package net.dragonmounts.entity.behavior;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathNode;
import net.dragonmounts.entity.breath.effects.IceBreathFX;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeaponIce;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class IceBehavior implements DragonType.Behavior {
    public static final float FOOTPRINT_CHANCE = 0.01F;

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (level.isRemote) return;
        if (dragon.isOverWater()) {
            EnchantmentFrostWalker.freezeNearby(dragon, level, new BlockPos(dragon), 1);
        }
        Random random = level.rand;
        if (!dragon.isDead && dragon.posY > level.getHeight() * 1.25 && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE) && BiomeDictionary.hasType(level.getBiome(dragon.getPosition()), BiomeDictionary.Type.SNOWY)) {

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
        // only apply on server adult dragons that isn't flying
        if (!dragon.isAdult() || dragon.isFlying()) return;

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
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return new BreathWeaponIce(dragon);
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.ice;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new IceBreathFX(world, position, direction, power, partialTicks));
    }
}
