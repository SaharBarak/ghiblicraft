package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import ghiblicraft.blocks.*;
import ghiblicraft.blocks.crops.RicePaddyBlock;
import ghiblicraft.blocks.japanese.*;
import ghiblicraft.blocks.multiblock.BathhouseBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    // Magical blocks
    public static final CalciferFurnaceBlock CALCIFER_FURNACE = new CalciferFurnaceBlock();
    public static final MusicBoxBlock MUSIC_BOX = new MusicBoxBlock();
    public static final TotoroBusStopBlock TOTORO_BUS_STOP = new TotoroBusStopBlock();
    public static final BathhouseBlock BATHHOUSE = new BathhouseBlock();
    public static final SootSpriteWorkshop SOOT_SPRITE_WORKSHOP = new SootSpriteWorkshop();
    public static final SpellInscriptionTable SPELL_INSCRIPTION_TABLE = new SpellInscriptionTable();

    // Japanese building blocks
    public static final TatamiBlock TATAMI = new TatamiBlock();
    public static final ShojiScreenBlock SHOJI_SCREEN = new ShojiScreenBlock();
    public static final PaperLanternBlock PAPER_LANTERN = new PaperLanternBlock();
    public static final StoneLanternBlock STONE_LANTERN = new StoneLanternBlock();
    public static final WindChimeBlock WIND_CHIME = new WindChimeBlock();

    // Crops
    public static final RicePaddyBlock RICE_PADDY = new RicePaddyBlock();

    public static void register() {
        // Magical blocks
        registerBlock("calcifer_furnace", CALCIFER_FURNACE);
        registerBlock("music_box", MUSIC_BOX);
        registerBlock("totoro_bus_stop", TOTORO_BUS_STOP);
        registerBlock("bathhouse", BATHHOUSE);
        registerBlock("soot_sprite_workshop", SOOT_SPRITE_WORKSHOP);
        registerBlock("spell_inscription_table", SPELL_INSCRIPTION_TABLE);

        // Japanese blocks
        registerBlock("tatami", TATAMI);
        registerBlock("shoji_screen", SHOJI_SCREEN);
        registerBlock("paper_lantern", PAPER_LANTERN);
        registerBlock("stone_lantern", STONE_LANTERN);
        registerBlock("wind_chime", WIND_CHIME);

        // Crops (block only, item registered in ModItems)
        Registry.register(Registries.BLOCK,
                new Identifier(GhibliCraft.MOD_ID, "rice_paddy"), RICE_PADDY);

        ItemGroupEvents.modifyEntriesEvent(ModItems.GHIBLICRAFT_GROUP).register(content -> {
            content.add(CALCIFER_FURNACE);
            content.add(MUSIC_BOX);
            content.add(TOTORO_BUS_STOP);
            content.add(BATHHOUSE);
            content.add(SOOT_SPRITE_WORKSHOP);
            content.add(SPELL_INSCRIPTION_TABLE);
            content.add(TATAMI);
            content.add(SHOJI_SCREEN);
            content.add(PAPER_LANTERN);
            content.add(STONE_LANTERN);
            content.add(WIND_CHIME);
        });

        GhibliCraft.LOGGER.info("Registered GhibliCraft blocks.");
    }

    private static void registerBlock(String name, Block block) {
        Identifier id = new Identifier(GhibliCraft.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new FabricItemSettings()));
    }
}
