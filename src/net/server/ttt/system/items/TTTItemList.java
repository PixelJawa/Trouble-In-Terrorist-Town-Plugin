package net.server.ttt.system.items;

import net.server.ttt.system.items.spawns.generic.Scout;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TTTItemList {

    static Scout scout = new Scout();

    public static Map<String, TTTItemWeapon> genericSpawnMap = new HashMap<>();
    static {
        genericSpawnMap.put(scout.getId(), scout);
    }
    public static Map<String, TTTItemWeapon> superSpawnMap = new HashMap<>();
    static {

    }

    public static Map<String, TTTItem> traitorBuyMap = new HashMap<>();
    static {

    }
    public static Map<String, TTTItem> detectiveBuyMap = new HashMap<>();
    static {

    }

    public static Map<String, TTTItem> startItemMap = new HashMap<>();
    static {

    }



    // total item maps
    public static Map<String, ItemStack> totalItemStackMap = new HashMap<>();
    static {
        totalItemStackMap.put(scout.getId(), scout.getItemStack());
        totalItemStackMap.put(scout.getAmmoId(), scout.getAmmoStack());
    }
    public static Map<String, TTTItem> totalTTTItemMap = new HashMap<>();
    static {
        totalTTTItemMap.putAll(genericSpawnMap);
        totalTTTItemMap.putAll(superSpawnMap);
        totalTTTItemMap.putAll(traitorBuyMap);
        totalTTTItemMap.putAll(detectiveBuyMap);
        totalTTTItemMap.putAll(startItemMap);
    }

}
