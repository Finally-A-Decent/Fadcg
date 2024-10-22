package info.asdev.fadcr.listeners;

import info.asdev.fadcr.chat.ChatManager;
import info.asdev.fadcr.utils.Text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")
public class ChatListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!ChatManager.getInstance().isRunning() || event.isCancelled() || !event.isAsynchronous()) {
            return;
        }

        ChatManager.getInstance().processChatMessage(event.getPlayer(), event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        ChatManager.getInstance().onPlayerLeave(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("26decebc-0c64-453f-98c5-939c42d17a08")) {
            Text.sendNoFetch(event.getPlayer(), "&c&l(!) &7Update checking for FADCR is disabled");
        }
    }
}
