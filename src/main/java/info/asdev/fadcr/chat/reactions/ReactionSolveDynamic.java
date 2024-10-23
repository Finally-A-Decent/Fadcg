package info.asdev.fadcr.chat.reactions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Random;

@RequiredArgsConstructor
public class ReactionSolveDynamic implements Reaction {

    @Getter private final String id, displayName;

    private ReactionImpl implementation;
    private String question;
    @Getter private String answer;

    @Getter private int digits, decimalPlaces;
    private final char[] symbols = new char[] {
            '*',
            '+',
            '-',
            '/'
    };

    @Override public boolean attempt(Player who, String message) {
        return answer.equalsIgnoreCase(message);
    }

    @Override public ReactionImpl getImplementation() {
        if (implementation == null) {
            implementation = getChatManager().getReactionsById(getId())[0];
            ConfigurationSection section = implementation.getSectionFromPath();
            digits = section.getInt("digits", 2);
            decimalPlaces = section.getInt("decimal-places", 2);

            if (digits < 1) {
                digits = 2;
            }
            if (decimalPlaces < 0) {
                decimalPlaces = 2;
            }

        }

        return implementation;
    }

    @Override public String getQuestion() {
        if (question == null || answer == null) {
            getImplementation();
            Random random = getChatManager().getRandom();
            char symbol = symbols[random.nextInt(symbols.length)];
            DecimalFormat format = new DecimalFormat("0." + "#".repeat(Math.max(1, decimalPlaces)));

            String left = generateRandomNumber(random, digits);
            String right = generateRandomNumber(random, digits);

            while (left.startsWith("0") && left.length() > 1 && left.indexOf('.') != 1) {
                left = left.substring(1);
            }
            while (right.startsWith("0") && right.length() > 1 && right.indexOf('.') != 1) {
                right = right.substring(1);
            }

            double leftVal = Double.parseDouble(left);
            double rightVal = Double.parseDouble(right);

            if (leftVal == rightVal && leftVal == 0d) {
                rightVal = 1d;
            }

            double answerLiteral = switch (symbol) {
                case '*' -> leftVal * rightVal;
                case '-' -> leftVal - rightVal;
                case '/' -> leftVal / rightVal;
                default -> leftVal + rightVal;
            };

            answer = format.format(answerLiteral);
            implementation.setAnswer(answer);
            return question = String.join("", left, String.valueOf(symbol), right);
        }

        return question;
    }

    private String generateRandomNumber(Random random, int digits) {
        StringBuilder result = new StringBuilder();

        int chosenDigits = random.nextInt(digits) + 1;
        for (int i = 0; i < chosenDigits; i++) {
            result.append(random.nextInt(10));
        }

        if (random.nextInt(100) <= 35) {
            result.insert(0, "-");
        }

        String resultStr = result.toString();
        if (resultStr.equals("-0")) {
            return "0";
        }

        return result.toString();
    }

    @Override public void reset() {
        answer = null;
        question = null;
    }
}
