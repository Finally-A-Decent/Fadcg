package info.asdev.fadcg.utils;
import info.asdev.fadcg.Fadcg;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class Text {
    private final Pattern FINAL_LEGACY_PATTERN = Pattern.compile("&#(\\w{5}[0-9a-fA-F])");
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");
    private static final Pattern MODERN_HEX_PATTERN = Pattern.compile("<#[a-fA-F0-9]{6}>");

    Random random = new Random();

    /**
     * Converts legacy colour codes to MiniMessage.
     *
     * @param message message with legacy codes
     * @return string with mini modernMessage formatting (not colorized)
     */
    public String legacyToMiniMessage(String message) {
        message = message.replace("&4", "<dark_red>");
        message = message.replace("&c", "<red>");
        message = message.replace("&6", "<gold>");
        message = message.replace("&e", "<yellow>");
        message = message.replace("&2", "<dark_green>");
        message = message.replace("&a", "<green>");
        message = message.replace("&b", "<aqua>");
        message = message.replace("&3", "<dark_aqua>");
        message = message.replace("&1", "<dark_blue>");
        message = message.replace("&9", "<blue>");
        message = message.replace("&d", "<light_purple>");
        message = message.replace("&5", "<dark_purple>");
        message = message.replace("&f", "<white>");
        message = message.replace("&7", "<gray>");
        message = message.replace("&8", "<dark_gray>");
        message = message.replace("&0", "<black>");
        message = message.replace("&l", "<b>");
        message = message.replace("&k", "<obf>");
        message = message.replace("&m", "<st>");
        message = message.replace("&n", "<u>");
        message = message.replace("&o", "<i>");
        message = message.replace("&r", "<reset>");

        Matcher match = LEGACY_HEX_PATTERN.matcher(message);
        String code = message;
        while (match.find()) {
            code = message.substring(match.start(), match.end());
            code = code.replace("&", "<");
            code = code + ">";
        }
        return message.replaceAll("&#[a-fA-F0-9]{6}", code);
    }

    /**
     * Converts MiniMessage to legacy colour codes.
     *
     * @param message message with mini message formatting
     * @return string with legacy formatting (not colorized)
     */
    public String miniMessageToLegacy(String message) {
        message = message.replace("<dark_red>", "&4");
        message = message.replace("<red>", "&c");
        message = message.replace("<gold>", "&6");
        message = message.replace("<yellow>", "&e");
        message = message.replace("<dark_green>", "&2");
        message = message.replace("<green>", "&a");
        message = message.replace("<aqua>", "&b");
        message = message.replace("<dark_aqua>", "&3");
        message = message.replace("<dark_blue>", "&1");
        message = message.replace("<blue>", "&9");
        message = message.replace("<light_purple>", "&d");
        message = message.replace("<purple>", "&5");
        message = message.replace("<white>", "&f");
        message = message.replace("<gray>", "&7");
        message = message.replace("<dark_gray>", "&8");
        message = message.replace("<black>", "&0");
        message = message.replace("<b>", "&l");
        message = message.replace("<obf>", "&k");
        message = message.replace("<st>", "&m");
        message = message.replace("<u>", "&n");
        message = message.replace("<i>", "&o");
        message = message.replace("<reset>", "&r");

        Matcher match = MODERN_HEX_PATTERN.matcher(message);
        String code = message;
        while (match.find()) {
            code = message.substring(match.start(), match.end());
            code = code.replace("<", "&");
            code = code.replace(">", "");
        }
        return message.replaceAll("<#[a-fA-F0-9]{6}>", code);
    }

    /**
     * Takes a string formatted in minimessage OR legacy and turns it into an Adventure Component.
     *
     * @param message the modernMessage
     * @return colorized component
     */
    public Component modernMessage(@NotNull String message) {
        return MiniMessage.miniMessage().deserialize(legacyToMiniMessage(message));
    }

    /**
     * Takes a string formatted in minimessage OR legacy and turns it into a legacy String.
     *
     * @param message the modernMessage
     * @return colorized component
     */
    public String legacyMessage(@NotNull String message) {
        message = miniMessageToLegacy(message);

        Matcher matcher = FINAL_LEGACY_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public List<String> legacyList(@NotNull List<String> list) {
        List<String> legacyList = new ArrayList<>();
        for (String s : list) {
            legacyList.add(legacyMessage(s));
        }
        return legacyList;
    }

    // FADCG specific
    public void send(CommandSender sender, String key, Object... replacements) {
        FileConfiguration lang = Fadcg.getLang();
        String message = lang.getString(key);

        if (message == null) {
            Fadcg.getInstance().getLogger().warning("Message key " + key + " is null. Cannot send.");
            return;
        }
        if (Fadcg.papi() && sender instanceof  Player player) {
            message = TextPapi.setPlaceholders(player, message);
        }

        for (int i = 0; i < replacements.length; i++) {
            if (!message.contains("{" + i + "}")) {
                continue;
            }

            message = message.replace("{" + i + "}", String.valueOf(replacements[i]));
        }

        sender.sendMessage(legacyMessage(message));
    }

    public void sendNoFetch(CommandSender sender, String message, Object... replacements) {
        if (message == null) {
            Fadcg.getInstance().getLogger().warning("Message is null. Cannot send.");
            return;
        }

        if (Fadcg.papi() && sender instanceof Player player) {
            message = TextPapi.setPlaceholders(player, message);
        }

        for (int i = 0; i < replacements.length; i++) {
            if (!message.contains("{" + i + "}")) {
                continue;
            }

            message = message.replace("{" + i + "}", String.valueOf(replacements[i]));
        }

        sender.sendMessage(legacyMessage(message));
    }

    public String getMessage(String key, boolean list, Object... replacements) {
        String message = list ? String.join("\n", Fadcg.getLang().getStringList(key)) : Fadcg.getLang().getString(key, "null");
        for (int i = 0; i < replacements.length; i++) {
            if (!message.contains("{" + i + "}")) {
                continue;
            }

            message = message.replace("{" + i + "}", String.valueOf(replacements[i]));
        }

        return message;
    }


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

    public String capitalizeFirst(String message) {
        return String.join("", message.substring(0, 1).toUpperCase(), message.substring(1));
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

    private final int CENTER_PX = 154;
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