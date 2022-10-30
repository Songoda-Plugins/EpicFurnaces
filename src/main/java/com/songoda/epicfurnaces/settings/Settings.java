package com.songoda.epicfurnaces.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.HologramManager;
import com.songoda.epicfurnaces.EpicFurnaces;

public final class Settings {

    static final Config config = EpicFurnaces.getInstance().getCoreConfig();

    public static final ConfigSetting UPGRADE_BY_SMELTING = new ConfigSetting(config, "Main.Upgrade By Smelting Materials", true);

    public static final ConfigSetting UPGRADE_WITH_ECONOMY = new ConfigSetting(config, "Main.Upgrade With Economy", true,
            "Should you be able to upgrade furnaces with economy?");

    public static final ConfigSetting UPGRADE_WITH_XP = new ConfigSetting(config, "Main.Upgrade With XP", true,
            "Should you be able to upgrade furnaces with experience?");

    public static final ConfigSetting AUTOSAVE = new ConfigSetting(config, "Main.Auto Save Interval In Seconds", 15,
            "The amount of time in between saving to file.",
            "This is purely a safety function to prevent against unplanned crashes or",
            "restarts. With that said it is advised to keep this enabled.",
            "If however you enjoy living on the edge, feel free to turn it off.");

    public static final ConfigSetting FURNACE_AREA = new ConfigSetting(config, "Main.Furnace Area", 1024,
            "This controls how big a furnace ticking area is.",
            "Higher size means decreased performance, but less chances of overheat",
            "missing a spot. Lower size means better performance, but overheat",
            "might leave a few spots. Minimum value is 16. To disable, just set",
            "this to a very high value, such as your worldborder size.");

    public static final ConfigSetting USE_PROTECTION_PLUGINS = new ConfigSetting(config, "Main.Use Protection Plugins", true,
            "Should we use protection plugins?");

    public static final ConfigSetting FURNACE_ITEM = new ConfigSetting(config, "Main.Remember Furnace Item Levels", true,
            "Should furnace levels be remembered when broken?");

    public static final ConfigSetting HOLOGRAM_PLUGIN = new ConfigSetting(config, "Main.Hologram",
            HologramManager.getHolograms() == null ? "HolographicDisplays" : HologramManager.getHolograms().getName(),
            "Which hologram plugin should be used?",
            "You can choose from \"" + String.join(", ", HologramManager.getManager().getRegisteredPlugins()) + "\".");

    public static final ConfigSetting HOLOGRAMS = new ConfigSetting(config, "Main.Furnaces Have Holograms", true);

    public static final ConfigSetting REDSTONE_DEACTIVATES = new ConfigSetting(config, "Main.Redstone Deactivates Furnaces", true);

    public static final ConfigSetting CUSTOM_RECIPES = new ConfigSetting(config, "Main.Use Custom Recipes", true);
    public static final ConfigSetting NO_REWARDS_FROM_RECIPES = new ConfigSetting(config, "Main.No Rewards From Custom Recipes", true);

    public static final ConfigSetting ALLOW_NORMAL_FURNACES = new ConfigSetting(config, "Main.Allow Normal Furnaces", false,
            "Should normal furnaces not be converted into",
            "Epic Furnaces?");

    public static final ConfigSetting USE_LIMIT_PERMISSION = new ConfigSetting(config, "Main.Use Limit Permission", true,
            "Should we use the epicfurnaces.limit.<amount>",
            "permission? This will limit the amount of Epic Furnaces",
            "a player can place.");

    public static final ConfigSetting PARTICLE_TYPE = new ConfigSetting(config, "Main.Upgrade Particle Type", "SPELL_WITCH",
            "The type of particle shown when a furnace is upgraded.");

    public static final ConfigSetting REMOTE = new ConfigSetting(config, "Main.Access Furnaces Remotely", true);

    public static final ConfigSetting TICK_SPEED = new ConfigSetting(config, "Main.Furnace Tick Speed", 10);
    public static final ConfigSetting OVERHEAT_PARTICLES = new ConfigSetting(config, "Main.Overheat Particles", true);

    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "Main.Economy", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy plugin should be used?",
            "Supported plugins you have installed: \"" + String.join("\", \"", EconomyManager.getManager().getRegisteredPlugins()) + "\".");

    public static final ConfigSetting REWARD_ICON = new ConfigSetting(config, "Interfaces.Reward Icon", "GOLDEN_APPLE");
    public static final ConfigSetting PERFORMANCE_ICON = new ConfigSetting(config, "Interfaces.Performance Icon", "REDSTONE");
    public static final ConfigSetting FUEL_SHARE_ICON = new ConfigSetting(config, "Interfaces.FuelShare Icon", "COAL_BLOCK");
    public static final ConfigSetting FUEL_DURATION_ICON = new ConfigSetting(config, "Interfaces.FuelDuration Icon", "COAL");
    public static final ConfigSetting OVERHEAT_ICON = new ConfigSetting(config, "Interfaces.Overheat Icon", "FIRE_CHARGE");

    public static final ConfigSetting ECO_ICON = new ConfigSetting(config, "Interfaces.Economy Icon", "SUNFLOWER");
    public static final ConfigSetting XP_ICON = new ConfigSetting(config, "Interfaces.XP Icon", "EXPERIENCE_BOTTLE");

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting MYSQL_ENABLED = new ConfigSetting(config, "MySQL.Enabled", false, "Set to 'true' to use MySQL instead of SQLite for data storage.");
    public static final ConfigSetting MYSQL_HOSTNAME = new ConfigSetting(config, "MySQL.Hostname", "localhost");
    public static final ConfigSetting MYSQL_PORT = new ConfigSetting(config, "MySQL.Port", 3306);
    public static final ConfigSetting MYSQL_DATABASE = new ConfigSetting(config, "MySQL.Database", "your-database");
    public static final ConfigSetting MYSQL_USERNAME = new ConfigSetting(config, "MySQL.Username", "user");
    public static final ConfigSetting MYSQL_PASSWORD = new ConfigSetting(config, "MySQL.Password", "pass");
    public static final ConfigSetting MYSQL_USE_SSL = new ConfigSetting(config, "MySQL.Use SSL", false);
    public static final ConfigSetting MYSQL_POOL_SIZE = new ConfigSetting(config, "MySQL.Pool Size", 3, "Determines the number of connections the pool is using. Increase this value if you are getting timeout errors when more players online.");

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Main.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Main.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Main.Economy", "PlayerPoints");
        }

        config.saveChanges();
    }
}