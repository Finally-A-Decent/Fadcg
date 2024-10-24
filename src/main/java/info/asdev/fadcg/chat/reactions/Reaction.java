package info.asdev.fadcg.chat.reactions;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.managers.ReactionConfigManager;
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

    default boolean isDisabled() {
        return ReactionConfigManager.isDisabled(getId());
    }
    default Fadcg getPlugin() {
        return Fadcg.getInstance();
    }
    default FileConfiguration getLang() {
        return Fadcg.getLang();
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
