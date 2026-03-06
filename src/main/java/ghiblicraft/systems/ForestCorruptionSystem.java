package ghiblicraft.systems;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

/**
 * Forest Corruption & Healing System (Princess Mononoke)
 *
 * The forest has health. Destroying trees, killing animals, and building
 * corrupting blocks (TNT, fire) reduces forest health. Planting saplings,
 * flowers, and leaving the forest intact heals it.
 *
 * Effects based on forest health:
 * - PRISTINE: Lush growth, spirit spawns, glowing particles, healing aura
 * - HEALTHY: Normal behavior, occasional spirit sighting
 * - STRESSED: Reduced spawns, some leaves decay, warning particles
 * - CORRUPTED: Dark fog, hostile creatures, withering plants, curse debuffs
 *
 * Players can heal corruption by planting trees, using spirit essence,
 * and performing the Forest Purification Ritual.
 */
public class ForestCorruptionSystem {
    // Track corruption per-chunk (key: "dim:chunkX:chunkZ")
    private static final Map<String, Integer> chunkHealth = new HashMap<>();
    private static final int DEFAULT_HEALTH = 100;
    private static final int MAX_HEALTH = 150; // Pristine forests can exceed 100
    private static final int MIN_HEALTH = 0;

    public enum ForestState {
        PRISTINE("Pristine Forest", Formatting.GREEN, 120),
        HEALTHY("Healthy Forest", Formatting.DARK_GREEN, 80),
        STRESSED("Stressed Forest", Formatting.YELLOW, 40),
        CORRUPTED("Corrupted Forest", Formatting.DARK_RED, 0);

        public final String name;
        public final Formatting color;
        public final int minHealth;

        ForestState(String name, Formatting color, int minHealth) {
            this.name = name;
            this.color = color;
            this.minHealth = minHealth;
        }
    }

    public static void register() {
        // Tree destruction causes corruption
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (world.isClient) return;

            if (isTreeBlock(state)) {
                damageForest((ServerWorld) world, pos, 3);
                SpiritReputationSystem.modifyKarma(player, -1);
            } else if (isFlower(state)) {
                damageForest((ServerWorld) world, pos, 1);
            }
        });

        // Periodic forest tick
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() != World.OVERWORLD) return;
            if (world.getTime() % 200 != 0) return; // Every 10 seconds

            for (ServerPlayerEntity player : world.getPlayers()) {
                BlockPos pos = player.getBlockPos();
                String key = getChunkKey(world, pos);
                int health = getHealth(key);
                ForestState state = getState(health);

                applyForestEffects(player, state, world, pos, health);

                // Natural regeneration (forests slowly heal)
                if (health < DEFAULT_HEALTH && world.random.nextInt(5) == 0) {
                    chunkHealth.put(key, Math.min(DEFAULT_HEALTH, health + 1));
                }
            }
        });

        GhibliCraft.LOGGER.info("Registered Forest Corruption System.");
    }

    public static void damageForest(ServerWorld world, BlockPos pos, int amount) {
        String key = getChunkKey(world, pos);
        int health = getHealth(key);
        int newHealth = Math.max(MIN_HEALTH, health - amount);
        chunkHealth.put(key, newHealth);

        ForestState oldState = getState(health);
        ForestState newState = getState(newHealth);

        if (oldState != newState && newState.ordinal() > oldState.ordinal()) {
            // Forest degraded — visual warning
            world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                    pos.getX(), pos.getY() + 1, pos.getZ(),
                    10, 2.0, 1.0, 2.0, 0.02);
            world.playSound(null, pos, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
                    SoundCategory.AMBIENT, 0.2f, 0.5f);
        }
    }

    public static void healForest(ServerWorld world, BlockPos pos, int amount) {
        String key = getChunkKey(world, pos);
        int health = getHealth(key);
        int newHealth = Math.min(MAX_HEALTH, health + amount);
        chunkHealth.put(key, newHealth);

        ForestState oldState = getState(health);
        ForestState newState = getState(newHealth);

        if (oldState != newState && newState.ordinal() < oldState.ordinal()) {
            // Forest improved!
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX(), pos.getY() + 1, pos.getZ(),
                    20, 3.0, 1.0, 3.0, 0.1);
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.AMBIENT, 0.5f, 1.2f);
        }
    }

    /**
     * Perform the Forest Purification Ritual.
     * Requires placing spirit essence on moss blocks in a circle, then using bone meal.
     * Massively heals the forest in a large radius.
     */
    public static void performPurificationRitual(ServerWorld world, BlockPos center, ServerPlayerEntity player) {
        // Heal in a 5-chunk radius
        for (int cx = -5; cx <= 5; cx++) {
            for (int cz = -5; cz <= 5; cz++) {
                BlockPos chunkCenter = center.add(cx * 16, 0, cz * 16);
                healForest(world, chunkCenter, 50);
            }
        }

        // Massive visual effect
        for (int i = 0; i < 100; i++) {
            double angle = (Math.PI * 2 * i) / 100;
            double radius = 5.0 + i * 0.3;
            world.spawnParticles(ParticleTypes.END_ROD,
                    center.getX() + Math.cos(angle) * radius,
                    center.getY() + 1 + (i * 0.1),
                    center.getZ() + Math.sin(angle) * radius,
                    1, 0, 0.1, 0, 0.01);
        }

        world.spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,
                center.getX(), center.getY() + 2, center.getZ(),
                100, 3.0, 3.0, 3.0, 0.5);

        world.playSound(null, center, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.AMBIENT, 1.0f, 1.0f);

        player.sendMessage(Text.literal("The forest spirits rejoice! The corruption has been cleansed!")
                .formatted(Formatting.GREEN, Formatting.BOLD), false);

        SpiritReputationSystem.modifyKarma(player, 20);

        // Grow trees and flowers in the ritual area
        for (int i = 0; i < 30; i++) {
            int rx = center.getX() + world.random.nextInt(20) - 10;
            int rz = center.getZ() + world.random.nextInt(20) - 10;
            for (int y = center.getY() - 5; y <= center.getY() + 5; y++) {
                BlockPos checkPos = new BlockPos(rx, y, rz);
                if (world.getBlockState(checkPos).isOf(Blocks.GRASS_BLOCK) &&
                        world.getBlockState(checkPos.up()).isAir()) {
                    if (world.random.nextInt(3) == 0) {
                        world.setBlockState(checkPos.up(), Blocks.OAK_SAPLING.getDefaultState(), 3);
                    } else {
                        Block[] flowers = {Blocks.DANDELION, Blocks.POPPY, Blocks.AZURE_BLUET,
                                Blocks.LILY_OF_THE_VALLEY, Blocks.CORNFLOWER};
                        world.setBlockState(checkPos.up(),
                                flowers[world.random.nextInt(flowers.length)].getDefaultState(), 3);
                    }
                    break;
                }
            }
        }
    }

    private static void applyForestEffects(ServerPlayerEntity player, ForestState state,
                                            ServerWorld world, BlockPos pos, int health) {
        switch (state) {
            case PRISTINE -> {
                // Healing aura, lush particles
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 0, true, false));
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + world.random.nextGaussian() * 5,
                        pos.getY() + 2,
                        pos.getZ() + world.random.nextGaussian() * 5,
                        2, 0, 0, 0, 0);

                // Sparkle on leaves nearby
                if (world.random.nextInt(3) == 0) {
                    world.spawnParticles(ParticleTypes.END_ROD,
                            pos.getX() + world.random.nextGaussian() * 8,
                            pos.getY() + 5 + world.random.nextFloat() * 5,
                            pos.getZ() + world.random.nextGaussian() * 8,
                            1, 0, 0, 0, 0);
                }
            }
            case HEALTHY -> {
                // Occasional pollen
                if (world.random.nextInt(3) == 0) {
                    world.spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                            pos.getX() + world.random.nextGaussian() * 8,
                            pos.getY() + 8,
                            pos.getZ() + world.random.nextGaussian() * 8,
                            1, 0, 0, 0, 0);
                }
            }
            case STRESSED -> {
                // Warning particles, reduced visibility
                if (world.random.nextInt(4) == 0) {
                    world.spawnParticles(ParticleTypes.SMOKE,
                            pos.getX() + world.random.nextGaussian() * 6,
                            pos.getY() + 1,
                            pos.getZ() + world.random.nextGaussian() * 6,
                            1, 0, 0.02, 0, 0);
                }

                // Wilt nearby flowers (rarely)
                if (world.random.nextInt(50) == 0) {
                    BlockPos randomNear = pos.add(
                            world.random.nextInt(8) - 4, 0, world.random.nextInt(8) - 4);
                    for (int y = pos.getY() - 3; y <= pos.getY() + 3; y++) {
                        BlockPos check = new BlockPos(randomNear.getX(), y, randomNear.getZ());
                        if (isFlower(world.getBlockState(check))) {
                            world.setBlockState(check, Blocks.DEAD_BUSH.getDefaultState(), 3);
                            break;
                        }
                    }
                }
            }
            case CORRUPTED -> {
                // Dark fog, curse effects, dying plants
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 300, 0, true, false));

                world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + world.random.nextGaussian() * 8,
                        pos.getY() + 0.5,
                        pos.getZ() + world.random.nextGaussian() * 8,
                        3, 0, 0.02, 0, 0);

                world.spawnParticles(ParticleTypes.SCULK_SOUL,
                        pos.getX() + world.random.nextGaussian() * 5,
                        pos.getY(),
                        pos.getZ() + world.random.nextGaussian() * 5,
                        1, 0, 0.05, 0, 0);

                // Spread corruption to nearby blocks
                if (world.random.nextInt(30) == 0) {
                    BlockPos randomNear = pos.add(
                            world.random.nextInt(6) - 3, 0, world.random.nextInt(6) - 3);
                    for (int y = pos.getY() - 3; y <= pos.getY() + 3; y++) {
                        BlockPos check = new BlockPos(randomNear.getX(), y, randomNear.getZ());
                        BlockState blockState = world.getBlockState(check);
                        if (blockState.isOf(Blocks.GRASS_BLOCK)) {
                            world.setBlockState(check, Blocks.PODZOL.getDefaultState(), 3);
                            break;
                        } else if (isFlower(blockState)) {
                            world.setBlockState(check, Blocks.DEAD_BUSH.getDefaultState(), 3);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static boolean isTreeBlock(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.DARK_OAK_LOG || block == Blocks.ACACIA_LOG ||
                block == Blocks.CHERRY_LOG || block == Blocks.MANGROVE_LOG;
    }

    private static boolean isFlower(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.DANDELION || block == Blocks.POPPY || block == Blocks.AZURE_BLUET ||
                block == Blocks.OXEYE_DAISY || block == Blocks.CORNFLOWER || block == Blocks.ALLIUM ||
                block == Blocks.LILY_OF_THE_VALLEY || block == Blocks.BLUE_ORCHID;
    }

    private static String getChunkKey(ServerWorld world, BlockPos pos) {
        return world.getRegistryKey().getValue() + ":" + (pos.getX() >> 4) + ":" + (pos.getZ() >> 4);
    }

    private static int getHealth(String key) {
        return chunkHealth.getOrDefault(key, DEFAULT_HEALTH);
    }

    private static ForestState getState(int health) {
        for (ForestState state : ForestState.values()) {
            if (health >= state.minHealth) return state;
        }
        return ForestState.CORRUPTED;
    }
}
