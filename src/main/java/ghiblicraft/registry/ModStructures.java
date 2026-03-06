package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.structures.ForestCabinStructure;
import ghiblicraft.structures.ShrineTempleStructure;
import ghiblicraft.structures.VillageCountrysideStructure;
import ghiblicraft.structures.WindmillVillageStructure;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class ModStructures {
    public static final RegistryKey<Structure> VILLAGE_COUNTRYSIDE = RegistryKey.of(
            RegistryKeys.STRUCTURE, new Identifier(GhibliCraft.MOD_ID, "village_countryside"));
    public static final RegistryKey<Structure> SHRINE_TEMPLE = RegistryKey.of(
            RegistryKeys.STRUCTURE, new Identifier(GhibliCraft.MOD_ID, "shrine_temple"));
    public static final RegistryKey<Structure> WINDMILL_VILLAGE = RegistryKey.of(
            RegistryKeys.STRUCTURE, new Identifier(GhibliCraft.MOD_ID, "windmill_village"));
    public static final RegistryKey<Structure> FOREST_CABIN = RegistryKey.of(
            RegistryKeys.STRUCTURE, new Identifier(GhibliCraft.MOD_ID, "forest_cabin"));

    public static StructureType<VillageCountrysideStructure> VILLAGE_COUNTRYSIDE_TYPE;
    public static StructureType<ShrineTempleStructure> SHRINE_TEMPLE_TYPE;
    public static StructureType<WindmillVillageStructure> WINDMILL_VILLAGE_TYPE;
    public static StructureType<ForestCabinStructure> FOREST_CABIN_TYPE;

    public static void register() {
        VILLAGE_COUNTRYSIDE_TYPE = Registry.register(Registries.STRUCTURE_TYPE,
                new Identifier(GhibliCraft.MOD_ID, "village_countryside"),
                () -> VillageCountrysideStructure.CODEC);
        SHRINE_TEMPLE_TYPE = Registry.register(Registries.STRUCTURE_TYPE,
                new Identifier(GhibliCraft.MOD_ID, "shrine_temple"),
                () -> ShrineTempleStructure.CODEC);
        WINDMILL_VILLAGE_TYPE = Registry.register(Registries.STRUCTURE_TYPE,
                new Identifier(GhibliCraft.MOD_ID, "windmill_village"),
                () -> WindmillVillageStructure.CODEC);
        FOREST_CABIN_TYPE = Registry.register(Registries.STRUCTURE_TYPE,
                new Identifier(GhibliCraft.MOD_ID, "forest_cabin"),
                () -> ForestCabinStructure.CODEC);

        GhibliCraft.LOGGER.info("Registered GhibliCraft structures.");
    }
}
