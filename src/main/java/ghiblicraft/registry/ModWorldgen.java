package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.worldgen.features.GiantCamphorTreeFeature;
import ghiblicraft.worldgen.features.SkyIslandFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModWorldgen {
    public static final Feature<DefaultFeatureConfig> GIANT_CAMPHOR_TREE =
            new GiantCamphorTreeFeature(DefaultFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> SKY_ISLAND =
            new SkyIslandFeature(DefaultFeatureConfig.CODEC);

    public static void register() {
        Registry.register(Registries.FEATURE,
                new Identifier(GhibliCraft.MOD_ID, "giant_camphor_tree"), GIANT_CAMPHOR_TREE);
        Registry.register(Registries.FEATURE,
                new Identifier(GhibliCraft.MOD_ID, "sky_island"), SKY_ISLAND);

        GhibliCraft.LOGGER.info("Registered GhibliCraft worldgen features.");
    }
}
