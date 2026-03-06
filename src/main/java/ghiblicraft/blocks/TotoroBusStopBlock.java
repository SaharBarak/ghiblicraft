package ghiblicraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.*;

public class TotoroBusStopBlock extends Block {
    // Global bus stop network - maps dimension + pos to name
    private static final Map<String, List<StopEntry>> BUS_STOP_NETWORK = new HashMap<>();

    public TotoroBusStopBlock() {
        super(Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .strength(2.0f)
                .luminance(state -> 5)
                .nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Signpost shape
        return VoxelShapes.union(
                Block.createCuboidShape(6, 0, 6, 10, 16, 10),  // Post
                Block.createCuboidShape(2, 12, 5, 14, 16, 11)   // Sign
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        String dimension = world.getRegistryKey().getValue().toString();
        String networkKey = dimension;

        // Register this stop if not already registered
        StopEntry thisStop = new StopEntry(pos, dimension);
        List<StopEntry> stops = BUS_STOP_NETWORK.computeIfAbsent(networkKey, k -> new ArrayList<>());

        boolean alreadyRegistered = stops.stream().anyMatch(s -> s.pos.equals(pos));
        if (!alreadyRegistered) {
            stops.add(thisStop);
            player.sendMessage(Text.literal("Bus stop registered! (#" + stops.size() + ")")
                    .formatted(Formatting.GREEN), true);
            return ActionResult.SUCCESS;
        }

        // Find next stop in network
        if (stops.size() <= 1) {
            player.sendMessage(Text.literal("No other bus stops found. Place another bus stop to create a route!")
                    .formatted(Formatting.YELLOW), true);
            return ActionResult.SUCCESS;
        }

        // Find current index and teleport to next
        int currentIndex = -1;
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).pos.equals(pos)) {
                currentIndex = i;
                break;
            }
        }

        int nextIndex = (currentIndex + 1) % stops.size();
        StopEntry destination = stops.get(nextIndex);

        // Teleport with effects
        ServerWorld serverWorld = (ServerWorld) world;

        // Departure effects
        serverWorld.spawnParticles(ParticleTypes.CLOUD,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                30, 1.0, 0.5, 1.0, 0.05);
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS, 0.8f, 0.5f);

        // Teleport
        player.teleport(
                destination.pos.getX() + 0.5,
                destination.pos.getY() + 1.0,
                destination.pos.getZ() + 0.5);

        // Arrival effects
        serverWorld.spawnParticles(ParticleTypes.CLOUD,
                destination.pos.getX() + 0.5, destination.pos.getY() + 1,
                destination.pos.getZ() + 0.5,
                30, 1.0, 0.5, 1.0, 0.05);
        world.playSound(null, destination.pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS, 0.8f, 0.5f);

        player.sendMessage(Text.literal("Arrived at stop #" + (nextIndex + 1) + "!")
                .formatted(Formatting.AQUA), true);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        String networkKey = world.getRegistryKey().getValue().toString();
        List<StopEntry> stops = BUS_STOP_NETWORK.get(networkKey);
        if (stops != null) {
            stops.removeIf(s -> s.pos.equals(pos));
        }
    }

    private static class StopEntry {
        final BlockPos pos;
        final String dimension;

        StopEntry(BlockPos pos, String dimension) {
            this.pos = pos;
            this.dimension = dimension;
        }
    }
}
