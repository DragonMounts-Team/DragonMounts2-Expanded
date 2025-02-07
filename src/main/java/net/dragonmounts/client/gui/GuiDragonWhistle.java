package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.network.CUnbindWhistlePacket;
import net.dragonmounts.network.MessageDragonSit;
import net.dragonmounts.network.MessageDragonTeleport;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.UUID;

public class GuiDragonWhistle extends GuiScreen {
    World world;
    UUID uuid;
    EnumHand hand;
    GuiButton unbind;
    String tooltip;

    public GuiDragonWhistle(World world, UUID uuid, EnumHand hand) {
        super();
        this.world = world;
        this.uuid = uuid;
        this.hand = hand;
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        FontRenderer font = this.fontRenderer;
        String sitText = ClientUtil.translateToLocal("gui.dragonmounts.sit");
        String tpText = ClientUtil.translateToLocal("gui.dragonmounts.teleportToPlayer");
        String unbindText = ClientUtil.translateToLocal("gui.dragonmounts.unbindWhistle");
        this.tooltip = ClientUtil.translateToLocal("gui.dragonmounts.unbindWhistle.tooltip");
        int buttonWidth = Math.max(font.getStringWidth(sitText), Math.max(font.getStringWidth(tpText), font.getStringWidth(unbindText))) + 25;
        int buttonStart = (width - buttonWidth) / 2;
        int center = height / 2;
        buttonList.add(this.unbind = new GuiButton(1, buttonStart, center - 50, buttonWidth, 20, unbindText));
        buttonList.add(new GuiButton(2, buttonStart, center - 25, buttonWidth, 20, tpText));
        buttonList.add(new GuiButton(3, buttonStart, center + 5, buttonWidth, 20, sitText));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (uuid != null) {
            switch (button.id) {
                case 1:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new CUnbindWhistlePacket(this.hand));
                    break;
                case 2:
                    EntityPlayer player = this.mc.player;
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonTeleport(uuid, player.rotationPitch, player.rotationYawHead));
                    break;
                case 3:
                    DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonSit(uuid));
                    break;
            }

            //Close GUI when option is selected
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.unbind.enabled = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.unbind.enabled && this.unbind.isMouseOver()) {
            this.drawHoveringText(this.tooltip, mouseX, mouseY);
        }
    }
}
