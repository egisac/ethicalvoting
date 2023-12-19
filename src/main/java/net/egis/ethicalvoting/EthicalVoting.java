package net.egis.ethicalvoting;

import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import net.egis.ethicalvoting.commands.EthicalVotingCommand;
import net.egis.ethicalvoting.data.ProfileManager;
import net.egis.ethicalvoting.data.StorageInterface;
import net.egis.ethicalvoting.data.interfaces.MySQLInterface;
import net.egis.ethicalvoting.data.interfaces.YamlInterface;
import net.egis.ethicalvoting.https.UpdateChecker;
import net.egis.ethicalvoting.listeners.PlayerConnectionListener;
import net.egis.ethicalvoting.listeners.VotifierVoteListener;
import net.egis.ethicalvoting.rewards.VoteRewardHandler;
import net.egis.ethicalvoting.voteparty.VotePartyHandler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class EthicalVoting extends JavaPlugin {

    @Getter
    private static EthicalVoting self;

    private StorageInterface storage;
    private ProfileManager profiles;
    private PlayerConnectionListener connectionListener;
    private VotifierVoteListener voteListener;
    private VotePartyHandler votePartyHandler;
    private VoteRewardHandler voteRewardHandler;

    @Override
    public void onEnable() {
        self = this;

        new InventoryAPI(this).init();

        saveDefaultConfig();
        pickStorageType();
        loadPluginData();
        loadFeatures();
        registerEventListeners();
        registerCommands();

        getLogger().info("Storage Type: " + storage.getAdapterType());
        checkUpdates();

        getLogger().info("EthicalVoting has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        profiles.shutdown();

        getLogger().info("EthicalVoting has been successfully disabled.");
    }

    /*
    Defines which database type will be used to store user data.
    Current options:
        - MySQL
        - YAML (Bukkit internal)
     */
    public void pickStorageType() {
        String storageInterface = getConfig().getString("database.type");

        if (storageInterface == null) {
            getLogger().severe("Storage Interface is null. Report this to plugin developer.");
            getServer().shutdown();
            return;
        }

        if (storageInterface.equalsIgnoreCase("mysql")) {
            storage = new MySQLInterface();

            String host = getConfig().getString("database.host");
            int port = getConfig().getInt("database.port");
            String database = getConfig().getString("database.database");
            String username = getConfig().getString("database.username");
            String password = getConfig().getString("database.password");

            if (host == null || database == null || username == null || password == null) {
                getLogger().severe("MySQL credentials are null. Report this to plugin developer.");
                getServer().shutdown();
                return;
            }

            if (!storage.jdbcInit("jdbc:mysql://" + host + ":" + port + "/" + database, username, password)) {
                getLogger().severe("Failed to connect to MySQL database. Report this to plugin developer.");
                getServer().shutdown();
                return;
            }
        } else {
            storage = new YamlInterface(this);
        }
    }

    public void registerCommands() {
        EthicalVotingCommand ethicalVotingCommand = new EthicalVotingCommand();
        getCommand("ethicalvoting").setExecutor(ethicalVotingCommand);
        getCommand("ethicalvoting").setTabCompleter(ethicalVotingCommand);
    }

    public void loadPluginData() {
        this.profiles = new ProfileManager(storage);
    }

    public void registerEventListeners() {
        this.connectionListener = new PlayerConnectionListener(this);
        this.voteListener = new VotifierVoteListener(this);
        getServer().getPluginManager().registerEvents(connectionListener, this);
        getServer().getPluginManager().registerEvents(voteListener, this);
    }

    public void loadFeatures() {
        votePartyHandler = new VotePartyHandler(this);
        voteRewardHandler = new VoteRewardHandler(this);
    }

    public void checkUpdates() {
        new UpdateChecker(this, 12345).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp()) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&x&0&8&4&c&f&b&lE&x&1&c&5&b&f&b&lt&x&3&1&6&a&f&b&lh&x&4&5&7&8&f&c&li&x&5&a&8&7&f&c&lc&x&6&e&9&6&f&c&la&x&8&3&a&5&f&c&ll&x&9&7&b&3&f&c&lV&x&a&b&c&2&f&c&lo&x&c&0&d&1&f&d&lt&x&d&4&e&0&f&d&li&x&e&9&e&e&f&d&ln&x&f&d&f&d&f&d&lg &8&l> &7There is a new &aupdate &7available &fhttps://www.spigot.org/"));
                        getLogger().warning("There is a new update available at https://www.spigot.org/ [" + version + "]");
                    }
                }
            }
        });
    }

}
