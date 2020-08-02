package net.server.ttt.system.handling;

import net.server.ttt.system.utils.enums.Role;
import net.server.ttt.system.utils.menus.DetectiveShop;
import net.server.ttt.system.utils.menus.TraitorShop;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class HandleShop implements Listener {

    // this class manages everything that has to do with the Traitor and Detective shop

    // triggers when a player moves an item into his offhand
    public void onOffhand(PlayerSwapHandItemsEvent event) {

        System.out.println("1");

        Player player = event.getPlayer();

        if(!player.hasMetadata("ttt_role")) return;

        Role role = (Role) player.getMetadata("ttt_role").get(0).value();

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
}
