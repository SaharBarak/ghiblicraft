package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.entities.CatSpiritEntity;
import ghiblicraft.entities.ForestGuardianEntity;
import ghiblicraft.entities.KodamaSpiritEntity;
import ghiblicraft.entities.SootSpriteEntity;
import ghiblicraft.entities.NoFaceEntity;
import ghiblicraft.entities.BroomstickEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<KodamaSpiritEntity> KODAMA_SPIRIT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "kodama_spirit"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, KodamaSpiritEntity::new)
                    .dimensions(EntityDimensions.fixed(0.4f, 0.8f))
                    .trackRangeChunks(8)
                    .build()
    );

    public static final EntityType<SootSpriteEntity> SOOT_SPRITE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "soot_sprite"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, SootSpriteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.3f, 0.3f))
                    .trackRangeChunks(6)
                    .build()
    );

    public static final EntityType<ForestGuardianEntity> FOREST_GUARDIAN = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "forest_guardian"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ForestGuardianEntity::new)
                    .dimensions(EntityDimensions.fixed(1.8f, 3.5f))
                    .trackRangeChunks(10)
                    .build()
    );

    public static final EntityType<CatSpiritEntity> CAT_SPIRIT = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "cat_spirit"),
            FabricEntityTypeBuilder.create(SpawnGroup.AMBIENT, CatSpiritEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.5f))
                    .trackRangeChunks(8)
                    .build()
    );

    public static final EntityType<NoFaceEntity> NO_FACE = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "no_face"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NoFaceEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 2.2f))
                    .trackRangeChunks(10)
                    .build()
    );

    public static final EntityType<BroomstickEntity> BROOMSTICK = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(GhibliCraft.MOD_ID, "broomstick"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, BroomstickEntity::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 0.5f))
                    .trackRangeChunks(10)
                    .build()
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(KODAMA_SPIRIT, KodamaSpiritEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(SOOT_SPRITE, SootSpriteEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(FOREST_GUARDIAN, ForestGuardianEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(CAT_SPIRIT, CatSpiritEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NO_FACE, NoFaceEntity.createAttributes());

        GhibliCraft.LOGGER.info("Registered GhibliCraft entities.");
    }
}
