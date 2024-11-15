package top.dragonmounts.objects.entity.entitytameabledragon.breeds;

import top.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundEffectName;
import top.dragonmounts.objects.entity.entitytameabledragon.breath.sound.SoundState;
import top.dragonmounts.objects.entity.entitytameabledragon.helper.DragonLifeStage;
import top.dragonmounts.objects.items.EnumItemBreedTypes;
import top.dragonmounts.util.EnumSerializer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Set;

public class DragonBreedForest extends DragonBreed {

    DragonBreedForest() {
        super("forest", 0x298317);

        setImmunity(DamageSource.MAGIC);
        setImmunity(DamageSource.HOT_FLOOR);
        setImmunity(DamageSource.LIGHTNING_BOLT);
        setImmunity(DamageSource.WITHER);

        setHabitatBlock(Blocks.YELLOW_FLOWER);
        setHabitatBlock(Blocks.RED_FLOWER);
        setHabitatBlock(Blocks.MOSSY_COBBLESTONE);
        setHabitatBlock(Blocks.VINE);
        setHabitatBlock(Blocks.SAPLING);
        setHabitatBlock(Blocks.LEAVES);
        setHabitatBlock(Blocks.LEAVES2);

        setHabitatBiome(Biomes.JUNGLE);
        setHabitatBiome(Biomes.JUNGLE_HILLS);

    }

    @Override
    public void onEnable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDisable(EntityTameableDragon dragon) {
    }

    @Override
    public void onDeath(EntityTameableDragon dragon) {
    }

    @Override
    public void onLivingUpdate(EntityTameableDragon dragon) {
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(dragon.world.getBiome(dragon.getPosition()));
        if (types.contains(BiomeDictionary.Type.SAVANNA) || types.contains(BiomeDictionary.Type.DRY) || types.contains(BiomeDictionary.Type.MESA) || types.contains(BiomeDictionary.Type.SANDY)) {
            dragon.setForestType(SubType.DRY);
        } else if (types.contains(BiomeDictionary.Type.COLD) || types.contains(BiomeDictionary.Type.MOUNTAIN)) {
            dragon.setForestType(SubType.TAIGA);
        } else {
            dragon.setForestType(SubType.FOREST);
        }
    }

    @Override
    public SoundEffectName getBreathWeaponSoundEffect(DragonLifeStage stage, SoundState state) {
        return state.forest;
    }

    @Override
    public EnumItemBreedTypes getItemBreed(EntityTameableDragon dragon) {
        return EnumItemBreedTypes.FOREST;
    }

    public enum SubType implements IStringSerializable {
        NONE,
        FOREST,
        TAIGA,
        DRY;
        public static final EnumSerializer<SubType> SERIALIZER = new EnumSerializer<>(SubType.class, SubType.NONE);

        public final String identifier = this.name().toLowerCase();

        @Override
        public String getName() {
            return this.identifier;
        }
    }
}
	
