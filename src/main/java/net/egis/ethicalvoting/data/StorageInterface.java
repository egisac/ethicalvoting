package net.egis.ethicalvoting.data;

import net.egis.ethicalvoting.data.player.EthicalProfile;

import java.util.List;
import java.util.UUID;

public interface StorageInterface {

    boolean jdbcInit(String url, String user, String pass);
    String getAdapterType();
    List<EthicalProfile> getProfiles();
    EthicalProfile getProfile(UUID uuid);
    EthicalProfile getProfile(String lastUsername);
    boolean saveProfile(EthicalProfile profile);
    int getGlobalVotes();

}
