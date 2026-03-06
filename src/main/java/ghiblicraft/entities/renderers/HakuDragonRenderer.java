package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.mounts.HakuDragonEntity;
import ghiblicraft.entities.models.HakuDragonModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class HakuDragonRenderer extends MobEntityRenderer<HakuDragonEntity, HakuDragonModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/haku_dragon.png");

    public HakuDragonRenderer(EntityRendererFactory.Context context) {
        super(context, new HakuDragonModel(context.getPart(HakuDragonModel.LAYER)), 0.8f);
    }

    @Override
    public Identifier getTexture(HakuDragonEntity entity) {
        return TEXTURE;
    }
}
