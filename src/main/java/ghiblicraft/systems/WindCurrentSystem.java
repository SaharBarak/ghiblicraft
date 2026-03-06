package ghiblicraft.systems;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Wind Current System (Nausicaä)
 *
 * Generates dynamic wind currents based on:
 * - Altitude (stronger winds at higher altitude)
 * - Time of day (wind shifts direction)
 * - Biome (open plains = stronger horizontal, mountains = updrafts)
 * - Near water = sea breeze
 */
public class WindCurrentSystem {

    public static Vec3d getWindAt(BlockPos pos, World world) {
        double windX = 0;
        double windY = 0;
        double windZ = 0;

        long time = world.getTimeOfDay();
        double timeAngle = (time % 24000) / 24000.0 * Math.PI * 2;

        // Altitude-based wind (stronger as you go higher)
        double altitudeFactor = Math.max(0, (pos.getY() - 80) / 200.0);
        windX += Math.sin(timeAngle) * altitudeFactor * 0.05;
        windZ += Math.cos(timeAngle) * altitudeFactor * 0.05;

        // Updraft near high terrain (check blocks below for mountain detection)
        int solidBelow = 0;
        for (int y = pos.getY() - 1; y >= pos.getY() - 20 && y >= world.getBottomY(); y--) {
            if (!world.getBlockState(new BlockPos(pos.getX(), y, pos.getZ())).isAir()) {
                solidBelow = pos.getY() - y;
                break;
            }
        }

        if (solidBelow > 0 && solidBelow < 15) {
            // Close to terrain = updraft (thermal)
            windY += 0.02 * (1.0 - solidBelow / 15.0);
        }

        // Valley wind (check for terrain walls to the side)
        boolean wallLeft = !world.getBlockState(pos.add(-5, 0, 0)).isAir();
        boolean wallRight = !world.getBlockState(pos.add(5, 0, 0)).isAir();
        boolean wallFront = !world.getBlockState(pos.add(0, 0, -5)).isAir();
        boolean wallBack = !world.getBlockState(pos.add(0, 0, 5)).isAir();

        // Channel wind between walls
        if (wallLeft && wallRight) {
            windZ += 0.03 * Math.sin(timeAngle); // Wind funnels through valley
        }
        if (wallFront && wallBack) {
            windX += 0.03 * Math.cos(timeAngle);
        }

        // High altitude jet stream
        if (pos.getY() > 180) {
            double jetStrength = (pos.getY() - 180) / 80.0 * 0.08;
            windX += Math.sin(timeAngle + 0.5) * jetStrength;
            windZ += Math.cos(timeAngle + 0.5) * jetStrength;
        }

        return new Vec3d(windX, windY, windZ);
    }
}
