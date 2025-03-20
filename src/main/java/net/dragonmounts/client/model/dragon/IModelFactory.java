package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;

public interface IModelFactory {
    default TailPart makeTail(ModelBase base) {
        return new TailPart(base, "tail");
    }

    default NeckPart makeNeck(ModelBase base) {
        return new NeckPart(base, "neck");
    }

    LegPart makeForeLeg(ModelBase base);

    LegPart makeHindLeg(ModelBase base);
}
