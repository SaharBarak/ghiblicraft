package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.mounts.CatbusEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class CatbusModel extends SinglePartEntityModel<CatbusEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "catbus"), "main");

    private final ModelPart root;

    public CatbusModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Large cat body
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-6.0f, -10.0f, -12.0f, 12.0f, 10.0f, 24.0f),
                ModelTransform.pivot(0.0f, 14.0f, 0.0f));

        // Head
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 34).cuboid(-4.0f, -14.0f, -16.0f, 8.0f, 8.0f, 6.0f),
                ModelTransform.pivot(0.0f, 14.0f, 0.0f));

        // Legs (6 legs - it's a catbus!)
        for (int i = 0; i < 3; i++) {
            String leftName = "left_leg_" + i;
            String rightName = "right_leg_" + i;
            float zOffset = -8.0f + i * 8.0f;

            modelPartData.addChild(leftName, ModelPartBuilder.create()
                    .uv(48, 0).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
                    ModelTransform.pivot(-5.0f, 14.0f, zOffset));

            modelPartData.addChild(rightName, ModelPartBuilder.create()
                    .uv(48, 13).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 10.0f, 3.0f),
                    ModelTransform.pivot(5.0f, 14.0f, zOffset));
        }

        // Tail
        modelPartData.addChild("tail", ModelPartBuilder.create()
                .uv(48, 26).cuboid(-1.0f, -12.0f, 12.0f, 2.0f, 2.0f, 10.0f),
                ModelTransform.pivot(0.0f, 14.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(CatbusEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }
}
