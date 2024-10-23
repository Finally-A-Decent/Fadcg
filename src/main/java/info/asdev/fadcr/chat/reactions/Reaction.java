package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.ChatManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public interface Reaction {
    String getId();
    String getDisplayName();
    boolean attempt(Player who, String message);

    ReactionImpl getImplementation();
    String getQuestion();
    String getAnswer();

    void reset();

    default FADCR getPlugin() {
        return FADCR.getInstance();
    }
    // TODO: Change this to a lang.yml instead of the config.
    default FileConfiguration getLang() {
        return FADCR.getLang();
    }

    default ChatManager getChatManager() {
        return ChatManager.getInstance();
    }

    default ReactionImpl[] getReactions() {
        return getChatManager().getReactionsById(getId());
    }
}
