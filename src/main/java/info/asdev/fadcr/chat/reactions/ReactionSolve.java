package info.asdev.fadcr.chat.reactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Random;

@RequiredArgsConstructor
public class ReactionSolve implements Reaction {
    @Getter private final String id, displayName;
    @Getter private ReactionImpl implementation;
    private String question;
    @Getter private String answer;

    @Override public boolean attempt(Player who, String message) {
        return answer.equalsIgnoreCase(message);
    }

    @Override public String getQuestion() {
        if (question == null || answer == null) {
            Random random = getChatManager().getRandom();
            ReactionImpl[] impls = getChatManager().getReactionsById(id);
            implementation = impls.length == 1 ? impls[0] : impls[random.nextInt(impls.length)];

            answer = implementation.getAnswer();
            return question = implementation.getQuestion();
        }

        return question;
    }

    @Override public void reset() {
        answer = null;
    }
}
