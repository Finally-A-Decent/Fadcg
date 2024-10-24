package info.asdev.fadcg.chat.reactions.impl;

import info.asdev.fadcg.chat.reactions.Reaction;
import info.asdev.fadcg.chat.reactions.ReactionImpl;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReactionType implements Reaction {
    private final String id, displayName;
    private String answer;
    private ReactionImpl implementation;

    @Override public void init() {
        List<ReactionImpl> reactions = getReactions();
        implementation = reactions.size() == 1 ? reactions.getFirst(): reactions.get(getRandom().nextInt(reactions.size()));
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
