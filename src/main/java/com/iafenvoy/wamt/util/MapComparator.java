package com.iafenvoy.wamt.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapComparator {
    public static <K, V> List<K> findUnchangedKeys(Map<K, V> originalMap, Map<K, V> modifiedMap) {
        List<K> unchangedKeys = new LinkedList<>();
        for (Map.Entry<K, V> entry : originalMap.entrySet()) {
            K key = entry.getKey();
            if (entry.getValue().equals(modifiedMap.get(key))) unchangedKeys.add(key);
        }
        return unchangedKeys;
    }
}
