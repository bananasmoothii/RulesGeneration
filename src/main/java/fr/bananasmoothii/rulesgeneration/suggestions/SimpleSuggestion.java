package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import org.jetbrains.annotations.Contract;

public class SimpleSuggestion extends Suggestion {

    public final int x, y, z;
    public final CubicChunk what;

    public SimpleSuggestion(CubicChunkEnvironment environment, int x, int y, int z, CubicChunk what) {
        super(environment);
        this.x = x;
        this.y = y;
        this.z = z;
        this.what = what;
    }

    @Override
    @Contract(pure = true)
    public CubicChunkCoords[] applyWouldChange() {
        return new CubicChunkCoords[] {new CubicChunkCoords(what, x, y, z)};
    }

    @Override
    public float shouldFollow() {
        return what.rarity;
    }
}
