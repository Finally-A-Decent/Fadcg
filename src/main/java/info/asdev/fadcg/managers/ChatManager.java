package info.asdev.fadcg.managers;

import info.asdev.fadcg.Fadcg;
import info.asdev.fadcg.chat.ReactionImpl;
import info.asdev.fadcg.chat.ReactionMode;
import info.asdev.fadcg.events.PlayerBarterEvent;
import info.asdev.fadcg.managers.reaction.ReactionCategory;
import info.asdev.fadcg.managers.reaction.Reward;
import info.asdev.fadcg.utils.Job;
import info.asdev.fadcg.utils.Text;
import info.asdev.fadcg.utils.Util;
import io.papermc.paper.event.player.PlayerTradeEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
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
    private Reward reward;
    private Job job;
    private ReactionCategory active;
    @Getter private BukkitRunnable timeoutRunnable;
    private Pattern spacesPattern = Pattern.compile(" ");
    private String timeoutMessage;

    public void init() {
        shutdown();

        FileConfiguration config = Fadcg.getInstance().getConfig();

        caseSensitiveAnswers = config.getBoolean("options.case-sensitive-answers", true);
        centerFormat = Fadcg.getLang().getBoolean("chat-reaction.center-reaction-format", false);

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
            timeoutMessage = Text.getMessage("chat-reaction.reaction-cancelled-not-enough-players", false, active.getActiveImplementation().getAnswer());
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
        active = ReactionManager.createReaction();

        if (active == null) {
            return;
        }

        //active.create();
        active.init();

        String message = Text.getMessage("chat-reaction.format", true, active.getMessage());
        final String message2 = isCenterFormat() ? Text.getMultilineCenteredMessage(message) : message;

        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, message2);
        });

        timeoutRunnable = new BukkitRunnable() {
            @Override public void run() {
                if (running) {
                    timeout();

                    timeoutMessage = active.getExpiryMessage();
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

    public void runSpecificReaction(ReactionCategory category, ReactionImpl implementation, boolean force) {
        if ((running || Bukkit.getOnlinePlayers().size() < minPlayers)) {
            return;
        }

        running = true;
        active = category;
        active.setActiveImplementation(implementation);
        category.init(implementation);

        if (active == null) {
            return;
        }

        active.init();
        String message = Text.getMessage("chat-reaction.format", true, active.getMessage());
        Bukkit.getOnlinePlayers().forEach(player -> {
            Text.sendNoFetch(player, isCenterFormat() ? Text.getMultilineCenteredMessage(message) : message);
        });

        timeoutRunnable = new BukkitRunnable() {
            @Override public void run() {
                if (running) {
                    timeout();

                    timeoutMessage = active.getExpiryMessage();
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

    @SuppressWarnings("deprecation")
    public void onPlayerEvent(ReactionMode mode, Event event) {
        if (active == null || !running) {
            return;
        }
        ReactionMode activeMode = active.getMode();
        if (!mode.equals(activeMode)) {
            return;
        }

        boolean result = false;
        Player who = null;
        switch(mode) {
            case CHAT_MESSAGE -> {
                AsyncPlayerChatEvent ev = (AsyncPlayerChatEvent) event;
                who = ev.getPlayer();
                result = active.attempt(ev.getPlayer(), ev.getMessage(), event);
            }
            case BLOCK_BREAK -> {
                BlockBreakEvent ev = (BlockBreakEvent) event;
                who = ev.getPlayer();
                result = active.attempt(ev.getPlayer(), null, event);
            }
            case BLOCK_PLACE -> {
                BlockPlaceEvent ev = (BlockPlaceEvent) event;
                who = ev.getPlayer();
                result = active.attempt(ev.getPlayer(), null, event);
            }
            case CRAFT_ITEM -> {
                CraftItemEvent ev = (CraftItemEvent) event;
                who = (Player) ev.getWhoClicked();
                result = active.attempt((Player) ev.getWhoClicked(), null, event);
            }
            case USE_ITEM -> {
                PlayerItemConsumeEvent ev = (PlayerItemConsumeEvent) event;
                who = ev.getPlayer();
                result = active.attempt(ev.getPlayer(), null, event);
            }
            case KILL_MOB -> {
                EntityDeathEvent ev = (EntityDeathEvent) event;
                who = ev.getEntity().getKiller();
                result = active.attempt(ev.getEntity().getKiller(), null, event);
            }
            case VILLAGER_TRADE -> {
                PlayerTradeEvent ev = (PlayerTradeEvent) event;
                who = ev.getPlayer();
                result = active.attempt(ev.getPlayer(), null, event);
            }
//            case PIGLIN_BARTER -> {
//                PlayerBarterEvent ev = (PlayerBarterEvent) event;
//                who = ev.getPlayer();
//                result = active.attempt(ev.getPlayer(), null, event);
//            }
        }

        if (!result) {
            return;
        }

        onCorrectAnswer(who);
    }

    private void onCorrectAnswer(Player who) {
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
        ReactionImpl activeImpl = active.getActiveImplementation();
        String reward = activeImpl.getReward();

        this.reward = reward.equalsIgnoreCase("random") ? RewardManager.getRandomReward() : RewardManager.getReward(reward);
        Bukkit.getScheduler().runTask(Fadcg.getInstance(), () -> this.reward.give(who));
        Text.send(who, "chat-reaction.reaction-won", this.reward.getDisplayName());
    }

    private void timeout() {
        if (!running) {
            return;
        }

        running = false;
        if (timeoutRunnable != null && !timeoutRunnable.isCancelled()) {
            timeoutRunnable.cancel();
        }
    }
    public static ChatManager getInstance() {
        return instance == null ? instance = new ChatManager() : instance;
    }
}
