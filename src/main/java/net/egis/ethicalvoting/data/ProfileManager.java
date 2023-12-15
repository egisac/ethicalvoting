package net.egis.ethicalvoting.data;

import lombok.Getter;
import net.egis.ethicalvoting.data.player.EthicalProfile;

import java.util.List;
import java.util.UUID;

@Getter
public class ProfileManager {

    private final StorageInterface storage;
    private final List<EthicalProfile> profiles;

    public ProfileManager(StorageInterface storage) {
        profiles = storage.getProfiles();
        this.storage = storage;
    }

    public EthicalProfile getByUUID(UUID uuid) {
        for (EthicalProfile profile : profiles) {
            if (profile.getUuid().equals(uuid)) return profile;
        }
        return null;
    }

    public EthicalProfile getByUsername(String username) {
        for (EthicalProfile profile : profiles) {
            if (profile.getLastUsername().equalsIgnoreCase(username)) return profile;
        }
        return null;
    }

    public void addProfile(EthicalProfile profile) {
        profiles.add(profile);
        saveProfile(profile);
    }

    public void saveProfile(EthicalProfile profile) {
        storage.saveProfile(profile);
    }

}
