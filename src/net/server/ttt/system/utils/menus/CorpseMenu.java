package net.server.ttt.system.utils.menus;

import net.server.ttt.main.Main;
import net.server.ttt.system.utils.corpse.Corpse;
import net.server.ttt.system.utils.enums.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CorpseMenu {

    Player victim;
    Role victimRole;
    Player killer;
    Role killerRole;
    String cause;
    boolean headShot;
    boolean scanned;
    String victimName;
    String killerName;
    String invTitle;

    static ItemStack empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    static ItemStack victimInfoItem = new ItemStack(Material.BLACK_CONCRETE);
    static ItemStack killerInfoItem = new ItemStack(Material.WITHER_SKELETON_SKULL);
    static ItemStack causeInfoItem = new ItemStack(Material.SPECTRAL_ARROW);
    static ItemStack headShotInfo = new ItemStack(Material.TOTEM_OF_UNDYING);
    static List<String> lore = new ArrayList<>();
    static {
        lore.add(Main.revealText("ttt_item_corpse_menu_uninteractabe"));

        ItemMeta emptyMeta = empty.getItemMeta();
        emptyMeta.setLore(lore);
        emptyMeta.setDisplayName(" ");
        empty.setItemMeta(emptyMeta);
    }

    public CorpseMenu(Corpse corpse) {

        victim = corpse.victim;
        victimRole = (Role) victim.getMetadata("ttt").get(0).value();
        killer = corpse.killer;
        killerRole = (Role) killer.getMetadata("ttt").get(0).value();
        cause = corpse.cause;
        headShot = corpse.isHeadShot;
        scanned = corpse.scanned;
        victimName = victim.getName();
        killerName = killer.getName();

        // manage victim info
        switch (victimRole) {
            case TRAITOR:
                victimName = ChatColor.DARK_RED + victimName;
                victimInfoItem.setType(Material.RED_CONCRETE);
                break;
            case DETECTIVE:
                victimName = ChatColor.DARK_BLUE + victimName;
                victimInfoItem.setType(Material.BLUE_CONCRETE);
                break;
            case INNOCENT:
                victimName = ChatColor.GREEN + victimName;
                victimInfoItem.setType(Material.LIME_CONCRETE);
                break;
        }

        ItemMeta victimInfoItemMeta = victimInfoItem.getItemMeta();
        victimInfoItemMeta.setDisplayName(victimName);
        victimInfoItemMeta.setLore(lore);

        // manage killer info
        if(!scanned) killerName = ChatColor.MAGIC + killerName;
        else switch (victimRole) {
                case TRAITOR:
                    victimName = ChatColor.DARK_RED + victimName;
                    break;
                case DETECTIVE:
                    victimName = ChatColor.DARK_BLUE + victimName;
                    break;
                case INNOCENT:
                    victimName = ChatColor.GREEN + victimName;
                    break;
            }

        ItemMeta killerInfoItemMeta = killerInfoItem.getItemMeta();
        killerInfoItemMeta.setDisplayName(killerName);
        killerInfoItemMeta.setLore(lore);

        // manage cause info
        ItemMeta causeInfoItemMeta = causeInfoItem.getItemMeta();
        causeInfoItemMeta.setDisplayName(cause);
        causeInfoItemMeta.setLore(lore);

        // manage head shot info
        ItemMeta headShotInfoMeta = headShotInfo.getItemMeta();
        if(headShot) {
            headShotInfo.setType(Material.BARRIER);
            headShotInfoMeta.setDisplayName(ChatColor.RED + "This corps can't be revived!");
        }
        else {
            headShotInfo.setType(Material.TOTEM_OF_UNDYING);
            headShotInfoMeta.setDisplayName(ChatColor.DARK_GREEN + "This corps can be revived!");
        }
        headShotInfoMeta.setLore(lore);

        invTitle = Main.hideText("TTT_Menu") + "Corpse of " + victimName;
    }

    public Inventory genInv() {

        Inventory inv = Bukkit.createInventory(null, 9, invTitle);

        inv.setItem(0, empty);
        inv.setItem(1, victimInfoItem);
        inv.setItem(2, empty);
        inv.setItem(3, causeInfoItem);
        inv.setItem(4, empty);
        inv.setItem(5, killerInfoItem);
        inv.setItem(6, empty);
        inv.setItem(7, headShotInfo);
        inv.setItem(8, empty);

        return inv;
    }


}
