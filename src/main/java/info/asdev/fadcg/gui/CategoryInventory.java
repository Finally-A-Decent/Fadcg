package info.asdev.fadcg.gui;

import info.asdev.fadcg.gui.lib.GuiClickEvent;
import info.asdev.fadcg.gui.lib.ItemBuilder;
import info.asdev.fadcg.gui.lib.pagination.PaginatedFastInv;
import info.asdev.fadcg.gui.lib.pagination.PaginatedItem;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryInventory extends PaginatedFastInv {
    private Map<String, ReactionCategory> categories;
    List<ItemStack> categoryItems = new ArrayList<>();
    private ItemStack[] pagination;

    protected CategoryInventory(int size, @NotNull String title, @NotNull Player player) {
        super(size, title, player);

        categories = ReactionCategory.getInstances();
        ReactionCategory[] values = categories.values().toArray(new ReactionCategory[0]);

        for (int i = 0; i < categories.size(); i++) {
            boolean enabled = !values[i].isDisabled();
            ItemBuilder builder = new ItemBuilder(enabled ? Material.LIME_CONCRETE : Material.RED_CONCRETE);
            builder.name(Text.legacyMessage(String.join("", enabled ? "&a" : "&c", Text.capitalizeFirst(values[i].getId()).replace("_", " "))));

            categoryItems.add(builder.build());
        }

        pagination = new ItemStack[3];
        pagination[0] = new ItemBuilder(Material.ARROW)
                .name(Text.legacyMessage(Text.getMessage("gui.pagination.previous", false)))
                .build();
        pagination[1] = new ItemBuilder(Material.ARROW)
                .name(Text.legacyMessage(Text.getMessage("gui.pagination.next", false)))
                .build();
        pagination[2] = new ItemBuilder(Material.BARRIER)
                .name(Text.legacyMessage(Text.getMessage("gui.close", false)))
                .build();

        fillBorders();
        fillPaginationItems();
        addPaginationControls();
    }

    private void openCategory(GuiClickEvent event) {
        event.getPlayer().sendMessage(String.valueOf((char)event.getSlot()));
    }

    @Override protected void fillPaginationItems() {
        categoryItems.forEach(item -> addPaginationItem(new PaginatedItem(item, this::openCategory)));

    }

    @Override protected void addPaginationControls() {
        // 39, 40, 41
        setItem(39, pagination[0], event -> previousPage());
        setItem(40, pagination[2], event -> player.closeInventory());
        setItem(41, pagination[1], event -> nextPage());
    }
}
