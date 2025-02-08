package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.effects.PoisonBreathFX;
import net.dragonmounts.entity.breath.impl.ZombieBreath;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ZombieType extends DragonType {
    public ZombieType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Nullable
    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new ZombieBreath(dragon, 0.6F);
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
    public void spawnClientBreath(World world, Vec3d position, Vec3d direction, BreathPower power, float partialTicks) {
        world.spawnEntity(new PoisonBreathFX(world, position, direction, power, partialTicks));
    }
}
