package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.SootSpriteEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class SootSpriteModel extends SinglePartEntityModel<SootSpriteEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "soot_sprite"), "main");

    private final ModelPart root;

    public SootSpriteModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Simple fuzzy ball shape
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-1.5f, -3.0f, -1.5f, 3.0f, 3.0f, 3.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Tiny legs
        modelPartData.addChild("left_leg", ModelPartBuilder.create()
                .uv(0, 6).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 1.0f, 1.0f),
                ModelTransform.pivot(-1.0f, 24.0f, 0.0f));

        modelPartData.addChild("right_leg", ModelPartBuilder.create()
                .uv(4, 6).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 1.0f, 1.0f),
                ModelTransform.pivot(1.0f, 24.0f, 0.0f));

        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(SootSpriteEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }
}
