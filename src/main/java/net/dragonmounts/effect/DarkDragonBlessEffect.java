package net.dragonmounts.effect;

import net.dragonmounts.client.gui.MobEffectIcon;
import net.dragonmounts.init.DMEffect;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class DarkDragonBlessEffect extends Potion {

    public DarkDragonBlessEffect(boolean isBadEffect, int color) {
        super(isBadEffect, color);
        this.setEffectiveness(0.25D);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (this == DMEffect.DARK_DRAGON_BLESS) {
            if (entity.getHealth() < entity.getMaxHealth()) {
                entity.heal(0.5F);
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 50 >> amplifier;
        return k == 0 || duration % k == 0;
    }

    /**
     * @return false, so that we can render icon via methods below
     */
    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @Override
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        MobEffectIcon.render(gui, x + 3, y + 3, 0, 0);
    }

    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        MobEffectIcon.render(gui, x + 6, y + 7, 0, 0);
    }
}
