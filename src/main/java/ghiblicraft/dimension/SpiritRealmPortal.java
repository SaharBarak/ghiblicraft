package ghiblicraft.dimension;

import ghiblicraft.GhibliCraft;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class SpiritRealmPortal {

    /**
     * Attempt to enter the Spirit Realm. Called when player uses spirit_essence
     * on a Giant Camphor Tree hollow or a special portal frame.
     */
    public static boolean tryEnterSpiritRealm(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return false;

        ServerWorld spiritRealm = server.getWorld(SpiritRealmDimension.SPIRIT_REALM_KEY);
        if (spiritRealm == null) {
            player.sendMessage(Text.literal("The Spirit Realm shimmers but cannot be reached...")
                    .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC), false);
            return false;
        }

        ServerWorld currentWorld = player.getServerWorld();
        boolean enteringSpirit = currentWorld.getRegistryKey() != SpiritRealmDimension.SPIRIT_REALM_KEY;

        ServerWorld targetWorld;
        BlockPos targetPos;

        if (enteringSpirit) {
            targetWorld = spiritRealm;
            // Mirror position from overworld
            targetPos = new BlockPos(player.getBlockPos().getX(), 80, player.getBlockPos().getZ());
            announceEntry(player);
        } else {
            // Return to overworld
            targetWorld = server.getWorld(World.OVERWORLD);
            if (targetWorld == null) return false;
            targetPos = new BlockPos(player.getBlockPos().getX(), 64, player.getBlockPos().getZ());
            announceExit(player);
        }

        // Departure effects
        currentWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL,
                player.getX(), player.getY() + 1, player.getZ(),
                50, 0.5, 1.0, 0.5, 0.1);
        currentWorld.playSound(null, player.getBlockPos(),
                SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.AMBIENT, 0.8f, 1.5f);

        // Find safe landing position
        BlockPos safePos = findSafeLanding(targetWorld, targetPos);

        // Teleport
        player.teleport(targetWorld, safePos.getX() + 0.5, safePos.getY(),
                safePos.getZ() + 0.5, player.getYaw(), player.getPitch());

        // Arrival effects
        targetWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL,
                safePos.getX() + 0.5, safePos.getY() + 1, safePos.getZ() + 0.5,
                50, 0.5, 1.0, 0.5, 0.1);

        if (enteringSpirit) {
            // Spirit realm debuffs/buffs
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 6000, 0, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 6000, 0, true, false));
        }

        return true;
    }

    private static BlockPos findSafeLanding(ServerWorld world, BlockPos around) {
        // Search for a safe position
        for (int y = around.getY(); y < around.getY() + 30; y++) {
            BlockPos check = new BlockPos(around.getX(), y, around.getZ());
            if (world.getBlockState(check).isAir() && world.getBlockState(check.up()).isAir()) {
                // Ensure there's ground below
                if (!world.getBlockState(check.down()).isAir()) {
                    return check;
                }
            }
        }

        // Fallback: create a platform
        BlockPos platform = new BlockPos(around.getX(), 80, around.getZ());
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.setBlockState(platform.add(x, -1, z), Blocks.AMETHYST_BLOCK.getDefaultState(), 3);
            }
        }
        return platform;
    }

    private static void announceEntry(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("")
                .append(Text.literal("You feel yourself dissolving into the spirit world...\n")
                        .formatted(Formatting.DARK_PURPLE, Formatting.ITALIC))
                .append(Text.literal("The boundary between worlds grows thin.")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.ITALIC)),
                false);
    }

    private static void announceExit(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("")
                .append(Text.literal("The mortal world solidifies around you...\n")
                        .formatted(Formatting.GREEN, Formatting.ITALIC))
                .append(Text.literal("You have returned from the Spirit Realm.")
                        .formatted(Formatting.DARK_GREEN, Formatting.ITALIC)),
                false);
    }
}
