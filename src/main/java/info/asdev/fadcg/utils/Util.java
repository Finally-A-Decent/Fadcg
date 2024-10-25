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
    Random random = new Random();

    public String scramble(String original) {
        if (!original.contains(" ")) {
            return scramble2(original);
        }

        StringBuilder builder = new StringBuilder();
        String[] parts = original.split(" ");

        for (String part : parts) {
            builder.append(" ").append(scramble2(part));
        }

        return builder.substring(1);
    }

    private String scramble2(String in) {
        char[] characters = in.toCharArray();

        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];

            characters[i] = characters[j];
            characters[j] = temp;
        }

        return new String(characters);
    }

    public static String getMultilineCenteredMessage(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        StringBuilder builder = new StringBuilder();
        for (String s : message.split("\n")) {
            builder.append("\n").append(altCenter(s + "§r"));
        }

        return builder.substring(1);
    }




    public int getOnlineSizeExcluding(Player... players) {
        if (players.length < 1) {
            return Bukkit.getOnlinePlayers().size();
        }

        List<Player> players2 = Lists.newArrayList(players);
        return (int) Bukkit.getOnlinePlayers().stream().filter(player -> !players2.contains(player)).count();
    }

    public String reverse(String reverse) {
        StringBuilder builder = new StringBuilder();

        for (int i = reverse.length(); i > 0; i--) {
            builder.append(
                    reverse.charAt(
                            Math.max(0, i - 1)
                    )
            );
        }

        return builder.toString();
    }

    public String capitalizeFirst(String message) {
        return String.join("", message.substring(0, 1).toUpperCase(), message.substring(1));
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


    private final static int CENTER_PX = 154;
    public String altCenter(String message) {
        if (message == null || message.equals("")) return "";

        message = Text.legacyMessage(message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }
}
