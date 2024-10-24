package info.asdev.fadcg.commands;

import com.google.common.collect.Lists;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.gui.GuiManager;
import info.asdev.fadcg.managers.ReactionManager;
import info.asdev.fadcg.managers.RewardManager;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class CommandFadcg implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || args[0].equalsIgnoreCase("about")) {
            Text.sendNoFetch(sender, String.format("""
                    &#9555ffFADCG&7 - &c%s&r
                    &7Developers: &cASDEVJava
                    &7Contributors: &cPreva1l&7, &cIcanLevitate""", Fadcg.getInstance().getDescription().getVersion()));

            return true;
        }

        if (!sender.hasPermission("fadcg.admin")) {
            Text.send(sender, "commands.errors.no-permission");
            return true;
        }

        String action = args[0].toLowerCase();
        if (action.equals("reload")) {
            try {
                Fadcg.getInstance().reloadConfig();
                ReactionManager.init();
                RewardManager.init();
                ChatManager.getInstance().init();
                GuiManager.init();

                Text.send(sender, "commands.reload.success");
            } catch (Exception ex) {
                Text.sendNoFetch(sender, "&cRELOAD HAS FAILED! CHECK CONSOLE!");
                Fadcg.getInstance().getLogger().log(Level.SEVERE, "Failed to reload configs", ex);
            }
            return true;
        }

        if (action.equals("run-now")) {
            if (ChatManager.getInstance().isRunning()) {
                Text.send(sender, "commands.errors.requirements-not-met");
                return true;
            }

            ChatManager.getInstance().runNow();
            return true;
        }

        if (action.equalsIgnoreCase("config") && sender instanceof Player player && player.hasPermission("fadcg.admin.gui")) {
            GuiManager.openCategoriesInventory(player);
            return true;
        }

        Text.send(sender, "commands.errors.unknown-args");
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> available = Lists.newArrayList("reload", "run-now");

        if (!sender.hasPermission("fadcg.admin")) {
            return List.of();
        }

        if (sender.hasPermission("fadcg.admin.gui")) {
            available.add("config");
        }

        return filter(args[0], available.toArray(new String[0]));
    }

    private List<String> filter(String orig, String[] options) {
        List<String> a = new ArrayList<>();

        for (String option : options) {
            if (option.toLowerCase().startsWith(orig.toLowerCase())) {
                a.add(option);
            }
        }

        return a;
    }
}
