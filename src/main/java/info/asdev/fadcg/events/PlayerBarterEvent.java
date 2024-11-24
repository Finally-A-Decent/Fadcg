package info.asdev.fadcg.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * THIS EVENT IS NOT YET CALLED
 * @deprecated Not called yet.*/
@Deprecated
public class PlayerBarterEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    @Getter private final Piglin piglin;
    @Getter final ItemStack itemStack;

    public PlayerBarterEvent(@NotNull Player who, Piglin piglin, ItemStack itemStack) {
        super(who);
        this.piglin = piglin;
        this.itemStack = itemStack;
    }

    @NotNull @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
