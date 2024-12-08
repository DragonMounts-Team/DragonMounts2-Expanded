package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathNode;
import net.dragonmounts.entity.breath.effects.AetherBreathFX;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeaponAether;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class DarkType extends DragonType {
    public DarkType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() * 1.2 && level.isDaytime() && dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = level.rand;
            float s = dragon.getScale() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = 0; i < s; ++i) {
                double x = dragon.posX + (random.nextDouble() - 0.5) * f;
                double y = dragon.posY + (random.nextDouble() - 0.5) * h;
                double z = dragon.posZ + (random.nextDouble() - 0.5) * f;
                if (random.nextInt(5) == 0) {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 1, 1, 0, 0, 0, 0); //yellow
                } else {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0, 1, 1, 0, 0, 0); //aqua
                }
            }
        }
    }

    @Override
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return new BreathWeaponAether(dragon);
    }

    @Override
    public boolean isHabitatEnvironment(Entity egg) {
        return egg.posY > egg.world.getHeight() * 0.66;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new AetherBreathFX(world, position, direction, power, partialTicks));
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.aether;
    }
}
