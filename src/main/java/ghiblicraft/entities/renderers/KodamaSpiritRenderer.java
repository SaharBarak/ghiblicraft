package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.KodamaSpiritEntity;
import ghiblicraft.entities.models.KodamaSpiritModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class KodamaSpiritRenderer extends MobEntityRenderer<KodamaSpiritEntity, KodamaSpiritModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/kodama_spirit.png");

    public KodamaSpiritRenderer(EntityRendererFactory.Context context) {
        super(context, new KodamaSpiritModel(context.getPart(KodamaSpiritModel.LAYER)), 0.2f);
    }

    @Override
    public Identifier getTexture(KodamaSpiritEntity entity) {
        return TEXTURE;
    }
}
