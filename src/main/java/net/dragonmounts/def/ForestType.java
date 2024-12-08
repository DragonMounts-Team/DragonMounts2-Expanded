package net.dragonmounts.def;

import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;
import net.minecraft.util.ResourceLocation;

public class ForestType extends DragonType {
    public ForestType(ResourceLocation identifier, Properties props) {
        super(identifier, props);
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.forest;
    }
}
