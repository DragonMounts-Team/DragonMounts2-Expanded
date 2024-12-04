package net.dragonmounts.entity.behavior;

import net.dragonmounts.inits.ModSounds;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.BreathNode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.effects.EnderBreathFX;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.weapons.BreathWeaponEnder;
import net.dragonmounts.registry.DragonType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderBehavior implements DragonType.Behavior {
    @Override
    public void tick(EntityTameableDragon dragon) {}

    @Override
    public BreathWeapon createBreathWeapon(EntityTameableDragon dragon) {
        return new BreathWeaponEnder(dragon);
    }

    @Override
    public SoundEvent getLivingSound(EntityTameableDragon dragon) {
        return ModSounds.ENTITY_DRAGON_BREATHE;
    }

    @Override
    public SoundEvent getRoarSound(EntityTameableDragon dragon) {
        return SoundEvents.ENTITY_ENDERDRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new EnderBreathFX(world, position, direction, power, partialTicks));
    }
}
