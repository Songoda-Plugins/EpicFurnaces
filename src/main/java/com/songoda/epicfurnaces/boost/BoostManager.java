package com.songoda.epicfurnaces.boost;

import com.songoda.epicfurnaces.EpicFurnaces;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BoostManager {
    private final EpicFurnaces plugin;

    private final Set<BoostData> registeredBoosts = new HashSet<>();

    public BoostManager(EpicFurnaces plugin) {
        this.plugin = plugin;
    }

    public void addBoostToPlayer(BoostData data) {
        this.registeredBoosts.add(data);
    }

    public void addBoosts(List<BoostData> boosts) {
        this.registeredBoosts.addAll(boosts);
    }

    public void removeBoostFromPlayer(BoostData data) {
        this.registeredBoosts.remove(data);
    }

    public Set<BoostData> getBoosts() {
        return Collections.unmodifiableSet(this.registeredBoosts);
    }

    public BoostData getBoost(UUID player) {
        if (player == null) {
            return null;
        }

        for (BoostData boostData : this.registeredBoosts) {
            if (boostData.getPlayer().toString().equals(player.toString())) {
                if (System.currentTimeMillis() >= boostData.getEndTime()) {
                    removeBoostFromPlayer(boostData);
                    this.plugin.getDataManager().delete(boostData);
                }
                return boostData;
            }
        }
        return null;
    }

}
