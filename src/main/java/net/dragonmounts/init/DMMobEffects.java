package net.dragonmounts.init;

import net.dragonmounts.effect.DarkDragonsGraceEffect;
import net.minecraft.potion.Potion;

public class DMMobEffects {
    public static final Potion DARK_DRAGONS_GRACE = new DarkDragonsGraceEffect(false, 0x6908265)
            .setBeneficial()
            .setPotionName("effect.dragonmounts.dark_dragons_grace")
            .setRegistryName("dark_dragons_grace");
}
