package net.dragonmounts.util;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

public class ArmorSuit<T extends ItemArmor> implements Collection<T> {
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
    public Object[] toArray() {
        return new Object[]{this.helmet, this.chestplate, this.leggings, this.boots};
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(@Nonnull E[] a) {
        return (E[]) this.toArray();// it is safe to be cast, as a result of type erasure
    }

    @Override
    public boolean add(T t) {
        return false;
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

    @Override
    public final boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return this.helmet == o || this.chestplate == o || this.leggings == o || this.boots == o;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!this.contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
}
