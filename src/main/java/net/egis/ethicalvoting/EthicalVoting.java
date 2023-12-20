package net.egis.ethicalvoting;

import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import net.egis.ethicalvoting.commands.EthicalVotingCommand;
import net.egis.ethicalvoting.commands.VoteCommand;
import net.egis.ethicalvoting.data.ProfileManager;
import net.egis.ethicalvoting.data.StorageInterface;
import net.egis.ethicalvoting.data.interfaces.MySQLInterface;
import net.egis.ethicalvoting.data.interfaces.YamlInterface;
import net.egis.ethicalvoting.https.UpdateChecker;
import net.egis.ethicalvoting.integrations.EthicalPAPI;
import net.egis.ethicalvoting.listeners.FireworkDamageListener;
import net.egis.ethicalvoting.listeners.PlayerConnectionListener;
import net.egis.ethicalvoting.listeners.VotifierVoteListener;
import net.egis.ethicalvoting.rewards.VoteRewardHandler;
import net.egis.ethicalvoting.utils.Translate;
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
    private FireworkDamageListener fireworkDamageListener;
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
        registerIntegrations();

        getLogger().info("Storage Type: " + storage.getAdapterType());
        checkUpdates();

        getLogger().info("EthicalVoting has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        profiles.shutdown();
        voteRewardHandler.getVoteQueue().shutdown();

        getLogger().info("EthicalVoting has been successfully disabled.");
    }

    public void registerIntegrations() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EthicalPAPI().register();
            getLogger().info("PlaceholderAPI integration has been successfully enabled.");
        } else {
            getLogger().warning("PlaceholderAPI integration has failed to load.");
        }
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
        VoteCommand voteCommand = new VoteCommand();
        getCommand("vote").setExecutor(voteCommand);
        getCommand("vote").setTabCompleter(voteCommand);
    }

    public void loadPluginData() {
        this.profiles = new ProfileManager(storage);
    }

    public void registerEventListeners() {
        this.connectionListener = new PlayerConnectionListener(this);
        this.voteListener = new VotifierVoteListener(this);
        this.fireworkDamageListener = new FireworkDamageListener();
        getServer().getPluginManager().registerEvents(connectionListener, this);
        getServer().getPluginManager().registerEvents(voteListener, this);
        getServer().getPluginManager().registerEvents(fireworkDamageListener, this);
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
                        String prefix = getConfig().getString("messages.prefix");
                        player.sendMessage(Translate.translate(prefix + "&7There is a new &aupdate &7available &fhttps://www.spigot.org/"));
                        getLogger().warning("There is a new update available at https://www.spigot.org/ [" + version + "]");
                    }
                }
            }
        });
    }

}
