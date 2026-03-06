package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.NoFaceEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;

public class NoFaceModel extends SinglePartEntityModel<NoFaceEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "no_face"), "main");

    private final ModelPart root;

    public NoFaceModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Tall, slender body (like a dark robe)
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-4.0f, -20.0f, -3.0f, 8.0f, 20.0f, 6.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Mask/face
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 26).cuboid(-3.0f, -25.0f, -3.5f, 6.0f, 5.0f, 6.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Arms (hidden inside robe, subtly visible)
        modelPartData.addChild("left_arm", ModelPartBuilder.create()
                .uv(28, 0).cuboid(-1.0f, -18.0f, -1.5f, 2.0f, 12.0f, 3.0f),
                ModelTransform.pivot(-5.0f, 24.0f, 0.0f));

        modelPartData.addChild("right_arm", ModelPartBuilder.create()
                .uv(28, 15).cuboid(-1.0f, -18.0f, -1.5f, 2.0f, 12.0f, 3.0f),
                ModelTransform.pivot(5.0f, 24.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(NoFaceEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // Subtle hovering animation
    }
}
