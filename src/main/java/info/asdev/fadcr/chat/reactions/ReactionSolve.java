package info.asdev.fadcr.chat.reactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ReactionSolve implements Reaction {
    @Getter private final String id, displayName;

    @Override public boolean attempt(Player who, String message) {
        return false;
    }

    @Override public String getQuestion() {
        return "";
    }
}
