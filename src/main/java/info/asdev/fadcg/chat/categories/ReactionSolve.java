package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
public class ReactionSolve extends ReactionCategory {
    private String question;
    private String answer;

    public ReactionSolve(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        answer = implementation.getAnswer();
        question = implementation.getQuestion();
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

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, question);
    }
    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getAnswersAsString());
    }
}
