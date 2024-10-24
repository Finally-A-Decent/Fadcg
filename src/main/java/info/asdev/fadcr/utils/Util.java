package info.asdev.fadcr.utils;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImageOp;
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
            builder.append("\n").append(
                    Text.legacyMessage(getCenteredMessage(s))
            );
        }

        return builder.substring(1);
    }

    private final static int CENTER_PX = 154;
    public static String getCenteredMessage(String message) {
        if (message == null || message.isEmpty()) return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
                continue;
            }
            if (previousCode) {
                previousCode = false;
                if (c == 'l' || c == 'L') {
                    isBold = true;
                    continue;
                }
                isBold = false;
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
        return sb.toString() + message;
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

}
