package info.asdev.fadcg.gui;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class GuiManager {
    private CategoryInventory categoriesInv;

   public void openCategoriesInventory(Player who) {
        categoriesInv = new CategoryInventory(54, "Categories", who);
        categoriesInv.open(who);
   }
}
