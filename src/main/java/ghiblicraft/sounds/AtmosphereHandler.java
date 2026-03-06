package ghiblicraft.sounds;

import ghiblicraft.config.GhibliConfig;
import ghiblicraft.registry.ModBiomes;
import ghiblicraft.registry.ModParticles;
import ghiblicraft.registry.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.biome.Biome;

public class AtmosphereHandler {
    private static int soundCooldown = 0;
    private static int particleCooldown = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!GhibliConfig.get().enableAtmosphericSounds) return;

            ClientPlayerEntity player = client.player;
            RegistryEntry<Biome> biome = client.world.getBiome(player.getBlockPos());

            soundCooldown--;
            particleCooldown--;

            if (soundCooldown <= 0) {
                handleBiomeSounds(client, player, biome);
                soundCooldown = 200 + player.getRandom().nextInt(400); // 10-30 seconds
            }

            if (particleCooldown <= 0) {
                handleBiomeParticles(client, player, biome);
                particleCooldown = 5;
            }
        });
    }

    private static void handleBiomeSounds(MinecraftClient client, ClientPlayerEntity player,
                                           RegistryEntry<Biome> biome) {
        if (biome.matchesKey(ModBiomes.TOTORO_FOREST) || biome.matchesKey(ModBiomes.BAMBOO_HILLS)) {
            player.playSound(ModSounds.FOREST_AMBIENCE, SoundCategory.AMBIENT, 0.3f, 1.0f);
        } else if (biome.matchesKey(ModBiomes.WINDMILL_FIELDS)) {
            player.playSound(ModSounds.WIND_GRASS, SoundCategory.AMBIENT, 0.4f, 1.0f);
            if (player.getRandom().nextInt(3) == 0) {
                player.playSound(ModSounds.VILLAGE_BELLS, SoundCategory.AMBIENT, 0.2f, 1.0f);
            }
        } else if (biome.matchesKey(ModBiomes.SPIRIT_LAKE)) {
            player.playSound(ModSounds.RIVER_FLOW, SoundCategory.AMBIENT, 0.3f, 1.0f);
        } else if (biome.matchesKey(ModBiomes.FLOWER_VALLEY)) {
            player.playSound(ModSounds.WIND_GRASS, SoundCategory.AMBIENT, 0.2f, 1.2f);
        }
    }

    private static void handleBiomeParticles(MinecraftClient client, ClientPlayerEntity player,
                                              RegistryEntry<Biome> biome) {
        GhibliConfig config = GhibliConfig.get();
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        if (biome.matchesKey(ModBiomes.TOTORO_FOREST) || biome.matchesKey(ModBiomes.BAMBOO_HILLS)) {
            if (player.getRandom().nextFloat() < config.forestPollenDensity * 0.3f) {
                client.world.addParticle(ModParticles.FOREST_POLLEN,
                        px + randOffset(player, 16), py + player.getRandom().nextFloat() * 8,
                        pz + randOffset(player, 16), 0, 0, 0);
            }
            if (player.getRandom().nextFloat() < config.fallingLeafDensity * 0.2f) {
                client.world.addParticle(ModParticles.FALLING_LEAF,
                        px + randOffset(player, 12), py + 10 + player.getRandom().nextFloat() * 5,
                        pz + randOffset(player, 12), 0, 0, 0);
            }
        }

        if (!client.world.isDay() && (biome.matchesKey(ModBiomes.TOTORO_FOREST) ||
                biome.matchesKey(ModBiomes.SPIRIT_LAKE) || biome.matchesKey(ModBiomes.FLOWER_VALLEY))) {
            if (player.getRandom().nextFloat() < config.fireflyDensity * 0.15f) {
                client.world.addParticle(ModParticles.FIREFLY,
                        px + randOffset(player, 20), py + 1 + player.getRandom().nextFloat() * 5,
                        pz + randOffset(player, 20), 0, 0, 0);
            }
        }

        if (client.world.getTimeOfDay() % 24000 < 3000) { // Early morning
            if (player.getRandom().nextFloat() < config.morningMistDensity * 0.1f) {
                client.world.addParticle(ModParticles.MORNING_MIST,
                        px + randOffset(player, 24), py + player.getRandom().nextFloat() * 3,
                        pz + randOffset(player, 24), 0, 0, 0);
            }
        }
    }

    private static double randOffset(ClientPlayerEntity player, int range) {
        return (player.getRandom().nextFloat() - 0.5) * range * 2;
    }
}
