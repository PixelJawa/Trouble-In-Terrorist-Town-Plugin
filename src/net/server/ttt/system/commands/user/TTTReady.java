package net.server.ttt.system.commands.user;

import net.server.ttt.system.handling.HandleGame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TTTReady implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // check if sender is player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        // check for sufficient permissions
        if(!sender.hasPermission("ttt_cmd_ready")) {
            sender.sendMessage(ChatColor.RED + "No sufficient permission.");
            return true;
        }

        // inti vars
        Player player = (Player) sender;

        // loop through all worlds and join the first open one
        if(cmd.getName().equalsIgnoreCase("tttReady")) {

            if(!player.getWorld().hasMetadata("ttt_world")) {
                player.sendMessage(ChatColor.RED + "You are not in a TTT world.");
                return true;
            }

            HandleGame.readyPlayer(player, player.getWorld());
        }
        return false;
    }

}
