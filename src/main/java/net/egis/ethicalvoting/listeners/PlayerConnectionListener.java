package net.egis.ethicalvoting.listeners;

import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener {

    private final EthicalVoting plugin;

    public PlayerConnectionListener(EthicalVoting plugin) {
        this.plugin = plugin;

        for (Player player : Bukkit.getOnlinePlayers()) {
            EthicalProfile profile = plugin.getStorage().getProfile(player.getUniqueId());

            if (profile == null) {
                EthicalProfile pf = new EthicalProfile(player.getUniqueId(), player.getDisplayName(), 0);
                plugin.getProfiles().addProfile(pf);
            } else {
                profile.updateUsername(player.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent e) {
        EthicalProfile profile = plugin.getStorage().getProfile(e.getPlayer().getUniqueId());

        if (profile == null) {
            EthicalProfile pf = new EthicalProfile(e.getPlayer().getUniqueId(), e.getPlayer().getDisplayName(), 0);
            plugin.getProfiles().addProfile(pf);
        } else {
            profile.updateUsername(e.getPlayer().getDisplayName());
        }
    }

}
