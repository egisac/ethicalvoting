package net.egis.ethicalvoting.data.interfaces;

import lombok.Getter;
import net.egis.ethicalvoting.data.StorageInterface;
import net.egis.ethicalvoting.data.player.EthicalProfile;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLInterface implements StorageInterface {

    @Getter private Connection connection;

    private final String createTable = "CREATE TABLE IF NOT EXISTS ethicalvoting( uuid CHAR(36) NOT NULL, votes INT NOT NULL, lastUsername VARCHAR(16) NOT NULL, CONSTRAINT ethicalvoting_constraint UNIQUE (uuid));";
    private final String insertIntoTable = "INSERT INTO ethicalvoting(uuid, votes, lastUsername) VALUES(?, ?, ?);";
    private final String updateTable = "UPDATE ethicalvoting SET votes = ?, lastUsername = ? WHERE uuid = ?;";
    private final String selectAllProfiles = "SELECT uuid, votes, lastUsername FROM ethicalvoting;";
    private final String selectSingleProfile = "SELECT votes, lastUsername FROM ethicalvoting WHERE uuid = ?;";
    private final String selectSingleProfileUsername = "SELECT uuid, votes FROM ethicalvoting WHERE lastUsername = ?";
    private final String selectGlobalVotes = "SELECT SUM(votes) FROM ethicalvoting;";

    @Override
    public boolean jdbcInit(String url, String user, String pass) {
        try {
            this.connection = DriverManager.getConnection(url, user, pass);
            this.connection.prepareStatement(createTable).execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public String getAdapterType() {
        return "mysql";
    }

    @Override
    public List<EthicalProfile> getProfiles() {
        List<EthicalProfile> profiles = new ArrayList<>();

        try {
            PreparedStatement stmt = connection.prepareStatement(selectAllProfiles);
            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                String uuid = set.getString("uuid");
                int votes = set.getInt("votes");
                String lastUsername = set.getString("lastUsername");

                EthicalProfile profile = new EthicalProfile(UUID.fromString(uuid), lastUsername, votes);
                profiles.add(profile);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profiles;
    }

    @Override
    public EthicalProfile getProfile(UUID uuid) {
        EthicalProfile profile = null;

        try {
            PreparedStatement stmt = connection.prepareStatement(selectSingleProfile);
            stmt.setString(1, uuid.toString());
            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                int votes = set.getInt("votes");
                String lastUsername = set.getString("lastUsername");

                profile = new EthicalProfile(uuid, lastUsername, votes);
            }
        } catch (SQLException e) {}

        return profile;
    }

    @Override
    public EthicalProfile getProfile(String lastUsername) {
        EthicalProfile profile = null;

        try {
            PreparedStatement stmt = connection.prepareStatement(selectSingleProfileUsername);
            stmt.setString(1, lastUsername);
            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                int votes = set.getInt("votes");
                String uuid = set.getString("uuid");

                profile = new EthicalProfile(UUID.fromString(uuid), lastUsername, votes);
            }

        } catch (SQLException e) {}

        return profile;
    }

    @Override
    public boolean saveProfile(EthicalProfile profile) {
        EthicalProfile pf = getProfile(profile.getUuid());

        try {
            if (pf == null) {
                PreparedStatement stmt = connection.prepareStatement(insertIntoTable);
                stmt.setString(1, profile.getUuid().toString());
                stmt.setInt(2, profile.getVotes());
                stmt.setString(3, profile.getLastUsername());
                stmt.execute();
            } else {
                PreparedStatement stmt = connection.prepareStatement(updateTable);
                stmt.setInt(1, profile.getVotes());
                stmt.setString(2, profile.getLastUsername());
                stmt.setString(3, profile.getUuid().toString());
                stmt.execute();
            }

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public int getGlobalVotes() {
        try {
            PreparedStatement stmt = connection.prepareStatement(selectGlobalVotes);
            ResultSet set = stmt.executeQuery();

            while (set.next()) {
                return set.getInt(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }
}
