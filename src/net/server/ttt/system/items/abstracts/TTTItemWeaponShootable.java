package net.server.ttt.system.items.abstracts;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItemWeaponShootable extends TTTItemWeapon {

    public abstract String getAmmoId();

    public abstract void consumeAmmo(Player player, ItemStack item); // from magazine

    public abstract boolean isMagLoaded(Player player);
    public abstract boolean isNotReloading(Player player);

    public abstract boolean hasOneAmmo(Player player);

}
