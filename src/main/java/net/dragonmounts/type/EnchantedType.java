package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.Random;

public class EnchantedType extends DragonType {
    public EnchantedType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        if ((dragon.ticksExisted & 0b11) == 0 && dragon.getLifeStage().isOldEnough(DragonLifeStage.FLEDGLING)) {
            World level = dragon.world;
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 4.0F;
            float h = dragon.height * 1.2F;
            float f = dragon.width * 1.2F + 0.75F;
            for (int i = -2; i < s; ++i) {
                double x = dragon.posX + (random.nextDouble() - 0.5) * f;
                double y = dragon.posY + random.nextDouble() * h;
                double z = dragon.posZ + (random.nextDouble() - 0.5) * f;
                level.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, x, y, z, 0, 0, 0);
            }
        }
    }
}
