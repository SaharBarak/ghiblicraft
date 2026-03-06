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
import ghiblicraft.entities.renderers.KodamaSpiritRenderer;
import ghiblicraft.entities.renderers.SootSpriteRenderer;
import ghiblicraft.entities.renderers.ForestGuardianRenderer;
import ghiblicraft.entities.renderers.CatSpiritRenderer;
import ghiblicraft.entities.renderers.NoFaceRenderer;
import ghiblicraft.entities.renderers.BroomstickRenderer;
import ghiblicraft.entities.models.KodamaSpiritModel;
import ghiblicraft.entities.models.SootSpriteModel;
import ghiblicraft.entities.models.ForestGuardianModel;
import ghiblicraft.entities.models.CatSpiritModel;
import ghiblicraft.entities.models.NoFaceModel;
import ghiblicraft.sounds.AtmosphereHandler;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

public class GhibliCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register entity model layers
        EntityModelLayerRegistry.registerModelLayer(KodamaSpiritModel.LAYER, KodamaSpiritModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(SootSpriteModel.LAYER, SootSpriteModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ForestGuardianModel.LAYER, ForestGuardianModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(CatSpiritModel.LAYER, CatSpiritModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(NoFaceModel.LAYER, NoFaceModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BroomstickRenderer.LAYER, BroomstickRenderer::getTexturedModelData);

        ParticleFactoryRegistry.getInstance().register(ModParticles.FOREST_POLLEN, ForestPollenParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FIREFLY, FireflyParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.FALLING_LEAF, FallingLeafParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.MORNING_MIST, MorningMistParticle.Factory::new);

        EntityRendererRegistry.register(ModEntities.KODAMA_SPIRIT, KodamaSpiritRenderer::new);
        EntityRendererRegistry.register(ModEntities.SOOT_SPRITE, SootSpriteRenderer::new);
        EntityRendererRegistry.register(ModEntities.FOREST_GUARDIAN, ForestGuardianRenderer::new);
        EntityRendererRegistry.register(ModEntities.CAT_SPIRIT, CatSpiritRenderer::new);
        EntityRendererRegistry.register(ModEntities.NO_FACE, NoFaceRenderer::new);
        EntityRendererRegistry.register(ModEntities.BROOMSTICK, BroomstickRenderer::new);

        // Register atmosphere handler for ambient sounds and particles
        AtmosphereHandler.register();
    }
}
