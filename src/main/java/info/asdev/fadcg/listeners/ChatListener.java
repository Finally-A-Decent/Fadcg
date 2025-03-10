package info.asdev.fadcg.listeners;

import info.asdev.fadcg.managers.ChatManager;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!ChatManager.getInstance().isRunning() || !event.isAsynchronous()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTrade(PlayerTradeEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ChatManager.getInstance().onPlayerLeave(event);
    }
}
