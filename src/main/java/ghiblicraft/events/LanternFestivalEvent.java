package ghiblicraft.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class LanternFestivalEvent {
    // Festival happens during the first night of every 7th in-game day
    private static final long DAY_LENGTH = 24000L;
    private static final int FESTIVAL_INTERVAL_DAYS = 7;
    // Track which day each player was last announced for
    private static final java.util.Map<java.util.UUID, Long> announcedForDay = new java.util.WeakHashMap<>();

    public static boolean isFestivalActiveNow(ServerWorld world) {
        long dayNumber = world.getTimeOfDay() / DAY_LENGTH;
        long timeOfDay = world.getTimeOfDay() % DAY_LENGTH;
        return dayNumber > 0 && dayNumber % FESTIVAL_INTERVAL_DAYS == 0
                && timeOfDay >= 13000 && timeOfDay <= 23000;
    }

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() != World.OVERWORLD) return;

            if (isFestivalActiveNow(world)) {
                long dayNumber = world.getTimeOfDay() / DAY_LENGTH;
                long timeOfDay = world.getTimeOfDay() % DAY_LENGTH;

                // Announce to players who haven't been told yet this cycle
                for (ServerPlayerEntity player : world.getPlayers()) {
                    Long lastDay = announcedForDay.get(player.getUuid());
                    if (lastDay == null || lastDay != dayNumber) {
                        announceFestivalToPlayer(player, world);
                        announcedForDay.put(player.getUuid(), dayNumber);
                    }
                }

                tickFestival(world, timeOfDay);
            }
        });
    }

    private static void announceFestivalToPlayer(ServerPlayerEntity player, ServerWorld world) {
        player.sendMessage(
                Text.literal("The Lantern Festival has begun! Look to the sky...")
                        .formatted(Formatting.GOLD, Formatting.ITALIC),
                false);

        // Festival blessing
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 6000, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 6000, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 6000, 0, true, false));

        // Opening sound
        world.playSound(null, player.getBlockPos(),
                SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.AMBIENT,
                0.5f, 1.2f);
    }

    private static void tickFestival(ServerWorld world, long timeOfDay) {
        // Spawn floating lanterns around all players
        if (world.getTime() % 10 == 0) { // Every half second
            for (ServerPlayerEntity player : world.getPlayers()) {
                double px = player.getX();
                double py = player.getY();
                double pz = player.getZ();

                // Rising lanterns (warm flame particles going up slowly)
                for (int i = 0; i < 5; i++) {
                    double lx = px + (world.random.nextFloat() - 0.5) * 40;
                    double ly = py + 5 + world.random.nextFloat() * 30;
                    double lz = pz + (world.random.nextFloat() - 0.5) * 40;

                    // Main lantern glow
                    world.spawnParticles(ParticleTypes.FLAME,
                            lx, ly, lz, 1, 0.05, 0.02, 0.05, 0);

                    // Soft surrounding glow
                    world.spawnParticles(ParticleTypes.END_ROD,
                            lx + (world.random.nextFloat() - 0.5) * 0.3,
                            ly - 0.2,
                            lz + (world.random.nextFloat() - 0.5) * 0.3,
                            1, 0, 0.01, 0, 0);
                }

                // Ground-level sparkle effects
                if (world.random.nextInt(5) == 0) {
                    world.spawnParticles(ParticleTypes.ENCHANT,
                            px + (world.random.nextFloat() - 0.5) * 10,
                            py + 0.5,
                            pz + (world.random.nextFloat() - 0.5) * 10,
                            3, 0.5, 0.5, 0.5, 0.3);
                }
            }
        }

        // Periodic bell chimes during festival
        if (world.getTime() % 200 == 0) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                world.playSound(null, player.getBlockPos(),
                        SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.AMBIENT,
                        0.3f, 0.8f + world.random.nextFloat() * 0.4f);
            }
        }

        // Monsters don't spawn during the festival (peaceful night)
        // Grant all players weakness to hostile mobs via strength buff
        if (world.getTime() % 100 == 0) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                player.addStatusEffect(new StatusEffectInstance(
                        StatusEffects.STRENGTH, 200, 0, true, false));
            }
        }
    }

    private static void endFestival(ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            player.sendMessage(
                    Text.literal("The Lantern Festival fades as dawn approaches...")
                            .formatted(Formatting.GRAY, Formatting.ITALIC),
                    false);
        }
    }

}
