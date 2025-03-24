package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public interface IModelFactory {
    void defineTextures(ITextureOffsetDefiner definer);

    HeadPart makeHead(ModelBase base);

    ICachedPart makeNeck(ModelBase base);

    BodyPart makeBody(ModelBase base);

    WingPart makeWing(ModelBase base);

    ICachedPart makeTail(ModelBase base);

    LegPart makeForeLeg(ModelBase base);

    LegPart makeHindLeg(ModelBase base);

    ModelRenderer makeHorn(ModelBase base, HeadPart head, boolean mirror);

    ModelRenderer makeChest(ModelBase base);

    ModelRenderer makeSaddle(ModelBase base);
}
