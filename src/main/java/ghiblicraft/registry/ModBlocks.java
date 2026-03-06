package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.blocks.CalciferFurnaceBlock;
import ghiblicraft.blocks.MusicBoxBlock;
import ghiblicraft.blocks.SootSpriteWorkshop;
import ghiblicraft.blocks.SpellInscriptionTable;
import ghiblicraft.blocks.TotoroBusStopBlock;
import ghiblicraft.blocks.multiblock.BathhouseBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final CalciferFurnaceBlock CALCIFER_FURNACE = new CalciferFurnaceBlock();
    public static final MusicBoxBlock MUSIC_BOX = new MusicBoxBlock();
    public static final TotoroBusStopBlock TOTORO_BUS_STOP = new TotoroBusStopBlock();
    public static final BathhouseBlock BATHHOUSE = new BathhouseBlock();
    public static final SootSpriteWorkshop SOOT_SPRITE_WORKSHOP = new SootSpriteWorkshop();
    public static final SpellInscriptionTable SPELL_INSCRIPTION_TABLE = new SpellInscriptionTable();

    public static void register() {
        registerBlock("calcifer_furnace", CALCIFER_FURNACE);
        registerBlock("music_box", MUSIC_BOX);
        registerBlock("totoro_bus_stop", TOTORO_BUS_STOP);
        registerBlock("bathhouse", BATHHOUSE);
        registerBlock("soot_sprite_workshop", SOOT_SPRITE_WORKSHOP);
        registerBlock("spell_inscription_table", SPELL_INSCRIPTION_TABLE);

        ItemGroupEvents.modifyEntriesEvent(ModItems.GHIBLICRAFT_GROUP).register(content -> {
            content.add(CALCIFER_FURNACE);
            content.add(MUSIC_BOX);
            content.add(TOTORO_BUS_STOP);
            content.add(BATHHOUSE);
            content.add(SOOT_SPRITE_WORKSHOP);
            content.add(SPELL_INSCRIPTION_TABLE);
        });

        GhibliCraft.LOGGER.info("Registered GhibliCraft blocks.");
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = new Identifier(GhibliCraft.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new FabricItemSettings()));
    }
}
