package net.server.ttt.system.utils;

import net.server.ttt.main.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class YAMLManager {

    private static Main plugin = Main.getInstance();

    public static void loadConfig() {
        String maxPlayers = "Max.Players";
        String minPlayers = "Min.Players";
        String maxWorlds = "Max.Worlds";
        String threadTickRate = "Thread.TickRate"; // mc ticks per thread tick
        String scanRadius = "Scan.Radius";
        String minKarma = "Karma.min";
        String maxKarma = "Karma.max";

        FileConfiguration config = plugin.getConfig();

        config.addDefault(maxPlayers, 10);
        config.addDefault(minPlayers, 4);
        config.addDefault(maxWorlds, 4);
        config.addDefault(threadTickRate, 1);
        config.addDefault(scanRadius, 500);
        config.addDefault(minKarma, 5);
        config.addDefault(maxKarma, 100);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }

}
