package fr.bananasmoothii.rulesgeneration.rules;

import fr.bananasmoothii.rulesgeneration.CubicChunk;
import fr.bananasmoothii.rulesgeneration.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.CubicChunkEnvironment;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class Rule {
    protected CubicChunk concerned;

    public Rule(CubicChunk concerned) {
        this.concerned = concerned;
    }

    public abstract @Nullable Collection<CubicChunkCoords> testAndSuggest(CubicChunkEnvironment environment, int x, int y, int z);

    public boolean test(CubicChunkEnvironment environment, int x, int y, int z) {
        return testAndSuggest(environment, x, y, z) != null;
    }
}
