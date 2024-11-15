package top.dragonmounts.client.gui;

import top.dragonmounts.DragonMounts;
import top.dragonmounts.DragonMountsTags;
import top.dragonmounts.client.model.dragon.anim.DragonAnimator;
import top.dragonmounts.inventory.ContainerDragon;
import top.dragonmounts.network.MessageDragonGui;
import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.util.DMUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiDragon extends GuiContainer {

    public static final ResourceLocation lockOpen = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/lock_1.png");
    public static final ResourceLocation lockLocked = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/lock_2.png");
    public static final ResourceLocation lockDisabled = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/lock_3.png");
    private static final ResourceLocation mainGui = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/dragon.png");
    private static final ResourceLocation offhand = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/offhand.png");
    private static final ResourceLocation hunger_full = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/hunger_full.png");
    private static final ResourceLocation dismountTex = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/items/carriage/carriage_oak.png");
    private EntityTameableDragon dragon;
    private float mousePosX;
    private float mousePosY;
    private LockButton lockButton;
    //    private GuiButton dismount;
    private EntityPlayer player;

    public GuiDragon(EntityPlayer player, EntityTameableDragon dragon) {
        super(new ContainerDragon(dragon, player));
        this.player = player;
        this.dragon = dragon;
        this.allowUserInput = false;
        this.ySize = 214;
        this.xSize = 176;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the
     * items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(dragon.hasCustomName() ? dragon.getCustomNameTag() : DMUtils.translateToLocal(dragon.makeTranslationKey()), 8, 6, dragon.getBreed().getColor());
        if (dragon.isMale()) {
            this.fontRenderer.drawString("M", 155, 6, 0x0079be);
        } else {
            this.fontRenderer.drawString("FM", 155, 6, 0Xff8b8b);
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.6, 0.6, 0.6);
        this.fontRenderer.drawString(dragon.getHunger() + "/100", 60, 106, 0Xe99e0c);
        GlStateManager.popMatrix();
    }

    private void hunger(int x, int y) {
        this.mc.getTextureManager().bindTexture(hunger_full);
        drawModalRectWithCustomSizedTexture(x + 26, y + 60, 0.0F, 0.0F, 9, 9, 9, 9);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(mainGui);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

        if (this.dragon.isChested()) this.drawTexturedModalRect(x, y + 73, 0, 130, 170, 55);

        hunger(x, y);

        this.mc.getTextureManager().bindTexture(offhand);
        drawModalRectWithCustomSizedTexture(x - 22, y + 184, 0.0F, 0.0F, 25, 30, 25, 30);

        int size = 0;
        switch (dragon.getLifeStageHelper().getLifeStage()) {
            case EGG:
                size = 140;
                break;
            case HATCHLING:
                size = 55;
                break;
            case INFANT:
                size = 45;
                break;
            case PREJUVENILE:
                size = 18;
                break;
            case JUVENILE:
                size = 8;
                break;
            case ADULT:
                size = 7;
                break;
        }

        //draw dragon entity
        DragonAnimator animator = this.dragon.getAnimator();
        animator.isInGui = true;
        GuiInventory.drawEntityOnScreen(x + 90, y + 60, size, x + 125 - this.mousePosX, y + 28 - this.mousePosY, this.dragon);
        animator.isInGui = false;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        buttonList.add(lockButton = new LockButton(2, width / 2 + 63, height / 2 - 54, 18, 20, dragon));
        buttonList.add(new GuiButton(1, width / 2 + 45, height / 2 - 54, 18, 20, DMUtils.translateToLocal("gui.dragon.sit")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int id = button.id;
        if (id == 1 || id == 2) {
            DragonMounts.NETWORK_WRAPPER.sendToServer(new MessageDragonGui(dragon.getEntityId(), id));
        }
    }

    public void updateScreen() {
        lockButton.enabled = (player == dragon.getOwner());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.mousePosX = mouseX;
        this.mousePosY = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    static class LockButton extends GuiButton {

        private EntityTameableDragon dragon;

        public LockButton(int buttonId, int x, int y, int width, int height, EntityTameableDragon dragon) {
            super(buttonId, x, y, width, height, "");
            this.dragon = dragon;
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                int x = this.x, y = this.y, width = this.width, height = this.height, halfWidth = width / 2;
                mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
                int i = this.getHoverState(this.hovered);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(x, y, 0, 46 + i * 20, halfWidth, height);
                this.drawTexturedModalRect(x + halfWidth, y, 200 - halfWidth, 46 + i * 20, halfWidth, height);
                this.mouseDragged(mc, mouseX, mouseY);
                if (dragon.allowedOtherPlayers()) {
                    mc.getTextureManager().bindTexture(lockOpen);
                } else if (!dragon.allowedOtherPlayers()) {
                    mc.getTextureManager().bindTexture(lockLocked);
                } else if (!enabled) {
                    mc.getTextureManager().bindTexture(lockDisabled);
                }
                float uv = 0.0625F * 16;
                double renderX = x + 0.5;
                double renderY = y + 2;
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(renderX, renderY + 16, 0.0D).tex(0, uv).endVertex();
                bufferbuilder.pos(renderX + 16, renderY + 16, 0.0D).tex(uv, uv).endVertex();
                bufferbuilder.pos(renderX + 16, renderY, 0.0D).tex(uv, 0).endVertex();
                bufferbuilder.pos(renderX, renderY, 0.0D).tex(0, 0).endVertex();
                tessellator.draw();
            }
        }
    }
}