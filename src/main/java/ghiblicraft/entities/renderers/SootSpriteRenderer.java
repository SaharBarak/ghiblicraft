package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.SootSpriteEntity;
import ghiblicraft.entities.models.SootSpriteModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class SootSpriteRenderer extends MobEntityRenderer<SootSpriteEntity, SootSpriteModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/soot_sprite.png");

    public SootSpriteRenderer(EntityRendererFactory.Context context) {
        super(context, new SootSpriteModel(context.getPart(SootSpriteModel.LAYER)), 0.15f);
    }

    @Override
    public Identifier getTexture(SootSpriteEntity entity) {
        return TEXTURE;
    }
}
