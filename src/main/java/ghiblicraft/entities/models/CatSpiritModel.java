package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.CatSpiritEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class CatSpiritModel extends SinglePartEntityModel<CatSpiritEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "cat_spirit"), "main");

    private final ModelPart root;

    public CatSpiritModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Cat body
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2.0f, -3.0f, -4.0f, 4.0f, 3.0f, 8.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Head
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 11).cuboid(-2.0f, -5.0f, -6.0f, 4.0f, 3.0f, 3.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Ears
        modelPartData.addChild("left_ear", ModelPartBuilder.create()
                .uv(0, 17).cuboid(-0.5f, -6.5f, -5.5f, 1.0f, 1.5f, 1.0f),
                ModelTransform.pivot(-1.0f, 24.0f, 0.0f));

        modelPartData.addChild("right_ear", ModelPartBuilder.create()
                .uv(4, 17).cuboid(-0.5f, -6.5f, -5.5f, 1.0f, 1.5f, 1.0f),
                ModelTransform.pivot(1.0f, 24.0f, 0.0f));

        // Tail
        modelPartData.addChild("tail", ModelPartBuilder.create()
                .uv(16, 0).cuboid(-0.5f, -4.0f, 4.0f, 1.0f, 1.0f, 5.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Legs
        for (int i = 0; i < 4; i++) {
            float xOff = (i % 2 == 0) ? -1.5f : 1.5f;
            float zOff = (i < 2) ? -3.0f : 3.0f;
            modelPartData.addChild("leg_" + i, ModelPartBuilder.create()
                    .uv(20, i * 4).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f),
                    ModelTransform.pivot(xOff, 24.0f, zOff));
        }

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(CatSpiritEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }
}
