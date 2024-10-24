package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Getter
public class ReactionSolve extends ReactionCategory {
    private String question;
    private String answer;

    public ReactionSolve(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init() {
        answer = getActiveImplementation().getAnswer();
        question = getActiveImplementation().getQuestion();
    }

    @Override public boolean attempt(Player who, String message) {
        return answer.equalsIgnoreCase(message);
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, question);
    }
}
