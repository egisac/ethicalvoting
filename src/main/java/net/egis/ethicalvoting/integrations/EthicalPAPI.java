package net.egis.ethicalvoting.integrations;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EthicalPAPI extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "ethicalvoting";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Egis";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return List.of("votes", "vote_party");
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        EthicalVoting plugin = EthicalVoting.getSelf();

        if (params.equalsIgnoreCase("votes")) {
            EthicalProfile profile = plugin.getProfiles().getByUUID(player.getUniqueId());

            if (profile == null) {
                return "0";
            }

            return String.valueOf(profile.getVotes());
        }

        if (params.equalsIgnoreCase("vote_party")) {
            return String.valueOf(plugin.getVotePartyHandler().getRemainder());
        }

        return super.onPlaceholderRequest(player, params);
    }
}
