package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
public class ReactionFinishPhrase extends ReactionCategory {
    private String question;
    private String answer;

    public ReactionFinishPhrase(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init() {
        question = getActiveImplementation().getQuestion();
        answer = getActiveImplementation().getAnswer();
    }

    @Override public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, question);
    }
}
