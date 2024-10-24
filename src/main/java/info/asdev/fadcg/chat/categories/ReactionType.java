package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
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
    public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }
    public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, answer);
    }
}
