package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;

public interface IModelFactory {
    LegPart makeForeLeg(ModelBase base);

    LegPart makeHindLeg(ModelBase base);
}
