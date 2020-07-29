package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.utils.corpse.Corpse;
import net.server.ttt.system.utils.events.npc.CorpseInteractEvent;
import net.server.ttt.system.utils.menus.CorpseMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HandleCorpseInteraction implements Listener {

    @EventHandler
    public void onCorpseInteract(CorpseInteractEvent event) {

        // init vars
        Corpse corpse = event.getCorpse();
        Player player = event.getPlayer();

        // check if world is TTT world
        if(!player.getWorld().hasMetadata("ttt_world")) return;

        CorpseMenu corpseMenu = new CorpseMenu(corpse);

        player.openInventory(corpseMenu.genInv());
    }

    @EventHandler
    public void onCorpseMenuInteract(InventoryClickEvent event) {

        ItemStack item = event.getCurrentItem();

        if(!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore()) return;

        String id = Main.revealText(meta.getLore().get(0));

        if(id.equals("ttt_item_corpse_menu_uninteractabe"))
            event.setCancelled(true);
    }

}
