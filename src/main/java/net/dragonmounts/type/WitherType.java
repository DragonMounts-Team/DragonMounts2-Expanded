package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.WitherBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.Random;

public class WitherType extends DragonType {
    public WitherType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        if (!dragon.isDead && dragon.isUsingBreathWeapon() && !dragon.isEgg()) {
            Random random = dragon.world.rand;
            dragon.world.spawnParticle(
                    EnumParticleTypes.SMOKE_NORMAL,
                    dragon.posX + random.nextGaussian() * 0.5,
                    dragon.posY + dragon.getEyeHeight() + random.nextGaussian() * 0.25,
                    dragon.posZ + random.nextGaussian() * 0.5,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new WitherBreath(dragon, 0.6F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_NETHER_HATCHLING : DMSounds.DRAGON_PURR_NETHER;
    }
}
