package info.asdev.fadcg.commands;

import com.google.common.collect.Lists;
import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.managers.ChatManager;
import info.asdev.fadcg.gui.GuiManager;
import info.asdev.fadcg.managers.ReactionManager;
import info.asdev.fadcg.managers.RewardManager;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.utils.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true)
public class OldCommandFadcg implements CommandExecutor, TabCompleter {
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

                Text.send(sender, "commands.reload.success");
            } catch (Exception ex) {
                Text.sendNoFetch(sender, "&cRELOAD HAS FAILED! CHECK CONSOLE!");
                Fadcg.getInstance().getLogger().log(Level.SEVERE, "Failed to reload configs", ex);
            }
            return true;
        }

        if (action.equals("stop")) {
            if (ChatManager.getInstance().isRunning()) {
                ChatManager.getInstance().getTimeoutRunnable().run();
            }
            return true;
        }

        if (action.equals("run-now") || action.equals("force")) {
            if (ChatManager.getInstance().isRunning()) {
                Text.send(sender, "commands.errors.requirements-not-met");
                return true;
            }

            boolean force = action.equals("force");

            if (args.length >= 2) {
                String category = args[1].toLowerCase();
                String id = null; //args[2].toLowerCase();

                if (!ReactionCategory.getInstances().containsKey(category)) {
                    Text.send(sender, "commands.run-now.category-not-found", args[1]);
                    return true;
                }

                ReactionCategory categoryImpl = ReactionCategory.get(category);
                if (categoryImpl.isDisabled()) {
                    Text.send(sender, "commands.run-now.category-disabled", args[1]);
                    return true;
                }

                ReactionImpl impl = null;
                if (args.length >= 3) {
                    id = args[2].toLowerCase();
                    impl = categoryImpl.getImplementationByPath(id.toLowerCase());
                } else {
                    impl = categoryImpl.getImplementations().get(ChatManager.getInstance().getRandom().nextInt(categoryImpl.getImplementations().size()));
                }

                if (impl == null) {
                    Text.send(sender, "commands.run-now.id-not-found", args[2], args[1]);
                    return true;
                }

                ChatManager.getInstance().runSpecificReaction(categoryImpl, impl, force);
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
        List<String> available = Lists.newArrayList("reload", "run-now", "force");

        if (!sender.hasPermission("fadcg.admin")) {
            return List.of();
        }

        if (args[0].equalsIgnoreCase("run-now") || args[0].equalsIgnoreCase("force")) {
            return tabCompleteRunNow(sender, args);
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

    private List<String> tabCompleteRunNow(CommandSender sender, String[] args) {
        Set<String> categories = ReactionCategory.getInstances().keySet();
        categories = categories.stream().filter(category -> !ReactionCategory.get(category).isDisabled()).collect(Collectors.toSet());
        List<String> opts = filter(args.length == 2 ? args[1] : "", categories.toArray(new String[0]));

        if (args.length == 2 || args.length == 3) {
            ReactionCategory category = ReactionCategory.get(args[1].toLowerCase());
            if (category == null) {
                return opts;
            }
            opts.clear();
            List<ReactionImpl> impls = category.getImplementations();
            impls.forEach(impl -> opts.add(impl.getPath()));
        }

        return opts;
    }
}
