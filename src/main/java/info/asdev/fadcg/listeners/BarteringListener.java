package info.asdev.fadcg.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.events.PlayerBarterEvent;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.utils.Util;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Stream;

// This only exists as PiglinBarterEvent doesn't have a fucking Player.
// In other words, there's no better way to fucking do this.
// And I'm not creating a PR just for it be denied because Spigot/Paper dev ego, so.
public class BarteringListener implements Listener {

    private NamespacedKey tempKey;
    private Map<String, Piglin> bartering = new HashMap<>();

    public BarteringListener() {
        tempKey = new NamespacedKey(Fadcg.getInstance(), "temp_barter_item_owner");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player who = event.getPlayer();
        Chunk[] surrounding = Util.getSurroundingChunks(who.getWorld().getChunkAt(who.getLocation()));

        List<Piglin> piglins = new ArrayList<>();
        for (Chunk chunk : surrounding) {
            Entity[] entities = chunk.getEntities();
            Stream.of(entities).forEach(entity -> { if (entity instanceof Piglin piglin) piglins.add(piglin); });
        }

        if (piglins.isEmpty()) {
            return;
        }

        PersistentDataContainer pdc = event.getItemDrop().getPersistentDataContainer();
        pdc.set(tempKey, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!event.getEntityType().equals(EntityType.PIGLIN)) {
            return;
        }

        PersistentDataContainer pdc = event.getItem().getPersistentDataContainer();
        if (!pdc.has(tempKey)) {
            return;
        }

        String uuid = pdc.get(tempKey, PersistentDataType.STRING);
        Player who = Bukkit.getPlayer(UUID.fromString(uuid));

        if (who == null) {
            pdc.remove(tempKey);
            return;
        }

        bartering.putIfAbsent(uuid, (Piglin) event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (!event.getEntityType().equals(EntityType.PIGLIN)) {
            return;
        }

        Piglin piglin = (Piglin) event.getEntity();

        if (!bartering.containsValue((piglin))) {
            return;
        }

        String uuid = null;
        for (Map.Entry<String, Piglin> vals : bartering.entrySet()) {
            if (vals.getValue().equals(piglin)) {
                uuid = vals.getKey();
                break;
            }
        }
        if (uuid == null) {
            return;
        }

        Player who = Bukkit.getPlayer(UUID.fromString(uuid));
        if (who == null) {
            return;
        }

        bartering.remove(uuid);
        Bukkit.getPluginManager().callEvent(new PlayerBarterEvent(who, (Piglin) event.getEntity(), event.getItemDrop().getItemStack()));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPickupItem(PlayerAttemptPickupItemEvent event) {
        PersistentDataContainer pdc = event.getItem().getPersistentDataContainer();
        if (!pdc.has(tempKey)) {
            return;
        }
        pdc.remove(tempKey);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBarter(PlayerBarterEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.PIGLIN_BARTER, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!event.getEntityType().equals(EntityType.PIGLIN)) {
            return;
        }

        handleDeath((Piglin) event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        if (!event.getEntityType().equals(EntityType.PIGLIN)) {
            return;
        }

        handleDeath((Piglin) event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMorph(EntityTransformEvent event) {
        if (!event.getEntity().getType().equals(EntityType.PIGLIN)) {
            return;
        }

        handleDeath((Piglin) event.getEntity());
    }

    public void handleDeath(Piglin piglin) {
        String uuid = null;
        for (Map.Entry<String, Piglin> vals : bartering.entrySet()) {
            if (vals.getValue().equals(piglin)) {
               uuid = vals.getKey();
                break;
            }
        }

        if (uuid != null) {
            bartering.remove(uuid);
        }
    }
}
