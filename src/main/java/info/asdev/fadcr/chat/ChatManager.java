package info.asdev.fadcr.chat;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.reactions.*;
import info.asdev.fadcr.config.ReactionConfigManager;
import info.asdev.fadcr.utils.Job;
import info.asdev.fadcr.utils.RandomSelector;
import info.asdev.fadcr.utils.Text;
import info.asdev.fadcr.utils.Util;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatManager {
    private static ChatManager instance;
    @Getter private Random random = new Random();
    @Getter private boolean running = false;
    @Getter private boolean caseSensitiveAnswers = true;
    @Getter private boolean centerFormat = false;
    private RandomSelector<String> choiceSelector;
    private Pattern spacesPattern = Pattern.compile(" ");
    private Reaction active;
    private Job job;
    private BukkitRunnable timeoutRunnable;
    private Map<String, Reaction> registeredReactions = new HashMap<>();
    private Set<String> rewardKeys;
    private Reward reward;
    private long timeout = 60_000, interval = 300_000;
    private String timeoutMessage;
    private long startTime;
    private int minPlayers = 2;

    public void init() {
        shutdown();

        FileConfiguration config = FADCR.getInstance().getConfig();
        rewardKeys = ReactionConfigManager.getRewardsConfig().getConfig().getKeys(false);

        caseSensitiveAnswers = config.getBoolean("options.case-sensitive-answers", true);
        centerFormat = FADCR.getLang().getBoolean("chat-reaction.center-reaction-format", false);
        choiceSelector = RandomSelector.weighted(rewardKeys, (key) -> ReactionConfigManager.getRewardsConfig().getConfig().getDouble(key + ".chance", 1d));

        interval = config.getLong("options.interval", 300_000);
        timeout = config.getLong("options.timeout", 60_000);
        minPlayers = config.getInt("options.min-players", 2);

        job = Job.of("chatreactions_run", this::runJob, Duration.of(interval, ChronoUnit.MILLIS));
        job.start();
    }

    public void shutdown() {
        if (running) {
            timeout();
        }
        if (job != null) {
            job.shutdown();
        }
    }

    public void runNow() {
        runJob();
    }

    public void onPlayerLeave(PlayerQuitEvent event) {
        int count = Util.getOnlineSizeExcluding(event.getPlayer());
        if (count >= minPlayers) {
            return;
        }

        if (timeoutRunnable != null && running) {
            timeout();
            timeoutMessage = Text.getMessage("chat-reaction.reaction-cancelled-not-enough-players", false, active.getImplementation().getAnswer());
            Bukkit.getOnlinePlayers().forEach(player -> {
                Text.sendNoFetch(player, timeoutMessage);
            });
        }
    }

    private void runJob() {
        if (running || Bukkit.getOnlinePlayers().size() < minPlayers) {
            return;
        }

        running = true;
        active = ReactionConfigManager.random();
        active.init();

        String message = Text.getMessage("chat-reaction.format", true, active.getMessage());

        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, isCenterFormat() ? Util.getMultilineCenteredMessage(message) : message);
        });

        timeoutRunnable = new BukkitRunnable() {
            @Override public void run() {
                if (running) {
                    timeout();

                    timeoutMessage = Text.getMessage("chat-reaction.reaction-expired", false, active.getImplementation().getAnswer());
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        Text.sendNoFetch(player, timeoutMessage);
                    });
                }
            }
        };

        timeoutRunnable.runTaskLater(FADCR.getInstance(), timeout / 50L);
        startTime = System.currentTimeMillis();
    }

    public void processChatMessage(Player who, String message) {
        if (!running || active == null) {
            return;
        }

        boolean correct = active.attempt(who, message);
        if (!correct) {
            return;
        }

        active.reset();
        reward = null;

        running = false;
        awardPlayer(who);

        if (timeoutRunnable != null && !timeoutRunnable.isCancelled()) {
            timeoutRunnable.cancel();
        }

        DecimalFormat format = new DecimalFormat(Text.getMessage("chat-reaction.time-format", false));
        String solved = Text.getMessage("chat-reaction.solved-by", false, who.getName(), format.format((double) (System.currentTimeMillis() - startTime) / 1000d));
        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, solved);
        });
    }

    public void awardPlayer(Player who) {
        ReactionImpl activeImpl = active.getImplementation();
        String reward = activeImpl.getReward();

        ConfigurationSection rewardSection = ReactionConfigManager.getRewardsConfig().getConfig();
        rewardSection = !"random".equalsIgnoreCase(reward) ? rewardSection.getConfigurationSection(reward) : rewardSection.getConfigurationSection(choiceSelector.next(random));

        if (rewardSection != null && !rewardKeys.isEmpty()) {
            this.reward = Reward.builder().section(rewardSection).player(who).build();
            Bukkit.getScheduler().runTask(FADCR.getInstance(), this::giveReward);
            Text.send(who, "chat-reaction.reaction-won", rewardSection.getString("display_name"));
        } else {
            Text.send(who, "chat-reaction.no-reward");
        }
    }

    private void giveReward() {
        runCommands(reward.getPlayer(), reward.getSection().getStringList("commands"));
    }

    public void runCommands(Player who, List<String> commands) {
        for (String command : commands) {
            String parsed = command
                    .replace("{player}", who.getName())
                    .replace("{uuid}", who.getUniqueId().toString());
            try {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
            } catch (Exception ex) {
                FADCR.getInstance().getLogger().log(Level.WARNING, "Reward command execution failed for command: " + parsed, ex);
            }
        }
    }

    private void timeout() {
        if (!running) {
            return;
        }

        running = false;
        if (timeoutRunnable != null && !timeoutRunnable.isCancelled()) {
            timeoutRunnable.cancel();
        }
        active.reset();
    }

    public static ChatManager getInstance() {
        return instance == null ? instance = new ChatManager() : instance;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static class Reward {
        private ConfigurationSection section;
        private Player player;
    }
}
