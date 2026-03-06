package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.worldgen.features.GiantCamphorTreeFeature;
import ghiblicraft.worldgen.features.SkyIslandFeature;
import ghiblicraft.worldgen.trees.CherryBlossomTreeFeature;
import ghiblicraft.worldgen.trees.JapaneseMapleTreeFeature;
import ghiblicraft.worldgen.trees.WisteriaTreeFeature;
import ghiblicraft.worldgen.structures.JapaneseStructureFeatures;
import ghiblicraft.worldgen.structures.KoiPondFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModWorldgen {
    // Magical features
    public static final Feature<DefaultFeatureConfig> GIANT_CAMPHOR_TREE =
            new GiantCamphorTreeFeature(DefaultFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> SKY_ISLAND =
            new SkyIslandFeature(DefaultFeatureConfig.CODEC);

    // Japanese trees
    public static final Feature<DefaultFeatureConfig> CHERRY_BLOSSOM_TREE =
            new CherryBlossomTreeFeature(DefaultFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> JAPANESE_MAPLE_TREE =
            new JapaneseMapleTreeFeature(DefaultFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> WISTERIA_TREE =
            new WisteriaTreeFeature(DefaultFeatureConfig.CODEC);

    // Japanese structures
    public static final Feature<DefaultFeatureConfig> JAPANESE_STRUCTURES =
            new JapaneseStructureFeatures(DefaultFeatureConfig.CODEC);
    public static final Feature<DefaultFeatureConfig> KOI_POND =
            new KoiPondFeature(DefaultFeatureConfig.CODEC);

    public static void register() {
        reg("giant_camphor_tree", GIANT_CAMPHOR_TREE);
        reg("sky_island", SKY_ISLAND);
        reg("cherry_blossom_tree", CHERRY_BLOSSOM_TREE);
        reg("japanese_maple_tree", JAPANESE_MAPLE_TREE);
        reg("wisteria_tree", WISTERIA_TREE);
        reg("japanese_structures", JAPANESE_STRUCTURES);
        reg("koi_pond", KOI_POND);

        GhibliCraft.LOGGER.info("Registered GhibliCraft worldgen features.");
    }

    private static void reg(String name, Feature<?> feature) {
        Registry.register(Registries.FEATURE, new Identifier(GhibliCraft.MOD_ID, name), feature);
    }
}
