package me.buzz.coralmc.oneblockcore.common.structures.maps;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentStringMap<V> extends ConcurrentHashMap<String, V> {

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
