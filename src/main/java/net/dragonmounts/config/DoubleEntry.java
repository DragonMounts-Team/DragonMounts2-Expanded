package net.dragonmounts.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

import static net.minecraftforge.common.config.Property.Type.DOUBLE;

public class DoubleEntry implements IConfigEntry {
    public final String key;
    public final double fallback;
    public double value;

    public DoubleEntry(String key, double fallback) {
        this.key = key;
        this.value = this.fallback = fallback;
    }

    @Override
    public Property getOrCreate(ConfigCategory category) {
        String value = Double.toString(this.fallback);
        Property prop;
        if (category.containsKey(this.key)) {
            prop = category.get(this.key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), DOUBLE);
                category.put(this.key, prop);
            }
            if (!prop.isDoubleValue()) {
                prop.setValue(value);
            }
        } else {
            prop = new Property(this.key, value, DOUBLE);
            prop.setValue(value); //Set and mark as dirty to signify it should save
            category.put(this.key, prop);
        }
        return prop.setDefaultValue(value);
    }
}
