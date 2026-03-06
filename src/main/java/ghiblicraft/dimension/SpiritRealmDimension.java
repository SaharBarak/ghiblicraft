package ghiblicraft.dimension;

import ghiblicraft.GhibliCraft;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class SpiritRealmDimension {
    public static final RegistryKey<World> SPIRIT_REALM_KEY = RegistryKey.of(
            RegistryKeys.WORLD, new Identifier(GhibliCraft.MOD_ID, "spirit_realm"));

    public static final RegistryKey<DimensionType> SPIRIT_REALM_TYPE = RegistryKey.of(
            RegistryKeys.DIMENSION_TYPE, new Identifier(GhibliCraft.MOD_ID, "spirit_realm"));

    public static void register() {
        GhibliCraft.LOGGER.info("Registered Spirit Realm dimension.");
    }
}
