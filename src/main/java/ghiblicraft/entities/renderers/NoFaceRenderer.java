package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.NoFaceEntity;
import ghiblicraft.entities.models.NoFaceModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class NoFaceRenderer extends MobEntityRenderer<NoFaceEntity, NoFaceModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/no_face.png");

    public NoFaceRenderer(EntityRendererFactory.Context context) {
        super(context, new NoFaceModel(context.getPart(NoFaceModel.LAYER)), 0.4f);
    }

    @Override
    public Identifier getTexture(NoFaceEntity entity) {
        return TEXTURE;
    }
}
