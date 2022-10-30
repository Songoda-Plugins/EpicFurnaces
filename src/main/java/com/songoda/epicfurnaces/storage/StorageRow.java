package com.songoda.epicfurnaces.storage;

import java.util.Collections;
import java.util.Map;

public final class StorageRow {
    private final String key;

    private final Map<String, StorageItem> items;

    public StorageRow(String key, Map<String, StorageItem> items) {
        this.key = key;
        this.items = items;
    }

    public String getKey() {
        return key;
    }

    public Map<String, StorageItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public StorageItem get(String key) {
        if (!items.containsKey(key) || items.get(key).asObject().toString().equals("")) return new StorageItem(null);
        return items.get(key);
    }
}
