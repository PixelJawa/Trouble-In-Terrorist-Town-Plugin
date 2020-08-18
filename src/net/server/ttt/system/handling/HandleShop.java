package net.server.ttt.system.handling;

import net.server.ttt.system.utils.enums.Role;
import net.server.ttt.system.utils.menus.DetectiveShop;
import net.server.ttt.system.utils.menus.TraitorShop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;

public class HandleShop implements Listener {

    // this class manages everything that has to do with the Traitor and Detective shop

    // triggers when a player moves an item into his offhand via hotkey
    @EventHandler
    public void onOffhandHotkey(PlayerSwapHandItemsEvent event) {

        Player player = event.getPlayer();

        if(!player.hasMetadata("ttt_role")) return;
        Role role = (Role) player.getMetadata("ttt_role").get(0).value();

        if(role == null) {
            event.setCancelled(true);
            return;
        }

        switch (role) {
            case TRAITOR: {
                player.openInventory(TraitorShop.genInv());
                break;
            }
            case DETECTIVE: {
                player.openInventory(DetectiveShop.genInv());
                break;
            }
        }

        event.setCancelled(true);
    }
    @EventHandler
    public void onOffhandMouse(InventoryClickEvent event) {

        Inventory inv = event.getInventory();
        if(inv.getType() != InventoryType.CRAFTING) return;

        // offhand slot: 40
        if(event.getSlot() != 40) return;

        if(!(inv.getHolder() instanceof Player)) return;

        Player player = (Player) inv.getHolder();

        if(!player.hasMetadata("ttt_role")) return;

        event.setCancelled(true);
    }

    // TODO handle click on item in shop

}
