package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType FOREST_POLLEN = FabricParticleTypes.simple();
    public static final DefaultParticleType FIREFLY = FabricParticleTypes.simple();
    public static final DefaultParticleType FALLING_LEAF = FabricParticleTypes.simple();
    public static final DefaultParticleType MORNING_MIST = FabricParticleTypes.simple();

    public static void register() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GhibliCraft.MOD_ID, "forest_pollen"), FOREST_POLLEN);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GhibliCraft.MOD_ID, "firefly"), FIREFLY);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GhibliCraft.MOD_ID, "falling_leaf"), FALLING_LEAF);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(GhibliCraft.MOD_ID, "morning_mist"), MORNING_MIST);

        GhibliCraft.LOGGER.info("Registered GhibliCraft particles.");
    }
}
