package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.ForestGuardianEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class ForestGuardianModel extends SinglePartEntityModel<ForestGuardianEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "forest_guardian"), "main");

    private final ModelPart root;

    public ForestGuardianModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Large tree-like body
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-6.0f, -24.0f, -4.0f, 12.0f, 24.0f, 8.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Head
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 32).cuboid(-4.0f, -32.0f, -4.0f, 8.0f, 8.0f, 8.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Arms
        modelPartData.addChild("left_arm", ModelPartBuilder.create()
                .uv(32, 0).cuboid(-2.0f, -22.0f, -2.0f, 4.0f, 16.0f, 4.0f),
                ModelTransform.pivot(-8.0f, 24.0f, 0.0f));

        modelPartData.addChild("right_arm", ModelPartBuilder.create()
                .uv(32, 20).cuboid(-2.0f, -22.0f, -2.0f, 4.0f, 16.0f, 4.0f),
                ModelTransform.pivot(8.0f, 24.0f, 0.0f));

        // Legs
        modelPartData.addChild("left_leg", ModelPartBuilder.create()
                .uv(48, 0).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f),
                ModelTransform.pivot(-4.0f, 12.0f, 0.0f));

        modelPartData.addChild("right_leg", ModelPartBuilder.create()
                .uv(48, 18).cuboid(-3.0f, 0.0f, -3.0f, 6.0f, 12.0f, 6.0f),
                ModelTransform.pivot(4.0f, 12.0f, 0.0f));

        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(ForestGuardianEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }
}
