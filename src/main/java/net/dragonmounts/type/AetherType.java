package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.AetherBreath;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class AetherType extends DragonType {
    public AetherType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() * 1.2 && level.isDaytime() && dragon.getLifeStage().isOldEnough(DragonLifeStage.FLEDGLING)) {
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = 0; i < s; ++i) {
                double x = dragon.posX + (random.nextDouble() - 0.5) * f;
                double y = dragon.posY + (random.nextDouble() - 0.5) * h;
                double z = dragon.posZ + (random.nextDouble() - 0.5) * f;
                if (random.nextFloat() < 0.2F) {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 1, 1, 0, 0, 0, 0); //yellow
                } else {
                    level.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, 0, 1, 1, 0, 0, 0); //aqua
                }
            }
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new AetherBreath(dragon, 0.7F);
    }

    @Override
    public boolean isInHabitat(Entity egg) {
        return egg.posY > egg.world.getHeight() * 0.66;
    }
}
