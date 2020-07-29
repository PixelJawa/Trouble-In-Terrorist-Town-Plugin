package net.server.ttt.system.handling;

import net.server.ttt.main.Main;
import net.server.ttt.system.items.TTTItem;
import net.server.ttt.system.items.TTTItemList;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HandleItems implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // init player var
        Player player = event.getPlayer();

        // abort if the event has no item
        if(!event.hasItem()) return;
        // abort if player has no role meta
        if(!player.hasMetadata("ttt_role")) return;

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

        String id = Main.revealText(meta.getLore().get(0));

        if(!id.contains("ttt_item")) return;

        if(!TTTItemList.totalItemMap.containsKey(id)) return;

        TTTItem tttItem = TTTItemList.totalItemMap.get(id);

        // init action var
        Action action = event.getAction();

        // cast action
        if(action.name().contains("LEFT_CLICK")) tttItem.leftAction();
        else if(action.name().contains("RIGHT_CLICK")) tttItem.rightAction();

        // TODO this
    }
}
