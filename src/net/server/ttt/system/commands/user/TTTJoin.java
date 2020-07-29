package net.server.ttt.system.commands.user;

import net.server.ttt.main.Main;
import net.server.ttt.system.handling.HandleGame;
import net.server.ttt.system.handling.HandleWorldCreation;

import net.server.ttt.system.utils.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TTTJoin implements CommandExecutor {

    // join the first open TTT world
    // create a new TTT world and join it of no other world is open

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // check if sender is player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        // check for sufficient permissions
        if(!sender.hasPermission("ttt_cmd_join")) {
            sender.sendMessage(ChatColor.RED + "No sufficient permission.");
            return true;
        }

        // inti vars
        Player player = (Player) sender;
        List<World> worlds = Bukkit.getWorlds();

        // loop through all worlds and join the first open one
        if(cmd.getName().equalsIgnoreCase("tttJoin")) {

            if(player.getWorld().hasMetadata("ttt_world")) {
                sender.sendMessage(ChatColor.RED + "You are already in a TTT world.");
                return true;
            }

            for(World w : worlds) {
                if (w.hasMetadata("ttt_world") && w.hasMetadata("ttt_state")) {

                    if(w.getMetadata("ttt_state").get(0).value().equals(GameState.CLOSED)) continue;

                    if (w.getPlayers().size() >= Main.getInstance().getConfig().getInt("Max.Players") ) continue;
                    HandleGame.joinGame(player, w);
                    return true;
                }
            }

            // create new world when no open world exists
            player.sendMessage(ChatColor.GREEN + "A new game is being created. Please be patient.");
            World w = HandleWorldCreation.createRand();
            HandleGame.joinGame(player, w);
            return true;
        }

        return false;
    }

}
