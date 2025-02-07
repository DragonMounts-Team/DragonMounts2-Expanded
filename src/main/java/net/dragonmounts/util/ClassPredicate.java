package net.dragonmounts.util;

import com.google.common.base.Predicate;

public class ClassPredicate<T> implements Predicate<T> {
    private final Class<?>[] classes;

    public ClassPredicate(Class<?>... classes) {
        this.classes = classes;
    }

    @Override
    public boolean apply(T input) {
        if (input == null) return false;
        Class<?> clazz = input.getClass();
        for (Class<?> candidate : this.classes) {
            if (candidate.isAssignableFrom(clazz)) return true;
        }
        return false;
    }
}
