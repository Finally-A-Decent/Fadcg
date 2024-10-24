package info.asdev.fadcg.managers.reaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@AllArgsConstructor
public class Reward {
    private List<String> commands;
    private String displayName;
    private double chance = 1d;

    public void give(Player player) {
        ConsoleCommandSender executor = Bukkit.getConsoleSender();

        commands.forEach(command -> {
            Bukkit.dispatchCommand(executor, command
                    .replace("{player}", player.getName())
                    .replace("{uuid}", player.getUniqueId().toString())
            );
        });
    }
}
