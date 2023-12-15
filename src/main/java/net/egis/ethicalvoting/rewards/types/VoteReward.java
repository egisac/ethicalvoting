package net.egis.ethicalvoting.rewards.types;

import lombok.Getter;
import lombok.Setter;
import net.egis.ethicalvoting.EthicalVoting;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter @Setter
public class VoteReward {
    private List<String> commands;
    private List<String> messages;

    private boolean hasRequiredServices;
    private List<String> requiredServices;

    private boolean hasFirework;
    private String firework;

    private boolean hasParticle;
    private String particle;

    private boolean hasSound;
    private String sound;

    private boolean hasTitle;
    private String title;

    private boolean hasSubtitle;
    private String subtitle;

    private boolean hasBossBar;
    private String bossBar;

    public VoteReward(List<String> commands, List<String> messages) {
        this.commands = commands;
        this.messages = messages;
    }

    public void execute(Player player, EthicalVoting plugin) {
        for (String command : getCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
        }
        for (String message : getMessages()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("%player%", player.getName())));
        }
    }
}
