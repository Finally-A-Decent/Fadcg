package info.asdev.fadcg.managers;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

@UtilityClass
public class ReactionManager {

    // TODO:
    // Load files from reactions/
    // Register all

    private Fadcg plugin;
    @Getter private File configFolder;

    public void init() {
        plugin = Fadcg.getInstance();

        // Load files from reactions
        configFolder = new File(plugin.getDataFolder(), "reactions/");
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        File[] files = configFolder.listFiles(file -> file.getName().toLowerCase().endsWith(".yml"));
        if (files == null) {
            throw new RuntimeException("Files returned null.");
        }

        // Register all reactions
        Stream.of(files).forEach(file -> new ReactionCategory(
                plugin,
                file.getName().substring(0, file.getName().length() - 4).toLowerCase(),
                file
        ));

        // Initialize subtypes

    }

    public void register(Plugin plugin, String id, File file) {
        if (ReactionManager.plugin.equals(plugin)) {
            throw new IllegalArgumentException("You cannot register reactions as Fadcg.");
        }

        try {
            new ReactionCategory(plugin, id, file);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.WARNING, "Failed to load reaction type " + id, ex);
        }
    }

    public ReactionCategory createReaction() {
        Random random = ChatManager.getInstance().getRandom();
        String[] keys = ReactionCategory.getInstances().keySet().toArray(new String[0]);
        ReactionCategory category = ReactionCategory.get(keys[random.nextInt(keys.length)]);

        category.create();
        return category;
    }
}
