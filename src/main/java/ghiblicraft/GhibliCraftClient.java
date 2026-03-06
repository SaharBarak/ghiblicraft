package ghiblicraft;

import ghiblicraft.particles.FallingLeafParticle;
import ghiblicraft.particles.FireflyParticle;
import ghiblicraft.particles.ForestPollenParticle;
import ghiblicraft.particles.MorningMistParticle;
import ghiblicraft.registry.ModEntities;
import ghiblicraft.registry.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import ghiblicraft.entities.renderers.*;
import ghiblicraft.entities.models.*;
import ghiblicraft.sounds.AtmosphereHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class GhibliCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Entity model layers — original spirits
        EntityModelLayerRegistry.registerModelLayer(KodamaSpiritModel.LAYER, KodamaSpiritModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SootSpriteModel.LAYER, SootSpriteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ForestGuardianModel.LAYER, ForestGuardianModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CatSpiritModel.LAYER, CatSpiritModel::getTexturedModelData);

        // Wave 1: No-Face, Broomstick
        EntityModelLayerRegistry.registerModelLayer(NoFaceModel.LAYER, NoFaceModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BroomstickRenderer.LAYER, BroomstickRenderer::getTexturedModelData);

        // Wave 2: Catbus, Haku Dragon, Turnip Head
        EntityModelLayerRegistry.registerModelLayer(CatbusModel.LAYER, CatbusModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(HakuDragonModel.LAYER, HakuDragonModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TurnipHeadModel.LAYER, TurnipHeadModel::getTexturedModelData);

        // Particles
        ParticleFactoryRegistry.getInstance().register(ModParticles.FOREST_POLLEN, ForestPollenParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FIREFLY, FireflyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FALLING_LEAF, FallingLeafParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MORNING_MIST, MorningMistParticle.Factory::new);

        // Entity renderers — original spirits
        EntityRendererRegistry.register(ModEntities.KODAMA_SPIRIT, KodamaSpiritRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOOT_SPRITE, SootSpriteRenderer::new);
        EntityRendererRegistry.register(ModEntities.FOREST_GUARDIAN, ForestGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.CAT_SPIRIT, CatSpiritRenderer::new);

        // Wave 1
        EntityRendererRegistry.register(ModEntities.NO_FACE, NoFaceRenderer::new);
        EntityRendererRegistry.register(ModEntities.BROOMSTICK, BroomstickRenderer::new);

        // Wave 2
        EntityRendererRegistry.register(ModEntities.CATBUS, CatbusRenderer::new);
        EntityRendererRegistry.register(ModEntities.HAKU_DRAGON, HakuDragonRenderer::new);
        EntityRendererRegistry.register(ModEntities.TURNIP_HEAD, TurnipHeadRenderer::new);

        // Atmosphere handler for ambient sounds and particles
        AtmosphereHandler.register();
    }
}
