package net.dragonmounts.init;

import net.dragonmounts.effect.DarkDragonsGraceEffect;
import net.minecraft.potion.Potion;

import static net.dragonmounts.DragonMounts.applyId;

public class DMMobEffects {
    public static final Potion DARK_DRAGONS_GRACE = applyId(
            new DarkDragonsGraceEffect(false, 0x6908265)
                    .setBeneficial()
                    .setPotionName("effect.dragonmounts.dark_dragons_grace"),
            "dark_dragons_grace"
    );
}
