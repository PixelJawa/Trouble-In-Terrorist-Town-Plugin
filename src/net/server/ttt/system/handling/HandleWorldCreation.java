package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.utils.enums.GameState;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HandleWorldCreation {

    // this class handles the creation and removal of game worlds

    public static File dir = new File(Main.getInstance().getDataFolder().getAbsolutePath() + File.separator + ".." + File.separator + ".." + File.separator + "TTT_map_prefabs");

    public static World createRand() {

        if(!dir.exists()) {
            System.out.println("[TTT] -- HandleWorldCreation --- the world prefab file doesn't exist at " + dir.toString());
            return null;
        }

        File[] files = dir.listFiles();
        assert files != null;
        if(files.length == 0) {
            System.out.println("[TTT] -- HandleWorldCreation --- the world prefab file is empty");
            return null;
        }

        String name = "TTT_" + UUID.randomUUID();

        // init file vars
        File srcDir = files[Main.randomInt(0, files.length-1)].getAbsoluteFile();
        String destString = Main.getInstance().getServer().getWorldContainer().getAbsolutePath() + File.separator + name;
        File destDir = new File(destString);

        destDir.mkdir();

        // copy world file to Worlds folder
        try {
            FileUtils.copyDirectory(srcDir, destDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // init world creator
        WorldCreator creator = new WorldCreator(name);
        World world = creator.createWorld();

        world.setMetadata("ttt_world", new FixedMetadataValue(Main.getInstance(), true));
        world.setMetadata("ttt_state", new FixedMetadataValue(Main.getInstance(), GameState.OPEN));
        world.setPVP(false);

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

        scanGameArea(world, world.getSpawnLocation());

        return world;
    }

    // delete the given TTT world
    public static void delete(World world) {

        if(!world.hasMetadata("ttt_world")) return;

        Bukkit.unloadWorld(world, false);

        File dir = new File(world.getWorldFolder().getPath());
        try {
            FileUtils.deleteDirectory(dir);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // scan the game area and save the spawn locations in a yaml file
    public static void scanGameArea(World world, Location loc) {
        String path = world.getWorldFolder().getAbsolutePath() + File.separator + "TTT_data" + File.separator + "spawns.yml";
        File file = new File(path);

        if(!file.exists()) {
            file.mkdir();
        }

        YamlConfiguration spawnsYML = YamlConfiguration.loadConfiguration(file);

        List<Location> playerSpawnList = new ArrayList<>();
        List<Location> itemSpawnList = new ArrayList<>();
        List<Location> superSpawnList = new ArrayList<>();

        int radius = Main.getInstance().getConfig().getInt("Scan.Radius");

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    Block block = world.getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);

                    if(block.getType() != Material.DISPENSER) continue;

                    Dispenser dispenser = (Dispenser) block;
                    String name = dispenser.getCustomName();

                    assert name != null;
                    if(name.equalsIgnoreCase("ttt_spawn"))
                        playerSpawnList.add(block.getLocation());
                    else if(name.equalsIgnoreCase("ttt_item"))
                        itemSpawnList.add(block.getLocation());
                    else if(name.equalsIgnoreCase("ttt_super"))
                        superSpawnList.add(block.getLocation());

                    // remove block
                    block.setType(Material.AIR);
                }
            }
        }

        spawnsYML.set("playerSpawnList", playerSpawnList);
        spawnsYML.set("itemSpawnList", itemSpawnList);
        spawnsYML.set("superSpawnList", superSpawnList);
    }

}
