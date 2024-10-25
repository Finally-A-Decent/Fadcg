package info.asdev.fadcg.chat.categories;

import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReactionKillMob extends ReactionCategory {
    private List<EntityType> entityTypes;
    @Getter private final ReactionMode mode = ReactionMode.KILL_MOB;

    public ReactionKillMob(Plugin plugin, String id, File file) {
        super(plugin, id, file);
    }

    @Override public void init(ReactionImpl implementation) {
        entityTypes = new ArrayList<>();
        List<String> types;
        types = implementation.hasMultipleAnswers() ? implementation.getAnswers() : List.of(implementation.getAnswer());;

        for (String type : types) {
            try {
                EntityType entityType = null;
                entityType = EntityType.valueOf(type.toUpperCase());

                if (!entityType.isAlive()) {
                    throw new IllegalArgumentException(type + " is not a living entity.");
                }

                entityTypes.add(entityType);
            } catch (EnumConstantNotPresentException | IllegalArgumentException ex) {
                getPlugin().getLogger().log(Level.WARNING, "Unable to add chat reaction answer for KILL_MOB: " + implementation.getAnswer().toUpperCase() + ": " + ex.getMessage());
            }
        }
    }

    @Override public boolean attempt(Player who, String message, @Nullable Event event) {
        EntityDeathEvent event1 = (EntityDeathEvent) event;
        return event1.getEntity().getKiller().equals(who) && entityTypes.contains(event1.getEntityType());
    }

    @Override public String getMessage() {
        return Text.getMessage("reactions." + getId(), false, getActiveImplementation().getQuestion());
    }

    @Override public String getExpiryMessage() {
        return Text.getMessage("chat-reaction.reaction-expired." + getId(), false, getActiveImplementation().getQuestion());
    }
}
