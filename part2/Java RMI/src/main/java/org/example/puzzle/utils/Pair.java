package org.example.puzzle.utils;

import java.io.Serializable;

public class Pair<K, V> implements Serializable {

    private final K a;
    private final V b;

    public static <K, V> Pair<K, V> createPair(K a, V b) {
        return new Pair<K, V>(a, b);
    }

    public Pair(K a, V b) {
        this.a = a;
        this.b = b;
    }

    public K getA() {
        return a;
    }

    public V getB() {
        return b;
    }

    public boolean isEmpty() {
        return a == null && b == null;
    }

}
