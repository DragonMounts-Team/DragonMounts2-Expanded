package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.render.dragon.DragonRenderMode;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.item.DragonArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by TheRPGAdventurer based on ice and fire code5.
 */
public class LayerRendererDragonArmor extends DragonLayerRenderer {
    @Override
    public void doRenderLayer(TameableDragonEntity dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        ItemStack stack = dragon.getArmor();
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        if (item instanceof DragonArmorItem) {
            this.renderer.bindTexture(((DragonArmorItem) item).texture);
            this.model.render(DragonRenderMode.FULL, dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
	}

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
