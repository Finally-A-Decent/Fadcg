package info.asdev.fadcg.listeners;

import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.utils.Text;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!ChatManager.getInstance().isRunning() || !event.isAsynchronous()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.CHAT_MESSAGE, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.BLOCK_BREAK, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.BLOCK_PLACE, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(CraftItemEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.CRAFT_ITEM, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.USE_ITEM, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }
        if (event.getEntity().getKiller() == null) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.KILL_MOB, event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTrade(PlayerTradeEvent event) {
        if (!ChatManager.getInstance().isRunning()) {
            return;
        }

        ChatManager.getInstance().onPlayerEvent(ReactionMode.VILLAGER_TRADE, event);
    }





    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ChatManager.getInstance().onPlayerLeave(event);
    }
    // hi prevail
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("26decebc-0c64-453f-98c5-939c42d17a08")) {
            Text.sendNoFetch(event.getPlayer(), "&c&l(!) &7Update checking for Fadcg is disabled");
        }
    }
}
