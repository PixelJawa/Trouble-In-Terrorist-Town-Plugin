package net.server.ttt.system.items.spawns.generic;

import net.server.ttt.main.Main;
import net.server.ttt.system.items.TTTItemWeapon;
import net.server.ttt.system.utils.enums.WeaponType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scout extends TTTItemWeapon {

    static Map<Player, Long> lastFireMap = new HashMap<>();

    static double damage = 10;
    static double headMultiplier = 2.5;
    static double fireRate = 0.8; // shots per second
    static String weaponName = ChatColor.LIGHT_PURPLE + "Scout";

    static WeaponType weaponType = WeaponType.PRIMARY;
    static int ammoBatchAmount = 4;
    static String weaponId = "ttt_item_weapon_scout";
    static String ammoId = "ttt_item_ammo_scout";

    // weapon
    static ItemStack weapon = new ItemStack(Material.STONE_HOE, 1);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = weapon.getItemMeta();

        meta.setDisplayName(weaponName);

        lore.add(Main.hideText(weaponId));
        lore.add(ChatColor.GRAY + "A rifle for long ranges.");
        lore.add(ChatColor.GRAY + "damage: " + damage);
        lore.add(ChatColor.GRAY + "head shot multiplier: " + headMultiplier);
        lore.add(ChatColor.GRAY + "fire rate: " + fireRate);
        lore.add(ChatColor.GRAY + "weapon type: " + weaponType.toString());

        meta.setLore(lore);
    }

    // ammo
    static ItemStack ammo = new ItemStack(Material.GHAST_TEAR, ammoBatchAmount);
    static {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta meta = ammo.getItemMeta();

        meta.setDisplayName(ChatColor.DARK_GRAY + "Scout Ammo");
        lore.add(Main.hideText(ammoId));

        meta.setLore(lore);
    }

    public boolean hasAmmo(Player player) {
        if(player.getInventory().containsAtLeast(ammo, 1))
            return true;
        else
            return false;
    }
    public boolean canFireAgain(Player player) {
        if(!lastFireMap.containsKey(player)) {
            lastFireMap.put(player, System.currentTimeMillis());
            return true;
        }
        else {
            return System.currentTimeMillis() - lastFireMap.get(player) >= 1 / fireRate * 1000;
        }
    }
    public boolean conditionsClear(Player player) {
        return hasAmmo(player) && canFireAgain(player);
    }

    public void consumeAmmo(Player player) {
        player.getInventory().remove(ammo);
    }

    public String getName() {
        return weaponName;
    }

    public String getId() {
        return weaponId;
    }
    public String getAmmoId() {
        return ammoId;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public ItemStack getItemStack() {
        return weapon;
    }
    public ItemStack getAmmoStack() {
        return ammo;
    }

    public void leftAction(Player player) {
        // TODO reload
    }

    public void rightAction(Player player) {
        // TODO shoot
    }

    public void sneakAction(Player player) {
        // TODO zoom
    }

}
