package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactionBlockPlace extends ReactionCategory {
    private List<Material> toPlace;
    @Getter private final ReactionMode mode = ReactionMode.BLOCK_PLACE;

    public ReactionBlockPlace(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        toPlace = new ArrayList<>();
        List<String> blocks = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());

        for (String block : blocks) {
            try {
                Material material = Material.valueOf(block.toUpperCase());
                toPlace.add(material);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for VILLAGER_TRADE: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }
    }

    @Override public boolean attempt(Player who, String message, Event event) {
        BlockPlaceEvent event1 = (BlockPlaceEvent) event;
        return event1.getPlayer().equals(who) && toPlace.contains(event1.getBlock().getType());
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }

    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
