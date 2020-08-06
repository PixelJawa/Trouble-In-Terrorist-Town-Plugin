package net.server.ttt.system.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItem {

    public abstract void leftAction(Player player);
    public abstract void rightAction(Player player);

    public abstract boolean conditionsClear(Player player);
    public abstract String getId();

    public abstract String getName();

    public abstract ItemStack getItemStack();

}
