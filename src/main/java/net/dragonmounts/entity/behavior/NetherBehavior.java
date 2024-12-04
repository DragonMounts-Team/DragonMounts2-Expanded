package net.dragonmounts.entity.behavior;

import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.NetherBreathFX;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponNether;
import net.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class NetherBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (level.isRemote || dragon.isDead || !dragon.getLifeStageHelper().isOldEnough(DragonLifeStage.PREJUVENILE))
            return;
        Random random = level.rand;
        float s = dragon.getScale();
        float h = dragon.height * s;
        float f = (dragon.width - 0.65F) * s;
        boolean isWet = dragon.isWet();
        for (int i = -1; i < s; ++i) {
            level.spawnParticle(
                    EnumParticleTypes.DRIP_LAVA,
                    dragon.posX + (random.nextDouble() - 0.5) * f,
                    dragon.posY + (random.nextDouble() - 0.5) * h,
                    dragon.posZ + (random.nextDouble() - 0.5) * f,
                    0,
                    0,
                    0
            );
            if (isWet) {
                level.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponNether(dragon);
    }

    @Override
    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_NETHER_DRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new NetherBreathFX(world, position, direction, power, partialTicks));
    }
}
