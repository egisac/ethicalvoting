package net.egis.ethicalvoting.rewards;

import lombok.Getter;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import net.egis.ethicalvoting.rewards.types.CumulativeReward;
import net.egis.ethicalvoting.rewards.types.VoteReward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VoteRewardHandler {

    private final EthicalVoting plugin;
    private final List<VoteReward> rewards;
    private final List<CumulativeReward> cumulativeRewards;
    private final VoteQueue voteQueue;

    public VoteRewardHandler(EthicalVoting plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();

        rewards = new ArrayList<>();
        cumulativeRewards = new ArrayList<>();
        voteQueue = new VoteQueue(this);

        ConfigurationSection rewardsSection = config.getConfigurationSection("voting_rewards");
        ConfigurationSection cumulativeRewardsSection = config.getConfigurationSection("cumulative_rewards");
        rewardsSection.getKeys(false).forEach(key -> {
            ConfigurationSection rewardSection = rewardsSection.getConfigurationSection(key);
            List<String> commands = rewardSection.getStringList("commands");
            List<String> messages = rewardSection.getStringList("messages");
            VoteReward vr = new VoteReward(commands, messages);

            if (rewardSection.contains("required_services", false)) {
                vr.setHasRequiredServices(true);
                vr.setRequiredServices(rewardSection.getStringList("required_services"));
            }

            if (rewardSection.contains("firework", false)) {
                vr.setHasFirework(true);
                vr.setFirework(rewardSection.getString("firework"));
            }

            if (rewardSection.contains("particle", false)) {
                vr.setHasParticle(true);
                vr.setParticle(rewardSection.getString("particle"));
            }

            if (rewardSection.contains("sound", false)) {
                vr.setHasSound(true);
                vr.setSound(rewardSection.getString("sound"));
            }

            if (rewardSection.contains("title", false)) {
                vr.setHasTitle(true);
                vr.setTitle(rewardSection.getString("title"));
            }

            if (rewardSection.contains("subtitle", false)) {
                vr.setHasSubtitle(true);
                vr.setSubtitle(rewardSection.getString("subtitle"));
            }

            if (rewardSection.contains("bossbar", false)) {
                vr.setHasBossBar(true);
                vr.setBossBarText(rewardSection.getString("bossbar.text"));
                vr.setBossBarColor(rewardSection.getString("bossbar.color"));
            }

            rewards.add(vr);
        });

        cumulativeRewardsSection.getKeys(false).forEach(key -> {
            ConfigurationSection rewardSection = cumulativeRewardsSection.getConfigurationSection(key);
            int requiredVotes = rewardSection.getInt("required_votes");
            List<String> commands = rewardSection.getStringList("commands");
            List<String> messages = rewardSection.getStringList("messages");

            CumulativeReward vr = new CumulativeReward(requiredVotes, commands, messages);

            if (rewardSection.contains("required_services", false)) {
                vr.setHasRequiredServices(true);
                vr.setRequiredServices(rewardSection.getStringList("required_services"));
            }

            if (rewardSection.contains("firework", false)) {
                vr.setHasFirework(true);
                vr.setFirework(rewardSection.getString("firework"));
            }

            if (rewardSection.contains("particle", false)) {
                vr.setHasParticle(true);
                vr.setParticle(rewardSection.getString("particle"));
            }

            if (rewardSection.contains("sound", false)) {
                vr.setHasSound(true);
                vr.setSound(rewardSection.getString("sound"));
            }

            if (rewardSection.contains("title", false)) {
                vr.setHasTitle(true);
                vr.setTitle(rewardSection.getString("title"));
            }

            if (rewardSection.contains("subtitle", false)) {
                vr.setHasSubtitle(true);
                vr.setSubtitle(rewardSection.getString("subtitle"));
            }

            if (rewardSection.contains("bossbar", false)) {
                vr.setHasBossBar(true);
                vr.setBossBarText(rewardSection.getString("bossbar.text"));
                vr.setBossBarColor(rewardSection.getString("bossbar.color"));
            }

            cumulativeRewards.add(vr);
        });
    }

    // Sends out the rewards in-game if the player is online.
    // TODO: Add a way to send rewards to offline players.
    public void executeRewards(EthicalProfile profile) {
        Player player = plugin.getServer().getPlayer(profile.getUuid());
        if (player == null) {
            voteQueue.addVote(profile);
            return;
        }
        for (VoteReward reward : rewards) {
            reward.execute(player, plugin);
        }

        for (CumulativeReward reward : cumulativeRewards) {
            if (profile.getVotes() == 0) return;
            if (profile.getVotes() % reward.getRequiredVotes() == 0) {
                reward.execute(player, plugin);
            }
        }
    }

}
