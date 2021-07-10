package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import org.jetbrains.annotations.Contract;

/**
 * Basically a suggestions that says "set that here."
 */
public class SimpleSuggestion extends Suggestion {

    public final int x, y, z;
    public final CubicChunk what;

    float shouldFollow;

    /**
     * New instance using {@link CubicChunk#rarity} as value for {@link #shouldFollow()}
     * @see #SimpleSuggestion(CubicChunkEnvironment, int, int, int, CubicChunk, float)
     */
    public SimpleSuggestion(CubicChunkEnvironment environment, int x, int y, int z, CubicChunk what) {
        this(environment, x, y, z, what, what.rarity);
    }

    /**
     * Read "Set 'what' in 'environment' at 'x', 'y', 'z'. If {@link #shouldFollow()} is high, then you should really
     * consider apply that suggestion."
     */
    public SimpleSuggestion(CubicChunkEnvironment environment, int x, int y, int z, CubicChunk what, float shouldFollow) {
        super(environment);
        this.x = x;
        this.y = y;
        this.z = z;
        this.what = what;
        this.shouldFollow = shouldFollow;
    }

    @Override
    @Contract(pure = true)
    public CubicChunkCoords[] applyWouldChange() {
        return new CubicChunkCoords[] {new CubicChunkCoords(what, x, y, z)};
    }

    @Override
    public float shouldFollow() {
        return shouldFollow;
    }
    
    public void setShouldFollow(float shouldFollow) {
        this.shouldFollow = shouldFollow;
    }

    /**
     * Basically for debugging
     */
    @Override
    public String toString() {
        return "SimpleSuggestion{" +
                "set " + what +
                " at x=" + x + ",y=" + y + ",z=" + z +
                ", shouldFollow=" + shouldFollow +
                '}';
    }
}
