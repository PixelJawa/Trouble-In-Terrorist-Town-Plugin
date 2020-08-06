package net.server.ttt.system.commands.admin;

import net.server.ttt.system.items.TTTItemList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TTTGive implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // check if sender is player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        // check for sufficient permissions
        if(!sender.hasPermission("ttt_cmd_give")) {
            sender.sendMessage(ChatColor.RED + "No sufficient permission.");
            return true;
        }

        if(args.length == 0 || args.length > 2) return false;

        // inti vars
        Player player = (Player) sender;
        String key = args[0];
        int amount = 1;

        if(args.length == 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                amount = 1;
            }
        }

        // loop through all worlds and join the first open one
        if(cmd.getName().equalsIgnoreCase("tttGive")) {

            if(!TTTItemList.totalItemStackMap.containsKey(key)) {
                player.sendMessage(ChatColor.AQUA + "That item doesn't exist!");
                return true;
            }

            for(int i = 0; i < amount; i++)
                player.getInventory().addItem(TTTItemList.totalItemStackMap.get(key));

            return true;
        }
        return false;
    }

}
