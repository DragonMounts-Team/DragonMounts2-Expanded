package net.dragonmounts.util;

import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;

public class BlockProperties {
    public CreativeTabs creativeTab;
    public SoundType sound;
    public float light;
    public int hardness;
    public int resistance;

    public BlockProperties setCreativeTab(CreativeTabs tab) {
        this.creativeTab = tab;
        return this;
    }

    public BlockProperties setSoundType(SoundType sound) {
        this.sound = sound;
        return this;
    }

    public BlockProperties setLightLevel(float light) {
        this.light = light;
        return this;
    }

    public BlockProperties setHardness(int hardness) {
        this.hardness = hardness;
        return this;
    }

    public BlockProperties setResistance(int resistance) {
        this.resistance = resistance;
        return this;
    }
}
