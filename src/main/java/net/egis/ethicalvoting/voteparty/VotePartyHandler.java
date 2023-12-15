package net.egis.ethicalvoting.voteparty;

import lombok.Getter;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.rewards.types.VoteReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VotePartyHandler {

    private final EthicalVoting plugin;
    private int requiredVotes;
    private List<VoteReward> rewards;

    public VotePartyHandler(EthicalVoting plugin) {
        this.plugin = plugin;
        requiredVotes = plugin.getConfig().getInt("vote_party.required_votes");
        if (requiredVotes <= 0) requiredVotes = 1;

        rewards = new ArrayList<>();
        ConfigurationSection rewardsSection = plugin.getConfig().getConfigurationSection("vote_party.rewards");
        rewardsSection.getKeys(false).forEach(key -> {
            ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(key);
            List<String> commands = rewardSection.getStringList("commands");
            List<String> messages = rewardSection.getStringList("messages");

            VoteReward vr = new VoteReward(commands, messages);

            if (rewardSection.contains("required_services")) {
                vr.setHasRequiredServices(true);
                vr.setRequiredServices(rewardSection.getStringList("required_services"));
            }

            if (rewardSection.contains("firework")) {
                vr.setHasFirework(true);
                vr.setFirework(rewardSection.getString("firework"));
            }

            if (rewardSection.contains("particle")) {
                vr.setHasParticle(true);
                vr.setParticle(rewardSection.getString("particle"));
            }

            if (rewardSection.contains("sound")) {
                vr.setHasSound(true);
                vr.setSound(rewardSection.getString("sound"));
            }

            if (rewardSection.contains("title")) {
                vr.setHasTitle(true);
                vr.setTitle(rewardSection.getString("title"));
            }

            if (rewardSection.contains("subtitle")) {
                vr.setHasSubtitle(true);
                vr.setSubtitle(rewardSection.getString("subtitle"));
            }

            if (rewardSection.contains("bossbar")) {
                vr.setHasBossBar(true);
                vr.setBossBar(rewardSection.getString("bossbar"));
            }

            rewards.add(vr);
        });
    }

    public void checkVoteParty() {
        int globalVotes = plugin.getStorage().getGlobalVotes();
        if (globalVotes % requiredVotes == 0) {
            performVoteParty();
        }
    }

    public void performVoteParty() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            for (VoteReward reward : rewards) {
                reward.execute(player, plugin);
            }
        }
    }
}
