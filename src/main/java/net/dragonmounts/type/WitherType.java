package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.WitherBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WitherType extends DragonType {
    public WitherType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
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

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new WitherBreath(dragon, 0.6F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_NETHER_HATCHLING : DMSounds.DRAGON_PURR_NETHER;
    }
}
