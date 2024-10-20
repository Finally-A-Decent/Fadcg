package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.FADCR;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@AllArgsConstructor
@Getter
public class ReactionImpl {
    private String path;
    private String type;
    private String answer;
    private String question;
    private String reward;

    public ConfigurationSection getSectionFromPath() {
        return FADCR.getInstance().getConfig().getConfigurationSection(path);
    }
}
