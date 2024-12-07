package net.dragonmounts.effects;

import net.dragonmounts.init.DMEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;

public class DarkDragonBlessEffect extends Potion{
    private int statusIconIndex = -1;
    public static final RegistryNamespaced<ResourceLocation, Potion> REGISTRY = net.minecraftforge.registries.GameData.getWrapper(Potion.class);
    public DarkDragonBlessEffect(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setIconIndex(0, 0);
        this.setEffectiveness(0.25D);
        this.setPotionName("effect.dark_dragon_bless");
    }
    private static Potion getRegisteredMobEffect(String id)
    {
        Potion potion = REGISTRY.getObject(new ResourceLocation(id));

        if (potion == null)
        {
            throw new IllegalStateException("Invalid MobEffect requested: " + id);
        }
        else
        {
            return potion;
        }
    }
    public DarkDragonBlessEffect setDMIconIndex(int x, int y) {
        this.setIconIndex(x, y); // 调用 Potion 类中的 protected 方法
        return this;
    }
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier){
        if (this == DMEffect.DARK_DRAGON_BLESS){
            if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth()){
                entityLivingBaseIn.heal(0.5F);
            }
        }
    }
    public boolean isReady(int duration, int amplifier){
        int k = 50 >> amplifier;
        if (k > 0){
            return duration % k == 0;
        }
        else{
            return true;
        }
    }
   /* public static void registerPotions()
    {
        REGISTRY.register
    }*/
}
