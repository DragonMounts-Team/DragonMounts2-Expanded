package net.dragonmounts.type;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.DragonBreath;
import net.dragonmounts.entity.breath.impl.DarkBreath;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class DarkType extends DragonType {
    public DarkType(ResourceLocation identifier, DragonTypeBuilder builder) {
        super(identifier, builder);
    }

    @Override
    public DragonBreath initBreath(TameableDragonEntity dragon) {
        return new DarkBreath(dragon, 0.6F);
    }

    @Override
    public boolean isInHabitat(Entity egg) {
        return egg.posY > egg.world.getHeight() * 0.66;
    }
}
