package net.dragonmounts.util;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;

import javax.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.Iterator;

public class ArmorSuit<T extends ItemArmor> extends AbstractCollection<T> {
    public final T helmet;
    public final T chestplate;
    public final T leggings;
    public final T boots;

    public ArmorSuit(T helmet, T chestplate, T leggings, T boots) {
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public final T bySlot(@Nonnull EntityEquipmentSlot slot) {
        switch (slot.getSlotIndex()) {
            case 4:
                return this.helmet;
            case 3:
                return this.chestplate;
            case 2:
                return this.leggings;
            case 1:
                return this.boots;
            default:
                return null;
        }
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return new ObjectArrayIterator<>(this.helmet, this.chestplate, this.leggings, this.boots);
    }

    @Override
    public final int size() {
        return 4;
    }
}
