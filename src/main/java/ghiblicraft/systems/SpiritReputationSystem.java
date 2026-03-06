package ghiblicraft.systems;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Spirit Reputation System — persisted via PersistentState so karma survives restarts.
 */
public class SpiritReputationSystem {
    private static final int DEFAULT_KARMA = 50;
    private static final int MAX_KARMA = 100;
    private static final int MIN_KARMA = 0;
    private static final String DATA_KEY = "ghiblicraft_karma";

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

    public static class KarmaState extends PersistentState {
        final Map<UUID, Integer> playerKarma = new HashMap<>();

        public static KarmaState fromNbt(NbtCompound nbt) {
            KarmaState state = new KarmaState();
            NbtCompound data = nbt.getCompound("karma");
            for (String key : data.getKeys()) {
                try {
                    state.playerKarma.put(UUID.fromString(key), data.getInt(key));
                } catch (IllegalArgumentException ignored) {}
            }
            return state;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            NbtCompound data = new NbtCompound();
            playerKarma.forEach((uuid, karma) -> data.putInt(uuid.toString(), karma));
            nbt.put("karma", data);
            return nbt;
        }
    }

    private static KarmaState getState(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        return overworld.getPersistentStateManager().getOrCreate(
                KarmaState::fromNbt, KarmaState::new, DATA_KEY);
    }

    public static void register() {
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

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() != World.OVERWORLD) return;
            if (world.getTime() % 100 != 0) return;

            for (ServerPlayerEntity player : world.getPlayers()) {
                int karma = getKarma(player);
                KarmaTier tier = getTier(karma);
                applyTierEffects(player, tier, world);

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
        if (player instanceof ServerPlayerEntity sp && sp.getServer() != null) {
            return getState(sp.getServer()).playerKarma.getOrDefault(player.getUuid(), DEFAULT_KARMA);
        }
        return DEFAULT_KARMA;
    }

    public static KarmaTier getTier(int karma) {
        for (KarmaTier tier : KarmaTier.values()) {
            if (karma >= tier.minKarma) return tier;
        }
        return KarmaTier.CURSED;
    }

    public static void modifyKarma(PlayerEntity player, int amount) {
        if (!(player instanceof ServerPlayerEntity sp) || sp.getServer() == null) return;

        KarmaState state = getState(sp.getServer());
        int current = state.playerKarma.getOrDefault(player.getUuid(), DEFAULT_KARMA);
        int newKarma = Math.max(MIN_KARMA, Math.min(MAX_KARMA, current + amount));

        KarmaTier oldTier = getTier(current);
        KarmaTier newTier = getTier(newKarma);

        state.playerKarma.put(player.getUuid(), newKarma);
        state.markDirty();

        if (oldTier != newTier) {
            player.sendMessage(
                    Text.literal("Spirit Reputation: " + newTier.title)
                            .formatted(newTier.color, Formatting.BOLD),
                    false);

            ServerWorld world = sp.getServerWorld();
            if (world != null) {
                if (newTier.ordinal() < oldTier.ordinal()) {
                    world.spawnParticles(ParticleTypes.ENCHANT,
                            sp.getX(), sp.getY() + 1, sp.getZ(), 30, 1.0, 1.0, 1.0, 0.5);
                    world.playSound(null, sp.getBlockPos(),
                            SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5f, 1.5f);
                } else {
                    world.spawnParticles(ParticleTypes.SMOKE,
                            sp.getX(), sp.getY() + 1, sp.getZ(), 20, 0.5, 0.5, 0.5, 0.1);
                    world.playSound(null, sp.getBlockPos(),
                            SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.HOSTILE, 0.3f, 1.0f);
                }
            }
        }
    }

    private static void applyTierEffects(ServerPlayerEntity player, KarmaTier tier, ServerWorld world) {
        switch (tier) {
            case REVERED -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200, 1, true, false));
                world.spawnParticles(ParticleTypes.END_ROD,
                        player.getX(), player.getY() + 2, player.getZ(), 1, 0.5, 0.5, 0.5, 0.01);
                List<PathAwareEntity> spirits = world.getEntitiesByClass(PathAwareEntity.class,
                        new Box(player.getBlockPos()).expand(16),
                        e -> e.getType().getRegistryEntry().registryKey().getValue().getNamespace()
                                .equals(GhibliCraft.MOD_ID));
                for (PathAwareEntity spirit : spirits) {
                    spirit.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0, true, false));
                }
            }
            case TRUSTED -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 200, 0, true, false));
            case NEUTRAL -> {}
            case WARY -> {
                if (world.random.nextInt(5) == 0) {
                    world.spawnParticles(ParticleTypes.SMOKE,
                            player.getX(), player.getY() + 2, player.getZ(), 1, 0.3, 0.3, 0.3, 0);
                }
            }
            case CURSED -> {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 200, 1, true, false));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0, true, false));
                world.spawnParticles(ParticleTypes.SCULK_SOUL,
                        player.getX(), player.getY() + 1, player.getZ(), 2, 0.5, 0.5, 0.5, 0.02);
            }
        }
    }

    private static void broadcastSpiritAnger(PlayerEntity player, ServerWorld world) {
        List<PathAwareEntity> spirits = world.getEntitiesByClass(PathAwareEntity.class,
                new Box(player.getBlockPos()).expand(32),
                e -> e.getType().getRegistryEntry().registryKey().getValue().getNamespace()
                        .equals(GhibliCraft.MOD_ID));
        for (PathAwareEntity spirit : spirits) {
            double dx = spirit.getX() - player.getX();
            double dz = spirit.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) {
                spirit.getNavigation().startMovingTo(
                        spirit.getX() + (dx / dist) * 15, spirit.getY(),
                        spirit.getZ() + (dz / dist) * 15, 1.5);
            }
            world.spawnParticles(ParticleTypes.ANGRY_VILLAGER,
                    spirit.getX(), spirit.getY() + 1, spirit.getZ(), 3, 0.3, 0.3, 0.3, 0);
        }
        world.playSound(null, player.getBlockPos(),
                SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 0.3f, 2.0f);
    }

    public static void onPlantGrown(PlayerEntity player) { modifyKarma(player, 2); }
    public static void onSpiritFed(PlayerEntity player) { modifyKarma(player, 5); }
}
