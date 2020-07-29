package net.server.ttt.system.utils.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTakeDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private Player slayer;
    private double damage;
    private String cause;

    public PlayerTakeDamageEvent(Player player, Player slayer, double damage, String cause) {
        this.player = player;
        this.slayer = slayer;
        this.damage = damage;
        this.cause = cause;
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getSlayer() {
        return slayer;
    }
    public void setSlayer(Player slayer) {
        this.slayer = slayer;
    }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    public String getCause() {
        return cause;
    }
    public void setCause(String cause) {
        this.cause = cause;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
