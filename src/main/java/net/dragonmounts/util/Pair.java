package net.dragonmounts.util;

/**
 * User: The Grey Ghost
 * Date: 12/07/2014
 * very simple class to group two objects into a pair
 */
public final class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }
}