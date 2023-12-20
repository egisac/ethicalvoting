package net.egis.ethicalvoting.commands;

import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import net.egis.ethicalvoting.inventories.LeaderboardInventory;
import net.egis.ethicalvoting.lists.PagedList;
import net.egis.ethicalvoting.utils.Translate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EthicalVotingCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //If first arg equals leaderboard, then show leaderboard - add null checks where appropriate END

        if (args.length == 0) {
            List<String> message = Translate.translate(EthicalVoting.getSelf().getConfig().getStringList("messages.help"));
            message.forEach(sender::sendMessage);
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return false;
        }

        Player player = (Player) sender;

        //If first arg equals leaderboard, then show leaderboard - add null checks where appropriate START
        String arg = args[0];

        if (arg.equalsIgnoreCase("leaderboard") || arg.equalsIgnoreCase("top")) {
            boolean useGui = EthicalVoting.getSelf().getConfig().getBoolean("leaderboard.use_gui");

            //Show leaderboard
            if (useGui) {
                if (args.length != 2) {
                    LeaderboardInventory leaderboardInventory = new LeaderboardInventory(player, "leaderboard", "Leaderboard", 3, 0);
                    leaderboardInventory.open();
                } else {
                    Integer n = silentParse(args[1]);
                    if (n == null) {
                        player.sendMessage("§cUsage: /vote leaderboard <page>");
                        return false;
                    }

                    LeaderboardInventory leaderboardInventory = new LeaderboardInventory(player, "leaderboard", "Leaderboard", 3, n);
                    leaderboardInventory.open();
                }
            } else {
                PagedList leaderboard = EthicalVoting.getSelf().getProfiles().getPaged();
                String header = EthicalVoting.getSelf().getConfig().getString("leaderboard.leaderboard_header");
                String footer = EthicalVoting.getSelf().getConfig().getString("leaderboard.leaderboard_footer");
                String entry = EthicalVoting.getSelf().getConfig().getString("leaderboard.leaderboard_entry");

                if (args.length != 2) {
                    List<?> page = leaderboard.getPage(0, 10);

                    player.sendMessage(Translate.translate(player, header));
                    for (Object o : page) {
                        int place = page.indexOf(o) + 1;
                        EthicalProfile profile = (EthicalProfile) o;
                        String message = entry
                                .replace("%position%", String.valueOf(place))
                                .replace("%player%", profile.getLastUsername())
                                .replace("%votes%", String.valueOf(profile.getVotes()));
                        player.sendMessage(Translate.translate(player, message));
                    }
                    footer = footer.replace("%page%", "1");
                    footer = footer.replace("%max_pages%", String.valueOf(leaderboard.getPages(10)));
                    player.sendMessage(Translate.translate(player, footer));
                } else {
                    Integer n = silentParse(args[1]);
                    if (n == null) {
                        player.sendMessage("§cUsage: /vote leaderboard <page>");
                        return false;
                    }

                    List<?> page = leaderboard.getPage(n-1, 10);

                    player.sendMessage(Translate.translate(player, header));
                    for (Object o : page) {
                        int place = (page.indexOf(o) + 1) * n;
                        EthicalProfile profile = (EthicalProfile) o;
                        String message = entry
                                .replace("%position%", String.valueOf(place))
                                .replace("%player%", profile.getLastUsername())
                                .replace("%votes%", String.valueOf(profile.getVotes()));
                        player.sendMessage(Translate.translate(player, message));
                    }
                    footer = footer.replace("%page%", String.valueOf(n));
                    footer = footer.replace("%max_pages%", String.valueOf(leaderboard.getPages(10)));
                    player.sendMessage(Translate.translate(player, footer));
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("leaderboard");
            return list;
        }

        return null;
    }

    private Integer silentParse(String i) {
        try {
            return Integer.parseInt(i);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
