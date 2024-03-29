package me.buzz.coralmc.oneblockcore.structures;

import java.util.Map;

public final class Pair<K, V> implements Map.Entry<K, V> {

    private final K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Pair{" + key + "=" + value + '}';
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }

}
