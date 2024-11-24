package info.asdev.fadcg.utils;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@UtilityClass
public class Util {
    public int getOnlineSizeExcluding(Player... players) {
        if (players.length < 1) {
            return Bukkit.getOnlinePlayers().size();
        }

        List<Player> players2 = Lists.newArrayList(players);
        return (int) Bukkit.getOnlinePlayers().stream().filter(player -> !players2.contains(player)).count();
    }

    public static Chunk[] getSurroundingChunks(Chunk chunk) {
        List<Chunk> surrounding = new ArrayList<>();
        int cX = chunk.getX();
        int cZ = chunk.getZ();

        for (int i = -1, cid = 1; i < 1; i++) {
            for (int z = -1; z < 1; z++) {
                Chunk c = chunk.getWorld().getChunkAt(cX + i, cZ + z);
                if (chunk.equals(c)) continue;

                surrounding.add(c);
                cid++;
            }
        }

        surrounding.addFirst(chunk);
        return surrounding.toArray(new Chunk[0]);
    }
}
