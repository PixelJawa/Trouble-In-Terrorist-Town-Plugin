package net.server.ttt.main;

import net.server.ttt.system.commands.user.TTTJoin;
import net.server.ttt.system.commands.user.TTTReady;
import net.server.ttt.system.handling.HandleGame;
import net.server.ttt.system.handling.HandleItems;
import net.server.ttt.system.handling.HandleWorldCreation;
import net.server.ttt.system.utils.YAMLManager;
import net.server.ttt.system.utils.events.EventCaller;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class Main extends JavaPlugin {
    private static Main instance;

    public Main() {
        super();
        if (instance == null)
            instance = this;
    }

    @Override
    public void onEnable() {

//methods
        YAMLManager.loadConfig();
        System.out.print("[TTT] Config Loaded!");
//events
        // register in the listener class
        Bukkit.getPluginManager().registerEvents(new HandleGame(), this);
        Bukkit.getPluginManager().registerEvents(new HandleItems(), this);
        Bukkit.getPluginManager().registerEvents(new EventCaller(), this);
//commands
        // register in the executor class
        getCommand("tttJoin").setExecutor(new TTTJoin());
        getCommand("tttReady").setExecutor(new TTTReady());

    }

    public void onDisable() {
        saveConfig();

        // remove all ttt worlds
        for (World w : Bukkit.getWorlds() ) {
            if(w.hasMetadata("ttt_world")) {
                HandleGame.removePlayers(w);
                HandleWorldCreation.delete(w);
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    //Randoms------------------------------------------------
    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static double randomDouble(double min, double max) {

        return min + (max - min) * new Random().nextDouble();
    }

    public static boolean randBoolean() {
        return new Random().nextBoolean();
    }

    public static boolean isWithinEntityBoundingBox(Location location, Entity entity, double scale) {

        double BBmaxX = entity.getBoundingBox().getMaxX() ;
        double BBmaxY = entity.getBoundingBox().getMaxY() ;
        double BBmaxZ = entity.getBoundingBox().getMaxZ() ;
        double BBminX = entity.getBoundingBox().getMinX() ;
        double BBminY = entity.getBoundingBox().getMinY() ;
        double BBminZ = entity.getBoundingBox().getMinZ() ;

        double dx = (BBmaxX - BBminX) * scale;
        double dy = (BBmaxY - BBminY) * scale;
        double dz = (BBmaxZ - BBminZ) * scale;

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return
                x >= BBminX - dx &&
                        x <= BBmaxX + dx &&
                        y >= BBminY - dy &&
                        y <= BBmaxY + dy &&
                        z >= BBminZ - dz &&
                        z <= BBmaxZ + dz ;
    }

    public static String hideText(String s) {
        String hidden = "";
        for (char c : s.toCharArray())
            hidden += ChatColor.COLOR_CHAR+""+c;
        return hidden;
    }
    public static String revealText(String s) {
        return s.replaceAll("§", "");
    }

    public static double getArmor(Player player)
    {
        org.bukkit.inventory.PlayerInventory inv = player.getInventory();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        double red = 0.0;

        // helm
        switch (helmet.getType()) {
            case LEATHER_HELMET: red = red + 0.04; break;
            case GOLDEN_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
                red = red + 0.08;
                break;
            case DIAMOND_HELMET: red = red + 0.12; break;
        }
        // boots
        switch (boots.getType()) {
            case LEATHER_BOOTS:
            case GOLDEN_BOOTS:
            case CHAINMAIL_BOOTS:
                red = red + 0.04;
                break;
            case IRON_BOOTS: red = red + 0.08; break;
            case DIAMOND_BOOTS: red = red + 0.12; break;
        }
        // pants
        switch (pants.getType()) {
            case LEATHER_LEGGINGS: red = red + 0.08; break;
            case GOLDEN_LEGGINGS: red = red + 0.12; break;
            case CHAINMAIL_LEGGINGS: red = red + 0.16; break;
            case IRON_LEGGINGS: red = red + 0.20; break;
            case DIAMOND_LEGGINGS: red = red + 0.24; break;
        }
        // chest
        switch (chest.getType()) {
            case LEATHER_CHESTPLATE: red = red + 0.12; break;
            case GOLDEN_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
                red = red + 0.20;
                break;
            case IRON_CHESTPLATE: red = red + 0.24; break;
            case DIAMOND_CHESTPLATE: red = red + 0.32; break;
        }

        return red;
    }
}
