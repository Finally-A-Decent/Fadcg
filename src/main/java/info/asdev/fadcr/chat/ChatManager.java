package info.asdev.fadcr.chat;

import info.asdev.fadcr.FADCR;
import info.asdev.fadcr.chat.reactions.Reaction;
import info.asdev.fadcr.chat.reactions.ReactionImpl;
import info.asdev.fadcr.utils.Job;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatManager {
    private static ChatManager instance;
    @Getter private Random random = new Random();

    private Pattern spacesPattern = Pattern.compile(" ");
    private Reaction active;
    private Job job;

    private Map<String, Reaction> registeredReactions = new HashMap<>();
    private List<ReactionImpl> loadedReactionImplementations = new ArrayList<>();

    @Getter private boolean running = false;
    private boolean caseSensitiveAnswers = true;


    public void init() {
        ConfigurationSection reactionsSection = FADCR.getInstance().getConfig().getConfigurationSection("reactions");
        Set<String> reactionKeys = reactionsSection.getKeys(false);

        for (String key : reactionKeys) {
            ConfigurationSection section = reactionsSection.getConfigurationSection(key);

            String type = section.getString("type");
            String answer = section.getString("answer");
            String question = section.getString("question");
            String reward = section.getString("reward");

            loadedReactionImplementations.add(new ReactionImpl("reactions." + key, type, answer, question, reward));
        }

        caseSensitiveAnswers = FADCR.getInstance().getConfig().getBoolean("options.case-sensitive-answers", true);
        job = Job.of("chatreactions_run", this::runJob, Duration.of(5L, ChronoUnit.MINUTES));
    }

    public void shutdown() {
        if (job != null) {
            job.shutdown();
        }
    }

    private void runJob() {
        if (running) {
            return;
        }

        running = true;
        active = activateRandomReaction();
    }

    public boolean registerReaction(Reaction reaction) {
        if (registeredReactions.containsKey(reaction.getId())) {
            return false;
        }

        return registeredReactions.putIfAbsent(reaction.getId(), reaction) != null;
    }

    public Reaction activateRandomReaction() {
        Reaction[] reactions = registeredReactions.values().toArray(new Reaction[0]);
        return active = reactions[random.nextInt(reactions.length - 1)];
    }

    public ReactionImpl[] getReactionsById(String id) {
        ReactionImpl[] all = loadedReactionImplementations.toArray(new ReactionImpl[0]);
        return all = (ReactionImpl[]) Stream.of(all).filter(impl -> impl.getType().equals(id)).toArray();
    }

    public void processChatMessage(Player who, String message) {
        if (!running || active == null) {
            return;
        }

        boolean correct = active.attempt(who, message);
        if (!correct) {
            return;
        }

        running = false;
        awardPlayer(who);
    }

    public void awardPlayer(Player who) {

    }

    public boolean areAnswersCaseSensitive() {
        return caseSensitiveAnswers;
    }

    public static ChatManager getInstance() {
        return instance == null ? instance = new ChatManager() : instance;
    }
}
