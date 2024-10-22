package info.asdev.fadcr.listeners;

import info.asdev.fadcr.chat.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
}
