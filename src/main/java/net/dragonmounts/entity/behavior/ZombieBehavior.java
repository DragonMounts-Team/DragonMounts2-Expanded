package net.dragonmounts.entity.behavior;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathNode;
import net.dragonmounts.entity.breath.effects.PoisonBreathFX;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.breath.weapons.BreathWeapon;
import net.dragonmounts.entity.breath.weapons.BreathWeaponPoison;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ZombieBehavior implements DragonType.Behavior {
    @Override
    public void tick(TameableDragonEntity dragon) {}

    @Nullable
    @Override
    public BreathWeapon createBreathWeapon(TameableDragonEntity dragon) {
        return new BreathWeaponPoison(dragon);
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.ice;// why
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isBaby() ? DMSounds.ENTITY_DRAGON_HATCHLING_GROWL : DMSounds.ZOMBIE_DRAGON_GROWL;
    }

    @Override
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathNode.Power power, float partialTicks) {
        world.spawnEntity(new PoisonBreathFX(world, position, direction, power, partialTicks));
    }
}
