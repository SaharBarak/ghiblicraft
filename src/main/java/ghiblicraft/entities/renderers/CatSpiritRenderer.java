package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.CatSpiritEntity;
import ghiblicraft.entities.models.CatSpiritModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class CatSpiritRenderer extends MobEntityRenderer<CatSpiritEntity, CatSpiritModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/cat_spirit.png");

    public CatSpiritRenderer(EntityRendererFactory.Context context) {
        super(context, new CatSpiritModel(context.getPart(CatSpiritModel.LAYER)), 0.3f);
    }

    @Override
    public Identifier getTexture(CatSpiritEntity entity) {
        return TEXTURE;
    }
}
