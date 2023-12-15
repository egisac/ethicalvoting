package net.egis.ethicalvoting.data.interfaces;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.Getter;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.config.EthicalConfig;
import net.egis.ethicalvoting.data.StorageInterface;
import net.egis.ethicalvoting.data.player.EthicalProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class YamlInterface implements StorageInterface {

    private final EthicalConfig config;

    public YamlInterface(EthicalVoting plugin) {
        config = new EthicalConfig(plugin, "data", false);
    }

    @CanIgnoreReturnValue
    @Override
    public boolean jdbcInit(String url, String user, String pass) {
        return false;
    }

    @Override
    public String getAdapterType() {
        return "yaml";
    }

    @Override
    public List<EthicalProfile> getProfiles() {
        Set<String> singleLayer = config.getConfig().getKeys(false);
        List<EthicalProfile> profiles = new ArrayList<>();

        for (String key : singleLayer) {
            UUID uuid = UUID.fromString(key);
            int votes = config.getConfig().getInt(uuid + ".votes");
            String lastUsername = config.getConfig().getString(uuid + ".lastUsername");

            EthicalProfile profile = new EthicalProfile(uuid, lastUsername, votes);
            profiles.add(profile);
        }

        return profiles;
    }

    @Override
    public EthicalProfile getProfile(UUID uuid) {
        if (config.getConfig().contains(uuid.toString())) {
            int votes = config.getConfig().getInt(uuid + ".votes");
            String lastUsername = config.getConfig().getString(uuid + ".lastUsername");

            return new EthicalProfile(uuid, lastUsername, votes);
        }

        return null;
    }

    @Override
    public EthicalProfile getProfile(String lastUsername) {
        Set<String> singleLayer = config.getConfig().getKeys(false);

        for (String key : singleLayer) {
            String username = config.getConfig().getString(key + ".lastUsername");
            if (username == null) continue;
            if (!username.equalsIgnoreCase(lastUsername)) continue;
            UUID uuid = UUID.fromString(key);
            int votes = config.getConfig().getInt(key + ".votes");
            return new EthicalProfile(uuid, lastUsername, votes);
        }

        return null;
    }

    @Override
    public boolean saveProfile(EthicalProfile profile) {
        try {
            config.getConfig().set(profile.getUuid().toString() + ".lastUsername", profile.getLastUsername());
            config.getConfig().set(profile.getUuid().toString() + ".votes", profile.getVotes());
            config.save();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getGlobalVotes() {
        int total = 0;
        Set<String> singleLayer = config.getConfig().getKeys(false);
        for (String key : singleLayer) {
            total += config.getConfig().getInt(key + ".votes");
        }
        return total;
    }
}
