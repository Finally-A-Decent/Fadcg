package info.asdev.fadcr;

import info.asdev.fadcr.chat.ChatManager;
import info.asdev.fadcr.gui.GuiManager;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class FADCR extends JavaPlugin {
    @Getter private static FADCR instance;

    @Getter private static FileConfiguration lang;
    private File langFile;

    @Override public void onEnable() {
        instance = this;
        saveDefaultConfig();

        ChatManager.getInstance().init();
        GuiManager.getInstance().init();
    }

    @Override public void onDisable() {
        ChatManager.getInstance().shutdown();
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
}
