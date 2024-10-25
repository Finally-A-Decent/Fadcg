package info.asdev.fadcg.managers.reaction;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.ChatManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Getter
@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ReactionCategory {
    @Getter private static final Map<String, ReactionCategory> instances = new HashMap<>();

    public static ReactionCategory get(String id) {
        return instances.getOrDefault(id, null);
    }

    private final List<ReactionImpl> implementations = new ArrayList<>();
    private FileConfiguration config;
    @Setter private ReactionImpl activeImplementation;
    private ChatManager chatManager;

    @NotNull private final Plugin plugin;
    @NotNull private final String id;
    @NotNull private final File file;
    @Nullable private final InputStream defaults;
    @Setter private boolean disabled;

    private ReactionMode mode = ReactionMode.CHAT_MESSAGE;

    public ReactionCategory(Plugin plugin, String id, File file) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null.");
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.file = Objects.requireNonNull(file, "File cannot be null");
        this.defaults = plugin.getResource(String.join("", "reactions/", id, ".yml"));
        this.chatManager = ChatManager.getInstance();

        this.disabled = Fadcg.getInstance().getConfig().getStringList("options.disabled-chat-reactions").contains(id.toLowerCase());

        loadConfig();
        loadImplementations();
        instances.putIfAbsent(id, this);
    }

    private void loadConfig() {
        Validate.notNull(file, "Cannot load a config from a null file, id " + id);

        try {
            if (!file.exists()) {
                plugin.saveResource(String.join("", "reactions/", file.getName()), false);
            }
            config = YamlConfiguration.loadConfiguration(file);

            if (defaults != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaults)));
            }

            config.save(file);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadImplementations() {
        implementations.clear();

        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            ConfigurationSection section = config.getConfigurationSection(key);

            String question = section.getString("question");
            List<String> answers = section.getStringList("answers");
            String reward = section.getString("reward");

            if (answers.isEmpty()) {
                answers = List.of(section.getString("answer"));
            }

            implementations.add(new ReactionImpl(id, key, id, question, answers, reward));
        }
    }

    public void create() {
        activeImplementation = implementations.get(ChatManager.getInstance().getRandom().nextInt(implementations.size()));
    }

    public ReactionImpl getImplementationById(String id) {
        for (ReactionImpl implementation : implementations) {
            if (implementation.getId().equalsIgnoreCase(id)) return implementation;
        }

        return null;
    }

    public ReactionImpl getImplementationByPath(String path) {
        for (ReactionImpl implementation : implementations) {
            if (implementation.getPath().equalsIgnoreCase(path)) return implementation;
        }

        return null;
    }

    public final void init() {
        init(activeImplementation);
    }

    public abstract void init(ReactionImpl implementation);

    public abstract boolean attempt(Player who, String message, @Nullable Event event);

    public abstract String getMessage();

    public abstract String getExpiryMessage();
}
