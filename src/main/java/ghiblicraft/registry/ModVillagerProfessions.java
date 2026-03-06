package ghiblicraft.registry;

import com.google.common.collect.ImmutableSet;
import ghiblicraft.GhibliCraft;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;

public class ModVillagerProfessions {
    // Points of Interest
    public static final PointOfInterestType HERBALIST_POI = PointOfInterestHelper.register(
            new Identifier(GhibliCraft.MOD_ID, "herbalist_poi"),
            1, 1, Blocks.FLOWERING_AZALEA);

    public static final PointOfInterestType FISHER_POI = PointOfInterestHelper.register(
            new Identifier(GhibliCraft.MOD_ID, "fisher_poi"),
            1, 1, Blocks.BARREL);

    public static final PointOfInterestType TRAVELING_MERCHANT_POI = PointOfInterestHelper.register(
            new Identifier(GhibliCraft.MOD_ID, "traveling_merchant_poi"),
            1, 1, Blocks.LOOM);

    // Professions
    public static final VillagerProfession HERBALIST = new VillagerProfession(
            "herbalist",
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "herbalist_poi"))),
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "herbalist_poi"))),
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FARMER);

    public static final VillagerProfession FISHER = new VillagerProfession(
            "fisher",
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "fisher_poi"))),
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "fisher_poi"))),
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);

    public static final VillagerProfession TRAVELING_MERCHANT = new VillagerProfession(
            "traveling_merchant",
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "traveling_merchant_poi"))),
            entry -> entry.matchesKey(RegistryKey.of(Registries.POINT_OF_INTEREST_TYPE.getKey(), new Identifier(GhibliCraft.MOD_ID, "traveling_merchant_poi"))),
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);

    public static void register() {
        Registry.register(Registries.VILLAGER_PROFESSION,
                new Identifier(GhibliCraft.MOD_ID, "herbalist"), HERBALIST);
        Registry.register(Registries.VILLAGER_PROFESSION,
                new Identifier(GhibliCraft.MOD_ID, "fisher"), FISHER);
        Registry.register(Registries.VILLAGER_PROFESSION,
                new Identifier(GhibliCraft.MOD_ID, "traveling_merchant"), TRAVELING_MERCHANT);

        registerTrades();

        GhibliCraft.LOGGER.info("Registered GhibliCraft villager professions.");
    }

    private static void registerTrades() {
        // Herbalist trades
        TradeOfferHelper.registerVillagerOffers(HERBALIST, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 2),
                    new ItemStack(ModItems.FOREST_HERB, 3), 12, 2, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 5),
                    new ItemStack(ModItems.SPIRIT_ESSENCE, 1), 6, 5, 0.1f));
        });
        TradeOfferHelper.registerVillagerOffers(HERBALIST, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(ModItems.FOREST_HERB, 8),
                    new ItemStack(Items.EMERALD, 1), 12, 5, 0.05f));
        });

        // Fisher trades
        TradeOfferHelper.registerVillagerOffers(FISHER, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 3),
                    new ItemStack(ModItems.FISH_STEW, 1), 8, 3, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.COD, 6),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));
        });

        // Traveling Merchant trades
        TradeOfferHelper.registerVillagerOffers(TRAVELING_MERCHANT, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 4),
                    new ItemStack(ModItems.RICE, 8), 12, 3, 0.05f));
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModItems.NOODLES, 4), 8, 5, 0.1f));
        });
        TradeOfferHelper.registerVillagerOffers(TRAVELING_MERCHANT, 2, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new ItemStack(Items.EMERALD, 8),
                    new ItemStack(ModItems.RAMEN, 2), 6, 8, 0.15f));
        });
    }
}
