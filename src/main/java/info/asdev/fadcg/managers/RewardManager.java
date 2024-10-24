package info.asdev.fadcg.managers;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.reaction.Reward;
import info.asdev.fadcg.utils.RandomSelector;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class RewardManager {
    private Map<String, Reward> rewards = new HashMap<>();
    private Fadcg plugin;
    private File file;
    private FileConfiguration config;
    private InputStream defaults;

    private RandomSelector<Reward> rewardSelector;

    public void init() {
        plugin = Fadcg.getInstance();

        try {
            file = new File(plugin.getDataFolder(), "rewards.yml");
            defaults = plugin.getResource("rewards.yml");

            if (!file.exists()) {
                plugin.saveResource("rewards.yml", false);
            }

            config = YamlConfiguration.loadConfiguration(file);
            if (defaults != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaults)));
            }

            config.save(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        loadRewards();
    }

    private void loadRewards() {
        Set<String> keys = config.getKeys(false);

        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);
            List<String> commands = section.getStringList("commands");
            String displayName = section.getString("display_name", "REWARD NAME");
            double chance = section.getDouble("chance", 1d);

            rewards.put(key, new Reward(commands, displayName, chance));
        }

        rewardSelector = RandomSelector.weighted(rewards.values(), Reward::getChance);
    }

    public Reward getRandomReward() {
        return rewardSelector.next(ChatManager.getInstance().getRandom());
    }

    @Nullable
    public Reward getReward(String id) {
        return rewards.getOrDefault(id, null);
    }
}
