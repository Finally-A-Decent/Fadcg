package info.asdev.fadcg.managers;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.reactions.*;
import info.asdev.fadcg.utils.Job;
import info.asdev.fadcg.utils.RandomSelector;
import info.asdev.fadcg.utils.Text;
import info.asdev.fadcg.utils.Util;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatManager {
    private static ChatManager instance;
    @Getter private Random random = new Random();
    @Getter private boolean running = false;
    @Getter private boolean caseSensitiveAnswers = true;
    @Getter private boolean centerFormat = false;
    private long timeout = 60_000, interval = 300_000;
    private long startTime;
    private int minPlayers = 2;
    private RandomSelector<String> choiceSelector;
    private Reward reward;
    private Job job;
    private Reaction active;
    private BukkitRunnable timeoutRunnable;
    private Pattern spacesPattern = Pattern.compile(" ");
    private Map<String, Reaction> registeredReactions = new HashMap<>();
    private Set<String> rewardKeys;
    private String timeoutMessage;

    public void init() {
        shutdown();

        FileConfiguration config = Fadcg.getInstance().getConfig();
        rewardKeys = ReactionConfigManager.getRewardsConfig().getConfig().getKeys(false);

        caseSensitiveAnswers = config.getBoolean("options.case-sensitive-answers", true);
        centerFormat = Fadcg.getLang().getBoolean("chat-reaction.center-reaction-format", false);
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

        double mspt = 1000;
        float tickRate = Bukkit.getServerTickManager().getTickRate();
        mspt = 1000d / tickRate;

        timeoutRunnable.runTaskLater(Fadcg.getInstance(), (long) (timeout / mspt));
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
            Bukkit.getScheduler().runTask(Fadcg.getInstance(), this::giveReward);
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
                Fadcg.getInstance().getLogger().log(Level.WARNING, "Reward command execution failed for command: " + parsed, ex);
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
