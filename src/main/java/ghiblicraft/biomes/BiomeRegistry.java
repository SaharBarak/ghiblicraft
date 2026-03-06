package ghiblicraft.biomes;

import ghiblicraft.GhibliCraft;
import ghiblicraft.config.GhibliConfig;
import ghiblicraft.registry.ModBiomes;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.minecraft.entity.SpawnGroup;
import ghiblicraft.registry.ModEntities;

public class BiomeRegistry {
    public static void register() {
        GhibliConfig config = GhibliConfig.get();

        // Add biomes to overworld generation with configurable weights
        OverworldBiomes.addContinentalBiome(ModBiomes.TOTORO_FOREST, OverworldClimate.TEMPERATE, config.totoroForestWeight / 10.0);
        OverworldBiomes.addContinentalBiome(ModBiomes.FLOWER_VALLEY, OverworldClimate.TEMPERATE, config.flowerValleyWeight / 10.0);
        OverworldBiomes.addContinentalBiome(ModBiomes.BAMBOO_HILLS, OverworldClimate.TEMPERATE, config.bambooHillsWeight / 10.0);
        OverworldBiomes.addContinentalBiome(ModBiomes.WINDMILL_FIELDS, OverworldClimate.TEMPERATE, config.windmillFieldsWeight / 10.0);
        OverworldBiomes.addContinentalBiome(ModBiomes.SPIRIT_LAKE, OverworldClimate.TEMPERATE, config.spiritLakeWeight / 10.0);

        // Add entity spawns to GhibliCraft biomes
        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModBiomes.TOTORO_FOREST, ModBiomes.FLOWER_VALLEY, ModBiomes.BAMBOO_HILLS),
                SpawnGroup.AMBIENT, ModEntities.KODAMA_SPIRIT, 20, 1, 3);

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModBiomes.TOTORO_FOREST, ModBiomes.SPIRIT_LAKE),
                SpawnGroup.AMBIENT, ModEntities.SOOT_SPRITE, 15, 3, 8);

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModBiomes.TOTORO_FOREST, ModBiomes.BAMBOO_HILLS),
                SpawnGroup.CREATURE, ModEntities.FOREST_GUARDIAN, 3, 1, 1);

        BiomeModifications.addSpawn(
                BiomeSelectors.includeByKey(ModBiomes.FLOWER_VALLEY, ModBiomes.WINDMILL_FIELDS),
                SpawnGroup.AMBIENT, ModEntities.CAT_SPIRIT, 10, 1, 2);
    }
}
