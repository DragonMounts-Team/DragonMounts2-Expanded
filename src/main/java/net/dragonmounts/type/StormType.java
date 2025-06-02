package net.dragonmounts.type;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.StormBreath;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class StormType extends WaterType {
    public StormType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public void tickServer(ServerDragonEntity dragon) {
        super.tickServer(dragon);
        EntityLivingBase target = dragon.getAttackTarget();
        if (target != null && target.isEntityAlive() && (!(target instanceof EntityPlayer) || !((EntityPlayer) target).capabilities.isCreativeMode) && target.world.isRaining() && target.world.rand.nextInt(70) == 0) {
            target.world.addWeatherEffect(new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false));
        }
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new StormBreath(dragon, 0.7F);
    }
}
