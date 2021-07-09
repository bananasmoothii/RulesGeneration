package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Suggestion {

    CubicChunkEnvironment environment;

    public Suggestion(CubicChunkEnvironment environment) {
        this.environment = environment;
    }

    protected @Nullable ArrayList<CubicChunkCoords> changed;

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

    @Contract(pure = true)
    public abstract CubicChunkCoords[] applyWouldChange();

    /**
     * @throws NullPointerException if {@link #apply()} was not called
     */
    public void undo() {
        for (CubicChunkCoords changed1 : Objects.requireNonNull(changed, "as warned in the JavaDoc, apply() was not called")) {
            environment.set(changed1.cubicChunk, changed1.x, changed1.y, changed1.z);
        }
        changed = null;
    }

    public boolean canUndo() {
        return changed != null;
    }

    /** Similar to {@link CubicChunk#rarity}. Please keep values in the range [-100; 100] */
    public abstract @Range(from = -100, to = 100) float shouldFollow();

}
