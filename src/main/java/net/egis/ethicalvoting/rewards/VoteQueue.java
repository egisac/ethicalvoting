package net.egis.ethicalvoting.rewards;

import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class VoteQueue {

    private final Map<EthicalProfile, Integer> queuedVotes;
    private final VoteRewardHandler voteRewardHandler;
    private final BukkitTask task;

    public VoteQueue(VoteRewardHandler voteRewardHandler) {
        this.queuedVotes = new HashMap<>();
        this.voteRewardHandler = voteRewardHandler;

        task = Bukkit.getScheduler().runTaskTimer(EthicalVoting.getSelf(), () -> {
            for (EthicalProfile profile : queuedVotes.keySet()) {
                attemptReward(profile);
            }
        }, 0L, 20L * 10L);
    }

    public void shutdown() {
        task.cancel();
    }

    public void addVote(EthicalProfile profile) {
        if (queuedVotes.containsKey(profile)) {
            queuedVotes.put(profile, queuedVotes.get(profile) + 1);
        } else {
            queuedVotes.put(profile, 1);
        }
    }

    public void removeVote(EthicalProfile profile) {
        if (queuedVotes.containsKey(profile)) {
            queuedVotes.put(profile, queuedVotes.get(profile) - 1);
        }
    }

    public int getVotes(EthicalProfile profile) {
        return queuedVotes.getOrDefault(profile, 0);
    }

    public void attemptReward(EthicalProfile profile) {
        int votes = getVotes(profile);
        Player player = Bukkit.getPlayer(profile.getUuid());
        if (player == null) return;
        if (votes > 0) {
            for (int i = 0; i < votes; i++) {
                voteRewardHandler.executeRewards(profile);
            }
            queuedVotes.remove(profile);
        } else {
            queuedVotes.remove(profile);
        }
    }

}
