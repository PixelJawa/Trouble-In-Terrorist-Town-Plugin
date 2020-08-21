package net.server.ttt.system.items.abstracts;

import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItemWeapon extends TTTItem {

    public abstract boolean getIsHoldable();
    public abstract boolean canFireAgain(Player player);

    public abstract WeaponType getWeaponType();
    public abstract ItemStack getAmmoStack();


}
