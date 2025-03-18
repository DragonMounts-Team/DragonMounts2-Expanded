package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.block.entity.DragonCoreBlockEntity;
import net.dragonmounts.inventory.DragonCoreContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class DragonCoreGui extends GuiContainer {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/dragon_core.png");
    private final InventoryPlayer inventory;
    private final DragonCoreBlockEntity core;

    public DragonCoreGui(InventoryPlayer inventory, DragonCoreBlockEntity core, EntityPlayer player) {
        super(new DragonCoreContainer(inventory, core, player));
        this.inventory = inventory;
        this.core = core;

        this.xSize=175;
        this.ySize=165;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.core.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.inventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96, 4210742);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }


}
