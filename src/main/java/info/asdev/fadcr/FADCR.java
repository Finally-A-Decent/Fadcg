package info.asdev.fadcr;

import info.asdev.fadcr.chat.ChatManager;
import info.asdev.fadcr.gui.GuiManager;
import info.asdev.fadcr.listeners.ChatListener;
import info.asdev.fadcr.utils.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class FADCR extends JavaPlugin {
    public static final double MIN_CONFIG_VERSION = 1d;

    @Getter private static FADCR instance;
    private static boolean papiInstalled = false;
    @Getter private static FileConfiguration lang;
    @Getter private File langFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!checkConfig()) {
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);

        ChatManager.getInstance().init();
        GuiManager.getInstance().init();

        papiInstalled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("""
                &a-------------------------------
                &a Finally a Decent Chat Reactor
                &a     Enabled Successfully
                &a-------------------------------"""));
    }

    @Override
    public void onDisable() {
        ChatManager.getInstance().shutdown();
        GuiManager.getInstance().shutdown();
    }

    private boolean checkConfig() {
        double detectedVersion = getConfig().getDouble("version");

        if (detectedVersion < MIN_CONFIG_VERSION) {
            getLogger().severe(String.format("Unable to continue, unsupported config version detected (%s). You may want to migrate to a new config.", detectedVersion));
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    private void loadLanguageFile() {
        langFile = new File(getDataFolder(), "lang.yml");

        if (!langFile.exists()) {
            saveResource(langFile.getName(), false);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);
        lang.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(langFile.getName()))));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        loadLanguageFile();
    }

    public static boolean papi() {
        return papiInstalled;
    }
}
