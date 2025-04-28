package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.NetherBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;

public class NetherType extends DragonType {
    public NetherType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        if (dragon.isDead || !dragon.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING)) return;
        World level = dragon.world;
        Random random = level.rand;
        float s = dragon.getAdjustedSize();
        float h = dragon.height * s;
        float f = (dragon.width - 0.65F) * s;
        level.spawnParticle(
                EnumParticleTypes.DRIP_LAVA,
                dragon.posX + (random.nextDouble() - 0.5) * f,
                dragon.posY + (random.nextDouble() - 0.5) * h,
                dragon.posZ + (random.nextDouble() - 0.5) * f,
                0,
                0,
                0
        );
        if (dragon.isWet()) {
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

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new NetherBreath(dragon, 0.9F);
    }

    @Override
    public SoundEvent getLivingSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_PURR_NETHER_HATCHLING : DMSounds.DRAGON_PURR_NETHER;
    }
}
