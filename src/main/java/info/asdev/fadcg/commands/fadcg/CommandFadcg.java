package info.asdev.fadcg.commands.fadcg;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.fadcg.commands.fadcg.subcommands.*;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

@Command(name = "fadcg", aliases = {"fadcr", "chatgame", "chatgames", "finallyadecentchatgames"})
@NoArgsConstructor
public class CommandFadcg extends PluginCommand {
    protected void execute(CommandSender sender, String label, String[] args) {
        runSubcommand(sender, args, "about");
    }

    protected void registerSubcommands() {
        Stream.of(
                new SubcommandAbout(),
                new SubcommandReload(),
                new SubcommandConfig(),
                new SubcommandRun(),
                new SubcommandStop()
        ).forEach(this::registerSubcommand);
    }
}
