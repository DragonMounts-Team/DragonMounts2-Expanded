package net.dragonmounts.client.render.dragon.layer;

import net.dragonmounts.client.model.dragon.DragonModel;
import net.dragonmounts.client.render.dragon.DragonRenderer;
import net.dragonmounts.client.render.dragon.breeds.DefaultDragonBreedRenderer;
import net.dragonmounts.item.DragonArmorItem;
import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by TheRPGAdventurer based on ice and fire code5.
 */
public class LayerRendererDragonArmor extends LayerRendererDragon {
    public LayerRendererDragonArmor(DragonRenderer renderer, DefaultDragonBreedRenderer breedRenderer, DragonModel model) {
        super(renderer, breedRenderer, model);
    }
    
    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        ItemStack stack = dragon.getArmor();
        if (stack.isEmpty()) return;
        Item item = stack.getItem();
        if (item instanceof DragonArmorItem) {
            this.renderer.bindTexture(((DragonArmorItem) item).texture);
            this.model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
        }
	}

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
