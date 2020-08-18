package net.server.ttt.system.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItem {

    public abstract void leftAction(Player player, ItemStack item);
    public abstract void rightAction(Player player, ItemStack item);

    public abstract void sneakStartAction(Player player, ItemStack item);
    public abstract void sneakEndAction(Player player, ItemStack item);

    public abstract boolean conditionsLeftClear(Player player);
    public abstract boolean conditionsRightClear(Player player);
    public abstract String getId();

    public abstract String getName();

    public abstract ItemStack getItemStack();

}
