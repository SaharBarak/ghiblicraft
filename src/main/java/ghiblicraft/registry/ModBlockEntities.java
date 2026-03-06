package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.blocks.entity.CalciferFurnaceBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<CalciferFurnaceBlockEntity> CALCIFER_FURNACE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    new Identifier(GhibliCraft.MOD_ID, "calcifer_furnace"),
                    FabricBlockEntityTypeBuilder.create(CalciferFurnaceBlockEntity::new,
                            ModBlocks.CALCIFER_FURNACE).build());

    public static void register() {
        GhibliCraft.LOGGER.info("Registered GhibliCraft block entities.");
    }
}
