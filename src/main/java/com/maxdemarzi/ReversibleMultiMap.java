package com.maxdemarzi;

import com.google.common.collect.*;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

// Adapted from http://stackoverflow.com/questions/23646186/a-java-multimap-which-allows-fast-lookup-of-key-by-value

public class ReversibleMultiMap<K extends String, V extends String> implements Multimap<K, V> {

        private ListMultimap<K, V> key2Value = ArrayListMultimap.create();
        private ListMultimap<V, K> value2key = ArrayListMultimap.create();

        public Collection<K> getKeysByValue(V value) {
            return value2key.get(value);
        }

        @Override
        public int size() {
            return key2Value.size();
        }

        @Override
        public boolean isEmpty() {
            return key2Value.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return key2Value.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return key2Value.containsValue(value);
        }

        @Override
        public boolean containsEntry(Object key, Object value) {
            return key2Value.containsEntry(key, value);
        }

        @Override
        public boolean put(K key, V value) {
            Multimaps.synchronizedListMultimap(value2key).put(value, key);
            return Multimaps.synchronizedListMultimap(key2Value).put(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            Multimaps.synchronizedListMultimap(value2key).remove(value, key);
            return Multimaps.synchronizedListMultimap(key2Value).remove(key, value);
        }

        @Override
        public boolean putAll(K key, Iterable<? extends V> values) {
            for (V value : values) {
                Multimaps.synchronizedListMultimap(value2key).put(value, key);
            }
            return Multimaps.synchronizedListMultimap(key2Value).putAll(key, values);
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            for (Entry<? extends K, ? extends V> e : multimap.entries()) {
                Multimaps.synchronizedListMultimap(value2key).put(e.getValue(), e.getKey());
            }
            return Multimaps.synchronizedListMultimap(key2Value).putAll(multimap);
        }

        @Override
        public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
            Collection<V> replaced = Multimaps.synchronizedListMultimap(key2Value).replaceValues(key, values);
            for (V value : replaced) {
                Multimaps.synchronizedListMultimap(value2key).remove(value, key);
            }
            for (V value : values) {
                Multimaps.synchronizedListMultimap(value2key).put(value, key);
            }
            return replaced;
        }

        @Override
        public Collection<V> removeAll(Object key) {
            Collection<V> removed = Multimaps.synchronizedListMultimap(key2Value).removeAll(key);
            for (V value : removed) {
                Multimaps.synchronizedListMultimap(value2key).remove(value, key);
            }
            for (K reverse : Multimaps.synchronizedListMultimap(value2key).removeAll(key)){
                Multimaps.synchronizedListMultimap(key2Value).remove(reverse, key);
            }
            return removed;
        }

        @Override
        public void clear() {
            Multimaps.synchronizedListMultimap(value2key).clear();
            Multimaps.synchronizedListMultimap(key2Value).clear();
        }

        @Override
        public Collection<V> get(K key) {
            return key2Value.get(key);
        }

        @Override
        public Set<K> keySet() {
            return key2Value.keySet();
        }

        @Override
        public Multiset<K> keys() {
            return key2Value.keys();
        }

        @Override
        public Collection<V> values() {
            return key2Value.values();
        }

        @Override
        public Collection<Entry<K, V>> entries() {
            return key2Value.entries();
        }

        @Override
        public Map<K, Collection<V>> asMap() {
            return key2Value.asMap();
        }
}
