package net.egis.ethicalvoting.data.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.egis.ethicalvoting.EthicalVoting;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class EthicalProfile implements Comparable<EthicalProfile> {

    private UUID uuid;
    private String lastUsername;
    private int votes;

    public void incrementVotes(int v) {
        votes+=v;
        EthicalVoting plugin = EthicalVoting.getSelf();
        plugin.getProfiles().saveProfile(this);
    }

    public void updateUsername(String username) {
        lastUsername = username;
        EthicalVoting plugin = EthicalVoting.getSelf();
        plugin.getProfiles().saveProfile(this);
    }

    @Override
    public int compareTo(EthicalProfile o) {
        return o.getVotes() - votes;
    }

}
