package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ChatGameImpl;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import io.papermc.paper.event.player.PlayerTradeEvent;
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

    public ReactionTrade(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ChatGameImpl implementation) {
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
