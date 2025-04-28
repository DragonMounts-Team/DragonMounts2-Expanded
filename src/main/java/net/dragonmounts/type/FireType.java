package net.dragonmounts.type;

import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.entity.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

public class FireType extends DragonType {
    public FireType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tickClient(ClientDragonEntity dragon) {
        if (dragon.lifeStageHelper.isOldEnough(DragonLifeStage.FLEDGLING)) {
            World level = dragon.world;
            AxisAlignedBB box = dragon.getEntityBoundingBox().grow(-0.1, -0.4, -0.1);
            if (!level.isMaterialInBB(box, Material.LAVA) && !level.isMaterialInBB(box, Material.FIRE)) return;
            Random random = level.rand;
            float s = dragon.getAdjustedSize() * 1.2f;
            float h = dragon.height * s;
            float f = (dragon.width - 0.65F) * s;
            for (int i = -2; i < s; ++i) {
                level.spawnParticle(
                        EnumParticleTypes.FLAME,
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
}
