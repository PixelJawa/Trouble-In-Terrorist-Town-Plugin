package net.server.ttt.system.utils.menus;

import net.server.ttt.system.items.TTTItemList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TraitorMenu {

    // TODO this


    static List<ItemStack> itemList = new ArrayList<>();
    static{
        for(String s : TTTItemList.traitorBuyMap.keySet())
            itemList.add(TTTItemList.traitorBuyMap.get(s).getItem());
    }

    public Inventory genInv() {

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_RED + "Traitor Shop");
        return inv;
    }
}
