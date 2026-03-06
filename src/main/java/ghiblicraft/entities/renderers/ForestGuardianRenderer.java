package ghiblicraft.entities.renderers;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.ForestGuardianEntity;
import ghiblicraft.entities.models.ForestGuardianModel;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class ForestGuardianRenderer extends MobEntityRenderer<ForestGuardianEntity, ForestGuardianModel> {
    private static final Identifier TEXTURE = new Identifier(GhibliCraft.MOD_ID, "textures/entity/forest_guardian.png");

    public ForestGuardianRenderer(EntityRendererFactory.Context context) {
        super(context, new ForestGuardianModel(context.getPart(ForestGuardianModel.LAYER)), 0.8f);
    }

    @Override
    public Identifier getTexture(ForestGuardianEntity entity) {
        return TEXTURE;
    }
}
