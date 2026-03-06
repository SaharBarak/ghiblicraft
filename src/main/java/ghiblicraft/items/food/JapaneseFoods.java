package ghiblicraft.items.food;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;

/**
 * Japanese food items for the GhibliCraft mod.
 * Each food has unique buff effects reflecting its cultural significance.
 */
public class JapaneseFoods {

    // Onigiri — simple, filling rice ball. Staple travel food.
    public static final FoodComponent ONIGIRI_FOOD = new FoodComponent.Builder()
            .hunger(6).saturationModifier(0.7f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 600, 0), 0.5f)
            .build();
    public static final Item ONIGIRI = new Item(new FabricItemSettings().food(ONIGIRI_FOOD));

    // Mochi — chewy rice cake. Sweet and energizing.
    public static final FoodComponent MOCHI_FOOD = new FoodComponent.Builder()
            .hunger(4).saturationModifier(0.5f)
            .statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 900, 0), 0.8f)
            .snack()
            .build();
    public static final Item MOCHI = new Item(new FabricItemSettings().food(MOCHI_FOOD));

    // Green Tea (Matcha) — not filling but gives great buffs. Ceremonial.
    public static final FoodComponent GREEN_TEA_FOOD = new FoodComponent.Builder()
            .hunger(1).saturationModifier(0.3f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1200, 0), 1.0f)
            .alwaysEdible()
            .snack()
            .build();
    public static final Item GREEN_TEA = new Item(new FabricItemSettings().food(GREEN_TEA_FOOD).maxCount(16));

    // Dango — sweet dumpling on a stick. Festival food.
    public static final FoodComponent DANGO_FOOD = new FoodComponent.Builder()
            .hunger(5).saturationModifier(0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.LUCK, 1200, 0), 0.7f)
            .snack()
            .build();
    public static final Item DANGO = new Item(new FabricItemSettings().food(DANGO_FOOD));

    // Taiyaki — fish-shaped cake filled with sweet bean paste. Popular street food.
    public static final FoodComponent TAIYAKI_FOOD = new FoodComponent.Builder()
            .hunger(5).saturationModifier(0.6f)
            .statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 600, 0), 0.4f)
            .snack()
            .build();
    public static final Item TAIYAKI = new Item(new FabricItemSettings().food(TAIYAKI_FOOD));

    // Tempura — light crispy fried vegetables/shrimp. Rich and satisfying.
    public static final FoodComponent TEMPURA_FOOD = new FoodComponent.Builder()
            .hunger(8).saturationModifier(0.9f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 600, 0), 0.5f)
            .build();
    public static final Item TEMPURA = new Item(new FabricItemSettings().food(TEMPURA_FOOD));

    // Yakitori — grilled chicken skewer. Simple but effective.
    public static final FoodComponent YAKITORI_FOOD = new FoodComponent.Builder()
            .hunger(7).saturationModifier(0.8f)
            .statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 300, 0), 0.3f)
            .build();
    public static final Item YAKITORI = new Item(new FabricItemSettings().food(YAKITORI_FOOD));

    // Bento Box — premium meal combining multiple items. The ultimate food.
    public static final FoodComponent BENTO_FOOD = new FoodComponent.Builder()
            .hunger(12).saturationModifier(1.2f)
            .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 400, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, 0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 600, 0), 0.5f)
            .build();
    public static final Item BENTO_BOX = new Item(new FabricItemSettings().food(BENTO_FOOD).maxCount(1));
}
