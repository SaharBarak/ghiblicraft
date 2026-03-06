package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.mounts.CatbusEntity;
import ghiblicraft.entities.models.CatbusModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CatbusRenderer extends MobEntityRenderer<CatbusEntity, CatbusModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/catbus.png");

    public CatbusRenderer(EntityRendererFactory.Context context) {
        super(context, new CatbusModel(context.getPart(CatbusModel.LAYER)), 1.0f);
    }

    @Override
    public Identifier getTexture(CatbusEntity entity) {
        return TEXTURE;
    }
}
