package net.dragonmounts.effect;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.dragonmounts.client.ClientUtil.renderEffectIcon;

@ParametersAreNonnullByDefault
public class DarkDragonsGraceEffect extends Potion {
    public DarkDragonsGraceEffect(boolean bad, int color) {
        super(bad, color);
        this.setEffectiveness(0.25);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity.getHealth() < entity.getMaxHealth() && entity.world.getLightFromNeighbors(entity.getPosition()) < 8) {
            entity.heal(0.5F);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 50 >> amplifier;
        return k == 0 || duration % k == 0;
    }

    @Override
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        renderEffectIcon(gui, x + 3, y + 3, 0, 0);
    }

    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        renderEffectIcon(gui, x + 6, y + 7, 0, 0);
    }
}
