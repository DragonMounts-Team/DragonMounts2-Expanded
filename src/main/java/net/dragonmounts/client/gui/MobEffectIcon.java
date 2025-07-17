package net.dragonmounts.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import static net.dragonmounts.DragonMounts.makeId;

public abstract class MobEffectIcon {
    public static final ResourceLocation ICONS = makeId("textures/gui/effects.png");

    public static void render(Gui gui, int x, int y, int u, int v) {
        Minecraft.getMinecraft().renderEngine.bindTexture(ICONS);
        gui.drawTexturedModalRect(x, y, u, v, 18, 18);
    }

    private MobEffectIcon() {}
}
