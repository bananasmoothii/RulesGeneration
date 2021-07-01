package fr.bananasmoothii.rulesgeneration;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RulesGenerationGenerator extends ChunkGenerator {
    @NotNull
    @Override
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.BiomeGrid biome) {
        ChunkData chunk = Bukkit.getServer().createChunkData(world);
        CubicChunkEnvironment environment = CubicChunkEnvironment.withSeed(random, world.getMaxHeight() - world.getMinHeight());
        return null;
    }
}
