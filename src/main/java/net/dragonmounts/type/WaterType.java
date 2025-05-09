package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.WaterBreath;
import net.dragonmounts.init.DMSounds;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.Random;

public class WaterType extends DragonType {
    public WaterType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickServer(ServerDragonEntity dragon) {
        if (dragon.isInWater()) {
            EntityUtil.addOrResetEffect(dragon, MobEffects.WATER_BREATHING, 200, 0, false, false, 21);
        }
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        if (dragon.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING)) {
            World level = dragon.world;
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = -2; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.DRIP_WATER,
                        dragon.posX + (random.nextDouble() - 0.5) * f,
                        dragon.posY - 1 + (random.nextDouble() - 0.5) * h,
                        dragon.posZ + (random.nextDouble() - 0.5) * f,
                        0,
                        0,
                        0
                );
            }
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new WaterBreath(dragon, 0.7F);
    }

    @Override
    public SoundEvent getRoarSound(TameableDragonEntity dragon) {
        return dragon.isChild() ? DMSounds.DRAGON_ROAR_HATCHLING : DMSounds.DRAGON_ROAR_WATER;
    }
}
