package info.asdev.fadcg.chat.reactions.impl;

import info.asdev.fadcg.chat.reactions.Reaction;
import info.asdev.fadcg.chat.reactions.ReactionImpl;
import info.asdev.fadcg.utils.Text;
import info.asdev.fadcg.utils.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@RequiredArgsConstructor
public class ReactionUnscramble implements Reaction {
    @Getter private final String id, displayName;
    @Getter private ReactionImpl implementation;
    @Getter private String answer;
    private String question;

    @Override public void init() {
        List<ReactionImpl> implementations = getReactions();

        implementation = implementations.size() == 1 ? implementations.getFirst() : implementations.get(getRandom().nextInt(implementations.size()));
        answer = implementation.getAnswer();
        question = Util.scramble(answer);
    }

    @Override
    public boolean attempt(Player who, String message) {
        return getChatManager().isCaseSensitiveAnswers() ? answer.equals(message) : answer.equalsIgnoreCase(message);
    }

    @Override public void reset() {
        answer = null;
        question = null;
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + id, false, question);
    }

}
