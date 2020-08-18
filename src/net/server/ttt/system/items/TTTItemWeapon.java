package net.server.ttt.system.items;

import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItemWeapon extends TTTItem {

    public abstract String getAmmoId();

    public abstract void consumeAmmo(Player player, ItemStack item); // from magazine
    public abstract ItemStack getAmmoStack();
    public abstract WeaponType getWeaponType();

    public abstract boolean hasOneAmmo(Player player);
    public abstract boolean isMagLoaded(Player player);
    public abstract boolean canFireAgain(Player player);
    public abstract boolean isNotReloading(Player player);

}
