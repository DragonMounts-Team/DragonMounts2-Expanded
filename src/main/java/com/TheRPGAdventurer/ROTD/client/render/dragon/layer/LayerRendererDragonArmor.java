package com.TheRPGAdventurer.ROTD.client.render.dragon.layer;

import com.TheRPGAdventurer.ROTD.DragonMountsTags;
import com.TheRPGAdventurer.ROTD.client.model.dragon.DragonModel;
import com.TheRPGAdventurer.ROTD.client.render.dragon.DragonRenderer;
import com.TheRPGAdventurer.ROTD.client.render.dragon.breeds.DefaultDragonBreedRenderer;
import com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.minecraft.util.ResourceLocation;

/**
 * Created by TheRPGAdventurer based on ice and fire code5.
 */
public class LayerRendererDragonArmor extends LayerRendererDragon {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/armor/armor_iron.png"),
            new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/armor/armor_gold.png"),
            new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/armor/armor_diamond.png"),
            new ResourceLocation(DragonMountsTags.MOD_ID, "textures/entities/armor/armor_emerald.png")
    };

    public LayerRendererDragonArmor(DragonRenderer renderer, DefaultDragonBreedRenderer breedRenderer, DragonModel model) {
        super(renderer, breedRenderer, model);
    }
    
    @Override
    public void doRenderLayer(EntityTameableDragon dragon, float moveTime, float moveSpeed, float partialTicks, float ticksExisted, float lookYaw, float lookPitch, float scale) {
        int armor = dragon.getArmorType();
        if (armor < 1 || armor > 4) return;
        this.renderer.bindTexture(TEXTURES[armor - 1]);
        this.model.render(dragon, moveTime, moveSpeed, ticksExisted, lookYaw, lookPitch, scale);
	}

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
