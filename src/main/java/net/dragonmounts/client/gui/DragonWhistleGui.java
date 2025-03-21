package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.network.CFollowOrderPacket;
import net.dragonmounts.network.CSitOrderPacket;
import net.dragonmounts.network.CTeleportOrderPacket;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

public class DragonWhistleGui extends GuiScreen {
    public final UUID uuid;

    public DragonWhistleGui(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        String tpText = ClientUtil.translateToLocal("gui.dragonmounts.teleportToPlayer");
        String sitText = ClientUtil.translateToLocal("gui.dragonmounts.toggleSit");
        String followText = ClientUtil.translateToLocal("gui.dragonmounts.toggleFollow");
        int buttonWidth = 25 + maxTextWidth(
                this.fontRenderer,
                sitText,
                tpText,
                followText
        );
        int buttonStart = (width - buttonWidth) / 2;
        int center = height / 2;
        buttonList.add(new GuiButton(1, buttonStart, center - 25, buttonWidth, 20, tpText));
        buttonList.add(new GuiButton(2, buttonStart, center + 5, buttonWidth, 20, sitText));
        buttonList.add(new GuiButton(3, buttonStart, center + 30, buttonWidth, 20, followText));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (uuid != null) {
            switch (button.id) {
                case 1:
                    EntityPlayer player = this.mc.player;
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new CTeleportOrderPacket(uuid, player.rotationPitch, player.rotationYawHead));
                    break;
                case 2:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new CSitOrderPacket(uuid));
                    break;
                case 3:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new CFollowOrderPacket(uuid));
                    break;
            }
        }
        //Close GUI when option is selected
        this.mc.displayGuiScreen(null);
    }

    private static int maxTextWidth(FontRenderer font, String... text) {
        int max = 0;
        for (String str : text) {
            int width = font.getStringWidth(str);
            if (width > max) {
                max = width;
            }
        }
        return max;
    }
}
