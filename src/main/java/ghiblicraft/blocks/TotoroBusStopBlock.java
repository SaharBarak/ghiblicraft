package ghiblicraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.*;

public class TotoroBusStopBlock extends Block {

    public TotoroBusStopBlock() {
        super(Settings.create()
                .mapColor(MapColor.OAK_TAN)
                .strength(2.0f)
                .luminance(state -> 5)
                .nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(
                Block.createCuboidShape(6, 0, 6, 10, 16, 10),  // Post
                Block.createCuboidShape(2, 12, 5, 14, 16, 11)   // Sign
        );
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                               Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        ServerWorld serverWorld = (ServerWorld) world;
        BusStopState busState = getState(serverWorld.getServer());
        String dimension = world.getRegistryKey().getValue().toString();
        List<BlockPos> stops = busState.getStops(dimension);

        // Register this stop if not already registered
        boolean alreadyRegistered = stops.stream().anyMatch(s -> s.equals(pos));
        if (!alreadyRegistered) {
            stops.add(pos.toImmutable());
            busState.markDirty();
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
            if (stops.get(i).equals(pos)) {
                currentIndex = i;
                break;
            }
        }

        int nextIndex = (currentIndex + 1) % stops.size();
        BlockPos destination = stops.get(nextIndex);

        // Departure effects
        serverWorld.spawnParticles(ParticleTypes.CLOUD,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                30, 1.0, 0.5, 1.0, 0.05);
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS, 0.8f, 0.5f);

        // Teleport
        player.teleport(
                destination.getX() + 0.5,
                destination.getY() + 1.0,
                destination.getZ() + 0.5);

        // Arrival effects
        serverWorld.spawnParticles(ParticleTypes.CLOUD,
                destination.getX() + 0.5, destination.getY() + 1,
                destination.getZ() + 0.5,
                30, 1.0, 0.5, 1.0, 0.05);
        world.playSound(null, destination, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS, 0.8f, 0.5f);

        player.sendMessage(Text.literal("Arrived at stop #" + (nextIndex + 1) + "!")
                .formatted(Formatting.AQUA), true);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        if (world instanceof ServerWorld serverWorld) {
            BusStopState busState = getState(serverWorld.getServer());
            String dimension = world.getRegistryKey().getValue().toString();
            List<BlockPos> stops = busState.getStops(dimension);
            stops.removeIf(s -> s.equals(pos));
            busState.markDirty();
        }
    }

    private static BusStopState getState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager()
                .getOrCreate(BusStopState::fromNbt, BusStopState::new, "ghiblicraft_bus_stops");
    }

    public static class BusStopState extends PersistentState {
        private final Map<String, List<BlockPos>> networks = new HashMap<>();

        public BusStopState() {}

        public List<BlockPos> getStops(String dimension) {
            return networks.computeIfAbsent(dimension, k -> new ArrayList<>());
        }

        public static BusStopState fromNbt(NbtCompound nbt) {
            BusStopState state = new BusStopState();
            for (String dim : nbt.getKeys()) {
                NbtList list = nbt.getList(dim, 10); // 10 = NbtCompound
                List<BlockPos> stops = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    NbtCompound entry = list.getCompound(i);
                    stops.add(new BlockPos(entry.getInt("x"), entry.getInt("y"), entry.getInt("z")));
                }
                state.networks.put(dim, stops);
            }
            return state;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            for (Map.Entry<String, List<BlockPos>> entry : networks.entrySet()) {
                NbtList list = new NbtList();
                for (BlockPos pos : entry.getValue()) {
                    NbtCompound compound = new NbtCompound();
                    compound.putInt("x", pos.getX());
                    compound.putInt("y", pos.getY());
                    compound.putInt("z", pos.getZ());
                    list.add(compound);
                }
                nbt.put(entry.getKey(), list);
            }
            return nbt;
        }
    }
}
