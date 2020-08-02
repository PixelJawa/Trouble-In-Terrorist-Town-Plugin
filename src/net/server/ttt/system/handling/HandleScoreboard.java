package net.server.ttt.system.handling;

import net.server.ttt.system.utils.enums.Role;
import net.server.ttt.system.utils.threads.GameThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class HandleScoreboard {

    static ScoreboardManager manager = Bukkit.getScoreboardManager();

    public static Scoreboard settingsBoard = manager.getNewScoreboard();
    static {
        Team traitor = settingsBoard.registerNewTeam("T");
        Team detective = settingsBoard.registerNewTeam("D");
        Team innocent = settingsBoard.registerNewTeam("I");

        traitor.setCanSeeFriendlyInvisibles(true);
        traitor.setAllowFriendlyFire(true);
        traitor.setColor(ChatColor.DARK_RED);

        detective.setCanSeeFriendlyInvisibles(true);
        detective.setAllowFriendlyFire(true);
        detective.setColor(ChatColor.DARK_BLUE);

        innocent.setCanSeeFriendlyInvisibles(false);
        innocent.setAllowFriendlyFire(true);
        innocent.setColor(ChatColor.GREEN);
    }
    public static Scoreboard traitorBoard = manager.getNewScoreboard();
    static {
        Team traitor = traitorBoard.getTeam("T");
        Team detective = traitorBoard.getTeam("D");
        Team innocent = traitorBoard.getTeam("I");
        Team confirmed = traitorBoard.getTeam("C");

        traitor.setColor(ChatColor.DARK_RED);
        detective.setColor(ChatColor.DARK_BLUE);
        innocent.setColor(ChatColor.GREEN);
        confirmed.setColor(ChatColor.GRAY);

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
        Team detective = detectiveBoard.getTeam("D");
        Team other = detectiveBoard.getTeam("O");
        Team confirmed = detectiveBoard.getTeam("C");

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);
        confirmed.setColor(ChatColor.GRAY);

        // objectives
        detectiveBoard.registerNewObjective("role", "dummy", ChatColor.DARK_BLUE + "Detective");

        Objective points = detectiveBoard.registerNewObjective("points", "dummy", "Credits:");
        points.getScore(ChatColor.DARK_BLUE + "");

        Objective karma = detectiveBoard.registerNewObjective("karma", "dummy", "Karma:");
        karma.getScore(ChatColor.AQUA + "");

        Objective time = detectiveBoard.registerNewObjective("time", "dummy", "Time Left:");
        time.getScore(ChatColor.GOLD + "");
    }
    public static Scoreboard innocentBoard = manager.getNewScoreboard();
    static {
        Team detective = innocentBoard.getTeam("D");
        Team other = innocentBoard.getTeam("0");
        Team confirmed = innocentBoard.getTeam("C");

        detective.setColor(ChatColor.DARK_BLUE);
        other.setColor(ChatColor.GREEN);
        confirmed.setColor(ChatColor.GRAY);

        // objectives
        innocentBoard.registerNewObjective("role", "dummy", ChatColor.GREEN + "Innocent");

        Objective karma = innocentBoard.registerNewObjective("karma", "dummy", "Karma:");
        karma.getScore(ChatColor.AQUA + "");

        Objective time = innocentBoard.registerNewObjective("time", "dummy", "Time Left:");
        time.getScore(ChatColor.GOLD + "");
    }


    public static void updateSettingsBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            playerClearTeams(p);

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            switch (role) {
                case TRAITOR:
                    settingsBoard.getTeam("T").addEntry(p.getName());
                    break;
                case DETECTIVE:
                    settingsBoard.getTeam("D").addEntry(p.getName());
                    break;
                case INNOCENT:
                    settingsBoard.getTeam("I").addEntry(p.getName());
                    break;
            }

        }

    }
    public static void updateTraitorBoard(World world) {

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        // teams
            for(Player p : world.getPlayers()) {

                if(!p.hasMetadata("ttt_role")) continue;

                playerClearTeams(p);

                // check if player is dead
                if(thread.dead.contains(p)) {
                    traitorBoard.getTeam("C").addEntry(p.getName());
                    continue;
                }

                // check the players role
                Role role = (Role) p.getMetadata("ttt_role").get(0).value();
                switch (role) {
                    case TRAITOR:
                        traitorBoard.getTeam("T").addEntry(p.getName());
                        break;
                    case DETECTIVE:
                        traitorBoard.getTeam("D").addEntry(p.getName());
                        break;
                    case INNOCENT:
                        traitorBoard.getTeam("I").addEntry(p.getName());
                        break;
                }

            }

        // objectives
    }
    public static void updateDetectiveBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            playerClearTeams(p);

            if(thread.confirmedDead.contains(p)) {
                detectiveBoard.getTeam("C").addEntry(p.getName());
                continue;
            }

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();
            if (role == Role.DETECTIVE) {
                detectiveBoard.getTeam("D").addEntry(p.getName());
            } else {
                detectiveBoard.getTeam("O").addEntry(p.getName());
            }

        }
    }
    public static void updateInnocentBoard(World world) {

        //Scoreboard board = manager.getNewScoreboard();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);

        for(Player p : world.getPlayers()) {

            if(!p.hasMetadata("ttt_role")) continue;

            if(thread.confirmedDead.contains(p)) {
                innocentBoard.getTeam("C").addEntry(p.getName());
                continue;
            }

            Role role = (Role) p.getMetadata("ttt_role").get(0).value();

            if (role == Role.DETECTIVE) {
                innocentBoard.getTeam("D").addEntry(p.getName());
            } else {
                innocentBoard.getTeam("O").addEntry(p.getName());
            }

        }
    }

    public static void updateObjectives(Player player) {

        if(!player.hasMetadata("ttt_role")) return;

        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        GameThread thread = HandleGame.gameThreadMap.get(world);
        Role role = (Role) player.getMetadata("ttt_role").get(0).value();

        switch (role) {
            case TRAITOR: {
                Objective tPoints = traitorBoard.getObjective("points");
                tPoints.getScore(ChatColor.DARK_RED + String.valueOf(HandlePlayer.getCredits(player)));

                Objective tKarma = traitorBoard.getObjective("karma");
                tKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayer.getKarma(player)));

                Objective tTime = traitorBoard.getObjective("time");
                tTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;
            }

            case DETECTIVE: {
                Objective dPoints = detectiveBoard.getObjective("points");
                dPoints.getScore(ChatColor.DARK_BLUE + String.valueOf(HandlePlayer.getCredits(player)));

                Objective dKarma = detectiveBoard.getObjective("karma");
                dKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayer.getKarma(player)));

                Objective dTime = detectiveBoard.getObjective("time");
                dTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;
            }

            case INNOCENT: {
                Objective iKarma = innocentBoard.getObjective("karma");
                iKarma.getScore(ChatColor.AQUA + String.valueOf(HandlePlayer.getKarma(player)));

                Objective iTime = innocentBoard.getObjective("time");
                iTime.getScore(ChatColor.GOLD + thread.getTimeInMinAsString());
                break;
            }
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
