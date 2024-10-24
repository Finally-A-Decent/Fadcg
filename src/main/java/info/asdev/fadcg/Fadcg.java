package info.asdev.fadcg;

import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.commands.CommandFadcg;
import info.asdev.fadcg.gui.GuiManager;
import info.asdev.fadcg.listeners.ChatListener;
import info.asdev.fadcg.managers.FastInvManager;
import info.asdev.fadcg.managers.ReactionManager;
import info.asdev.fadcg.managers.RewardManager;
import info.asdev.fadcg.utils.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;

public final class Fadcg extends JavaPlugin {
    public static final double MIN_CONFIG_VERSION = 1d;
    private static boolean papiInstalled = false;

    @Getter private static Fadcg instance;
    @Getter private static FileConfiguration lang;
    @Getter private File langFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        FastInvManager.register(this);

        //ReactionConfigManager.init();
        ReactionManager.init();
        RewardManager.init();
        ChatManager.getInstance().init();
        GuiManager.init();

        getCommand("fadcg").setExecutor(new CommandFadcg());

        papiInstalled = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        Bukkit.getConsoleSender().sendMessage(Text.legacyMessage("""
                &a--------------------------------
                &a   Finally a Decent Chat Games
                &a      Enabled Successfully
                &a--------------------------------"""));
    }

    @Override
    public void onDisable() {
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
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
        loadLanguageFile();
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
