package net.dragonmounts.entity.behavior;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.sound.SoundEffectName;
import net.dragonmounts.entity.breath.sound.SoundState;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.registry.DragonType;

public class ForestBehavior implements DragonType.Behavior {
    @Override
    public void tick(TameableDragonEntity dragon) {
        /*Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(dragon.world.getBiome(dragon.getPosition()));
        if (types.contains(BiomeDictionary.Type.SAVANNA) || types.contains(BiomeDictionary.Type.DRY) || types.contains(BiomeDictionary.Type.MESA) || types.contains(BiomeDictionary.Type.SANDY)) {
            dragon.setForestType(DragonBreedForest.SubType.DRY);
        } else if (types.contains(BiomeDictionary.Type.COLD) || types.contains(BiomeDictionary.Type.MOUNTAIN)) {
            dragon.setForestType(DragonBreedForest.SubType.TAIGA);
        } else {
            dragon.setForestType(DragonBreedForest.SubType.FOREST);
        }*/
    }

    @Override
    public SoundEffectName getBreathSound(DragonLifeStage stage, SoundState state) {
        return state.forest;
    }
}
