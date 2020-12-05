package com.songoda.epicfurnaces.database;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.database.DataManagerAbstract;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.epicfurnaces.EpicFurnaces;
import com.songoda.epicfurnaces.boost.BoostData;
import com.songoda.epicfurnaces.furnace.Furnace;
import com.songoda.epicfurnaces.furnace.FurnaceBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;

public class DataManager extends DataManagerAbstract {

    public DataManager(DatabaseConnector connector, Plugin plugin) {
        super(connector, plugin);
    }

    public void createBoost(BoostData boostData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createBoostedPlayer = "INSERT INTO " + this.getTablePrefix() + "boosted_players (player, multiplier, end_time) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createBoostedPlayer)) {
                statement.setString(1, boostData.getPlayer().toString());
                statement.setInt(2, boostData.getMultiplier());
                statement.setLong(3, boostData.getEndTime());
                statement.executeUpdate();
            }
        }));
    }

    public void getBoosts(Consumer<List<BoostData>> callback) {
        List<BoostData> boosts = new ArrayList<>();
        this.async(() -> this.databaseConnector.connect(connection -> {
            try (Statement statement = connection.createStatement()) {
                String selectBoostedPlayers = "SELECT * FROM " + this.getTablePrefix() + "boosted_players";
                ResultSet result = statement.executeQuery(selectBoostedPlayers);
                while (result.next()) {
                    UUID player = UUID.fromString(result.getString("player"));
                    int multiplier = result.getInt("multiplier");
                    long endTime = result.getLong("end_time");
                    boosts.add(new BoostData(multiplier, endTime, player));
                }
            }

            this.sync(() -> callback.accept(boosts));
        }));
    }

    public void deleteBoost(BoostData boostData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteBoost = "DELETE FROM " + this.getTablePrefix() + "boosted_players WHERE end_time = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteBoost)) {
                statement.setLong(1, boostData.getEndTime());
                statement.executeUpdate();
            }
        }));
    }

    public void createFurnaces(List<Furnace> furnaces) {
        for (Furnace furnace : furnaces) {
            createFurnace(furnace);
        }
    }

    public void createFurnace(Furnace furnace) {
        this.queueAsync(() -> this.databaseConnector.connect(connection -> {
            String createFurnace = "INSERT INTO " + this.getTablePrefix() + "active_furnaces (level, uses, nickname, placed_by, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createFurnace)) {
                statement.setInt(1, furnace.getLevel().getLevel());
                statement.setInt(2, furnace.getUses());
                statement.setString(3, furnace.getNickname());
                statement.setString(4,
                        furnace.getPlacedBy() == null ? null : furnace.getPlacedBy().toString());

                statement.setString(5, furnace.getLocation().getWorld().getName());
                statement.setInt(6, furnace.getLocation().getBlockX());
                statement.setInt(7, furnace.getLocation().getBlockY());
                statement.setInt(8, furnace.getLocation().getBlockZ());

                statement.executeUpdate();
            }

            int furnaceId = this.lastInsertedId(connection, "active_furnaces");
            furnace.setId(furnaceId);

            String createAccessList = "INSERT INTO " + this.getTablePrefix() + "access_list (furnace_id, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createAccessList)) {
                for (UUID uuid : furnace.getAccessList()) {
                    statement.setInt(1, furnace.getId());
                    statement.setString(2, uuid.toString());
                    statement.addBatch();
                }
                statement.executeBatch();
            }

            String createNewLevel = "INSERT INTO " + this.getTablePrefix() + "to_level_new (furnace_id, item, amount) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createNewLevel)) {
                for (Map.Entry<CompatibleMaterial, Integer> entry : furnace.getToLevel().entrySet()) {
                    statement.setInt(1, furnace.getId());
                    statement.setString(2, entry.getKey().name());
                    statement.setInt(3, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }), "create");
    }

    public void updateFurnace(Furnace furnace) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateHopper = "UPDATE " + this.getTablePrefix() + "active_furnaces SET level = ?, nickname = ?, uses = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateHopper)) {
                statement.setInt(1, furnace.getLevel().getLevel());
                statement.setString(2, furnace.getNickname());
                statement.setInt(3, furnace.getUses());
                statement.setInt(4, furnace.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void deleteFurnace(Furnace furnace) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteFurnace = "DELETE FROM " + this.getTablePrefix() + "active_furnaces WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteFurnace)) {
                statement.setInt(1, furnace.getId());
                statement.executeUpdate();
            }

            String deleteAccessList = "DELETE FROM " + this.getTablePrefix() + "access_list WHERE furnace_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteAccessList)) {
                statement.setInt(1, furnace.getId());
                statement.executeUpdate();
            }

            String deleteLevelupItems = "DELETE FROM " + this.getTablePrefix() + "to_level_new WHERE furnace_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteLevelupItems)) {
                statement.setInt(1, furnace.getId());
                statement.executeUpdate();
            }
        }));
    }

    public void createAccessPlayer(Furnace furnace, UUID uuid) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createAccessPlayer = "INSERT INTO " + this.getTablePrefix() + "access_list (furnace_id, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createAccessPlayer)) {
                statement.setInt(1, furnace.getId());
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        }));
    }

    // These will be used in the future when the access list gets revamped.
    // Probably by me since I already have a custom version in my server.
    public void deleteAccessPlayer(Furnace furnace, UUID uuid) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteAccessPlayer = "DELETE FROM " + this.getTablePrefix() + "access_list WHERE furnace_id = ? AND uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteAccessPlayer)) {
                statement.setInt(1, furnace.getId());
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        }));
    }

    public void updateAccessPlayers(Furnace furnace) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deletePlayers = "DELETE FROM " + this.getTablePrefix() + "access_list WHERE furnace_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(deletePlayers)) {
                statement.setInt(1, furnace.getId());
                statement.executeUpdate();
            }

            String createAccessPlayer = "INSERT INTO " + this.getTablePrefix() + "access_list (furnace_id, uuid) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createAccessPlayer)) {
                for (UUID uuid : furnace.getAccessList()) {
                    statement.setInt(1, furnace.getId());
                    statement.setString(2, uuid.toString());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        }));
    }

    public void updateLevelupItems(Furnace furnace, CompatibleMaterial material, int amount) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteLevelupItem = "DELETE FROM " + this.getTablePrefix() + "to_level_new WHERE furnace_id = ? AND item = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteLevelupItem)) {
                statement.setInt(1, furnace.getId());
                statement.setString(2, material.name());
                statement.executeUpdate();
            }

            String createLevelupItem = "INSERT INTO " + this.getTablePrefix() + "to_level_new (furnace_id, item, amount) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createLevelupItem)) {
                statement.setInt(1, furnace.getId());
                statement.setString(2, material.name());
                statement.setInt(3, amount);
                statement.executeUpdate();
            }
        }));
    }

    public void getFurnaces(Consumer<Map<Integer, Furnace>> callback) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            Map<Integer, Furnace> furnaces = new HashMap<>();

            try (Statement statement = connection.createStatement()) {
                String selectFurnaces = "SELECT * FROM " + this.getTablePrefix() + "active_furnaces";
                ResultSet result = statement.executeQuery(selectFurnaces);
                while (result.next()) {
                    World world = Bukkit.getWorld(result.getString("world"));

                    if (world == null) {
                        continue;
                    }

                    int id = result.getInt("id");
                    int level = result.getInt("level");
                    int uses = result.getInt("uses");

                    String placedByStr = result.getString("placed_by");
                    UUID placedBy = placedByStr == null ? null : UUID.fromString(result.getString("placed_by"));

                    String nickname = result.getString("nickname");

                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    int z = result.getInt("z");
                    Location location = new Location(world, x, y, z);

                    Furnace furnace = new FurnaceBuilder(location)
                            .setId(id)
                            .setLevel(EpicFurnaces.getInstance().getLevelManager().getLevel(level))
                            .setUses(uses)
                            .setPlacedBy(placedBy)
                            .setNickname(nickname)
                            .build();

                    furnaces.put(id, furnace);
                }
            }

            try (Statement statement = connection.createStatement()) {
                String selectAccessList = "SELECT * FROM " + this.getTablePrefix() + "access_list";
                ResultSet result = statement.executeQuery(selectAccessList);
                while (result.next()) {
                    int id = result.getInt("furnace_id");
                    UUID uuid = UUID.fromString(result.getString("uuid"));

                    Furnace furnace = furnaces.get(id);
                    if (furnace == null) {
                        break;
                    }

                    furnace.addToAccessList(uuid);
                }
            }

            try (Statement statement = connection.createStatement()) {
                String selectLevelupItems = "SELECT * FROM " + this.getTablePrefix() + "to_level_new";
                ResultSet result = statement.executeQuery(selectLevelupItems);
                while (result.next()) {
                    int id = result.getInt("furnace_id");
                    CompatibleMaterial material = CompatibleMaterial.getMaterial(result.getString("item"));
                    int amount = result.getInt("amount");

                    Furnace furnace = furnaces.get(id);
                    if (furnace == null) {
                        break;
                    }

                    furnace.addToLevel(material, amount);
                }
            }
            this.sync(() -> callback.accept(furnaces));
        }));
    }
}
