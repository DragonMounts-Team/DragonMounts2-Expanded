package net.dragonmounts.client.gui;

import net.dragonmounts.DragonMounts;
import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.client.ClientDragonEntity;
import net.dragonmounts.client.ClientUtil;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.inventory.DragonContainer;
import net.dragonmounts.inventory.FluteSlot;
import net.dragonmounts.inventory.ISlotListener;
import net.dragonmounts.network.CDragonConfigPacket;
import net.dragonmounts.network.CRenameFlutePacket;
import net.dragonmounts.network.DragonStates;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @see net.minecraft.client.gui.GuiRepair
 */
@SideOnly(Side.CLIENT)
public class DragonInventoryGui extends GuiContainer implements ISlotListener<FluteSlot> {
    /// 176 x 214
    private static final ResourceLocation INVENTORY = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/dragon_inventory.png");
    /// 147 x 214
    private static final ResourceLocation PANEL = new ResourceLocation(DragonMountsTags.MOD_ID, "textures/gui/dragon_panel.png");
    private final DragonContainer<ClientDragonEntity> container;
    private final ClientDragonEntity dragon;
    private final EntityPlayer player;
    private GuiTextField nameField;
    private LockButton lock;
    private GuiButton order;
    private boolean chested;
    private String name;
    private String label;
    private String health;
    private String armor;
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
        this.ySize = 224;
        this.xSize = 324;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int x = this.guiLeft + 10, y = this.guiTop;
        GuiTextField name = this.nameField = new GuiTextField(31, this.fontRenderer, x + 22, y + 12, 104, 12);
        name.setTextColor(-1);
        name.setDisabledTextColour(-1);
        name.setEnableBackgroundDrawing(false);
        name.setMaxStringLength(35);
        this.buttonList.clear();
        this.stand = ClientUtil.translateToLocal("gui.dragonmounts.stand");
        this.buttonList.add(this.order = new GuiButton(DragonStates.SITTING_STATE, x, y + 172, 18, 20, this.sit = ClientUtil.translateToLocal("gui.dragonmounts.sit")));
        this.buttonList.add(this.lock = new LockButton(DragonStates.LOCKED_STATE, x, y + 194, 18, 20));
        this.updateScreen();
        FluteSlot slot = this.container.flute;
        slot.listener = this;
        ItemStack flute = slot.getStack();
        boolean enabled = !flute.isEmpty();
        name.setText(slot.desiredName = enabled ? flute.getDisplayName() : "");
        name.setEnabled(enabled);
    }

    @Override
    public void updateScreen() {
        this.label = this.player.inventory.getDisplayName().getUnformattedText();
        TameableDragonEntity dragon = this.dragon;
        this.lock.enabled = dragon.isOwner(this.player);
        boolean trustOther = dragon.allowedOtherPlayers();
        this.lock.iconTop = trustOther
                ? 32
                : this.lock.enabled
                ? 16
                : 0;
        this.tip = ClientUtil.translateBothToLocal("gui.dragonmounts.lock.tooltip", trustOther ? "options.on" : "options.off");
        this.name = dragon.getName();
        this.chested = dragon.isChested();
        this.health = String.format("%.2f/%.2f", dragon.getHealth(), dragon.getMaxHealth());
        this.armor = String.format("%.2f", dragon.getEntityAttribute(SharedMonsterAttributes.ARMOR).getAttributeValue());
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
            case FLEDGLING:
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

    private void renameflute() {
        ItemStack stack = this.container.flute.getStack();
        String text = this.nameField.getText();
        if (!stack.isEmpty() && !stack.hasDisplayName() && text.equals(stack.getDisplayName())) {
            text = "";
        }
        this.container.flute.applyName(text);
        DragonMounts.NETWORK_WRAPPER.sendToServer(new CRenameFlutePacket(text));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        FontRenderer font = this.fontRenderer;
        font.drawString(this.name, 156, 6, this.color);
        font.drawString(this.label, 156, this.ySize - 93, 0x404040);
        font.drawString(this.armor, 20, 33, 0xE99E0C);
        font.drawString(this.health, 20, 44, 0xE99E0C);
        font.drawString(this.hunger, 20, 55, 0xE99E0C);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager manager = this.mc.getTextureManager();
        manager.bindTexture(INVENTORY);
        int x = this.guiLeft, y = this.guiTop;
        int invStart = x + 148;
        this.drawTexturedModalRect(invStart, y, 0, 0, 176, this.ySize);
        if (this.chested) {
            this.drawTexturedModalRect(invStart, y + 73, 0, 140, 170, 55);
        }
        manager.bindTexture(PANEL);
        this.drawTexturedModalRect(x, y, 0, 0, 147, this.ySize);
        if (this.container.flute.getHasStack()) {
            this.drawTexturedModalRect(x + 29, y + 8, 0, this.ySize, 111, 16);
        }
        x += 10;
        // armor
        this.drawTexturedModalRect(x, y + 32, 147, 57, 9, 9);
        // health
        this.drawTexturedModalRect(x, y + 43, 147, 48, 9, 9);
        // hunger
        this.drawTexturedModalRect(x, y + 54, 147, 66, 9, 9);
        // dragon entity
        this.dragon.isInGui = true;
        GuiInventory.drawEntityOnScreen(invStart + 80, y + 60, this.size, invStart + 80 - mouseX, y + 28 - mouseY, this.dragon);
        this.dragon.isInGui = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        int id = button.id;
        switch (button.id) {
            case DragonStates.SITTING_STATE:
            case DragonStates.LOCKED_STATE:
                DragonMounts.NETWORK_WRAPPER.sendToServer(new CDragonConfigPacket(this.dragon.getEntityId(), button.id));
                break;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.container.flute.getHasStack() && this.nameField.textboxKeyTyped(typedChar, keyCode)) {
            this.renameflute();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (this.container.flute.getHasStack()) {
            this.nameField.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.container.flute.getHasStack()) {
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            this.nameField.drawTextBox();
        }
        this.renderHoveredToolTip(mouseX, mouseY);
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
    public void beforePlaceItem(FluteSlot slot, ItemStack stack) {
        if (stack.isEmpty()) {
            this.nameField.setEnabled(false);
            this.nameField.setText(slot.desiredName = "");
        } else {
            this.nameField.setEnabled(true);
            String name = stack.getDisplayName();
            if (!name.equals(slot.getStack().getDisplayName())) {
                this.nameField.setText(slot.desiredName = name);
            }
        }
    }

    @Override
    public void afterTakeItem(FluteSlot slot, ItemStack stack) {
        this.nameField.setText("");
        this.nameField.setEnabled(false);
    }

    static class LockButton extends GuiButton {
        public int iconTop;

        public LockButton(int buttonId, int x, int y, int width, int height) {
            super(buttonId, x, y, width, height, "");
        }

        public boolean isHovered() {
            return this.hovered;
        }

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
                mc.getTextureManager().bindTexture(PANEL);
                this.drawTexturedModalRect(x + 0.5F, y + 2, 147, this.iconTop, 16, 16);
            } else {
                this.hovered = false;
            }
        }
    }
}