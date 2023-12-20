package net.egis.ethicalvoting.rewards.types;

import lombok.Getter;
import lombok.Setter;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.utils.Translate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.List;

@Getter @Setter
public class CumulativeReward {
    private int requiredVotes;
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
    private String bossBarText;
    private String bossBarColor;

    public CumulativeReward(int requiredVotes, List<String> commands, List<String> messages) {
        this.requiredVotes = requiredVotes;
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

        if (hasFirework) {
            Color color = Color.LIME;

            switch(firework) {
                case "LIME":
                    color = Color.LIME;
                    break;
                case "RED":
                    color = Color.RED;
                    break;
                case "BLUE":
                    color = Color.BLUE;
                    break;
                case "AQUA":
                    color = Color.AQUA;
                    break;
                case "BLACK":
                    color = Color.BLACK;
                    break;
                case "FUCHSIA":
                    color = Color.FUCHSIA;
                    break;
                case "GRAY":
                    color = Color.GRAY;
                    break;
                case "GREEN":
                    color = Color.GREEN;
                    break;
                case "MAROON":
                    color = Color.MAROON;
                    break;
                case "NAVY":
                    color = Color.NAVY;
                    break;
                case "PURPLE":
                    color = Color.PURPLE;
                    break;
                case "WHITE":
                    color = Color.WHITE;
                    break;
                case "YELLOW":
                    color = Color.YELLOW;
                    break;
            }

            FireworkEffect effect = FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(color).build();
            Firework firework = player.getWorld().spawn(player.getLocation().add(0, 1, 0), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.setDisplayName("ethdevfwk");
            meta.addEffect(effect);
            meta.setPower(0);
            firework.setFireworkMeta(meta);
            firework.detonate();
        }

        if (hasSound) {
            player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
        }

        if (hasBossBar) {
            BossBar bar = Bukkit.createBossBar(Translate.translate(player, bossBarText), BarColor.valueOf(bossBarColor), BarStyle.SOLID);
            bar.addPlayer(player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> bar.removePlayer(player), 100L);
        }
    }
}
