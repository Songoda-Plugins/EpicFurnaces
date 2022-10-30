package com.songoda.epicfurnaces.storage.types;

import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.storage.Storage;
import com.songoda.epicfurnaces.storage.StorageItem;
import com.songoda.epicfurnaces.storage.StorageRow;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StorageYaml extends Storage {

    private final Map<String, Object> toSave = new HashMap<>();
    private Map<String, Object> lastSave = null;

    public StorageYaml(EpicFurnaces plugin) {
        super(plugin);
    }

    @Override
    public boolean containsGroup(String group) {
        return dataFile.contains("data." + group);
    }

    @Override
    public List<StorageRow> getRowsByGroup(String group) {
        List<StorageRow> rows = new ArrayList<>();
        ConfigurationSection currentSection = dataFile.getConfigurationSection("data." + group);
        for (String key : currentSection.getKeys(false)) {

            Map<String, StorageItem> items = new HashMap<>();
            ConfigurationSection currentSection2 = dataFile.getConfigurationSection("data." + group + "." + key);
            for (String key2 : currentSection2.getKeys(false)) {
                String path = "data." + group + "." + key + "." + key2;
                items.put(key2, new StorageItem(dataFile.get(path) instanceof MemorySection
                        ? convertToInLineList(path) : dataFile.get(path)));
            }
            if (items.isEmpty()) continue;
            StorageRow row = new StorageRow(key, items);
            rows.add(row);
        }
        return rows;
    }

    private String convertToInLineList(String path) {
        StringBuilder converted = new StringBuilder();
        for (String key : dataFile.getConfigurationSection(path).getKeys(false)) {
            converted.append(key).append(":").append(dataFile.getInt(path + "." + key)).append(";");
        }
        return converted.toString();
    }

    @Override
    public void prepareSaveItem(String group, StorageItem... items) {
        for (StorageItem item : items) {
            if (item == null || item.asObject() == null) continue;
            toSave.put("data." + group + "." + items[0].asString() + "." + item.getKey(), item.asObject());
        }
    }

    @Override
    public void doSave() {
        this.updateData();

        if (lastSave == null)
            lastSave = new HashMap<>(toSave);

        if (toSave.isEmpty()) return;
        Map<String, Object> nextSave = new HashMap<>(toSave);

        this.makeBackup();
        this.save();

        toSave.clear();
        lastSave.clear();
        lastSave.putAll(nextSave);
    }

    @Override
    public void save() {
        try {
            for (Map.Entry<String, Object> entry : lastSave.entrySet()) {
                final String key = entry.getKey();
                if (toSave.containsKey(key)) {
                    Object newValue = toSave.get(key);
                    if (!entry.getValue().equals(newValue)) {
                        dataFile.set(key, newValue);
                    }
                    toSave.remove(key);
                } else {
                    dataFile.set(key, null);
                }
            }

            for (Map.Entry<String, Object> entry : toSave.entrySet()) {
                dataFile.set(entry.getKey(), entry.getValue());
            }

            dataFile.save();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void makeBackup() {
    }

    @Override
    public void closeConnection() {
        dataFile.save();
    }

}
