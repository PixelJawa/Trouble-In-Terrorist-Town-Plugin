package net.server.ttt.system.utils.events;

import net.server.ttt.system.handling.HandlePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;


public class EventCaller implements Listener {

    // triggers when a player hits another player
    @EventHandler
    public void onPlayerHitPlayer(EntityDamageByEntityEvent event) {

        if(event.getEntity().getType() != EntityType.PLAYER) return;
        if(event.getDamager().getType() != EntityType.PLAYER) return;

        Player target = (Player) event.getEntity();
        Player source = (Player) event.getDamager();
        if(!target.hasMetadata("ttt_role")) return;

        event.setCancelled(true);
        double damage = event.getDamage();

        HandlePlayer.damageTarget(target, source, damage, "melee");
    }

}
