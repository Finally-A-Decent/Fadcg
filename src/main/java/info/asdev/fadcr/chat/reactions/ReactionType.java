package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class ReactionType implements Reaction {
    private final String id, displayName;
    private String answer;
    private ReactionImpl implementation;

    @Override public void init() {
        ReactionImpl[] reactions = getReactions();
        implementation = reactions.length == 1 ? reactions[0] : reactions[getChatManager().getRandom().nextInt(reactions.length)];
        answer = implementation.getAnswer();
    }

    @Override public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + id, false, answer);
    }

    @Override public void reset() {
        answer = null;
    }
}
