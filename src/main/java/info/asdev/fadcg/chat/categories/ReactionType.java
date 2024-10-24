package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
public class ReactionType extends ReactionCategory {
    private String answer;

    public ReactionType(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    public void init() {
        answer = getActiveImplementation().getAnswer();
    }
    @Override public boolean attempt(Player who, String message, Event event) {
        if (!getActiveImplementation().hasMultipleAnswers()) {
            return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
        }

        for (String answer : getActiveImplementation().getAnswers()) {
            boolean correct = getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
            if (correct) return true;
        }

        return false;
    }
    public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, answer);
    }
}
