package net.server.ttt.system.handling;

import net.server.ttt.system.utils.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleScoreboard {

    // TODO objectives of boards
    // TODO entry removal

    static ScoreboardManager manager = Bukkit.getScoreboardManager();

    //public static Map<World, Scoreboard> settingsBoardMap = new HashMap<>();
    //public static Map<World, Scoreboard> goodTeamBoardMap = new HashMap<>();
    //public static Map<World, Scoreboard> badTeamBoardMap = new HashMap<>();

    public static Scoreboard settingsBoard = manager.getNewScoreboard();
    public static Scoreboard traitorBoard = manager.getNewScoreboard();
    public static Scoreboard detectiveBoard = manager.getNewScoreboard();
    public static Scoreboard innocentBoard = manager.getNewScoreboard();
    static {
        settingsBoard.registerNewTeam("T");
        settingsBoard.registerNewTeam("D");
        settingsBoard.registerNewTeam("I");
        traitorBoard.registerNewTeam("T");
        traitorBoard.registerNewTeam("D");
        traitorBoard.registerNewTeam("I");
        detectiveBoard.registerNewTeam("D");
        detectiveBoard.registerNewTeam("O");
        innocentBoard.registerNewTeam("D");
        innocentBoard.registerNewTeam("O");
    }


    public static void initSettingsBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;

        Team traitor = settingsBoard.getTeam("T");
        Team detective = settingsBoard.getTeam("D");
        Team innocent = settingsBoard.getTeam("I");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case TRAITOR:
                    traitor.addEntry(p.getName());
                    break;
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    break;
                case INNOCENT:
                    innocent.addEntry(p.getName());
                    break;
            }

        }

        traitor.setCanSeeFriendlyInvisibles(true);
        traitor.setAllowFriendlyFire(true);
        traitor.setColor(ChatColor.DARK_RED);

        detective.setCanSeeFriendlyInvisibles(true);
        detective.setAllowFriendlyFire(true);
        detective.setColor(ChatColor.DARK_BLUE);

        innocent.setCanSeeFriendlyInvisibles(false);
        innocent.setAllowFriendlyFire(true);
        innocent.setColor(ChatColor.GREEN);

        //settingsBoardMap.put(world, board);
    }
    public static void initTraitorBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;

        Team traitor = traitorBoard.getTeam("T");
        Team detective = traitorBoard.getTeam("D");
        Team innocent = traitorBoard.getTeam("I");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case TRAITOR:
                    traitor.addEntry(p.getName());
                    break;
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    break;
                case INNOCENT:
                    innocent.addEntry(p.getName());
                    break;
            }

        }

        traitor.setColor(ChatColor.DARK_RED);
        detective.setColor(ChatColor.DARK_BLUE);
        innocent.setColor(ChatColor.GREEN);


        //traitorBoardMap.put(world, board);
    }
    public static void initDetectiveBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;

        Team detective = traitorBoard.getTeam("D");
        Team other = traitorBoard.getTeam("0");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    other.removeEntry(p.getName());
                    break;
                default:
                    other.addEntry(p.getName());
                    detective.removeEntry(p.getName());
                    break;
            }

        }

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);

        //detectiveBoardMap.put(world, board);
    }
    public static void initInnocentBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;

        Team detective = traitorBoard.getTeam("D");
        Team other = traitorBoard.getTeam("0");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;
            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    other.removeEntry(p.getName());
                    break;
                default:
                    other.addEntry(p.getName());
                    detective.removeEntry(p.getName());
                    break;
            }

        }

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);

        //innocentBoardMap.put(world, board);
    }

    public static void setBoard(Player player) {

        if(!player.hasMetadata("ttt_role")) return;

        World world = player.getWorld();
        if(!world.hasMetadata("ttt_world")) return;

        Role role = (Role) player.getMetadata("ttt_role").get(0).value();

        switch (role) {
            case TRAITOR:
                player.setScoreboard(traitorBoard);
                break;
            case DETECTIVE:
                player.setScoreboard(detectiveBoard);
                break;
            case INNOCENT:
                player.setScoreboard(innocentBoard);
                break;
        }

        // TODO this
    }

    // removes the player from all teams
    public void playerClearTeams(Player player) {
        // TODO this
    }
}
