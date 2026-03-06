package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.companions.TurnipHeadEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TurnipHeadModel extends SinglePartEntityModel<TurnipHeadEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "turnip_head"), "main");

    private final ModelPart root;

    public TurnipHeadModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Scarecrow post (body)
        modelPartData.addChild("post", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-1.0f, -24.0f, -1.0f, 2.0f, 24.0f, 2.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Cross-beam (arms)
        modelPartData.addChild("crossbeam", ModelPartBuilder.create()
                .uv(8, 0).cuboid(-8.0f, -20.0f, -0.5f, 16.0f, 1.0f, 1.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Turnip/Pumpkin head
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 26).cuboid(-3.0f, -30.0f, -3.0f, 6.0f, 6.0f, 6.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Tattered coat
        modelPartData.addChild("coat", ModelPartBuilder.create()
                .uv(24, 0).cuboid(-3.0f, -18.0f, -2.0f, 6.0f, 12.0f, 4.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        // Hat
        modelPartData.addChild("hat", ModelPartBuilder.create()
                .uv(24, 26).cuboid(-4.0f, -32.0f, -4.0f, 8.0f, 2.0f, 8.0f),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(TurnipHeadEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // Bounce!
    }
}
