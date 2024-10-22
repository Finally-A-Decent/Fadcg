package info.asdev.fadcr.chat;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.reactions.*;
import info.asdev.fadcr.utils.Job;
import info.asdev.fadcr.utils.RandomSelector;
import info.asdev.fadcr.utils.Text;
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
    private RandomSelector<String> choiceSelector;
    private Pattern spacesPattern = Pattern.compile(" ");
    private Reaction active;
    private Job job;
    private BukkitRunnable timeoutRunnable;
    private Map<String, Reaction> registeredReactions = new HashMap<>();
    private List<ReactionImpl> loadedReactionImplementations = new ArrayList<>();
    private Set<String> rewardKeys;
    private Reward reward;
    private boolean caseSensitiveAnswers = true;
    private long timeout = 60_000, interval = 300_000;
    private String timeoutMessage;
    private long startTime;
    private int minPlayers = 2;

    public void init() {
        FileConfiguration config = FADCR.getInstance().getConfig();
        ConfigurationSection reactionsSection = config.getConfigurationSection("reactions");
        Set<String> reactionKeys = reactionsSection.getKeys(false);
        rewardKeys = config.getConfigurationSection("rewards").getKeys(false);

        for (String key : reactionKeys) {
            ConfigurationSection section = reactionsSection.getConfigurationSection(key);

            String type = section.getString("type");
            String answer = section.getString("answer");
            String question = section.getString("question");
            String reward = section.getString("reward");

            loadedReactionImplementations.add(new ReactionImpl("reactions." + key, type, answer, question, reward));
        }

        caseSensitiveAnswers = config.getBoolean("options.case-sensitive-answers", true);
        choiceSelector = RandomSelector.weighted(rewardKeys, (key) -> FADCR.getInstance().getConfig().getDouble("rewards." + key + ".chance", 1d));

        interval = config.getLong("options.interval", 300_000);
        timeout = config.getLong("options.timeout", 60_000);
        minPlayers = config.getInt("options.min-players", 2);

        registerReaction(new ReactionUnscramble("unscramble", "Unscramble"));
        registerReaction(new ReactionType("type", "Type"));
        registerReaction(new ReactionSolve("solve", "Solve"));

        job = Job.of("chatreactions_run", this::runJob, Duration.of(interval, ChronoUnit.MILLIS));
        job.start();
    }

    public void shutdown() {
        if (job != null) {
            job.shutdown();
        }
    }

    public void onPlayerLeave(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() < minPlayers && timeoutRunnable != null && running) {
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
        active = activateRandomReaction();

        String message = Text.getMessage("chat-reaction.format", true,
                         Text.getMessage("reactions." + active.getId().toLowerCase(), false, active.getQuestion())
        );
        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, message);
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

        timeoutRunnable.runTaskLater(FADCR.getInstance(), timeout / 20L);
        startTime = System.currentTimeMillis();
    }

    public boolean registerReaction(Reaction reaction) {
        if (registeredReactions.containsKey(reaction.getId())) {
            return false;
        }

        return registeredReactions.putIfAbsent(reaction.getId(), reaction) != null;
    }

    public Reaction activateRandomReaction() {
        Reaction[] reactions = registeredReactions.values().toArray(new Reaction[0]);
        return active = reactions[random.nextInt(reactions.length)];
    }

    public ReactionImpl[] getReactionsById(String id) {
        List<ReactionImpl> all = loadedReactionImplementations.stream().filter(impl -> impl.getType().equals(id)).toList();
        return all.toArray(new ReactionImpl[0]);
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

        DecimalFormat format = new DecimalFormat(Text.getMessage("chat-reaction.time-format", false));
        String solved = Text.getMessage("chat-reaction.solved-by", false, who.getName(), format.format((double) (System.currentTimeMillis() - startTime) / 1000d));
        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, solved);
        });
    }

    public void awardPlayer(Player who) {
        ReactionImpl activeImpl = active.getImplementation();
        String reward = activeImpl.getReward();

        ConfigurationSection rewardSection = FADCR.getInstance().getConfig().getConfigurationSection("rewards");
        rewardSection = !"random".equalsIgnoreCase(reward) ? rewardSection.getConfigurationSection(reward) : rewardSection.getConfigurationSection(choiceSelector.next(random));

        if (rewardSection != null) {
            this.reward = Reward.builder().section(rewardSection).player(who).build();
            Bukkit.getScheduler().runTask(FADCR.getInstance(), this::giveReward);
            Text.send(who, "rewards.won", rewardSection.getString("display_name"));
        } else {
            Text.send(who, "rewards.no-reward");
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
            } catch(Exception ex) {
                FADCR.getInstance().getLogger().log(Level.WARNING, "Reward command execution failed for command: " + parsed, ex);
            }
        }
    }

    private void timeout() {
        if (!running) {
            return;
        }

        running = false;
        active.reset();
        reward = null;
    }

    public boolean areAnswersCaseSensitive() {
        return caseSensitiveAnswers;
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
