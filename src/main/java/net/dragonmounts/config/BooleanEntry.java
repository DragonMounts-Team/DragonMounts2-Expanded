package net.dragonmounts.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

import static net.minecraftforge.common.config.Property.Type.BOOLEAN;

public class BooleanEntry implements IConfigEntry {
    public final String key;
    public final boolean fallback;
    public boolean value;

    public BooleanEntry(String key, boolean fallback) {
        this.key = key;
        this.value = this.fallback = fallback;
    }

    @Override
    public Property getOrCreate(ConfigCategory category) {
        String value = Boolean.toString(this.fallback);
        Property prop;
        if (category.containsKey(this.key)) {
            prop = category.get(this.key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), BOOLEAN);
                category.put(this.key, prop);
            }
            if (!prop.isBooleanValue()) {
                prop.setValue(value);
            }
        } else {
            prop = new Property(this.key, value, BOOLEAN);
            prop.setValue(value); //Set and mark as dirty to signify it should save
            category.put(this.key, prop);
        }
        return prop.setDefaultValue(value);
    }
}
