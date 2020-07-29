package net.server.ttt.system.utils.threads;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandleGame;
import net.server.ttt.system.utils.corpse.CorpsePacketReader;
import net.server.ttt.system.utils.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GameThread extends BukkitRunnable {

    int count = 0;
    int gameCount = 0;

    World world = null;
    int ticksPerThreadTick = Main.getInstance().getConfig().getInt("Tread.TickRate");
    int ticksPerSecond = 20 / ticksPerThreadTick;

    public boolean isEnding = false;
    public boolean hasStarted = false; // is true when the game has actually stated
    public boolean isCombat = false; // is true when combat is enabled

    public List<Player> players = new ArrayList<>();

    public List<Player> aliveBad = new ArrayList<>();
    public List<Player> aliveGood = new ArrayList<>();

    public List<Player> alive = new ArrayList<>();
    public List<Player> dead = new ArrayList<>();



    public GameThread(World world) {
        this.world = world;
    }

    @Override
    public void run() {

        count ++;
        players = world.getPlayers();

        if(hasStarted)
            gameCount ++;

        // called at system start
        if(count == 1) {

            for (Player p : players) {
                // announce game start
                p.sendMessage(ChatColor.AQUA + "Game starting in 10 seconds!");
                // set game mode
                p.setGameMode(GameMode.ADVENTURE);
                // inject corpse packet reader
                CorpsePacketReader.inject(p);
            }

            // fill up lists
            for(Player p : players) {

                // aliveGood | aliveBad
                Role role = (Role) p.getMetadata("ttt_role").get(0).value();
                if(role == Role.TRAITOR) {
                    aliveBad.add(p);
                }
                else if(role == Role.INNOCENT || role == Role.DETECTIVE) {
                    aliveGood.add(p);
                }

                // aliveTab
                alive.addAll(players);
            }
        }
        // called at 8 system seconds
        else if(count == ticksPerSecond * 8) {
            // announce game starting in 3 sec
            for (Player p : players) {
                p.sendMessage(ChatColor.AQUA + "Game starting in 3 seconds!");
            }
        }
        // called at 9 system seconds
        else if(count == ticksPerSecond * 9) {
            // announce game starting in 2 sec
            for (Player p : players) {
                p.sendMessage(ChatColor.AQUA + "Game starting in 2 seconds!");
            }
        }
        // called at 10 system seconds
        else if(count == ticksPerSecond * 10) {
            // announce game starting in 1 sec
            for (Player p : players) {
                p.sendMessage(ChatColor.AQUA + "Game starting in 1 second!");
            }
        }
        // called at 11 system seconds
        else if(count == ticksPerSecond * 11) {

            // spread players
            HandleGame.spreadPlayers(world, players);

            // stop player movement and announce countdown
            for (Player p : players) {
                p.setWalkSpeed(0);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW ,3, 255), true);

                p.sendTitle(ChatColor.GOLD + "3", "", 0, 1, 0);
            }


        }
        // called at 12 system seconds
        else if(count == ticksPerSecond * 12) {
            // announce countdown
            for (Player p : players) {
                p.sendTitle(ChatColor.GOLD + "2", "", 0, 1, 0);
            }
        }
        // called at 13 system seconds
        else if(count == ticksPerSecond * 13) {
            // announce countdown
            for (Player p : players) {
                p.sendTitle(ChatColor.GOLD + "1", "", 0, 1, 0);
            }
        }
        // called at 14 system seconds
        else if(count == ticksPerSecond * 14) {
            // resume player movement and announce countdown
            for (Player p : players) {
                p.setWalkSpeed(0.2f);
                p.removePotionEffect(PotionEffectType.SLOW);

                p.sendTitle(ChatColor.GOLD + "Go!", "", 0, 1, 0);
            }

            // declare the game as started to the thread
            hasStarted = true;

            // spawn general game items
            HandleGame.spawnItems(world);
        }

        // called at game start
        if(gameCount == 1) {
            HandleGame.spawnItems(world);
            for(Player p : players)
                p.sendTitle("", ChatColor.GRAY + "Combat is enabled in 15 seconds.", 1,2, 1);
        }
        // called at 5 game seconds
        else if(gameCount == ticksPerSecond * 5) {
            for(Player p : players)
                p.sendTitle("", ChatColor.GRAY + "Combat is enabled 10 seconds", 1,2, 1);
        }
        // called at 10 game seconds
        else if(gameCount == ticksPerSecond * 10) {
            for(Player p : players)
                p.sendTitle("", ChatColor.GRAY + "Combat is enabled 5 seconds", 1,2, 1);
        }
        // called at 15 game seconds
        else if(gameCount == ticksPerSecond * 15) {
            HandleGame.distributeRoles(players);
            world.setPVP(true);
            isCombat = true;

            for(Player p : players)
                p.sendTitle("", ChatColor.GRAY + "Combat is enabled.", 1,2, 1);
        }
        // called at 10 game minutes
        else if(gameCount == ticksPerSecond * (10 * 60)) {
            isEnding = true;
            HandleGame.declareWin(world, Role.INNOCENT);
        }

        // checks if any team has won the game
        if(aliveBad.isEmpty()) {
            isEnding = true;
            HandleGame.declareWin(world, Role.INNOCENT);
        }
        else if(aliveGood.isEmpty()) {
            isEnding = true;
            HandleGame.declareWin(world, Role.TRAITOR);
        }

        // called when the game is ending / isEnding is true
        if(isEnding) {
            try {
                this.wait(1000 * 8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(world.getPlayers().isEmpty())
                HandleGame.stopGame(world);
            else
                HandleGame.restartGame(world);
        }

        // called every tick
        HandleGame.updateBadTeamTab(world);
        HandleGame.updateGoodTeamTab(world);

        // stop thread if world doesn't exist
        if(!Bukkit.getWorlds().contains(world)) {
            HandleGame.stopGame(world);
        }

    }

}
