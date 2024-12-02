package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.network.MessageDragonSit;
import net.dragonmounts.network.MessageDragonTeleport;
import net.dragonmounts.util.DMUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

public class GuiDragonWhistle extends GuiScreen {
    World world;
    UUID uuid;

    public GuiDragonWhistle(World world, UUID uuid) {
        super();
        this.world = world;
        this.uuid = uuid;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        String tpText = DMUtils.translateToLocal("gui.cometoPlayer");
        String sitText = DMUtils.translateToLocal("gui.dragon.sit");
        int buttonWidth = Math.max(this.fontRenderer.getStringWidth(tpText), this.fontRenderer.getStringWidth(sitText)) + 25;
        int buttonStart = (width - buttonWidth) / 2;
        int center = height / 2;
        buttonList.add(new GuiButton(1, buttonStart, center - 25, buttonWidth, 20, tpText));
        buttonList.add(new GuiButton(2, buttonStart, center, buttonWidth, 20, sitText));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (uuid != null) {
            switch (button.id) {
                case 1:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonTeleport(uuid));
                    break;
                case 2:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonSit(uuid));
                    break;
            }

            //Close GUI when option is selected
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
