package info.asdev.fadcr.chat.reactions;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.config.ReactionConfigManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

@AllArgsConstructor
@Getter
public class ReactionImpl {
    private String id;
    private String path;
    private String type;
    private String question;
    @Setter private String answer;
    private String reward;


    public ConfigurationSection getSectionFromPath() {
        return ReactionConfigManager.getConfig(id).getConfig().getConfigurationSection(path);
    }
}
