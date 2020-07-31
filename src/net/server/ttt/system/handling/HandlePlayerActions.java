package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.utils.corpse.CorpsePacketReader;
import net.server.ttt.system.utils.events.player.PlayerSlainEvent;
import net.server.ttt.system.utils.events.player.PlayerTakeDamageEvent;
import net.server.ttt.system.utils.threads.GameThread;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HandlePlayerActions {

    public static Map<Player, Integer> karmaMap = new HashMap<>();
    public static Map<Player, Integer> creditsMap = new HashMap<>();

    public static void setSpectator(Player player) {
        World world = player.getWorld();

        if(!world.hasMetadata("ttt_world")) return;
        if(!HandleGame.gameThreadMap.containsKey(world)) return;

        // hide player
        for(Player p : world.getPlayers())
            p.hidePlayer(Main.getInstance(), player);

        // enable flying
        player.setAllowFlight(true);

        GameThread thread = HandleGame.gameThreadMap.get(world);

        thread.alive.remove(player);
        thread.aliveBad.remove(player);
        thread.aliveGood.remove(player);
        thread.dead.add(player);
    }
    // reset everything that has been changed about the player
    public static void resetPlayer(Player player, GameThread thread) {

        // un hide player
        for(Player p : Bukkit.getOnlinePlayers())
            p.showPlayer(Main.getInstance(), player);

        // disable flying
        player.setAllowFlight(false);

        // remove players from thread lists
        thread.players.remove(player);
        thread.alive.remove(player);
        thread.aliveBad.remove(player);
        thread.aliveGood.remove(player);
        thread.dead.remove(player);

        // make custom name visible
        player.setCustomNameVisible(true);

        // eject from NPC packet reader
        CorpsePacketReader.eject(player);

        // remove player from all teams
        HandleScoreboard.playerClearTeams(player);

        // clear player scoreboard
        HandleScoreboard.clearBoard(player);
    }

    // damages the given player for the given amount (takes armor in account)
    public static void damagePlayer(Player target, Player source, double damage, String cause) {

        PlayerTakeDamageEvent PTDEvent = new PlayerTakeDamageEvent(target, source, damage, cause);
        Bukkit.getPluginManager().callEvent(PTDEvent);

        if(PTDEvent.isCancelled()) return;

        double armor = Main.getArmor(target);
        double postDamage = damage * armor;

        // make player spectator if damage would kill him
        if(target.getHealth() - postDamage <= 0) {
            target.playEffect(EntityEffect.HURT);
            PTDEvent.setCancelled(true);

            PlayerSlainEvent PSEvent = new PlayerSlainEvent(target, source, cause);
            Bukkit.getPluginManager().callEvent(PTDEvent);

            if(PSEvent.isCancelled()) return;

            setSpectator(target);
            HandleGame.spawnCorpse(target.getLocation(), target, source, cause, false); // TODO head shot
        }

        // damage entity
        target.setHealth(target.getHealth() - postDamage);
        target.playEffect(EntityEffect.HURT);

    }


    public static int getKarma(Player player) {
        if(!karmaMap.containsKey(player)) {
            karmaMap.put(player, 100);
            return 100;
        }

        return karmaMap.get(player);
    }
    public static void removeKarma(Player player, int amount) {

        int min = Main.getInstance().getConfig().getInt("Karma.min");
        int value = getKarma(player) - amount;

        if(value < min)
            value = min;

        karmaMap.put(player, value);
    }
    public static void addKarma(Player player, int amount) {
        int max = Main.getInstance().getConfig().getInt("Karma.max");
        int value = getKarma(player) + amount;

        if(value > max)
            value = max;

        karmaMap.put(player, value);
    }
    public static void resetKarma(Player player) {
        karmaMap.put(player, 100);
    }

    public static int getCredits(Player player) {
        if(!creditsMap.containsKey(player)) {
            creditsMap.put(player, 0);
            return 0;
        }

        return creditsMap.get(player);
    }
    public static void removeCredits(Player player, int amount) {

        int value = getCredits(player) - amount;

        if(value < 0)
            value = 0;

        creditsMap.put(player, value);
    }
    public static void addCredits(Player player, int amount) {
        int value = getKarma(player) + amount;
        creditsMap.put(player, value);
    }
    public static void resetCredits(Player player) {
        creditsMap.remove(player);
    }
}
