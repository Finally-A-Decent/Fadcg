package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.ChatManager;
import info.asdev.fadcr.config.ReactionConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public interface Reaction {
    void init();
    boolean attempt(Player who, String message);
    void reset();

    String getId();
    String getDisplayName();
    ReactionImpl getImplementation();
    String getAnswer();

    String getMessage();

    default FADCR getPlugin() {
        return FADCR.getInstance();
    }
    default FileConfiguration getLang() {
        return FADCR.getLang();
    }
    default ChatManager getChatManager() {
        return ChatManager.getInstance();
    }
    default List<ReactionImpl> getReactions() {
        return ReactionConfigManager.getReactionImplementationsById(getId());
    }
    default Random getRandom() {
        return getChatManager().getRandom();
    }
}
