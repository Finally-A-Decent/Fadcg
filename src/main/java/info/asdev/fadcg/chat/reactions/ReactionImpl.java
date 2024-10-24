package info.asdev.fadcg.chat.reactions;

import info.asdev.fadcg.managers.ReactionConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class ReactionImpl {
    private String id;
    private String path;
    private String type;
    private String question;
    @Setter private String answer;
    private String reward;

    public ReactionImpl(String id, String path, String type, String question, String answer, String reward) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.reward = reward;
    }

    public ConfigurationSection getSectionFromPath() {
        return ReactionConfigManager.getConfig(id).getConfig().getConfigurationSection(path);
    }
}
