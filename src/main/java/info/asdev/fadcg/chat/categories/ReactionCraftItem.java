package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactionCraftItem extends ReactionCategory {
    private List<Material> toCraft;
    @Getter private final ReactionMode mode = ReactionMode.CRAFT_ITEM;

    public ReactionCraftItem(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        toCraft = new ArrayList<>();
        List<String> items;
        items = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());

        for (String item : items) {
            try {
                Material material = Material.valueOf(item.toUpperCase());
                if (!material.isItem()) {
                    throw new IllegalArgumentException(item + " is not an item.");
                }

                toCraft.add(material);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for CRAFT_ITEM: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }
    }

    @Override public boolean attempt(Player who, String message, @Nullable Event event) {
        CraftItemEvent event1 = (CraftItemEvent) event;
        Player whoClicked = (Player) event1.getWhoClicked();
        return whoClicked.equals(who) && toCraft.contains(event1.getInventory().getResult().getType());
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }

    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
