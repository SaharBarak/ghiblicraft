package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.companions.TurnipHeadEntity;
import ghiblicraft.entities.models.TurnipHeadModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class TurnipHeadRenderer extends MobEntityRenderer<TurnipHeadEntity, TurnipHeadModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/turnip_head.png");

    public TurnipHeadRenderer(EntityRendererFactory.Context context) {
        super(context, new TurnipHeadModel(context.getPart(TurnipHeadModel.LAYER)), 0.4f);
    }

    @Override
    public Identifier getTexture(TurnipHeadEntity entity) {
        return TEXTURE;
    }
}
