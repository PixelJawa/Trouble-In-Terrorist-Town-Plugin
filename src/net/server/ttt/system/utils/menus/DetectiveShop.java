package net.server.ttt.system.utils.menus;

import net.server.ttt.system.items.TTTItemList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DetectiveShop {

    // TODO this

    static List<ItemStack> itemList = new ArrayList<>();
    static{
        for(String s : TTTItemList.detectiveBuyMap.keySet())
            itemList.add(TTTItemList.detectiveBuyMap.get(s).getItem());
    }

    public static Inventory genInv() {

        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_BLUE + "Detective Shop");
        return inv;
    }
}
