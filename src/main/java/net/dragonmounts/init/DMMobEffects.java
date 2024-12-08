package net.dragonmounts.init;

import net.dragonmounts.effect.DarkDragonBlessEffect;
import net.minecraft.potion.Potion;

public class DMMobEffects {
    public static final Potion DARK_DRAGON_BLESS = new DarkDragonBlessEffect(false, 0x6908265)
            .setBeneficial()
            .setPotionName("effect.dark_dragon_bless")
            .setRegistryName("dark_dragon_bless");
}
