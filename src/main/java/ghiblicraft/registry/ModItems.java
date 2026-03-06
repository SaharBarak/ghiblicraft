package ghiblicraft.registry;

import ghiblicraft.GhibliCraft;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    // Comfort buff: Regeneration I + Resistance I for 60 seconds
    private static final FoodComponent RICE_BOWL_FOOD = new FoodComponent.Builder()
            .hunger(6).saturationModifier(0.8f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 0), 1.0f)
            .build();

    private static final FoodComponent RAMEN_FOOD = new FoodComponent.Builder()
            .hunger(8).saturationModifier(1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 0), 1.0f)
            .build();

    private static final FoodComponent DUMPLINGS_FOOD = new FoodComponent.Builder()
            .hunger(5).saturationModifier(0.7f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 0), 1.0f)
            .build();

    private static final FoodComponent FISH_STEW_FOOD = new FoodComponent.Builder()
            .hunger(7).saturationModifier(0.9f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 1200, 0), 1.0f)
            .build();

    // Food items
    public static final Item RICE_BOWL = new Item(new FabricItemSettings().food(RICE_BOWL_FOOD).maxCount(16));
    public static final Item RAMEN = new Item(new FabricItemSettings().food(RAMEN_FOOD).maxCount(16));
    public static final Item DUMPLINGS = new Item(new FabricItemSettings().food(DUMPLINGS_FOOD).maxCount(64));
    public static final Item FISH_STEW = new Item(new FabricItemSettings().food(FISH_STEW_FOOD).maxCount(16));

    // Ingredients
    public static final Item RICE = new Item(new FabricItemSettings().maxCount(64));
    public static final Item NOODLES = new Item(new FabricItemSettings().maxCount(64));
    public static final Item DUMPLING_WRAPPER = new Item(new FabricItemSettings().maxCount(64));
    public static final Item SPIRIT_ESSENCE = new Item(new FabricItemSettings().maxCount(16));
    public static final Item FOREST_HERB = new Item(new FabricItemSettings().maxCount(64));

    // Special items
    public static final Item MAGICAL_BROOMSTICK = new ghiblicraft.items.MagicalBroomstickItem();

    // Item Group
    public static final RegistryKey<ItemGroup> GHIBLICRAFT_GROUP = RegistryKey.of(
            RegistryKeys.ITEM_GROUP, new Identifier(GhibliCraft.MOD_ID, "ghiblicraft_items"));

    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "rice_bowl"), RICE_BOWL);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "ramen"), RAMEN);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "dumplings"), DUMPLINGS);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "fish_stew"), FISH_STEW);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "rice"), RICE);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "noodles"), NOODLES);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "dumpling_wrapper"), DUMPLING_WRAPPER);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "spirit_essence"), SPIRIT_ESSENCE);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "forest_herb"), FOREST_HERB);
        Registry.register(Registries.ITEM, new Identifier(GhibliCraft.MOD_ID, "magical_broomstick"), MAGICAL_BROOMSTICK);

        Registry.register(Registries.ITEM_GROUP, GHIBLICRAFT_GROUP,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(RICE_BOWL))
                        .displayName(Text.translatable("itemGroup.ghiblicraft.ghiblicraft_items"))
                        .build());

        ItemGroupEvents.modifyEntriesEvent(GHIBLICRAFT_GROUP).register(content -> {
            content.add(RICE_BOWL);
            content.add(RAMEN);
            content.add(DUMPLINGS);
            content.add(FISH_STEW);
            content.add(RICE);
            content.add(NOODLES);
            content.add(DUMPLING_WRAPPER);
            content.add(SPIRIT_ESSENCE);
            content.add(FOREST_HERB);
            content.add(MAGICAL_BROOMSTICK);
        });

        GhibliCraft.LOGGER.info("Registered GhibliCraft items.");
    }
}
