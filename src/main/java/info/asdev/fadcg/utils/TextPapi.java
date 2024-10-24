package info.asdev.fadcg.utils;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

@UtilityClass
public class TextPapi {
        public String setPlaceholders(Player who, String message) {
            return PlaceholderAPI.setPlaceholders(who, message);
        }
}
