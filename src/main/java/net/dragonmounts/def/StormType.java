package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class StormType extends WaterType {
    public StormType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public void tick(TameableDragonEntity dragon) {
        super.tick(dragon);
        EntityLivingBase target = dragon.getAttackTarget();
        if (target != null && target.isEntityAlive() && (!(target instanceof EntityPlayer) || !((EntityPlayer) target).capabilities.isCreativeMode) && dragon.world.isRaining() && dragon.world.rand.nextInt(70) == 0) {
            target.world.addWeatherEffect(new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, false));
        }
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.ice;// why?
    }
}
