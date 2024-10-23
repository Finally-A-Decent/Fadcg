package info.asdev.fadcr.commands;

import info.asdev.fadcr.commands.lib.BasicCommand;
import info.asdev.fadcr.commands.lib.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "fadcr", permission = "", inGameOnly = false)
public class CommandFADCR extends BasicCommand {
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1 || args[0].equalsIgnoreCase("about")) {
            return;
        }

        sender.sendMessage("ur gay lol");
    }
}
