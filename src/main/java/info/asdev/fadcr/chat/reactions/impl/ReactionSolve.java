package info.asdev.fadcr.chat.reactions.impl;

import info.asdev.fadcr.chat.reactions.Reaction;
import info.asdev.fadcr.chat.reactions.ReactionImpl;
import info.asdev.fadcr.config.ReactionConfigManager;
import info.asdev.fadcr.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReactionSolve implements Reaction {
    private final String id, displayName;
    private ReactionImpl implementation;
    private String question;
    private String answer;

    @Override public void init() {
        List<ReactionImpl> implementations = ReactionConfigManager.getReactionImplementationsById(id);
        implementation = implementations.size() == 1 ? implementations.getFirst() : implementations.get(getRandom().nextInt(implementations.size()));

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
