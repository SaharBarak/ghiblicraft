package ghiblicraft.systems;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Spirit Reputation System
 *
 * Players build karma through their actions in the world:
 * - Planting trees, flowers: +karma
 * - Feeding spirits: +karma
 * - Killing passive mobs: -karma
 * - Attacking spirits: -big karma
 * - Breaking spawners: -karma
 *
 * Karma tiers affect spirit behavior:
 * - REVERED (80+): Spirits actively seek you, give gifts, glow around you
 * - TRUSTED (50-79): Spirits approach, trade, follow
 * - NEUTRAL (20-49): Normal behavior
 * - WARY (1-19): Spirits flee, won't trade
 * - CURSED (0): Spirits attack, darkness debuff, bad luck
 */
public class SpiritReputationSystem {
    private static final Map<UUID, Integer> playerKarma = new HashMap<>();
    private static final int DEFAULT_KARMA = 50;
    private static final int MAX_KARMA = 100;
    private static final int MIN_KARMA = 0;

    public enum KarmaTier {
        REVERED("Revered by Spirits", Formatting.GOLD, 80),
        TRUSTED("Trusted by Spirits", Formatting.GREEN, 50),
        NEUTRAL("Known to Spirits", Formatting.GRAY, 20),
        WARY("Distrusted by Spirits", Formatting.YELLOW, 1),
        CURSED("Cursed by Spirits", Formatting.DARK_RED, 0);

        public final String title;
        public final Formatting color;
        public final int minKarma;

        KarmaTier(String title, Formatting color, int minKarma) {
            this.title = title;
            this.color = color;
            this.minKarma = minKarma;
        }
    }

    public static void register() {
        // Punish attacking GhibliCraft entities
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            if (entity.getType().getRegistryEntry().registryKey().getValue().getNamespace()
                    .equals(GhibliCraft.MOD_ID)) {
                modifyKarma(player, -15);
                broadcastSpiritAnger(player, (ServerWorld) world);
            } else if (entity instanceof net.minecraft.entity.passive.AnimalEntity) {
                modifyKarma(player, -3);
            }

            return ActionResult.PASS;
        });

        // Passive karma effects every 5 seconds
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 100 != 0) return;

            for (ServerPlayerEntity player : world.getPlayers()) {
                int karma = getKarma(player);
                KarmaTier tier = getTier(karma);

                applyTierEffects(player, tier, world);

                // Natural karma decay toward neutral
                if (karma > DEFAULT_KARMA && world.random.nextInt(10) == 0) {
                    modifyKarma(player, -1);
                } else if (karma < DEFAULT_KARMA && world.random.nextInt(10) == 0) {
                    modifyKarma(player, 1);
                }
            }
        });

        GhibliCraft.LOGGER.info("Registered Spirit Reputation System.");
    }

    public static int getKarma(PlayerEntity player) {
        return playerKarma.getOrDefault(player.getUuid(), DEFAULT_KARMA);
    }

    public static KarmaTier getTier(int karma) {
        for (KarmaTier tier : KarmaTier.values()) {
            if (karma >= tier.minKarma) return tier;
        }
        return KarmaTier.CURSED;
    }

    public static void modifyKarma(PlayerEntity player, int amount) {
        int current = getKarma(player);
        int newKarma = Math.max(MIN_KARMA, Math.min(MAX_KARMA, current + amount));

        KarmaTier oldTier = getTier(current);
        KarmaTier newTier = getTier(newKarma);

        playerKarma.put(player.getUuid(), newKarma);

        // Announce tier changes
        if (oldTier != newTier) {
            player.sendMessage(
                    Text.literal("Spirit Reputation: " + newTier.title)
                            .formatted(newTier.color, Formatting.BOLD),
                    false);

            if (player instanceof ServerPlayerEntity sp && sp.getServerWorld() != null) {
                if (newTier.ordinal() < oldTier.ordinal()) {
                    // Improved!
                    sp.getServerWorld().spawnParticles(ParticleTypes.ENCHANT,
                            sp.getX(), sp.getY() + 1, sp.getZ(),
                            30, 1.0, 1.0, 1.0, 0.5);
                    sp.getServerWorld().playSound(null, sp.getBlockPos(),
                            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.5f);
                } else {
                    // Worsened!
                    sp.getServerWorld().spawnParticles(ParticleTypes.SMOKE,
                            sp.getX(), sp.getY() + 1, sp.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);
                    sp.getServerWorld().playSound(null, sp.getBlockPos(),
                            SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 0.3f, 1.0f);
                }
            }
        }
    }

    private static void applyTierEffects(ServerPlayerEntity player, KarmaTier tier, ServerWorld world) {
        switch (tier) {
            case REVERED -> {
                // Golden aura, spirits give gifts
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200, 1, true, false));
                world.spawnParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 2, player.getZ(),
                        1, 0.5, 0.5, 0.5, 0.01);

                // Spirits nearby glow
                List<PathAwareEntity> spirits = world.getEntitiesByClass(PathAwareEntity.class,
                        new Box(player.getBlockPos()).expand(16),
                        e -> e.getType().getRegistryEntry().registryKey().getValue().getNamespace()
                                .equals(GhibliCraft.MOD_ID));
                for (PathAwareEntity spirit : spirits) {
                    spirit.addStatusEffect(new StatusEffectInstance(
                            StatusEffects.GLOWING, 200, 0, true, false));
                }
            }
            case TRUSTED -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200, 0, true, false));
            }
            case NEUTRAL -> {
                // No special effects
            }
            case WARY -> {
                // Spirits flee (handled in spirit AI)
                // Subtle warning particles
                if (world.random.nextInt(5) == 0) {
                    world.spawnParticles(ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 2, player.getZ(),
                            1, 0.3, 0.3, 0.3, 0);
                }
            }
            case CURSED -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 200, 1, true, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0, true, false));

                // Ominous particles
                world.spawnParticles(ParticleTypes.SCULK_SOUL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        2, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }

    private static void broadcastSpiritAnger(PlayerEntity player, ServerWorld world) {
        // All spirits in range become hostile/flee
        List<PathAwareEntity> spirits = world.getEntitiesByClass(PathAwareEntity.class,
                new Box(player.getBlockPos()).expand(32),
                e -> e.getType().getRegistryEntry().registryKey().getValue().getNamespace()
                        .equals(GhibliCraft.MOD_ID));

        for (PathAwareEntity spirit : spirits) {
            // Run away from the player
            double dx = spirit.getX() - player.getX();
            double dz = spirit.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) {
                spirit.getNavigation().startMovingTo(
                        spirit.getX() + (dx / dist) * 15,
                        spirit.getY(),
                        spirit.getZ() + (dz / dist) * 15,
                        1.5);
            }

            world.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                    spirit.getX(), spirit.getY() + 1, spirit.getZ(),
                    3, 0.3, 0.3, 0.3, 0);
        }

        world.playSound(null, player.getBlockPos(),
                SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 0.3f, 2.0f);
    }

    /**
     * Call this from planting-related events to reward good karma
     */
    public static void onPlantGrown(PlayerEntity player) {
        modifyKarma(player, 2);
    }

    /**
     * Call this when a player feeds a spirit
     */
    public static void onSpiritFed(PlayerEntity player) {
        modifyKarma(player, 5);
    }
}
