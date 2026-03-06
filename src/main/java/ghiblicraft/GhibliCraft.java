package ghiblicraft;

import ghiblicraft.config.GhibliConfig;
import ghiblicraft.registry.ModBiomes;
import ghiblicraft.registry.ModEntities;
import ghiblicraft.registry.ModItems;
import ghiblicraft.registry.ModParticles;
import ghiblicraft.registry.ModSounds;
import ghiblicraft.registry.ModStructures;
import ghiblicraft.registry.ModBlockEntities;
import ghiblicraft.registry.ModBlocks;
import ghiblicraft.registry.ModVillagerProfessions;
import ghiblicraft.registry.ModWorldgen;
import ghiblicraft.events.SeasonalCycleManager;
import ghiblicraft.events.LanternFestivalEvent;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GhibliCraft implements ModInitializer {
    public static final String MOD_ID = "ghiblicraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing GhibliCraft - A Studio Ghibli inspired world awaits!");

        GhibliConfig.load();
        ModBlocks.register();
        ModBlockEntities.register();
        ModItems.register();
        ModSounds.register();
        ModParticles.register();
        ModEntities.register();
        ModBiomes.register();
        ModStructures.register();
        ModWorldgen.register();
        ModVillagerProfessions.register();

        // Events
        SeasonalCycleManager.register();
        LanternFestivalEvent.register();

        LOGGER.info("GhibliCraft initialization complete.");
    }
}
