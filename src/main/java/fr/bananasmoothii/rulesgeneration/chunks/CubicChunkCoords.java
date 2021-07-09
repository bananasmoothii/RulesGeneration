package fr.bananasmoothii.rulesgeneration.chunks;

import org.jetbrains.annotations.Nullable;

public class CubicChunkCoords {

    public @Nullable CubicChunk cubicChunk;
    public int x, y, z;

    public CubicChunkCoords(@Nullable CubicChunk cubicChunk, int x, int y, int z) {
        this.cubicChunk = cubicChunk;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "CubicChunkCoords{" +
                "cubicChunk=" + cubicChunk +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
