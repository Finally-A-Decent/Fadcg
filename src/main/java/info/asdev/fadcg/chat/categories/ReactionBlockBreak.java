package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactionBlockBreak extends ReactionCategory {
    private List<Material> toBreak;
    @Getter private final ReactionMode mode = ReactionMode.BLOCK_BREAK;

    public ReactionBlockBreak(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        toBreak = new ArrayList<>();
        List<String> blocks = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());

        for (String block : blocks) {
            try {
                Material material = Material.valueOf(block.toUpperCase());
                toBreak.add(material);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for VILLAGER_TRADE: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }
    }

    @Override public boolean attempt(Player who, String message, Event event) {
        BlockBreakEvent event1 = (BlockBreakEvent) event;
        return event1.getPlayer().equals(who) && toBreak.contains(event1.getBlock().getType());
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }

    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
