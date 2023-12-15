package net.egis.ethicalvoting.config;

import lombok.Getter;
import lombok.SneakyThrows;
import net.egis.ethicalvoting.EthicalVoting;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class EthicalConfig {

    private final File file;
    private final FileConfiguration config;

    @SneakyThrows
    public EthicalConfig(EthicalVoting plugin, String fileName, boolean resource) {
        this.file = new File(plugin.getDataFolder(), fileName + ".yml");
        this.config = new YamlConfiguration();

        if (!this.file.exists()) {
            if (resource) {
                plugin.saveResource(fileName + ".yml", false);
            } else {
                file.createNewFile();
            }
        }

        try {
            this.config.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() throws IOException {
        config.save(file);
    }

}
