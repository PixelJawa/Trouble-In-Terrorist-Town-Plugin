package net.server.ttt.system.commands.admin;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandlePlayer;
import net.server.ttt.system.items.TTTItemList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class TTTPlayerToggle implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // check if sender is player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        // check for sufficient permissions
        if(!sender.hasPermission("ttt_cmd_playerToggle")) {
            sender.sendMessage(ChatColor.RED + "No sufficient permission.");
            return true;
        }

        // inti vars
        Player player = (Player) sender;

        // loop through all worlds and join the first open one
        if(cmd.getName().equalsIgnoreCase("tttPlayerToggle")) {

            if(player.hasMetadata("ttt_role")) {
                player.removeMetadata("ttt_role", Main.getInstance());
                player.sendMessage(ChatColor.DARK_AQUA + "You no longer have the ttt_role meta!");
            }
            else {
                player.setMetadata("ttt_role", new FixedMetadataValue(Main.getInstance(), null));
                player.sendMessage(ChatColor.AQUA + "You now have the ttt_role meta!");
            }

            return true;
        }
        return false;
    }

}
