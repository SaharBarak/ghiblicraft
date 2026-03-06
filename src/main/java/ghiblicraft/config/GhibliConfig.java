package ghiblicraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ghiblicraft.GhibliCraft;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class GhibliConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static GhibliConfig INSTANCE = new GhibliConfig();

    // Spawn rates (multiplier, 1.0 = default)
    public float kodamaSpiritSpawnRate = 1.0f;
    public float sootSpriteSpawnRate = 1.0f;
    public float forestGuardianSpawnRate = 0.5f;
    public float catSpiritSpawnRate = 0.8f;

    // Biome frequency (weight, higher = more common)
    public int totoroForestWeight = 10;
    public int flowerValleyWeight = 8;
    public int bambooHillsWeight = 7;
    public int windmillFieldsWeight = 6;
    public int spiritLakeWeight = 5;

    // Particle density (multiplier, 1.0 = default)
    public float forestPollenDensity = 1.0f;
    public float fireflyDensity = 1.0f;
    public float fallingLeafDensity = 1.0f;
    public float morningMistDensity = 1.0f;

    // General
    public boolean enableCustomMusic = true;
    public boolean enableAtmosphericSounds = true;
    public boolean enableComfortBuffs = true;

    public static GhibliConfig get() {
        return INSTANCE;
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("ghiblicraft-config.json");

        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                INSTANCE = GSON.fromJson(reader, GhibliConfig.class);
                GhibliCraft.LOGGER.info("Loaded GhibliCraft configuration.");
            } catch (IOException e) {
                GhibliCraft.LOGGER.error("Failed to load config, using defaults.", e);
                INSTANCE = new GhibliConfig();
            }
        } else {
            INSTANCE = new GhibliConfig();
            save();
        }
    }

    public static void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("ghiblicraft-config.json");

        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(INSTANCE, writer);
            GhibliCraft.LOGGER.info("Saved GhibliCraft configuration.");
        } catch (IOException e) {
            GhibliCraft.LOGGER.error("Failed to save config.", e);
        }
    }
}
