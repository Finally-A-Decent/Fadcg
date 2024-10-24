package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import info.asdev.fadcg.utils.Util;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ReactionUnscramble extends ReactionCategory {
    @Getter private String answer;
    private String question;

    public ReactionUnscramble(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init() {
        answer = getActiveImplementation().getAnswer();
        question = Util.scramble(answer);
    }

    @Override
    public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, question);
    }
}
