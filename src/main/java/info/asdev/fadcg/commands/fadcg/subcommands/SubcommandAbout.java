package info.asdev.fadcg.commands.fadcg.subcommands;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.CommandSender;

@Command(name = "about")
public class SubcommandAbout extends PluginCommand {
    @Override protected void execute(CommandSender sender, String s, String[] strings) {
        Text.sendNoFetch(sender, String.format("""
                    &#9555ffFADCG&7 - &c%s&r
                    &7Developers: &cASDEVJava
                    &7Contributors: &cPreva1l&7, &cIcanLevitate""", Fadcg.getInstance().getDescription().getVersion()));
    }
}
