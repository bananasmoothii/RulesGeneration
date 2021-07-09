package fr.bananasmoothii.rulesgeneration.rules;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import fr.bananasmoothii.rulesgeneration.suggestions.SimpleSuggestion;
import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class RelativeRule extends Rule {

    public final int relativeX;
    public final int relativeY;
    public final int relativeZ;
    public final boolean shouldBePresent;
    public final CubicChunk[] what;

    public RelativeRule(int relativeX, int relativeY, int relativeZ, CubicChunk... what) {
        this(relativeX, relativeY, relativeZ, true, what);
    }

    public RelativeRule(int relativeX, int relativeY, int relativeZ, boolean shouldBePresent, CubicChunk... what) {
        super();
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;
        this.shouldBePresent = shouldBePresent;
        this.what = what;
    }

    @Override
    public @Nullable SuggestionList testAndSuggest(CubicChunkEnvironment environment, int x, int y, int z) {
        if (test(environment, x, y, z)) return null;

        SuggestionList suggestions = new SuggestionList(environment);
        if (shouldBePresent) {
            for (CubicChunk chunk : what) {
                suggestions.add(new SimpleSuggestion(environment, x + relativeX, y + relativeY, z + relativeZ, chunk));
            }
        } else {
            for (CubicChunk usableChunk : CubicChunk.allAvailable()) {
                boolean whatContainsThatChunk = false;
                for (CubicChunk what1 : what) {
                    if (usableChunk == what1) {
                        whatContainsThatChunk = true;
                        break;
                    }
                }
                if (whatContainsThatChunk) continue;
                suggestions.add(new SimpleSuggestion(environment, x + relativeX, y + relativeY, z + relativeZ, usableChunk));
            }
        }
        suggestions.validate();
        if (suggestions.isEmpty()) return null;
        return suggestions;
    }

    @Override
    public boolean test(CubicChunkEnvironment environment, int x, int y, int z) {
        //if (!concerned.equals(environment.get(x, y, z))) throw new WrongCubicChunkException(environment.get(x, y, z), what);

        CubicChunk present = environment.get(x + relativeX, y + relativeY, z + relativeZ);
        for (CubicChunk what1 : what) {
            if (what1 == present) return shouldBePresent;
        }
        return !shouldBePresent;
    }
/*
    @Override
    public boolean concerns(int xConcerned, int yConcerned, int zConcerned, int xApplied, int yApplied, int zApplied) {
        return xConcerned - xApplied == relativeX && yConcerned - yApplied == relativeY && zConcerned - zApplied == relativeZ;
    }
*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelativeRule that = (RelativeRule) o;
        return relativeX == that.relativeX && relativeY == that.relativeY && relativeZ == that.relativeZ && shouldBePresent == that.shouldBePresent && Arrays.equals(what, that.what);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(relativeX, relativeY, relativeZ, shouldBePresent);
        result = 31 * result + Arrays.hashCode(what);
        return result;
    }
}
