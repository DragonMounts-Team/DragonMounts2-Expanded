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
        for (Class<?> candidate : this.classes) {
            if (candidate.isInstance(input)) return true;
        }
        return false;
    }
}
