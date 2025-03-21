package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public interface IModelFactory {
    void defineTextures(ITextureOffsetDefiner definer);

    HeadPart makeHead(ModelBase base);

    NeckPart makeNeck(ModelBase base);

    TailPart makeTail(ModelBase base);

    LegPart makeForeLeg(ModelBase base);

    LegPart makeHindLeg(ModelBase base);

    ModelRenderer makeHorn(ModelBase base, HeadPart head, boolean mirror);
}
