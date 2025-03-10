package info.asdev.aslib.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PluginSubcommand extends PluginCommand {
    private final PluginCommand parent;

    @Override
    public String getErrorFromKey(String key) {
        return parent.getErrorFromKey(key);
    }
}
