package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.FireBreath;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.util.ResourceLocation;

public class ForestType extends DragonType {
    public ForestType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new FireBreath(dragon, 0.7F);
    }
}
