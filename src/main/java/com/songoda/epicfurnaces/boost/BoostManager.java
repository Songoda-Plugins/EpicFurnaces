package com.songoda.epicfurnaces.boost;

import com.songoda.epicfurnaces.EpicFurnaces;

import java.util.*;

public class BoostManager {

    private final Set<BoostData> registeredBoosts = new HashSet<>();

    public void addBoostToPlayer(BoostData data) {
        this.registeredBoosts.add(data);
    }

    public void addBoosts(List<BoostData> boosts) {
        registeredBoosts.addAll(boosts);
    }

    public void removeBoostFromPlayer(BoostData data) {
        this.registeredBoosts.remove(data);
    }

    public Set<BoostData> getBoosts() {
        return Collections.unmodifiableSet(registeredBoosts);
    }

    public BoostData getBoost(UUID player) {
        if (player == null) return null;
        for (BoostData boostData : registeredBoosts) {
            if (boostData.getPlayer().toString().equals(player.toString())) {
                if (System.currentTimeMillis() >= boostData.getEndTime()) {
                    removeBoostFromPlayer(boostData);
                    EpicFurnaces.getInstance().getDataManager().deleteBoost(boostData);
                }
                return boostData;
            }
        }
        return null;
    }

}
