package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.Reaction;
import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class ReactionReverse implements Reaction {
    private final String id = "reverse";
    private String question, answer;
    private ReactionImpl implementation;

    @Override public void init() {
        implementation = getReactions().size() == 1 ? getReactions().getFirst() : getReactions().get(getRandom().nextInt(getReactions().size()));
        question = implementation.getQuestion();
        answer = implementation.getAnswer();
    }

    @Override public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

//    @Override public void reset() {
//        answer = null;
//        question = null;
//    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + id, false, question);
    }
}