package net.egis.ethicalvoting.commands;

import net.egis.ethicalvoting.inventories.LeaderboardInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class EthicalVotingCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If first arg equals leaderboard, then show leaderboard - add null checks where appropriate END

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /vote <player>");
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;

        //If first arg equals leaderboard, then show leaderboard - add null checks where appropriate START
        String arg = args[0];
        if (arg.equalsIgnoreCase("leaderboard")) {
            //Show leaderboard
            LeaderboardInventory leaderboardInventory = new LeaderboardInventory(player, "leaderboard", "Leaderboard", 3);
            leaderboardInventory.open();

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
