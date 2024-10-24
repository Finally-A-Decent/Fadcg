package info.asdev.fadcg.gui.pages;

import org.bukkit.entity.Player;

public interface GuiPage {
    void init(Player player);
    boolean isUniquePerPlayer();

    default int getRows() {
        return 3;
    }

    boolean isPaginated();
    default int getElementsPerPage() {
        return Math.clamp(getRows() * 9L, 9, 45);
    }

    default int getTotalSlots() {
        return getElementsPerPage() + 9;
    }
}
