package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.ZombieBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ZombieType extends DragonType {
    public ZombieType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new ZombieBreath(dragon, 0.6F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_HATCHLING : DMSounds.DRAGON_PURR_ZOMBIE;
    }

    @Override
    public SoundEvent getDeathSound(TameableDragonEntity dragon) {
        return dragon.isEgg() ? DMSounds.DRAGON_EGG_SHATTER : DMSounds.DRAGON_DEATH_ZOMBIE;
    }
}
