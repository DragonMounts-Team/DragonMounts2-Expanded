package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.inventory.DragonContainer;
import net.dragonmounts.inventory.ISlotListener;
import net.dragonmounts.inventory.WhistleSlot;
import net.dragonmounts.network.CDragonConfigPacket;
import net.dragonmounts.network.CRenameWhistlePacket;
import net.dragonmounts.network.DragonStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * @see net.minecraft.client.gui.GuiRepair
 */
@SideOnly(Side.CLIENT)
public class DragonInventoryGui extends GuiContainer implements ISlotListener<WhistleSlot> {
    public static final ResourceLocation LOCK_OPEN = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/lock_open.png");
    public static final ResourceLocation LOCK_LOCKED = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/lock_locked.png");
    public static final ResourceLocation LOCK_DISABLED = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/lock_disabled.png");
    private static final ResourceLocation INVENTORY = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/dragon.png");
    private static final ResourceLocation HUNGER_FULL = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/hunger_full.png");
    private final DragonContainer<ClientDragonEntity> container;
    private final ClientDragonEntity dragon;
    private final EntityPlayer player;
    private GuiTextField nameField;
    private LockButton lock;
    private GuiButton order;
    private boolean chested;
    private String name;
    private String hunger;
    private String tip;
    private String sit;
    private String stand;
    private int size;
    private int color;

    public DragonInventoryGui(EntityPlayer player, DragonContainer<ClientDragonEntity> container) {
        super(container);
        this.player = player;
        this.dragon = container.dragon;
        this.container = container;
        this.allowUserInput = true;
        this.ySize = 214;
        this.xSize = 176;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.nameField = new GuiTextField(31, this.fontRenderer, this.guiLeft, this.guiTop - 12, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(35);
        this.buttonList.clear();
        int x = this.width / 2 + 45, y = this.height / 2 - 54;
        this.stand = ClientUtil.translateToLocal("gui.dragonmounts.stand");
        this.buttonList.add(this.order = new GuiButton(DragonStates.SITTING_STATE, x, y, 18, 20, this.sit = ClientUtil.translateToLocal("gui.dragonmounts.sit")));
        this.buttonList.add(this.lock = new LockButton(DragonStates.LOCKED_STATE, x + 18, y, 18, 20));
        this.updateScreen();
        this.container.whistle.listener = this;
    }

    @Override
    public void updateScreen() {
        TameableDragonEntity dragon = this.dragon;
        this.lock.enabled = dragon.isOwner(this.player);
        boolean trustOther = dragon.allowedOtherPlayers();
        this.lock.icon = trustOther
                ? LOCK_OPEN
                : this.lock.enabled
                ? LOCK_LOCKED
                : LOCK_DISABLED;
        this.tip = ClientUtil.translateBothToLocal("gui.dragonmounts.lock.tooltip", trustOther ? "options.on" : "options.off");
        this.name = dragon.getName();
        this.chested = dragon.isChested();
        this.hunger = dragon.getHunger() + "/100";
        this.color = dragon.getVariant().type.color;
        this.order.displayString = dragon.isSitting() ? this.stand : this.sit;
        switch (dragon.lifeStageHelper.getLifeStage()) {
            case EGG:
                this.size = 140;
                break;
            case HATCHLING:
                this.size = 55;
                break;
            case INFANT:
                this.size = 45;
                break;
            case PREJUVENILE:
                this.size = 18;
                break;
            case JUVENILE:
                this.size = 8;
                break;
            case ADULT:
                this.size = 7;
                break;
        }
    }

    private void renameWhistle() {
        ItemStack stack = this.container.whistle.getStack();
        String text = this.nameField.getText();
        if (!stack.isEmpty() && !stack.hasDisplayName() && text.equals(stack.getDisplayName())) {
            text = "";
        }
        this.container.whistle.applyName(text);
        DragonMounts.NETWORK_WRAPPER.sendToServer(new CRenameWhistlePacket(text));
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the
     * items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.name, 8, 6, this.color);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.6, 0.6, 0.6);
        this.fontRenderer.drawString(this.hunger, 60, 106, 0Xe99e0c);
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY);
        int x = this.guiLeft, y = this.guiTop;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(x - 22, y + 184, 0, this.ySize, 25, 30);
        if (this.chested) {
            this.drawTexturedModalRect(x, y + 73, 0, 130, 170, 55);
        }
        this.mc.getTextureManager().bindTexture(HUNGER_FULL);
        drawModalRectWithCustomSizedTexture(x + 26, y + 60, 0.0F, 0.0F, 9, 9, 9, 9);
        //draw dragon entity
        this.dragon.isInGui = true;
        GuiInventory.drawEntityOnScreen(x + 90, y + 60, this.size, x + 125 - mouseX, y + 28 - mouseY, this.dragon);
        this.dragon.isInGui = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;
        switch (button.id) {
            case DragonStates.SITTING_STATE:
            case DragonStates.FOLLOWING_STATE:
                DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonConfigPacket(this.dragon.getEntityId(), button.id));
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.container.whistle.getHasStack() && this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            this.renameWhistle();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (this.container.whistle.getHasStack()) {
            this.nameField.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        if (this.container.whistle.getHasStack()) {
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            this.nameField.drawTextBox();
        }
    }

    @Override
    protected void renderHoveredToolTip(int x, int y) {
        super.renderHoveredToolTip(x, y);
        if (this.lock.isHovered()) {
            this.drawHoveringText(this.tip, x, y);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void beforePlaceItem(WhistleSlot slot, ItemStack stack) {
        boolean enabled = !stack.isEmpty();
        if (slot.getStack().getDisplayName().equals(stack.getDisplayName())) return;
        this.nameField.setText(slot.desiredName = enabled ? stack.getDisplayName() : "");
        this.nameField.setEnabled(enabled);
    }

    @Override
    public void afterTakeItem(WhistleSlot slot, ItemStack stack) {
        this.nameField.setText("");
        this.nameField.setEnabled(false);
    }

    static class LockButton extends GuiButton {
        public @Nonnull ResourceLocation icon = LOCK_DISABLED;

        public LockButton(int buttonId, int x, int y, int width, int height) {
            super(buttonId, x, y, width, height, "");
        }

        public boolean isHovered() {
            return this.hovered;
        }

        /**
         * Draws this button to the screen.
         */
        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (this.visible) {
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
                mc.getTextureManager().bindTexture(this.icon);
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
            } else {
                this.hovered = false;
            }
        }
    }
}