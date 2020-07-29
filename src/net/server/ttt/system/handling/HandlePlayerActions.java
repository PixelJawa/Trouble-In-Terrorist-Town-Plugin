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

public class HandlePlayerActions {

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

        CorpsePacketReader.eject(player);
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
}
