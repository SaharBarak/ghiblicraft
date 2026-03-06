package ghiblicraft.items;

import ghiblicraft.entities.BroomstickEntity;
import ghiblicraft.registry.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class MagicalBroomstickItem extends Item {
    public MagicalBroomstickItem() {
        super(new FabricItemSettings().maxCount(1).maxDamage(512));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Spawn broomstick entity and mount the player
            BroomstickEntity broom = new BroomstickEntity(ModEntities.BROOMSTICK, world);
            broom.setPosition(player.getX(), player.getY() + 0.5, player.getZ());
            broom.setYaw(player.getYaw());
            world.spawnEntity(broom);
            player.startRiding(broom);

            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PHANTOM_FLAP,
                    SoundCategory.PLAYERS, 1.0f, 1.5f);

            // Spawn sparkle particles in a circle
            for (int i = 0; i < 20; i++) {
                double angle = (Math.PI * 2 * i) / 20;
                serverWorld.spawnParticles(ParticleTypes.END_ROD,
                        player.getX() + Math.cos(angle) * 1.5,
                        player.getY() + 0.5,
                        player.getZ() + Math.sin(angle) * 1.5,
                        1, 0, 0.1, 0, 0.02);
            }

            stack.damage(1, player, p -> p.sendToolBreakStatus(hand));
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
