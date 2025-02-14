package net.dragonmounts.config;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

import static net.minecraftforge.common.config.Property.Type.INTEGER;

public class IntSetEntry implements IConfigEntry {
    private final int[] fallback;
    public final String key;
    public IntSet value;

    public IntSetEntry(String key, int... fallback) {
        this.key = key;
        this.fallback = fallback;
        this.value = new IntOpenHashSet(fallback);
    }

    @Override
    public Property getOrCreate(ConfigCategory category) {
        int len = this.fallback.length;
        String[] values = new String[this.fallback.length];
        for (int i = 0; i < len; ++i) {
            values[i] = Integer.toString(this.fallback[i]);
        }
        Property prop;
        if (category.containsKey(this.key)) {
            prop = category.get(this.key);
            if (prop.getType() == null) {
                prop = new Property(prop.getName(), prop.getString(), INTEGER);
                category.put(this.key, prop);
            }
            if (!prop.isIntList()) {
                prop.setValues(values);
            }
        } else {
            prop = new Property(this.key, values, INTEGER);
            prop.setValues(values); //Set and mark as dirty to signify it should save
            category.put(this.key, prop);
        }
        return prop.setDefaultValues(values);
    }
}
