package info.asdev.fadcr.utils;

import lombok.experimental.UtilityClass;

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


}
