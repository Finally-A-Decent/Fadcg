package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.events.PlayerBarterEvent;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactionBarter extends ReactionCategory {
    public ReactionBarter(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }
    private List<Material> toBarter;

    @Getter private final ReactionMode mode = ReactionMode.PIGLIN_BARTER;

    @Override public void init(ReactionImpl implementation) {
        toBarter = new ArrayList<>();
        List<String> trades = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());

        for (String trade : trades) {
            try {
                Material material = Material.valueOf(trade.toUpperCase());
                toBarter.add(material);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for PIGLIN_BARTER: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }

    }

    @Override public boolean attempt(Player who, String message, @Nullable Event event) {
        PlayerBarterEvent event1 = (PlayerBarterEvent) event;
        return event1.getPlayer().equals(who) && toBarter.contains(event1.getItemStack().getType());
    }


    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }
    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
