package com.iafenvoy.wamt.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class CopyOnWriteHashMap<K, V> implements Map<K, V> {
    private volatile Map<K, V> internalMap;
    private final ReentrantLock lock = new ReentrantLock();

    public CopyOnWriteHashMap() {
        this.internalMap = new HashMap<>();
    }

    public CopyOnWriteHashMap(Map<? extends K, ? extends V> m) {
        this.internalMap = new HashMap<>(m);
    }

    @Override
    public V get(Object key) {
        return this.internalMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        this.lock.lock();
        try {
            Map<K, V> newMap = new HashMap<>(this.internalMap);
            V oldValue = newMap.put(key, value);
            this.internalMap = newMap;
            return oldValue;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        this.lock.lock();
        try {
            Map<K, V> newMap = new HashMap<>(this.internalMap);
            newMap.putAll(m);
            this.internalMap = newMap;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        this.lock.lock();
        try {
            Map<K, V> newMap = new HashMap<>(this.internalMap);
            V oldValue = newMap.remove(key);
            this.internalMap = newMap;
            return oldValue;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.internalMap = new HashMap<>();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return this.internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.internalMap.containsValue(value);
    }

    @Override
    public int size() {
        return this.internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    @Override
    public @NotNull Set<K> keySet() {
        return Collections.unmodifiableSet(this.internalMap.keySet());
    }

    @Override
    public @NotNull Collection<V> values() {
        return Collections.unmodifiableCollection(this.internalMap.values());
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(this.internalMap.entrySet());
    }

    @Override
    public V putIfAbsent(K key, V value) {
        this.lock.lock();
        try {
            if (!this.internalMap.containsKey(key)) {
                return this.put(key, value);
            } else {
                return this.internalMap.get(key);
            }
        } finally {
            this.lock.unlock();
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean remove(Object key, Object value) {
        this.lock.lock();
        try {
            if (this.internalMap.containsKey(key) && Objects.equals(this.internalMap.get(key), value)) {
                this.remove(key);
                return true;
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        this.lock.lock();
        try {
            if (this.internalMap.containsKey(key) && Objects.equals(this.internalMap.get(key), oldValue)) {
                this.put(key, newValue);
                return true;
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        this.lock.lock();
        try {
            if (this.internalMap.containsKey(key)) {
                return this.put(key, value);
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String toString() {
        return this.internalMap.toString();
    }
}
