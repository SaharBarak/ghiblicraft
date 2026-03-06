package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.biomes.BiomeRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModBiomes {
    public static final RegistryKey<Biome> TOTORO_FOREST = RegistryKey.of(
            RegistryKeys.BIOME, new Identifier(GhibliCraft.MOD_ID, "totoro_forest"));
    public static final RegistryKey<Biome> FLOWER_VALLEY = RegistryKey.of(
            RegistryKeys.BIOME, new Identifier(GhibliCraft.MOD_ID, "flower_valley"));
    public static final RegistryKey<Biome> BAMBOO_HILLS = RegistryKey.of(
            RegistryKeys.BIOME, new Identifier(GhibliCraft.MOD_ID, "bamboo_hills"));
    public static final RegistryKey<Biome> WINDMILL_FIELDS = RegistryKey.of(
            RegistryKeys.BIOME, new Identifier(GhibliCraft.MOD_ID, "windmill_fields"));
    public static final RegistryKey<Biome> SPIRIT_LAKE = RegistryKey.of(
            RegistryKeys.BIOME, new Identifier(GhibliCraft.MOD_ID, "spirit_lake"));

    public static void register() {
        BiomeRegistry.register();
        GhibliCraft.LOGGER.info("Registered GhibliCraft biomes.");
    }
}
