package info.asdev.fadcr.chat.reactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ReactionType implements Reaction {
    @Getter private final String id, displayName;
    @Getter private String answer;
    @Getter private ReactionImpl implementation;

    @Override public boolean attempt(Player who, String message) {
        return getChatManager().areAnswersCaseSensitive() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public String getQuestion() {
        if (answer == null) {
            ReactionImpl[] reactions = getReactions();
            implementation = reactions.length == 1 ? reactions[0] :  reactions[getChatManager().getRandom().nextInt(reactions.length)];
            return answer = implementation.getAnswer();
        }

        return answer;
    }

    @Override public void reset() {
        answer = null;
    }
}
