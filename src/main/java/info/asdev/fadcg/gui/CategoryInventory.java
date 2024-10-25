package info.asdev.fadcg.gui;

import info.asdev.fadcg.gui.lib.FastInv;
import info.asdev.fadcg.gui.lib.GuiClickEvent;
import info.asdev.fadcg.gui.lib.ItemBuilder;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import info.asdev.fadcg.utils.Util;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Function;

public class CategoryInventory extends FastInv implements Gui {
    public CategoryInventory(int size) {
        super(size);
    }

    public CategoryInventory(int size, String title) {
        super(size, title);
    }

    public CategoryInventory(Function<InventoryHolder, Inventory> inventoryFunction) {
        super(inventoryFunction);
    }
    private Map<String, ReactionCategory> categories;

    public void init() {
        categories = ReactionCategory.getInstances();
        ReactionCategory[] values = categories.values().toArray(new ReactionCategory[0]);

        fillBorders();

        for (int i = 0; i < categories.size(); i++) {
            boolean enabled = !values[i].isDisabled();
            ItemBuilder builder = new ItemBuilder(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
            builder.name(Text.legacyMessage(String.join("", enabled ? "&a" : "&c", Util.capitalizeFirst(values[i].getId()).replace("_", " "))));

            addTryAvoidBorder(i, builder.build(), this::openCategory);
        }
    }

    private void openCategory(GuiClickEvent event) {
        event.getPlayer().sendMessage(String.valueOf((char)event.getSlot()));
    }
}
