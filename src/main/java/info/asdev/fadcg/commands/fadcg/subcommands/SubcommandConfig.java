package info.asdev.fadcg.commands.fadcg.subcommands;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.fadcg.gui.GuiManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "config", permission = "fadcg.admin.config", playerOnly = true)
public class SubcommandConfig extends PluginCommand {
    protected void execute(CommandSender sender, String s, String[] strings) {
        GuiManager.openCategoriesInventory((Player) sender);
    }
}
