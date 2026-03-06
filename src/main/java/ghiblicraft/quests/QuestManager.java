package ghiblicraft.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class QuestManager {
    private static final Map<UUID, List<Quest>> PLAYER_QUESTS = new HashMap<>();

    public static void assignQuest(PlayerEntity player, Quest quest) {
        PLAYER_QUESTS.computeIfAbsent(player.getUuid(), k -> new ArrayList<>()).add(quest);
        player.sendMessage(Text.literal("New quest: " + quest.getName()).formatted(Formatting.GOLD), false);
    }

    public static void completeQuest(PlayerEntity player, String questId) {
        List<Quest> quests = PLAYER_QUESTS.get(player.getUuid());
        if (quests == null) return;

        quests.stream()
                .filter(q -> q.getId().equals(questId) && !q.isCompleted())
                .findFirst()
                .ifPresent(quest -> {
                    quest.complete();
                    quest.grantRewards(player);
                    player.sendMessage(Text.literal("Quest completed: " + quest.getName())
                            .formatted(Formatting.GREEN), false);
                });
    }

    public static List<Quest> getActiveQuests(PlayerEntity player) {
        return PLAYER_QUESTS.getOrDefault(player.getUuid(), Collections.emptyList())
                .stream()
                .filter(q -> !q.isCompleted())
                .toList();
    }

    public static class Quest {
        private final String id;
        private final String name;
        private final String description;
        private boolean completed = false;

        public Quest(String id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isCompleted() { return completed; }
        public void complete() { this.completed = true; }

        public void grantRewards(PlayerEntity player) {
            // Override in subclasses for specific rewards
        }
    }
}
