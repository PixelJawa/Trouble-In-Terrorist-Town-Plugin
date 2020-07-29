package net.server.ttt.system.items;

import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.inventory.ItemStack;

public abstract class TTTItem {

    public abstract void leftAction();
    public abstract void rightAction();

    public abstract boolean conditionsClear();
    public abstract void consumeAmmo();
    public abstract String getId();
    public abstract WeaponType getWeaponType();
    public abstract String getName();

    public abstract ItemStack getItem();
    public abstract ItemStack getAmmo();

}
