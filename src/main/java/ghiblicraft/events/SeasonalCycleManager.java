package ghiblicraft.events;

import ghiblicraft.registry.ModParticles;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonalCycleManager {
    // Each season lasts 5 in-game days (120,000 ticks)
    private static final long SEASON_LENGTH = 120000L;

    public enum Season {
        SPRING("Spring", Formatting.GREEN, 0.3f),
        SUMMER("Summer", Formatting.YELLOW, 0.6f),
        AUTUMN("Autumn", Formatting.GOLD, 0.8f),
        WINTER("Winter", Formatting.AQUA, 0.1f);

        public final String name;
        public final Formatting color;
        public final float leafFallChance;

        Season(String name, Formatting color, float leafFallChance) {
            this.name = name;
            this.color = color;
            this.leafFallChance = leafFallChance;
        }
    }

    private static Season lastSeason = null;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() != World.OVERWORLD) return;

            Season currentSeason = getCurrentSeason(world);

            // Announce season change
            if (lastSeason != currentSeason) {
                announceSeason(world, currentSeason);
                lastSeason = currentSeason;
            }

            // Apply seasonal effects every 200 ticks
            if (world.getTime() % 200 == 0) {
                applySeasonalEffects(world, currentSeason);
            }
        });
    }

    public static Season getCurrentSeason(ServerWorld world) {
        long totalTime = world.getTimeOfDay();
        int seasonIndex = (int) ((totalTime / SEASON_LENGTH) % 4);
        return Season.values()[seasonIndex];
    }

    private static void announceSeason(ServerWorld world, Season season) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(
                    Text.literal("The season has changed to " + season.name + "!")
                            .formatted(season.color, Formatting.BOLD),
                    false);

            // Season-specific welcome buff
            switch (season) {
                case SPRING -> player.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.REGENERATION, 600, 0, true, false));
                case SUMMER -> player.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.HASTE, 600, 0, true, false));
                case AUTUMN -> player.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.LUCK, 600, 0, true, false));
                case WINTER -> player.addStatusEffect(
                        new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 0, true, false));
            }
        }
    }

    private static void applySeasonalEffects(ServerWorld world, Season season) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            BlockPos playerPos = player.getBlockPos();

            switch (season) {
                case SPRING -> {
                    // Flowers bloom nearby randomly
                    if (world.random.nextInt(10) == 0) {
                        BlockPos randomPos = playerPos.add(
                                world.random.nextInt(16) - 8, 0, world.random.nextInt(16) - 8);
                        // Find surface
                        for (int y = playerPos.getY() + 5; y >= playerPos.getY() - 5; y--) {
                            BlockPos checkPos = new BlockPos(randomPos.getX(), y, randomPos.getZ());
                            if (world.getBlockState(checkPos).isOf(Blocks.GRASS_BLOCK) &&
                                    world.getBlockState(checkPos.up()).isAir()) {
                                BlockState[] flowers = {
                                        Blocks.DANDELION.getDefaultState(),
                                        Blocks.POPPY.getDefaultState(),
                                        Blocks.AZURE_BLUET.getDefaultState(),
                                        Blocks.CORNFLOWER.getDefaultState()
                                };
                                world.setBlockState(checkPos.up(),
                                        flowers[world.random.nextInt(flowers.length)], 3);
                                break;
                            }
                        }
                    }

                    // Pollen particles
                    world.spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                            player.getX() + world.random.nextGaussian() * 8,
                            player.getY() + 5 + world.random.nextFloat() * 5,
                            player.getZ() + world.random.nextGaussian() * 8,
                            1, 0, 0, 0, 0);
                }

                case SUMMER -> {
                    // Fireflies at night
                    if (!world.isDay()) {
                        world.spawnParticles(ParticleTypes.END_ROD,
                                player.getX() + world.random.nextGaussian() * 12,
                                player.getY() + 1 + world.random.nextFloat() * 4,
                                player.getZ() + world.random.nextGaussian() * 12,
                                1, 0, 0, 0, 0.01);
                    }
                }

                case AUTUMN -> {
                    // Falling leaves effect
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticles(ParticleTypes.CHERRY_LEAVES,
                                player.getX() + world.random.nextGaussian() * 10,
                                player.getY() + 8 + world.random.nextFloat() * 5,
                                player.getZ() + world.random.nextGaussian() * 10,
                                1, 0, 0, 0, 0);
                    }
                }

                case WINTER -> {
                    // Gentle snowfall in all biomes
                    world.spawnParticles(ParticleTypes.SNOWFLAKE,
                            player.getX() + world.random.nextGaussian() * 12,
                            player.getY() + 10 + world.random.nextFloat() * 5,
                            player.getZ() + world.random.nextGaussian() * 12,
                            2, 0, 0, 0, 0);

                    // Frost breath effect when outside
                    if (world.isSkyVisible(playerPos)) {
                        world.spawnParticles(ParticleTypes.CLOUD,
                                player.getX(), player.getY() + 1.5, player.getZ(),
                                1, 0.1, 0, 0.1, 0.01);
                    }
                }
            }
        }
    }
}
