package net.server.ttt.system.items;

import net.server.ttt.system.items.abstracts.TTTItem;
import net.server.ttt.system.items.abstracts.TTTItemWeapon;
import net.server.ttt.system.items.spawns.generic.*;
import net.server.ttt.system.items.spawns.special.Kunai;
import net.server.ttt.system.items.spawns.special.Taser;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TTTItemList {

    static Scout scout = new Scout();
    static Shotgun shotgun = new Shotgun();
    static Pistol pistol = new Pistol();
    static SMG smg = new SMG();
    static LMG lmg = new LMG();
    static AssaultRifle assaultRifle = new AssaultRifle();

    static Taser taser = new Taser();
    static Kunai kunai = new Kunai();

    public static Map<String, TTTItemWeapon> genericSpawnMap = new HashMap<>();
    static {
        genericSpawnMap.put(scout.getId(), scout);
        genericSpawnMap.put(shotgun.getId(), shotgun);
        genericSpawnMap.put(pistol.getId(), pistol);
        genericSpawnMap.put(smg.getId(), smg);
        genericSpawnMap.put(lmg.getId(), lmg);
        genericSpawnMap.put(assaultRifle.getId(), assaultRifle);
    }
    public static Map<String, TTTItemWeapon> superSpawnMap = new HashMap<>();
    static {
        superSpawnMap.put(taser.getId(), taser);
        superSpawnMap.put(kunai.getId(), kunai);

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
        // generic weapons
        totalItemStackMap.put(scout.getId(), scout.getItemStack());
        totalItemStackMap.put(scout.getAmmoId(), scout.getAmmoStack());
        totalItemStackMap.put(shotgun.getId(), shotgun.getItemStack());
        totalItemStackMap.put(shotgun.getAmmoId(), shotgun.getAmmoStack());
        totalItemStackMap.put(pistol.getId(), pistol.getItemStack());
        totalItemStackMap.put(pistol.getAmmoId(), pistol.getAmmoStack());
        totalItemStackMap.put(smg.getId(), smg.getItemStack());
        totalItemStackMap.put(smg.getAmmoId(), smg.getAmmoStack());
        totalItemStackMap.put(lmg.getId(), lmg.getItemStack());
        totalItemStackMap.put(lmg.getAmmoId(), lmg.getAmmoStack());
        totalItemStackMap.put(assaultRifle.getId(), assaultRifle.getItemStack());
        totalItemStackMap.put(assaultRifle.getAmmoId(), assaultRifle.getAmmoStack());

        // super weapons
        totalItemStackMap.put(taser.getId(), taser.getItemStack());
        totalItemStackMap.put(taser.getAmmoId(), taser.getAmmoStack());
        totalItemStackMap.put(kunai.getId(), kunai.getItemStack());
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
