package net.dragonmounts.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

public interface IConfigEntry {
    Property getOrCreate(ConfigCategory category);
}
