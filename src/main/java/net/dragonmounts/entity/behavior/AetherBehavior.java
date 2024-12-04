package net.dragonmounts.entity.behavior;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.AetherBreathFX;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundState;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponAether;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class AetherBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {
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
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
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
