package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.KodamaSpiritEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KodamaSpiritModel extends SinglePartEntityModel<KodamaSpiritEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "kodama_spirit"), "main");

    private final ModelPart root;

    public KodamaSpiritModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Simple round body with small head
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-2.0f, -4.0f, -2.0f, 4.0f, 4.0f, 4.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 8).cuboid(-2.5f, -7.0f, -2.5f, 5.0f, 3.0f, 5.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(KodamaSpiritEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // Gentle bobbing animation
    }
}
