package info.asdev.aslib.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public abstract class PluginCommand {
    private final Map<String, PluginSubcommand> subcommands = new HashMap<>();

    public void registerSubcommand(PluginSubcommand command) {
        Command commandAnnotation = command.getData().orElseThrow();
        command.registerSubcommands();
        subcommands.putIfAbsent(command.getName(), command);
    }

    public void unregisterSubcommand(PluginSubcommand command) {
        Command commandAnnotation = command.getData().orElseThrow();
        subcommands.remove(commandAnnotation.name().toLowerCase());
    }

    public Optional<Command> getData() {
        return Optional.ofNullable(getClass().getAnnotation(Command.class));
    }

    public final Optional<PluginSubcommand> getSubcommand(String command) {
        Optional<PluginSubcommand> optional = Optional.ofNullable(subcommands.getOrDefault(command, null));
        if (optional.isEmpty()) {
            for (PluginSubcommand command1 : subcommands.values()) {
                if (List.of(command1.getAliases()).contains(command)) {
                    return Optional.of(command1);
                }
            }
        }

        return optional;
    }

    @Unmodifiable
    public Map<String, PluginCommand> getSubcommands() {
        return Collections.unmodifiableMap(subcommands);
    }

    protected final void preExecute(CommandSender sender, String alias, String[] args) {
        String permission = getPermission();
        if (permission != null && !permission.isEmpty() && !sender.hasPermission(getPermission())) {
            sender.sendMessage(getErrorFromKey(noPermissionKey()));
            return;
        }

        if (isPlayerOnly() && !(sender instanceof Player)) {
            // TODO: Not player message
            sender.sendMessage(getErrorFromKey(notPlayerKey()));
            return;
        }

        if (args.length < 1) {
            execute(sender, alias, args);
            return;
        }

        Optional<PluginSubcommand> subCommand = getSubcommand(args[0]);
        if (!subCommand.isPresent()) {
            execute(sender, alias, args);
            return;
        }

        runSubcommand(sender, args, subCommand.get());
    }

    public final String getName() {
        return getData().orElseThrow().name();
    }

    public final String getPermission() {
        return getData().orElseThrow().permission();
    }

    public final String[] getAliases() {
        return getData().orElseThrow().aliases();
    }

    public final boolean isPlayerOnly() {
        return getData().orElseThrow().playerOnly();
    }

    protected void registerSubcommands() {
    }

    protected void runSubcommand(CommandSender sender, String[] args, String name) {
        String[] newArgs = new String[0];
        if (args.length > 0) {
            newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        }

        getSubcommand(name).orElseThrow().preExecute(sender, name, newArgs);
    }

    private void runSubcommand(CommandSender sender, String[] args, PluginCommand subcommand) {
        String[] newArgs = new String[0];
        if (args.length > 0) {
            newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, args.length - 1);
        }

        subcommand.preExecute(sender, subcommand.getName(), newArgs);
    }

    protected List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }

    public String noPermissionKey() {
        return "errors.no-permission";
    }

    public String notPlayerKey() {
        return "errors.not-a-player";
    }

    public String getErrorFromKey(String key) {return "";}
    protected abstract void execute(CommandSender sender, String alias, String[] args);
}
