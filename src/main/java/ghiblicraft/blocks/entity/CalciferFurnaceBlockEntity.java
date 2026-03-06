package ghiblicraft.blocks.entity;

import ghiblicraft.blocks.CalciferFurnaceBlock;
import ghiblicraft.registry.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CalciferFurnaceBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int burnTime = 0;
    private int fuelTime = 0;
    private int cookTime = 0;
    private int cookTimeTotal = 100; // 2x faster than vanilla (200 -> 100)

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> fuelTime;
                case 2 -> cookTime;
                case 3 -> cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
                case 2 -> cookTime = value;
                case 3 -> cookTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public CalciferFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CALCIFER_FURNACE, pos, state);
    }

    // Inventory implementation — provides direct access to this BE's slots
    @Override
    public int size() { return inventory.size(); }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) { return Inventories.removeStack(inventory, slot); }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() { inventory.clear(); }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.ghiblicraft.calcifer_furnace");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        // Pass 'this' directly — now this BE implements Inventory so changes are live
        return new FurnaceScreenHandler(syncId, playerInventory, this, propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putInt("BurnTime", burnTime);
        nbt.putInt("FuelTime", fuelTime);
        nbt.putInt("CookTime", cookTime);
        nbt.putInt("CookTimeTotal", cookTimeTotal);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        burnTime = nbt.getInt("BurnTime");
        fuelTime = nbt.getInt("FuelTime");
        cookTime = nbt.getInt("CookTime");
        cookTimeTotal = nbt.getInt("CookTimeTotal");
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        boolean wasBurning = burnTime > 0;

        if (burnTime > 0) burnTime--;

        ItemStack input = inventory.get(0);
        ItemStack fuel = inventory.get(1);
        ItemStack output = inventory.get(2);

        if (!input.isEmpty()) {
            Optional<SmeltingRecipe> recipe = world.getRecipeManager()
                    .getFirstMatch(RecipeType.SMELTING,
                            new net.minecraft.inventory.SimpleInventory(input), world);

            if (recipe.isPresent()) {
                ItemStack result = recipe.get().getOutput(world.getRegistryManager());

                if (burnTime <= 0 && !fuel.isEmpty()) {
                    burnTime = net.minecraft.block.entity.AbstractFurnaceBlockEntity
                            .createFuelTimeMap().getOrDefault(fuel.getItem(), 0);
                    burnTime = (int) (burnTime * 1.5); // Calcifer's 50% fuel efficiency boost
                    fuelTime = burnTime;
                    fuel.decrement(1);
                }

                if (burnTime > 0) {
                    cookTime++;
                    if (cookTime >= cookTimeTotal) {
                        cookTime = 0;
                        if (output.isEmpty()) {
                            inventory.set(2, result.copy());
                        } else if (output.isOf(result.getItem())) {
                            output.increment(result.getCount());
                        }
                        input.decrement(1);
                    }
                }
            }
        } else {
            cookTime = 0;
        }

        boolean isBurning = burnTime > 0;
        if (wasBurning != isBurning) {
            world.setBlockState(pos, state.with(CalciferFurnaceBlock.LIT, isBurning), 3);
        }

        markDirty();
    }
}
