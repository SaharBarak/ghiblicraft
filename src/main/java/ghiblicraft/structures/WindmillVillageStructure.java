package ghiblicraft.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ghiblicraft.registry.ModStructures;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class WindmillVillageStructure extends Structure {
    public static final Codec<WindmillVillageStructure> CODEC = RecordCodecBuilder.<WindmillVillageStructure>mapCodec(
            instance -> instance.group(configCodecBuilder(instance))
                    .apply(instance, WindmillVillageStructure::new)
    ).codec();

    public WindmillVillageStructure(Config config) {
        super(config);
    }

    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        BlockPos blockPos = context.chunkPos().getCenterAtY(0);
        int y = context.chunkGenerator().getHeightInGround(
                blockPos.getX(), blockPos.getZ(),
                Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig());

        blockPos = blockPos.withY(y);

        return StructurePoolBasedGenerator.generate(
                context,
                context.registryManager().get(net.minecraft.registry.RegistryKeys.TEMPLATE_POOL)
                        .getEntry(net.minecraft.registry.RegistryKey.of(
                                net.minecraft.registry.RegistryKeys.TEMPLATE_POOL,
                                new Identifier("ghiblicraft", "windmill_village/start_pool"))).get(),
                Optional.empty(), 4, blockPos, false,
                Optional.of(Heightmap.Type.WORLD_SURFACE_WG), 60);
    }

    @Override
    public StructureType<?> getType() {
        return ModStructures.WINDMILL_VILLAGE_TYPE;
    }
}
