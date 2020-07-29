package net.server.ttt.system.utils;

import net.server.ttt.main.Main;

public class YAMLManager {

    private static Main plugin = Main.getInstance();

    public static void loadConfig() {
        String maxPlayers = "Max.Players";
        String minPlayers = "Min.Players";
        String maxWorlds = "Max.Worlds";
        String threadTickRate = "Thread.TickRate"; // mc ticks per thread tick
        String scanRadius = "Scan.Radius";

        plugin.getConfig().addDefault(maxPlayers, 10);
        plugin.getConfig().addDefault(minPlayers, 4);
        plugin.getConfig().addDefault(maxWorlds, 4);
        plugin.getConfig().addDefault(threadTickRate, 1);
        plugin.getConfig().addDefault(scanRadius, 500);

        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
    }

}
