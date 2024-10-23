package info.asdev.fadcr.config;

import com.google.common.collect.Lists;
import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.ChatManager;
import info.asdev.fadcr.chat.reactions.*;
import info.asdev.fadcr.chat.reactions.impl.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@UtilityClass
public class ReactionConfigManager {
    private final Map<String, List<ReactionImpl>> loadedReactions = new HashMap<>();
    private final Map<String, Config> reactionConfigs = new HashMap<>();

    @Getter private File reactionsFolder;
    @Getter private File rewardsFile;
    @Getter private Config rewardsConfig;

    public void init() {
        loadedReactions.clear();
        reactionConfigs.clear();

        reactionsFolder = new File(FADCR.getInstance().getDataFolder(), "reactions/");
        loadReactionConfigs();
    }

    private void loadReactionConfigs() {
        if (!reactionsFolder.exists()) {
            reactionsFolder.mkdir();
        }

        String[] ids = new String[]{
                "unscramble",
                "solve",
                "type",
                "finish_phrase",
                "reverse"
        };

        List<String> disabledTypes = FADCR.getInstance().getConfig().getStringList("options.disabled-chat-reactions");

        for (String id : ids) {
            if (disabledTypes.contains(id)) {
                FADCR.getInstance().getLogger().info("Skipping reaction type " + id + " as it isn disabled in the config.");
                continue;
            }

            File file = new File(reactionsFolder, "reactions/" + id + ".yml");
            if (!file.exists())
            {
                saveDefaults("reactions/" + id + ".yml", false);
            }

            Config config = new Config(new File(reactionsFolder, id + ".yml"));
            config.reload();

            reactionConfigs.put(id, config);
        }

        for (String id : reactionConfigs.keySet()) {
            loadReactionsById(id);
        }

        // Load reward config
        rewardsFile = new File(FADCR.getInstance().getDataFolder(), "rewards.yml");
        if (!rewardsFile.exists()) {
            saveDefaults("rewards.yml", false);
        }

        rewardsConfig = new Config(rewardsFile);
        rewardsConfig.reload();
        rewardsConfig.setDefaults(FADCR.getInstance().getResource("rewards.yml"));
    }

    public void loadReactionsById(String id) {
        for (Map.Entry<String, Config> configV : reactionConfigs.entrySet()) {
            FileConfiguration config = configV.getValue().config;
            Set<String> keys = config.getKeys(false);
            for (String key : keys) {
                ConfigurationSection path = config.getConfigurationSection(key);
                ReactionImpl implementation = new ReactionImpl(
                        id,
                        key,
                        id,
                        path.getString("question"),
                        path.getString("answer"),
                        path.getString("reward")
                );

                if (!loadedReactions.containsKey(configV.getKey())) {
                    loadedReactions.put(configV.getKey(), Lists.newArrayList(implementation));
                    continue;
                }

                List<ReactionImpl> implementations = loadedReactions.get(configV.getKey());
                implementations.add(implementation);
                loadedReactions.replace(configV.getKey(), implementations);
            }
        }
    }

    public void saveDefaults(String resourceName, boolean replace) {
        FADCR fadcr = FADCR.getInstance();
        if (fadcr.getResource(resourceName) != null) {
            fadcr.saveResource(resourceName, replace);
        }
    }

    public List<ReactionImpl> getReactionImplementationsById(String id) {
        return loadedReactions.containsKey(id) ? loadedReactions.get(id) : new ArrayList<>();
    }

    public Reaction random() {
        String id = "unscramble";
        Random random = ChatManager.getInstance().getRandom();
        id = reactionConfigs.keySet().toArray(new String[0])[random.nextInt(reactionConfigs.size())];

        return switch(id) {
            case "solve" -> new ReactionSolve("solve", "Solve");
            case "type" -> new ReactionType("type", "Type");
            case "reverse" -> new ReactionReverse("reverse", "Reverse");
            case "finish_phrase" -> new ReactionFinishPhrase("finish_phrase", "Finish Phrase");
            default -> new ReactionUnscramble("unscramble", "Unscramble");
        };
    }

    public Config getConfig(String id) {
        return reactionConfigs.getOrDefault(id, null);
    }

    @RequiredArgsConstructor
    public static class Config {
        private final File configFile;
        @Getter private FileConfiguration config, defaults;

        public void reload() {
            config = YamlConfiguration.loadConfiguration(configFile);
        }

        public void setDefaults(InputStream resource) {
            if (resource == null) {
                return;
            }

            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(resource)));
        }
    }

}
