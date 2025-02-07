package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.effects.EnderBreathFX;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeaponEnder;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EnderType extends DragonType {
    public EnderType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return new BreathWeaponEnder(dragon, 0.9F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return DMSounds.ENTITY_DRAGON_BREATHE;
    }

    @Override
    public SoundEvent getRoarSound(TameableDragonEntity dragon) {
        return SoundEvents.ENTITY_ENDERDRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new EnderBreathFX(world, position, direction, power, partialTicks));
    }
}
