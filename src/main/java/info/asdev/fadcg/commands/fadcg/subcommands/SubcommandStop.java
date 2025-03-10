package info.asdev.fadcg.commands.fadcg.subcommands;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.aslib.commands.PluginSubcommand;
import info.asdev.fadcg.managers.ChatManager;
import org.bukkit.command.CommandSender;

@Command(name = "stop", permission = "fadcg.admin.stop")
public class SubcommandStop extends PluginSubcommand {

    public SubcommandStop(PluginCommand parent) {
        super(parent);
    }

    protected void execute(CommandSender commandSender, String s, String[] strings) {
        if (ChatManager.getInstance().isRunning()) {
            ChatManager.getInstance().getTimeoutRunnable().run();
        }
    }
}
