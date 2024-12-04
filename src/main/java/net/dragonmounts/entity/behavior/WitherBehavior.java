package net.dragonmounts.entity.behavior;

import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.WitherBreathFX;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponWither;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

public class WitherBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {
        World level = dragon.world;
        if (level instanceof WorldServer && !dragon.isDead && dragon.isUsingBreathWeapon() && !dragon.isEgg()) {
            ((WorldServer) level).spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    dragon.posX,
                    dragon.posY + dragon.getEyeHeight(),
                    dragon.posZ,
                    1,
                    0.5,
                    0.25,
                    0.5,
                    0.0
            );
        }
    }

    @Nullable
    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponWither(dragon);
    }

    @Override
    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return dragon.isBaby() ? ModSounds.ENTITY_DRAGON_HATCHLING_GROWL : ModSounds.ENTITY_NETHER_DRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new WitherBreathFX(world, position, direction, power, partialTicks));
    }
}
