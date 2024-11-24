package info.asdev.fadcg.commands.fadcg.subcommands;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.managers.ReactionManager;
import info.asdev.fadcg.managers.RewardManager;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

@Command(name = "reload", permission = "fadcg.admin.reload")
public class SubcommandReload extends PluginCommand {
    protected void execute(CommandSender sender, String alias, String[] args) {
        try {
            Fadcg.getInstance().reloadConfig();
            ReactionManager.init();
            RewardManager.init();
            ChatManager.getInstance().init();

            Text.send(sender, "commands.reload.success");
        } catch (Exception ex) {
            Text.sendNoFetch(sender, "&cRELOAD HAS FAILED! CHECK CONSOLE!");
            Fadcg.getInstance().getLogger().log(Level.SEVERE, "Failed to reload configs", ex);
        }
    }
}
