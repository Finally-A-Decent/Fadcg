package info.asdev.fadcr.gui;

public class GuiManager {
    private static GuiManager instance;
    private GuiManager() {}

    public void init() {

    }

    public void shutdown() {

    }

    public static GuiManager getInstance() {
        return instance == null ? instance = new GuiManager() : instance;
    }
}
