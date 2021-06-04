package fr.bananasmoothii.rulesgeneration;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RulesGenerator extends ChunkGenerator {
    @NotNull
    @Override
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull ChunkGenerator.BiomeGrid biome) {
        // TODO
        ChunkData chunk = Bukkit.getServer().createChunkData(world);
        return null;
    }
}
