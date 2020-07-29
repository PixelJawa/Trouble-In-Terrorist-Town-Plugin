package net.server.ttt.system.utils.events.npc;

import net.server.ttt.system.utils.corpse.Corpse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CorpseAttackEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private Corpse corpse;

    public CorpseAttackEvent(Player player, Corpse corpse) {
        this.player = player;
        this.corpse = corpse;
    }

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Corpse getCorpse() {
        return corpse;
    }
    public void setCorpse(Corpse corpse) {
        this.corpse = corpse;
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
