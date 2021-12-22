package me.buzz.coralmc.oneblockcore.structures.maps;

import java.util.HashMap;

public class StringMap<V> extends HashMap<String, V> {

    @Override
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public V get(Object key) {
        return super.get(((String) key).toLowerCase());
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(((String) key).toLowerCase());
    }

    @Override
    public V remove(Object key) {
        return super.remove(((String) key).toLowerCase());
    }


}
