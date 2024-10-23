package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class ReactionFinishPhrase implements Reaction {
    private final String id, displayName;

    private String question;
    private String answer;
    private ReactionImpl implementation;

    @Override public void init() {
    }

    @Override public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public void reset() {

    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + id, false, question);
    }
}
