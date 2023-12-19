package net.egis.ethicalvoting.data;

public class SortPlayersTask implements Runnable {

    private final ProfileManager profileManager;

    public SortPlayersTask(ProfileManager profileManager) {
        this.profileManager = profileManager;
    }

    @Override
    public void run() {
        profileManager.sortProfiles();
    }

}
