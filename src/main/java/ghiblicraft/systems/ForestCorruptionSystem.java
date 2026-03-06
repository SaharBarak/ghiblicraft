package ghiblicraft.systems;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Forest Corruption & Healing System — persisted via PersistentState.
 * Uses block tags instead of hardcoded block lists where possible.
 */
public class ForestCorruptionSystem {
    private static final int DEFAULT_HEALTH = 100;
    private static final int MAX_HEALTH = 150;
    private static final int MIN_HEALTH = 0;
    private static final String DATA_KEY = "ghiblicraft_forest";

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

    public static class ForestHealthState extends PersistentState {
        final Map<String, Integer> chunkHealth = new HashMap<>();

        public static ForestHealthState fromNbt(NbtCompound nbt) {
            ForestHealthState state = new ForestHealthState();
            NbtCompound data = nbt.getCompound("health");
            for (String key : data.getKeys()) {
                state.chunkHealth.put(key, data.getInt(key));
            }
            return state;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            NbtCompound data = new NbtCompound();
            // Only persist non-default values to keep data small
            chunkHealth.forEach((key, health) -> {
                if (health != DEFAULT_HEALTH) {
                    data.putInt(key, health);
                }
            });
            nbt.put("health", data);
            return nbt;
        }
    }

    private static ForestHealthState getState(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        return overworld.getPersistentStateManager().getOrCreate(
                ForestHealthState::fromNbt, ForestHealthState::new, DATA_KEY);
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (world.isClient) return;

            if (isTreeBlock(state)) {
                damageForest((ServerWorld) world, pos, 3);
                SpiritReputationSystem.modifyKarma(player, -1);
            } else if (isFlower(state)) {
                damageForest((ServerWorld) world, pos, 1);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() != World.OVERWORLD) return;
            if (world.getTime() % 200 != 0) return;

            ForestHealthState state = getState(world.getServer());

            for (ServerPlayerEntity player : world.getPlayers()) {
                BlockPos pos = player.getBlockPos();
                String key = getChunkKey(pos);
                int health = state.chunkHealth.getOrDefault(key, DEFAULT_HEALTH);
                ForestState forestState = getState(health);

                applyForestEffects(player, forestState, world, pos);

                if (health < DEFAULT_HEALTH && world.random.nextInt(5) == 0) {
                    int newHealth = Math.min(DEFAULT_HEALTH, health + 1);
                    if (newHealth == DEFAULT_HEALTH) {
                        state.chunkHealth.remove(key);
                    } else {
                        state.chunkHealth.put(key, newHealth);
                    }
                    state.markDirty();
                }
            }
        });

        GhibliCraft.LOGGER.info("Registered Forest Corruption System.");
    }

    public static void damageForest(ServerWorld world, BlockPos pos, int amount) {
        ForestHealthState state = getState(world.getServer());
        String key = getChunkKey(pos);
        int health = state.chunkHealth.getOrDefault(key, DEFAULT_HEALTH);
        int newHealth = Math.max(MIN_HEALTH, health - amount);
        state.chunkHealth.put(key, newHealth);
        state.markDirty();

        ForestState oldState = getState(health);
        ForestState newState = getState(newHealth);

        if (oldState != newState && newState.ordinal() > oldState.ordinal()) {
            world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                    pos.getX(), pos.getY() + 1, pos.getZ(), 10, 2.0, 1.0, 2.0, 0.02);
            world.playSound(null, pos, SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE,
                    SoundCategory.AMBIENT, 0.2f, 0.5f);
        }
    }

    public static void healForest(ServerWorld world, BlockPos pos, int amount) {
        ForestHealthState state = getState(world.getServer());
        String key = getChunkKey(pos);
        int health = state.chunkHealth.getOrDefault(key, DEFAULT_HEALTH);
        int newHealth = Math.min(MAX_HEALTH, health + amount);

        if (newHealth == DEFAULT_HEALTH) {
            state.chunkHealth.remove(key);
        } else {
            state.chunkHealth.put(key, newHealth);
        }
        state.markDirty();

        ForestState oldState = getState(health);
        ForestState newState = getState(newHealth);

        if (oldState != newState && newState.ordinal() < oldState.ordinal()) {
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX(), pos.getY() + 1, pos.getZ(), 20, 3.0, 1.0, 3.0, 0.1);
            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.AMBIENT, 0.5f, 1.2f);
        }
    }

    public static void performPurificationRitual(ServerWorld world, BlockPos center, ServerPlayerEntity player) {
        for (int cx = -5; cx <= 5; cx++) {
            for (int cz = -5; cz <= 5; cz++) {
                BlockPos chunkCenter = center.add(cx * 16, 0, cz * 16);
                healForest(world, chunkCenter, 50);
            }
        }

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
                center.getX(), center.getY() + 2, center.getZ(), 100, 3.0, 3.0, 3.0, 0.5);
        world.playSound(null, center, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                SoundCategory.AMBIENT, 1.0f, 1.0f);

        player.sendMessage(Text.literal("The forest spirits rejoice! The corruption has been cleansed!")
                .formatted(Formatting.GREEN, Formatting.BOLD), false);
        SpiritReputationSystem.modifyKarma(player, 20);

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
                                            ServerWorld world, BlockPos pos) {
        switch (state) {
            case PRISTINE -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 0, true, false));
                world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + world.random.nextGaussian() * 5, pos.getY() + 2,
                        pos.getZ() + world.random.nextGaussian() * 5, 2, 0, 0, 0, 0);
                if (world.random.nextInt(3) == 0) {
                    world.spawnParticles(ParticleTypes.END_ROD,
                            pos.getX() + world.random.nextGaussian() * 8,
                            pos.getY() + 5 + world.random.nextFloat() * 5,
                            pos.getZ() + world.random.nextGaussian() * 8, 1, 0, 0, 0, 0);
                }
            }
            case HEALTHY -> {
                if (world.random.nextInt(3) == 0) {
                    world.spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM,
                            pos.getX() + world.random.nextGaussian() * 8, pos.getY() + 8,
                            pos.getZ() + world.random.nextGaussian() * 8, 1, 0, 0, 0, 0);
                }
            }
            case STRESSED -> {
                if (world.random.nextInt(4) == 0) {
                    world.spawnParticles(ParticleTypes.SMOKE,
                            pos.getX() + world.random.nextGaussian() * 6, pos.getY() + 1,
                            pos.getZ() + world.random.nextGaussian() * 6, 1, 0, 0.02, 0, 0);
                }
                if (world.random.nextInt(50) == 0) {
                    BlockPos randomNear = pos.add(world.random.nextInt(8) - 4, 0, world.random.nextInt(8) - 4);
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
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 300, 0, true, false));
                world.spawnParticles(ParticleTypes.LARGE_SMOKE,
                        pos.getX() + world.random.nextGaussian() * 8, pos.getY() + 0.5,
                        pos.getZ() + world.random.nextGaussian() * 8, 3, 0, 0.02, 0, 0);
                world.spawnParticles(ParticleTypes.SCULK_SOUL,
                        pos.getX() + world.random.nextGaussian() * 5, pos.getY(),
                        pos.getZ() + world.random.nextGaussian() * 5, 1, 0, 0.05, 0, 0);
                if (world.random.nextInt(30) == 0) {
                    BlockPos randomNear = pos.add(world.random.nextInt(6) - 3, 0, world.random.nextInt(6) - 3);
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
        return state.isIn(BlockTags.LOGS);
    }

    private static boolean isFlower(BlockState state) {
        return state.isIn(BlockTags.FLOWERS);
    }

    private static String getChunkKey(BlockPos pos) {
        return (pos.getX() >> 4) + ":" + (pos.getZ() >> 4);
    }

    private static ForestState getState(int health) {
        for (ForestState state : ForestState.values()) {
            if (health >= state.minHealth) return state;
        }
        return ForestState.CORRUPTED;
    }
}
