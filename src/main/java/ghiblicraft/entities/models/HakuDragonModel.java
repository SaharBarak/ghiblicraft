package ghiblicraft.entities.models;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.mounts.HakuDragonEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class HakuDragonModel extends SinglePartEntityModel<HakuDragonEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "haku_dragon"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;

    public HakuDragonModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Serpentine body (Eastern dragon style)
        modelPartData.addChild("body", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-3.0f, -3.0f, -8.0f, 6.0f, 6.0f, 16.0f),
                ModelTransform.pivot(0.0f, 16.0f, 0.0f));

        // Graceful head with mane
        modelPartData.addChild("head", ModelPartBuilder.create()
                .uv(0, 22).cuboid(-2.5f, -4.0f, -12.0f, 5.0f, 5.0f, 5.0f),
                ModelTransform.pivot(0.0f, 14.0f, -8.0f));

        // Tail segments (flowing serpentine tail)
        modelPartData.addChild("tail1", ModelPartBuilder.create()
                .uv(0, 32).cuboid(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f),
                ModelTransform.pivot(0.0f, 16.0f, 8.0f));

        modelPartData.addChild("tail2", ModelPartBuilder.create()
                .uv(0, 46).cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 10.0f),
                ModelTransform.pivot(0.0f, 16.0f, 18.0f));

        // Small legs
        modelPartData.addChild("left_front_leg", ModelPartBuilder.create()
                .uv(28, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.pivot(-3.0f, 18.0f, -4.0f));

        modelPartData.addChild("right_front_leg", ModelPartBuilder.create()
                .uv(36, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.pivot(3.0f, 18.0f, -4.0f));

        modelPartData.addChild("left_back_leg", ModelPartBuilder.create()
                .uv(28, 40).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.pivot(-3.0f, 18.0f, 4.0f));

        modelPartData.addChild("right_back_leg", ModelPartBuilder.create()
                .uv(36, 40).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 6.0f, 2.0f),
                ModelTransform.pivot(3.0f, 18.0f, 4.0f));

        // Whiskers / antler-like horns
        modelPartData.addChild("left_horn", ModelPartBuilder.create()
                .uv(44, 0).cuboid(-0.5f, -7.0f, -11.0f, 1.0f, 4.0f, 1.0f),
                ModelTransform.pivot(-2.0f, 14.0f, -8.0f));

        modelPartData.addChild("right_horn", ModelPartBuilder.create()
                .uv(48, 0).cuboid(-0.5f, -7.0f, -11.0f, 1.0f, 4.0f, 1.0f),
                ModelTransform.pivot(2.0f, 14.0f, -8.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(HakuDragonEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        // Serpentine undulation
        this.body.yaw = MathHelper.sin(animationProgress * 0.1f) * 0.1f;

        // Head tracking
        this.head.yaw = headYaw * (float) Math.PI / 180f;
        this.head.pitch = headPitch * (float) Math.PI / 180f;
    }
}
