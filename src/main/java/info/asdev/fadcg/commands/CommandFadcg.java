package info.asdev.fadcg.commands;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.managers.ReactionConfigManager;
import info.asdev.fadcg.gui.GuiManager;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class CommandFadcg implements CommandExecutor {

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
                ChatManager.getInstance().init();
                GuiManager.getInstance().init();
                ReactionConfigManager.init();

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

        Text.send(sender, "commands.errors.unknown-args");
        return true;
    }
}
