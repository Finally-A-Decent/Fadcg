package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.utils.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Random;

@RequiredArgsConstructor
public class ReactionUnscramble implements Reaction {
    @Getter private final String id, displayName;
    @Getter private ReactionImpl implementation;
    @Getter private String answer;
    private String question;

    @Override
    public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override
    public String getQuestion() {
        if (question == null || answer == null) {
            Random random = getChatManager().getRandom();
            ReactionImpl[] impls = getChatManager().getReactionsById(id);

            implementation = impls.length == 1 ? impls[0] : impls[random.nextInt(impls.length)];
            answer = implementation.getAnswer();
            return question = Util.scramble(answer);
        }

        return question;
    }

    @Override public void reset() {
        answer = null;
        question = null;
    }

}
