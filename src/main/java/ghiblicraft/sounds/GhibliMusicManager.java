package ghiblicraft.sounds;

import ghiblicraft.config.GhibliConfig;
import ghiblicraft.events.LanternFestivalEvent;
import ghiblicraft.registry.ModBiomes;
import ghiblicraft.registry.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Ghibli Music Manager — plays biome-specific and event-based background music.
 *
 * Music selection priority:
 * 1. Event music (Lantern Festival)
 * 2. Time-of-day music (dawn/night)
 * 3. Biome-specific music (Totoro Forest, Spirit Lake, etc.)
 *
 * Tracks fade between each other naturally by waiting for one to finish
 * before starting the next, with a configurable cooldown between tracks.
 */
public class GhibliMusicManager {
    private static SoundInstance currentMusic = null;
    private static SoundEvent lastTrack = null;
    private static int cooldownTicks = 0;
    private static final int MIN_COOLDOWN = 600;   // 30 seconds between tracks
    private static final int MAX_COOLDOWN = 2400;   // 2 minutes max gap

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!GhibliConfig.get().enableCustomMusic) return;

            // Count down cooldown
            if (cooldownTicks > 0) {
                cooldownTicks--;
                return;
            }

            // If music is currently playing, let it finish
            if (currentMusic != null && client.getSoundManager().isPlaying(currentMusic)) {
                return;
            }

            // Previous track finished — pick the next one
            currentMusic = null;
            SoundEvent track = selectTrack(client);

            if (track != null) {
                playTrack(client, track);
            }
        });
    }

    private static SoundEvent selectTrack(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return null;

        long timeOfDay = client.world.getTimeOfDay() % 24000;
        RegistryEntry<Biome> biome = client.world.getBiome(player.getBlockPos());

        // Priority 1: Event music
        // Check if it's festival time (night of every 7th day)
        long dayNumber = client.world.getTimeOfDay() / 24000;
        if (dayNumber > 0 && dayNumber % 7 == 0 && timeOfDay >= 13000 && timeOfDay <= 23000) {
            return ModSounds.MUSIC_LANTERN_FESTIVAL;
        }

        // Priority 2: Spirit Realm dimension
        if (client.world.getRegistryKey() != World.OVERWORLD
                && client.world.getRegistryKey() != World.NETHER
                && client.world.getRegistryKey() != World.END) {
            return ModSounds.MUSIC_SPIRIT_REALM;
        }

        // Priority 3: Time-of-day themes
        if (timeOfDay >= 0 && timeOfDay < 2000) {
            return ModSounds.MUSIC_DAWN_CHORUS;
        }
        if (timeOfDay >= 13500 && timeOfDay < 22500) {
            return ModSounds.MUSIC_NIGHT_SERENADE;
        }

        // Priority 4: Biome-specific music
        if (biome.matchesKey(ModBiomes.TOTORO_FOREST)) {
            return ModSounds.MUSIC_TOTORO_FOREST;
        }
        if (biome.matchesKey(ModBiomes.FLOWER_VALLEY)) {
            return ModSounds.MUSIC_FLOWER_VALLEY;
        }
        if (biome.matchesKey(ModBiomes.WINDMILL_FIELDS)) {
            return ModSounds.MUSIC_WINDMILL_FIELDS;
        }
        if (biome.matchesKey(ModBiomes.SPIRIT_LAKE)) {
            return ModSounds.MUSIC_SPIRIT_LAKE;
        }
        if (biome.matchesKey(ModBiomes.BAMBOO_HILLS)) {
            return ModSounds.MUSIC_BAMBOO_HILLS;
        }

        // Not in a Ghibli biome — no custom music
        return null;
    }

    private static void playTrack(MinecraftClient client, SoundEvent track) {
        // Avoid playing the same track twice in a row
        if (track == lastTrack && client.player != null && client.player.getRandom().nextInt(3) != 0) {
            cooldownTicks = MIN_COOLDOWN;
            return;
        }

        currentMusic = PositionedSoundInstance.music(track);
        client.getSoundManager().play(currentMusic);
        lastTrack = track;

        // Set cooldown for after this track finishes
        cooldownTicks = MIN_COOLDOWN + (client.player != null
                ? client.player.getRandom().nextInt(MAX_COOLDOWN - MIN_COOLDOWN)
                : 0);
    }

    /**
     * Play the music box melody at a specific position.
     * Called when a Music Box block is activated.
     */
    public static void playMusicBox(MinecraftClient client, double x, double y, double z) {
        SoundInstance instance = new PositionedSoundInstance(
                ModSounds.MUSIC_BOX_MELODY.getId(), SoundCategory.RECORDS,
                1.0f, 1.0f, SoundInstance.createRandom(),
                false, 0, SoundInstance.AttenuationType.LINEAR,
                x, y, z, false);
        client.getSoundManager().play(instance);
    }

    /**
     * Stop the currently playing Ghibli music track (if any).
     */
    public static void stopMusic(MinecraftClient client) {
        if (currentMusic != null) {
            client.getSoundManager().stop(currentMusic);
            currentMusic = null;
        }
    }
}
