package fr.bananasmoothii.rulesgeneration.rules;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.jetbrains.annotations.Nullable;

public abstract class Rule {

    public abstract @Nullable SuggestionList testAndSuggest(CubicChunkEnvironment environment, int x, int y, int z);

    public boolean test(CubicChunkEnvironment environment, int x, int y, int z) {
        return testAndSuggest(environment, x, y, z) != null;
    }

    public void apply(CubicChunkEnvironment environment, int x, int y, int z) {
        SuggestionList suggestions = testAndSuggest(environment, x, y, z);
        if (suggestions != null) suggestions.apply();
    }

    /*
     * This is an indicator telling if, applied at a position x, y, zApplied, the value of
     * {@link #test(CubicChunkEnvironment, int, int, int) test} could change if x, y, zConcerned are changed.
     *
     * While this returning {@code true} doesn't necessarily mean modifying the concerned chunk will affect the return value of
     * {@link #test(CubicChunkEnvironment, int, int, int) test} and {@link #testAndSuggest(CubicChunkEnvironment, int, int, int) testAndSuggest},
     * this returning {@code false} must imply that you can mess with the concerned chunk without worrying.
     * @return {@code false} if the value of {@link #test(CubicChunkEnvironment, int, int, int) test} will never move, no
     *      matter what you do to x, y and zConcerned, if the rule is applied at x, y and zApplied.
     *
    public abstract boolean concerns(int xConcerned, int yConcerned, int zConcerned, int xApplied, int yApplied, int zApplied);
     */
}
