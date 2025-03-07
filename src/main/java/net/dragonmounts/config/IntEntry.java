package net.dragonmounts.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

import static net.minecraftforge.common.config.Property.Type.INTEGER;

public class IntEntry implements IConfigEntry {
    public final String key;
    public final int fallback;
    public int value;

    public IntEntry(String key, int fallback) {
        this.key = key;
        this.value = this.fallback = fallback;
    }

    @Override
    public Property getOrCreate(ConfigCategory category) {
        String value = Integer.toString(this.fallback);
        Property prop;
        if (category.containsKey(this.key)) {
            prop = category.get(this.key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), INTEGER);
                category.put(this.key, prop);
            }
            if (!prop.isIntValue()) {
                prop.setValue(value);
            }
        } else {
            prop = new Property(this.key, value, INTEGER);
            prop.setValue(value); //Set and mark as dirty to signify it should save
            category.put(this.key, prop);
        }
        return prop.setDefaultValue(value);
    }
}
