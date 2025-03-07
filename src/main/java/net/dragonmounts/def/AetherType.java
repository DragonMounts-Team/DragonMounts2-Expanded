package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.AetherBreath;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class AetherType extends DragonType {
    public AetherType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        World level = dragon.world;
        if (dragon.posY > level.getHeight() * 1.2 && level.isDaytime() && dragon.lifeStageHelper.isOldEnough(DragonLifeStage.PREJUVENILE)) {
            Random random = level.rand;
            float s = dragon.getScale() * 1.2f;
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
    public boolean isHabitatEnvironment(Entity egg) {
        return egg.posY > egg.world.getHeight() * 0.66;
    }
}
