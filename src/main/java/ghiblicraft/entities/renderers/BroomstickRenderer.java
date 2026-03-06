package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.BroomstickEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class BroomstickRenderer extends EntityRenderer<BroomstickEntity> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/broomstick.png");
    public static final EntityModelLayer LAYER = new EntityModelLayer(
            new Identifier(GhibliCraft.MOD_ID, "broomstick"), "main");

    private final ModelPart root;

    public BroomstickRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.root = context.getPart(LAYER);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Broom handle
        modelPartData.addChild("handle", ModelPartBuilder.create()
                .uv(0, 0).cuboid(-0.5f, -0.5f, -8.0f, 1.0f, 1.0f, 16.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        // Broom bristles
        modelPartData.addChild("bristles", ModelPartBuilder.create()
                .uv(0, 17).cuboid(-2.0f, -1.0f, 7.0f, 4.0f, 3.0f, 5.0f),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(BroomstickEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                this.root.getDefaultTransform().getLayer(TEXTURE));
        this.root.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(BroomstickEntity entity) {
        return TEXTURE;
    }
}
