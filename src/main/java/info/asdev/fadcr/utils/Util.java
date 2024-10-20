package info.asdev.fadcr.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Util {
    static Random random = new Random();

    public static String scramble(String original) {
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

    private static String scramble2(String in) {
        char[] characters = in.toCharArray();

        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];

            characters[i] = characters[j];
            characters[j] = temp;
        }

        return new String(characters);
    }


}
