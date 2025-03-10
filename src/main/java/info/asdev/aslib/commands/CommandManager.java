package info.asdev.aslib.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandManager {
    private static CommandManager instance;
    private final List<PluginCommand> commands = new ArrayList<>();

    public void registerCommand(PluginCommand pluginCommand) {
        CommandMapUtil.getCommandMap().register("", new CommandExecutor(pluginCommand));
        pluginCommand.registerSubcommands();
        commands.add(pluginCommand);
    }

    public static CommandManager getInstance() {
        return instance == null ? instance = new CommandManager() : instance;
    }

    private static class CommandExecutor extends BukkitCommand {
        private final PluginCommand pluginCommand;

        public CommandExecutor(PluginCommand pluginCommand) {
            super(pluginCommand.getName());
            this.setAliases(Arrays.asList(pluginCommand.getAliases()));
            if (!pluginCommand.getPermission().isEmpty()) {
                this.setPermission(pluginCommand.getPermission());
            }
            this.pluginCommand = pluginCommand;
        }

        public boolean execute(@NotNull CommandSender commandUser, @NotNull String label, @NotNull String[] args) {
            pluginCommand.preExecute(commandUser, label, args);
            return true;
        }

        @NotNull
        public List<String> tabComplete(@NotNull CommandSender commandUser, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
            // Primary argument
            if (args.length <= 1) {
                List<String> completors = pluginCommand.tabComplete(commandUser, args);

                if (completors.isEmpty() && !pluginCommand.getSubcommands().isEmpty()) {
                    List<String> ret = new ArrayList<>();
                    for (PluginCommand subCommand : pluginCommand.getSubcommands().values()) {
                        if (!subCommand.getPermission().isEmpty() && !commandUser.hasPermission(subCommand.getPermission())) {
                            continue;
                        }
                        ret.add(subCommand.getName());
                        Collections.addAll(ret, subCommand.getAliases());
                    }
                    if (args.length == 0) {
                        completors.addAll(ret);
                    } else {
                        StringUtil.copyPartialMatches(args[0], ret, completors);
                    }
                    return completors;
                }

                return completors;
            }

            // Sub command tab completer
            List<String> completors = new ArrayList<>();
            List<String> ret = new ArrayList<>();
            for (PluginCommand subCommand : pluginCommand.getSubcommands().values()) {
                if (!subCommand.getName().equals(args[0]) && !Arrays.stream(subCommand.getAliases()).toList().contains(args[0])) {
                    continue;
                }
                if (!subCommand.getPermission().isEmpty() && !commandUser.hasPermission(subCommand.getPermission())) {
                    continue;
                }
                ret.addAll(subCommand.tabComplete(commandUser, removeFirstElement(args)));
            }
            StringUtil.copyPartialMatches(args[args.length - 1], ret, completors);
            return completors;
        }

        private String[] removeFirstElement(String[] array) {
            if (array == null || array.length == 0) {
                return new String[0];
            }

            String[] newArray = new String[array.length - 1];
            System.arraycopy(array, 1, newArray, 0, array.length - 1);

            return newArray;
        }
    }
}
