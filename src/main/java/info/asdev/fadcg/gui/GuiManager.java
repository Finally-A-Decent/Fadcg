package info.asdev.fadcg.gui;

import info.asdev.fadcg.gui.lib.FastInv;

public class GuiManager {
    private static GuiManager instance;
    private GuiManager() {}

    private FastInv categoriesInv;

    public void init() {
        categoriesInv = new FastInv(36);

        for (int i = 0; i < 1; i++) {}
    }

    public void shutdown() {

    }

    public static GuiManager getInstance() {
        return instance == null ? instance = new GuiManager() : instance;
    }
}
