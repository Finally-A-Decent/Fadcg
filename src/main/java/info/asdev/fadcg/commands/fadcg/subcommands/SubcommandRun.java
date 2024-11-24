package info.asdev.fadcg.commands.fadcg.subcommands;

import info.asdev.aslib.commands.Command;
import info.asdev.aslib.commands.PluginCommand;
import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.CommandSender;

@Command(name = "run-now", permission = "fadcg.admin.run-now")
public class SubcommandRun extends PluginCommand {
    protected void execute(CommandSender sender, String s, String[] args) {
        if (args.length >= 1) {
            String category = args[0].toLowerCase(), id;

            if (!ReactionCategory.getInstances().containsKey(category)) {
                Text.send(sender, "commands.run-now.category-not-found", args[0]);
                return;
            }

            ReactionCategory categoryImpl = ReactionCategory.get(category);
            if (categoryImpl.isDisabled()) {
                Text.send(sender, "commands.run-now.category-disabled", args[0]);
                return;
            }

            ReactionImpl impl = null;
            if (args.length >= 2) {
                id = args[1].toLowerCase();
                impl = categoryImpl.getImplementationByPath(id.toLowerCase());
            } else {
                impl = categoryImpl.getImplementations().get(ChatManager.getInstance().getRandom().nextInt(categoryImpl.getImplementations().size()));
            }

            if (impl == null) {
                Text.send(sender, "commands.run-now.id-not-found", args[1], args[0]);
                return;
            }

            ChatManager.getInstance().runSpecificReaction(categoryImpl, impl, false);
            return;
        }

        ChatManager.getInstance().runNow();
    }
}
