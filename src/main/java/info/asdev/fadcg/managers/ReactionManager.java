package info.asdev.fadcg.managers;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.categories.*;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.stream.Stream;

@UtilityClass
public class ReactionManager {
    private Fadcg plugin;
    @Getter private File configFolder;

    public void init() {
        plugin = Fadcg.getInstance();

        configFolder = new File(plugin.getDataFolder(), "reactions/");
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        String[] ids = new String[] {
                "finish_phrase",
                "reverse",
                "solve",
                "type",
                "unscramble"
        };

        Stream.of(ids).forEach(id -> {
            File file = new File(configFolder, String.join("", id, ".yml"));
            registerInternalReactionType(file.getName().substring(0, file.getName().length() - 4).toLowerCase(), file);
        });
    }

    private void registerInternalReactionType(String id, File file) {
        switch(id) {
            case "finish_phrase" -> new ReactionFinishPhrase(plugin, id, file);
            case "reverse" -> new ReactionReverse(plugin, id, file);
            case "solve" -> new ReactionSolve(plugin, id, file);
            case "type" -> new ReactionType(plugin, id, file);
            case "unscramble" -> new ReactionUnscramble(plugin, id, file);
            default -> {}
        }
    }

    public ReactionCategory createReaction() {
        Random random = ChatManager.getInstance().getRandom();
        List<String> keys = new ArrayList<>();

        ReactionCategory.getInstances().forEach((k,v) -> {
            if (!v.isDisabled()) keys.add(k);
        });
        if (keys.isEmpty()) {
            return null;
        }

        ReactionCategory category = ReactionCategory.get(keys.get(random.nextInt(keys.size())));
        category.create();

        return category;
    }
}
