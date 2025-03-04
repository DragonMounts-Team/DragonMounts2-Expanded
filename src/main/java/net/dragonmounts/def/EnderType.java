package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.EnderBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class EnderType extends DragonType {
    public EnderType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new EnderBreath(dragon, 0.9F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return DMSounds.DRAGON_AMBIENT;
    }

    @Override
    public SoundEvent getRoarSound(TameableDragonEntity dragon) {
        return SoundEvents.ENTITY_ENDERDRAGON_GROWL;
    }
}
