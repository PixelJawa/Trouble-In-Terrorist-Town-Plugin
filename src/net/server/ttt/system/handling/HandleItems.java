package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.items.TTTItem;
import net.server.ttt.system.items.TTTItemList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
        //if(!HandleGame.gameThreadMap.containsKey(world)) return;
        //if(!HandleGame.gameThreadMap.get(world).isCombat) return; TODO uncomment this

        Inventory inv = player.getInventory();
        ItemStack toItem = inv.getItem(event.getNewSlot());
        ItemStack fromItem = inv.getItem(event.getPreviousSlot());

        if(toItem == null) {
            inv.setItem(40, null);

            // cast sneakEndAction
            String fromID = null;
            try {
                fromID = Main.revealText(fromItem.getItemMeta().getLore().get(0));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(fromID != null) {
                // init tttItem var
                TTTItem tttItem = TTTItemList.totalTTTItemMap.get(fromID);
                tttItem.sneakEndAction(player, fromItem);
            }

            return;
        }

        // abort if toItem has no meta
        if (!toItem.hasItemMeta()) {
            inv.setItem(40, null);

            // cast sneakEndAction
            String fromID = null;
            try {
                fromID = Main.revealText(fromItem.getItemMeta().getLore().get(0));
            } catch (Exception e) {
               // e.printStackTrace();
            }
            if(fromID != null) {
                // init tttItem var
                TTTItem tttItem = TTTItemList.totalTTTItemMap.get(fromID);
                tttItem.sneakEndAction(player, fromItem);
            }

            return;
        }

        // init meta var
        ItemMeta meta = toItem.getItemMeta();

        // abort if toItem has no lore or meta
        if (meta == null) {
            inv.setItem(40, null);

            // cast sneakEndAction
            String fromID = null;
            try {
                fromID = Main.revealText(fromItem.getItemMeta().getLore().get(0));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(fromID != null) {
                // init tttItem var
                TTTItem tttItem = TTTItemList.totalTTTItemMap.get(fromID);
                tttItem.sneakEndAction(player, fromItem);
            }

            return;
        }
        if (!meta.hasLore()) {
            inv.setItem(40, null);

            // cast sneakEndAction
            String fromID = null;
            try {
                fromID = Main.revealText(fromItem.getItemMeta().getLore().get(0));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(fromID != null) {
                // init tttItem var
                TTTItem tttItem = TTTItemList.totalTTTItemMap.get(fromID);
                tttItem.sneakEndAction(player, fromItem);
            }

            return;
        }

        // init id var
        String id = Main.revealText(meta.getLore().get(0));

        if (TTTItemList.totalTTTItemMap.containsKey(id) && id.contains("ttt_item_weapon")) {

            // create shield toItem stack
            ItemStack shield = new ItemStack(Material.SHIELD, 1);
            ItemMeta shieldMeta = shield.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add(Main.hideText(id));
            lore.add(ChatColor.DARK_GRAY + "Why are you reading this?");
            shieldMeta.setDisplayName(" ");
            shieldMeta.setLore(lore);
            shield.setItemMeta(shieldMeta);

            // give shield toItem into player offhand
            inv.setItem(40, shield);
        }
        else {

            String fromID = null;
            try {
                fromID = Main.revealText(fromItem.getItemMeta().getLore().get(0));
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(fromID != null) {
                // init tttItem var
                TTTItem tttItem = TTTItemList.totalTTTItemMap.get(fromID);
                tttItem.sneakEndAction(player, fromItem);
            }

            inv.setItem(40, null);
        }
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
        //if(!HandleGame.gameThreadMap.containsKey(world)) return; TODO uncomment this
        //if(!HandleGame.gameThreadMap.get(world).isCombat) return; TODO uncomment this

        // init item
        ItemStack item = event.getItem();

        if(item == null) return;

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

        // init action var
        Action action = event.getAction();

        if(holding.contains(player)) return;

        // handle right click of weapons (if holding doesn't contain player)
        if(id.contains("ttt_item_weapon") && action.name().contains("RIGHT_CLICK") && item.getType() == Material.SHIELD) {

            item = player.getEquipment().getItemInMainHand();

            weaponHold(tttItem, player, item);
            holding.add(player);
            return;
        }

        // cast action
        if(action.name().contains("LEFT_CLICK") && tttItem.conditionsLeftClear(player)) {
            tttItem.leftAction(player, item);
            event.setCancelled(true);
        }
        else if(action.name().contains("RIGHT_CLICK") && tttItem.conditionsRightClear(player)) {
            tttItem.rightAction(player, item);
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {

        // init player var
        Player player = event.getPlayer();

        // init world var
        World world = player.getWorld();

        // check if combat is enabled
        //if(!HandleGame.gameThreadMap.containsKey(world)) return; TODO uncomment this
        //if(!HandleGame.gameThreadMap.get(world).isCombat) return; TODO uncomment this

        ItemStack item = player.getEquipment().getItemInMainHand();
        if(item.getType() == Material.AIR) return;

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

        if(event.isSneaking()) tttItem.sneakStartAction(player, item);
        else tttItem.sneakEndAction(player, item);
    }

    // this methods creates a thread the checks if the player is blocking (necessary because the PLayerInteractEvent only triggers every 4 ticks when holding)
    public void weaponHold(TTTItem tttItem, Player player, ItemStack itemStack) {

        // TODO prevent slow

        new BukkitRunnable() {
            public void run() {

                // check if conditions are clear
                if(tttItem.conditionsRightClear(player)) {
                    tttItem.rightAction(player, itemStack);
                }

                if(!player.isHandRaised()) {
                    player.setWalkSpeed(0.2f);
                    holding.remove(player);
                    cancel();
                }
            }
        }.runTaskTimer(Main.getInstance(), 1, 1);
    }
}
