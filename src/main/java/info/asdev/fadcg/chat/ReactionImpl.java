package info.asdev.fadcg.chat;

import info.asdev.fadcg.managers.reaction.ReactionCategory;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Getter
public class ReactionImpl {
    private String id;
    private String path;
    private String type;
    private String question;
    @Setter private List<String> answers;
    private String reward;

    private String answer;

    public ReactionImpl(String id, String path, String type, String question, List<String> answers, String reward) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.question = question;
        this.answers = answers;
        this.reward = reward;

        if (!hasMultipleAnswers()) answer = answers.getFirst();
    }

    public ConfigurationSection getSectionFromPath() {
        return ReactionCategory.get(id).getConfig().getConfigurationSection(path);
    }

    public boolean hasMultipleAnswers() {
        return answers.size() > 1;
    }

    public String getAnswersAsString() {
        if (!hasMultipleAnswers()) {
            return answer;
        }

        String answersString = String.join("&7, &c", answers);
        return answersString.substring(4);
    }
}
