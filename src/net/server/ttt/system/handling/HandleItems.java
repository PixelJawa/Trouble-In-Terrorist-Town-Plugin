package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.items.TTTItem;
import net.server.ttt.system.items.TTTItemList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class HandleItems implements Listener {

    public static List<Player> holding = new ArrayList<>();

    // give player shield when change to tttItem
    @EventHandler
    public void onChangeItem(PlayerItemHeldEvent event) {

        // init player var
        Player player = event.getPlayer();

        // abort if player has no role meta
        if(!player.hasMetadata("ttt_role")) return;

        World world = player.getWorld();

        // check if combat is enabled
        if(!HandleGame.gameThreadMap.containsKey(world)) return;
        if(!HandleGame.gameThreadMap.get(world).isCombat) return;

        Inventory inv = player.getInventory();
        ItemStack item = inv.getItem(event.getNewSlot());

        // abort if item has not meta
        if(!item.hasItemMeta()) return;

        // init meta var
        ItemMeta meta = item.getItemMeta();

        // abort if item has no lore or meta
        if(meta == null) return;
        if(!meta.hasLore()) return;

        // init id var
        String id = Main.revealText(meta.getLore().get(0));

        if(!TTTItemList.totalTTTItemMap.containsKey(id)) return;
        if(!id.contains("ttt_item_weapon")) return;

        // create shield item stack
        ItemStack shield = new ItemStack(Material.SHIELD,1 );
        ItemMeta shieldMeta = shield.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(Main.hideText(id));
        lore.add(ChatColor.DARK_GRAY + "Why are you reading this?");
        meta.setDisplayName(" ");
        meta.setLore(lore);
        shield.setItemMeta(shieldMeta);

        // give shield item into player offhand
        inv.setItem(40, shield);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // init player var
        Player player = event.getPlayer();

        // abort if the event has no item
        if(!event.hasItem()) return;
        // abort if player has no role meta
        if(!player.hasMetadata("ttt_role")) return;

        // init world var
        World world = player.getWorld();

        // check if combat is enabled
        if(!HandleGame.gameThreadMap.containsKey(world)) return;
        if(!HandleGame.gameThreadMap.get(world).isCombat) return;

        // init item
        ItemStack item = event.getItem();

        // abort if item has not meta
        if(!item.hasItemMeta()) return;

        // init meta var
        ItemMeta meta = item.getItemMeta();

        // abort if item has no lore or meta
        if(meta == null) return;
        if(!meta.hasLore()) return;

        // init id var
        String id = Main.revealText(meta.getLore().get(0));

        if(!id.contains("ttt_item")) return;
        if(!TTTItemList.totalTTTItemMap.containsKey(id)) return;

        // init tttItem var
        TTTItem tttItem = TTTItemList.totalTTTItemMap.get(id);

        // check if conditions are clear
        if(!tttItem.conditionsClear(player)) return;

        // init action var
        Action action = event.getAction();

        // handle right click of weapons (if holding doesn't contain player)
        if(id.contains("ttt_item_weapon") && action.name().contains("RIGHT_CLICK") && !holding.contains(player)) {
            weaponRight(tttItem, player);
            holding.add(player);
            return;
        }

        // cast action
        if(action.name().contains("LEFT_CLICK")) tttItem.leftAction(player);
        else if(action.name().contains("RIGHT_CLICK")) tttItem.rightAction(player);

    }

    // this methods creates a thread the checks if the player is blocking (necessary because the PLayerInteractEvent only triggers every 4 ticks when holding)
    public void weaponRight(TTTItem item, Player player) {

        new BukkitRunnable() {
            public void run() {

                // check if conditions are clear
                if(item.conditionsClear(player))
                    item.rightAction(player);

                if(!player.isBlocking()) {
                    holding.remove(player);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 1);
    }
}
