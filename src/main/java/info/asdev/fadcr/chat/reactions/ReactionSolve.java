package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Random;

@Getter
@RequiredArgsConstructor
public class ReactionSolve implements Reaction {
    private final String id, displayName;
    private ReactionImpl implementation;
    private String question;
    private String answer;

    @Override public void init() {
        Random random = getChatManager().getRandom();
        ReactionImpl[] impls = getChatManager().getReactionsById(id);
        implementation = impls.length == 1 ? impls[0] : impls[random.nextInt(impls.length)];

        answer = implementation.getAnswer();
        question = implementation.getQuestion();
    }

    @Override public boolean attempt(Player who, String message) {
        return answer.equalsIgnoreCase(message);
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + id, false, question);
    }

    @Override public void reset() {
        answer = null;
    }
}
