package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A suggestion is one or more changes to the link {@link CubicChunkEnvironment}. Suggestions are not thread-safe.
 */
public abstract class Suggestion {

    CubicChunkEnvironment environment;

    public Suggestion(CubicChunkEnvironment environment) {
        this.environment = environment;
    }

    protected @Nullable ArrayList<CubicChunkCoords> changed;

    /**
     * @return all what changed after you called this method.
     */
    public CubicChunkCoords[] apply() {
        CubicChunkCoords[] toChange = applyWouldChange();
        changed = new ArrayList<>();
        for (CubicChunkCoords cubicChunkCoords : toChange) {
            changed.add(
                    new CubicChunkCoords(environment.get(cubicChunkCoords.x, cubicChunkCoords.y, cubicChunkCoords.z),
                            cubicChunkCoords.x, cubicChunkCoords.y, cubicChunkCoords.z));
            environment.set(cubicChunkCoords);
        }
        return toChange;
    }

    /**
     * @return all what would change if you call {@link #apply()}.
     */
    @Contract(pure = true)
    public abstract CubicChunkCoords[] applyWouldChange();

    /**
     * @throws NullPointerException if {@link #apply()} was not called
     * @see #canUndo()
     */
    public void undo() {
        for (CubicChunkCoords changed1 : Objects.requireNonNull(changed, "as warned in the JavaDoc, apply() was not called")) {
            environment.set(changed1.cubicChunk, changed1.x, changed1.y, changed1.z);
        }
        changed = null;
    }

    /**
     * tells whether you can call{@link #undo()}
     */
    public boolean canUndo() {
        return changed != null;
    }

    /**
     * Similar to {@link CubicChunk#rarity}.
     * 0 = common (default), positive = very common, negative = rare. The value is in range [-100; 100]
     */
    public abstract @Range(from = -100, to = 100) float shouldFollow();

}
