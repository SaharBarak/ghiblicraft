package ghiblicraft.blocks;

import ghiblicraft.systems.SpiritReputationSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Map;

/**
 * Spell Inscription Table
 *
 * A magical workbench for casting rune-based spells. Place spirit essence
 * and lapis lazuli in specific patterns to activate different spells:
 *
 * Spells (activated by right-clicking with different items):
 * - Spirit Essence: "Sight Beyond" — 5 min Night Vision + Glowing nearby ores
 * - Lapis Lazuli: "Wind's Blessing" — 3 min Speed II + Jump Boost
 * - Amethyst Shard: "Spirit Shield" — 2 min Resistance II + Fire Resistance
 * - Gold Ingot: "Fortune's Favor" — 3 min Luck III
 * - Blaze Powder: "Flame Inscription" — Adds Fire Aspect to held weapon
 * - Prismarine Shard: "Ocean's Memory" — 5 min Water Breathing + Dolphin's Grace
 * - Diamond: "Eternal Edge" — Adds Sharpness/Efficiency to held tool
 */
public class SpellInscriptionTable extends Block {
    public SpellInscriptionTable() {
        super(Settings.create()
                .mapColor(MapColor.DARK_CRIMSON)
                .strength(4.0f)
                .luminance(state -> 6)
                .requiresTool());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack held = player.getStackInHand(hand);
        ServerWorld serverWorld = (ServerWorld) world;

        if (held.isEmpty()) {
            player.sendMessage(Text.literal("Place a reagent on the Inscription Table to cast a spell...")
                    .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC), true);
            return ActionResult.CONSUME;
        }

        // Check karma — need at least Neutral to cast
        int karma = SpiritReputationSystem.getKarma(player);
        if (karma < 20) {
            player.sendMessage(Text.literal("The spirits refuse your magic... Your karma is too low.")
                    .formatted(Formatting.DARK_RED), true);
            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                    10, 0.3, 0.3, 0.3, 0.02);
            return ActionResult.FAIL;
        }

        boolean spellCast = false;

        if (held.isOf(Items.AMETHYST_SHARD)) {
            // Sight Beyond
            castSpell(serverWorld, pos, "Sight Beyond", Formatting.LIGHT_PURPLE, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 6000, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 6000, 0, true, false));
            held.decrement(1);
            spellCast = true;

        } else if (held.isOf(Items.LAPIS_LAZULI)) {
            // Wind's Blessing
            castSpell(serverWorld, pos, "Wind's Blessing", Formatting.BLUE, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 3600, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600, 1, true, false));
            held.decrement(1);
            spellCast = true;

        } else if (held.isOf(Items.PRISMARINE_SHARD)) {
            // Spirit Shield
            castSpell(serverWorld, pos, "Spirit Shield", Formatting.AQUA, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 2400, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 2400, 0, true, false));
            held.decrement(1);
            spellCast = true;

        } else if (held.isOf(Items.GOLD_INGOT)) {
            // Fortune's Favor
            castSpell(serverWorld, pos, "Fortune's Favor", Formatting.GOLD, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 3600, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 3600, 0, true, false));
            held.decrement(1);
            spellCast = true;

        } else if (held.isOf(Items.BLAZE_POWDER)) {
            // Flame Inscription — enchant held weapon
            ItemStack offhand = player.getStackInHand(Hand.OFF_HAND);
            ItemStack toEnchant = hand == Hand.MAIN_HAND ? offhand : player.getMainHandStack();

            if (!toEnchant.isEmpty() && toEnchant.isEnchantable()) {
                castSpell(serverWorld, pos, "Flame Inscription", Formatting.RED, player);
                toEnchant.addEnchantment(Enchantments.FIRE_ASPECT, 2);
                held.decrement(1);
                spellCast = true;
            } else {
                player.sendMessage(Text.literal("Hold an enchantable item in your other hand...")
                        .formatted(Formatting.GRAY), true);
            }

        } else if (held.isOf(Items.HEART_OF_THE_SEA)) {
            // Ocean's Memory
            castSpell(serverWorld, pos, "Ocean's Memory", Formatting.DARK_AQUA, player);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, 6000, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 6000, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 6000, 0, true, false));
            held.decrement(1);
            spellCast = true;

        } else if (held.isOf(Items.DIAMOND)) {
            // Eternal Edge
            ItemStack offhand = player.getStackInHand(Hand.OFF_HAND);
            ItemStack toEnchant = hand == Hand.MAIN_HAND ? offhand : player.getMainHandStack();

            if (!toEnchant.isEmpty() && toEnchant.isEnchantable()) {
                castSpell(serverWorld, pos, "Eternal Edge", Formatting.AQUA, player);
                // Add Sharpness or Efficiency based on tool type
                if (toEnchant.isOf(Items.DIAMOND_SWORD) || toEnchant.isOf(Items.NETHERITE_SWORD) ||
                        toEnchant.isOf(Items.IRON_SWORD)) {
                    toEnchant.addEnchantment(Enchantments.SHARPNESS, 3);
                } else {
                    toEnchant.addEnchantment(Enchantments.EFFICIENCY, 3);
                }
                toEnchant.addEnchantment(Enchantments.UNBREAKING, 2);
                held.decrement(1);
                spellCast = true;
            } else {
                player.sendMessage(Text.literal("Hold a tool or weapon in your other hand...")
                        .formatted(Formatting.GRAY), true);
            }
        }

        if (!spellCast) {
            player.sendMessage(Text.literal("The table does not respond to this reagent...")
                    .formatted(Formatting.GRAY, Formatting.ITALIC), true);
        }

        return ActionResult.SUCCESS;
    }

    private void castSpell(ServerWorld world, BlockPos pos, String spellName,
                           Formatting color, PlayerEntity player) {
        // Rune circle effect
        for (int i = 0; i < 36; i++) {
            double angle = (Math.PI * 2 * i) / 36;
            world.spawnParticles(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5 + Math.cos(angle) * 2,
                    pos.getY() + 1.5,
                    pos.getZ() + 0.5 + Math.sin(angle) * 2,
                    2, 0, 0.5, 0, 0.1);
        }

        // Central burst
        world.spawnParticles(ParticleTypes.END_ROD,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                20, 0.3, 0.5, 0.3, 0.1);

        // Rising spiral
        for (int i = 0; i < 20; i++) {
            double angle = (Math.PI * 2 * i) / 10;
            double height = i * 0.15;
            world.spawnParticles(ParticleTypes.WITCH,
                    pos.getX() + 0.5 + Math.cos(angle) * (1.0 - height * 0.1),
                    pos.getY() + 1.0 + height,
                    pos.getZ() + 0.5 + Math.sin(angle) * (1.0 - height * 0.1),
                    1, 0, 0, 0, 0);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                SoundCategory.BLOCKS, 0.5f, 0.8f);

        player.sendMessage(
                Text.literal("Spell Cast: " + spellName).formatted(color, Formatting.BOLD),
                false);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Ambient enchanting particles
        if (random.nextInt(2) == 0) {
            world.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + 0.5 + random.nextGaussian() * 0.5,
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5 + random.nextGaussian() * 0.5,
                    0, 0.1, 0);
        }

        // Floating rune symbols (using end rod)
        if (random.nextInt(5) == 0) {
            double angle = random.nextFloat() * Math.PI * 2;
            world.addParticle(ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + Math.cos(angle) * 1.5,
                    pos.getY() + 1.5 + random.nextFloat(),
                    pos.getZ() + 0.5 + Math.sin(angle) * 1.5,
                    0, 0.01, 0);
        }
    }
}
