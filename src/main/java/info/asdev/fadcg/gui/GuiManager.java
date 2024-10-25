package info.asdev.fadcg.gui;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class GuiManager {
    private CategoryInventory categoriesInv;

    public void init() {
        categoriesInv = new CategoryInventory(54, "Reaction Categories");
        categoriesInv.init();
    }

   public void openCategoriesInventory(Player who) {
        categoriesInv.open(who);
   }


}
