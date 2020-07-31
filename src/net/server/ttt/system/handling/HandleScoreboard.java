package net.server.ttt.system.handling;

import net.server.ttt.system.utils.enums.Role;
import net.server.ttt.system.utils.threads.GameThread;
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

    static ScoreboardManager manager = Bukkit.getScoreboardManager();

    public static Scoreboard settingsBoard = manager.getNewScoreboard();
    static {
        settingsBoard.registerNewTeam("T");
        settingsBoard.registerNewTeam("D");
        settingsBoard.registerNewTeam("I");
        settingsBoard.registerNewTeam("C");
    }
    public static Scoreboard traitorBoard = manager.getNewScoreboard();
    static {
        traitorBoard.registerNewTeam("T");
        traitorBoard.registerNewTeam("D");
        traitorBoard.registerNewTeam("I");
        traitorBoard.registerNewTeam("C");

        // objectives
        traitorBoard.registerNewObjective("role", "dummy", ChatColor.DARK_RED + "Traitor");

        Objective points = traitorBoard.registerNewObjective("points", "dummy", "Credits:");
        points.getScore(ChatColor.DARK_RED + "");

        Objective karma = traitorBoard.registerNewObjective("karma", "dummy", "Karma:");
        karma.getScore(ChatColor.AQUA + "");

        Objective time = traitorBoard.registerNewObjective("time", "dummy", "Time Left:");
        time.getScore(ChatColor.GOLD + "");
    }
    public static Scoreboard detectiveBoard = manager.getNewScoreboard();
    static {
        detectiveBoard.registerNewTeam("D");
        detectiveBoard.registerNewTeam("O");
        detectiveBoard.registerNewTeam("C");

        // objectives
        detectiveBoard.registerNewObjective("role", "dummy", ChatColor.DARK_BLUE + "Detective");

        Objective points = detectiveBoard.registerNewObjective("points", "dummy", "Credits:");
        points.getScore(ChatColor.DARK_BLUE + "");

        Objective karma = detectiveBoard.registerNewObjective("karma", "dummy", "Karma:");
        karma.getScore(ChatColor.AQUA + "");

        Objective time = detectiveBoard.registerNewObjective("points", "dummy", "Time Left:");
        time.getScore(ChatColor.GOLD + "");
    }
    public static Scoreboard innocentBoard = manager.getNewScoreboard();
    static {
        innocentBoard.registerNewTeam("D");
        innocentBoard.registerNewTeam("O");
        innocentBoard.registerNewTeam("C");

        // objectives
        innocentBoard.registerNewObjective("role", "dummy", ChatColor.GREEN + "Innocent");

        Objective karma = innocentBoard.registerNewObjective("karma", "dummy", "Karma:");
        karma.getScore(ChatColor.AQUA + "");

        Objective time = innocentBoard.registerNewObjective("points", "dummy", "Time Left:");
        time.getScore(ChatColor.GOLD + "");
    }


    public static void updateSettingsBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        Team traitor = settingsBoard.getTeam("T");
        Team detective = settingsBoard.getTeam("D");
        Team innocent = settingsBoard.getTeam("I");


        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            playerClearTeams(p);

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
    public static void updateTraitorBoard(World world) {

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        // teams
            Team traitor = traitorBoard.getTeam("T");
            Team detective = traitorBoard.getTeam("D");
            Team innocent = traitorBoard.getTeam("I");
            Team confirmed = traitorBoard.getTeam("C");

            for(Player p : world.getPlayers()) {

                if(!p.hasMetadata("ttt_role")) continue;

                playerClearTeams(p);

                // check if player is dead
                if(thread.dead.contains(p)) {
                    confirmed.addEntry(p.getName());
                    continue;
                }

                // check the players role
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
            confirmed.setColor(ChatColor.GRAY);

    }
    public static void updateDetectiveBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        Team detective = traitorBoard.getTeam("D");
        Team other = traitorBoard.getTeam("0");
        Team confirmed = traitorBoard.getTeam("C");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            playerClearTeams(p);

            if(thread.confirmedDead.contains(p)) {
                confirmed.addEntry(p.getName());
                continue;
            }

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();
            switch (role) {
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    break;
                default:
                    other.addEntry(p.getName());
                    break;
            }

        }

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);
        confirmed.setColor(ChatColor.GRAY);

        //detectiveBoardMap.put(world, board);
    }
    public static void updateInnocentBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        Team detective = traitorBoard.getTeam("D");
        Team other = traitorBoard.getTeam("0");
        Team confirmed = traitorBoard.getTeam("C");

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            if(thread.confirmedDead.contains(p)) {
                confirmed.addEntry(p.getName());
                continue;
            }

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case DETECTIVE:
                    detective.addEntry(p.getName());
                    break;
                default:
                    other.addEntry(p.getName());
                    break;
            }

        }

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);
        confirmed.setColor(ChatColor.GRAY);

        //innocentBoardMap.put(world, board);
    }

    public static void updateObjectives(Player player) {

        if(!player.hasMetadata("ttt_role")) return;

        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);
        Role role = (Role) player.getMetadata("ttt_role").get(0).value();

        switch (role) {
            case TRAITOR:
                Objective tPoints = traitorBoard.getObjective("points");
                tPoints.getScore(ChatColor.DARK_RED + String.valueOf(HandlePlayerActions.getCredits(player)));

                Objective tKarma = traitorBoard.getObjective("karma");
                tKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayerActions.getKarma(player)));

                Objective tTime = traitorBoard.getObjective("time");
                tTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;

            case DETECTIVE:
                Objective dPoints = detectiveBoard.getObjective("points");
                dPoints.getScore(ChatColor.DARK_BLUE + String.valueOf(HandlePlayerActions.getCredits(player)));

                Objective dKarma = detectiveBoard.getObjective("karma");
                dKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayerActions.getKarma(player)));

                Objective dTime = detectiveBoard.getObjective("time");
                dTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;

            case INNOCENT:
                Objective iKarma = innocentBoard.getObjective("karma");
                iKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayerActions.getKarma(player)));

                Objective iTime = innocentBoard.getObjective("time");
                iTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;
        }

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
    }
    public static void clearBoard(Player player) {
        player.setScoreboard(manager.getNewScoreboard());
    }

    // removes the player from all teams
    public static void playerClearTeams(Player player) {
        for(Team t : settingsBoard.getTeams())
            t.removeEntry(player.getName());
        for(Team t : traitorBoard.getTeams())
            t.removeEntry(player.getName());
        for(Team t : detectiveBoard.getTeams())
            t.removeEntry(player.getName());
        for(Team t : innocentBoard.getTeams())
            t.removeEntry(player.getName());
    }



}
