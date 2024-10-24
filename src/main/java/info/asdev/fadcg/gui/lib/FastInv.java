package info.asdev.fadcg.gui.lib;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky
 * @version 3.0.4
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class FastInv implements InventoryHolder {

    private final Map<Integer, Consumer<GuiClickEvent>> itemHandlers = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED)
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<GuiClickEvent>> clickHandlers = new ArrayList<>();

    private final Inventory inventory;

    private Predicate<Player> closeFilter;

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size The size of the inventory.
     */
    public FastInv(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  The size of the inventory.
     * @param title The title (name) of the inventory.
     */
    public FastInv(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }


    public FastInv(Function<InventoryHolder, Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    protected void onOpen(InventoryOpenEvent event) {
    }

    protected void onClick(InventoryClickEvent event) {
    }

    protected void onDrag(InventoryDragEvent event) {
    }

    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot.
     *
     * @param item The ItemStack to add
     */
    public void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    The item to add.
     * @param handler The click handler for the item.
     */
    public void addItem(ItemStack item, Consumer<GuiClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    The slot where to add the item.
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItem(int slot, ItemStack item, Consumer<GuiClickEvent> handler) {
        if (slot == -1) return;
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots.
     *
     * @param slots The slots where to add the item
     * @param item  The item to add.
     */
    public void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   The slots where to add the item
     * @param item    The item to add.
     * @param handler The click handler for the item
     */
    public void setItems(int[] slots, ItemStack item, Consumer<GuiClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot The slot where to remove the item
     */
    public void removeItem(int slot) {
        if (slot == -1) return;
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * Open the inventory to a player.
     *
     * @param player The player to open the menu.
     */
    public void open(Player player) {
        player.openInventory(this.inventory);
    }

    /**
     * Get borders of the inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return inventory borders
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get the Bukkit inventory.
     *
     * @return The Bukkit inventory.
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    public boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    public void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(new GuiClickEvent((Player) e.getWhoClicked(), e.getSlot(), e.getClick())));

        Consumer<GuiClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(new GuiClickEvent((Player) e.getWhoClicked(), e.getSlot(), e.getClick()));
        }
    }

    public void handleDrag(InventoryDragEvent e) {
        onDrag(e);

        for (int slot : e.getNewItems().keySet()) {
            this.clickHandlers.forEach(c -> c.accept(new GuiClickEvent((Player) e.getWhoClicked(), slot, e.getType())));
            Consumer<GuiClickEvent> clickConsumer = this.itemHandlers.get(slot);

            if (clickConsumer != null) {
                clickConsumer.accept(new GuiClickEvent((Player) e.getWhoClicked(), slot, e.getType()));
            }
        }
    }
}