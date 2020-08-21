package net.server.ttt.system.items.abstracts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItemWeaponThrowable extends TTTItemWeapon{

    public abstract void consumeAmmo(Player player); // from magazine
    public abstract boolean hasOneAmmo(Player player);

}
