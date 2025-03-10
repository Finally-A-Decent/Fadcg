package info.asdev.fadcg.gui.lib.pagination;

import info.asdev.fadcg.gui.lib.GuiClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public record PaginatedItem(ItemStack itemStack, Consumer<GuiClickEvent> eventConsumer) {
}