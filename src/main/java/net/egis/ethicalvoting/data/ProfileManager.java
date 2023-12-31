package net.egis.ethicalvoting.data;

import lombok.Getter;
import net.egis.ethicalvoting.EthicalVoting;
import net.egis.ethicalvoting.data.player.EthicalProfile;
import net.egis.ethicalvoting.lists.PagedList;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.UUID;

@Getter
public class ProfileManager {

    private final StorageInterface storage;
    private final List<EthicalProfile> profiles;
    private final BukkitTask sortProfilesTask;

    public ProfileManager(StorageInterface storage) {
        profiles = storage.getProfiles();
        this.storage = storage;
        sortProfilesTask = Bukkit.getScheduler().runTaskTimerAsynchronously(EthicalVoting.getSelf(), new SortPlayersTask(this), 0, 20*60*5);
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

    public void sortProfiles() {
        profiles.sort(EthicalProfile::compareTo);
    }

    public void shutdown() {
        sortProfilesTask.cancel();
    }

    public PagedList getPaged() {
        return new PagedList(profiles);
    }

}
