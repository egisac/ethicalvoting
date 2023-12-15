package net.egis.ethicalvoting.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VotifierVoteListener implements Listener {

    private final EthicalVoting plugin;

    public VotifierVoteListener(EthicalVoting plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onVote(VotifierEvent e) {
        Vote vote = e.getVote();
        String username = vote.getUsername();
        EthicalProfile profile = plugin.getProfiles().getByUsername(username);
        profile.incrementVotes(1);
        plugin.getVoteRewardHandler().executeRewards(profile);
        plugin.getVotePartyHandler().checkVoteParty();
    }

}
