package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.ReactionManager;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import io.papermc.paper.event.player.PlayerTradeEvent;
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

public class ReactionTrade extends ReactionCategory {
    private List<Material> toTrade;
    @Getter private final ReactionMode mode = ReactionMode.VILLAGER_TRADE;

    public ReactionTrade(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        toTrade = new ArrayList<>();
        List<String> trades = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());

        for (String trade : trades) {
            try {
                Material material = Material.valueOf(trade.toUpperCase());
                toTrade.add(material);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for VILLAGER_TRADE: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }

    }

    @Override public boolean attempt(Player who, String message, @Nullable Event event) {
        PlayerTradeEvent event1 = (PlayerTradeEvent) event;
        return who.equals(event1.getPlayer()) && toTrade.contains(event1.getTrade().getResult().getType());
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }
    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
