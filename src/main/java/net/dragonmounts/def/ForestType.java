package net.dragonmounts.def;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.FireBreath;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.ResourceLocation;

public class ForestType extends DragonType {
    public ForestType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new FireBreath(dragon, 0.7F);
    }
}
