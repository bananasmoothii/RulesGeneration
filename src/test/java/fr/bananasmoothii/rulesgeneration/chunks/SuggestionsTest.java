package fr.bananasmoothii.rulesgeneration.chunks;

import fr.bananasmoothii.rulesgeneration.LogicalOperator;
import fr.bananasmoothii.rulesgeneration.rules.ProximityRule;
import fr.bananasmoothii.rulesgeneration.rules.RelativeRule;
import fr.bananasmoothii.rulesgeneration.rules.Rule;
import fr.bananasmoothii.rulesgeneration.suggestions.SimpleSuggestion;
import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.junit.jupiter.api.Test;

public class SuggestionsTest {

    static CubicChunkEnvironment environment;
    static CubicChunk stone, grass;

    static {
        environment = new CubicChunkEnvironment();
        stone = new CubicChunk(1, 30f, false);
        grass = new CubicChunk(2, 0f, false);

        Rule min5StoneAround = new ProximityRule(1, 2, stone);
        stone.rules.add(min5StoneAround);

        Rule aboveStone = new RelativeRule(0, -1, 0, stone);
        grass.rules.add(aboveStone);

        Rule underAir = new RelativeRule(0, 1, 0, CubicChunk.AIR_CHUNK);
        grass.rules.add(underAir);

        Rule aboveGrass = new RelativeRule(0, -1, 0, grass);
        CubicChunk.AIR_CHUNK.rules.add(aboveGrass);

        Rule min5AirAround = new ProximityRule(1, 2, CubicChunk.AIR_CHUNK);
        CubicChunk.AIR_CHUNK.rules.add(min5AirAround);
    }

    @Test
    void suggestions() {
        CubicChunkEnvironment environment = new CubicChunkEnvironment();
        SuggestionList suggestions = new SuggestionList(environment, LogicalOperator.AND);
        suggestions.add(new SimpleSuggestion(environment, 25, -20, 0, CubicChunk.AIR_CHUNK, 10f));
        suggestions.add(new SimpleSuggestion(environment, 36, 4, 0, CubicChunk.AIR_CHUNK, -10f));
        suggestions.add(new SimpleSuggestion(environment, -14, 6, 0, CubicChunk.AIR_CHUNK, 0f));
        suggestions.add(new SimpleSuggestion(environment, -16, -2, 0, CubicChunk.AIR_CHUNK, 100f));
        suggestions.validate();
        for (CubicChunkCoords change : suggestions.apply()) {
            System.out.println(change);
        }
        environment.debugPrint();
    }

    @Test
    void recursion() {
        environment.set(stone, 0, 0, 0);
        stone.rules.testAndSuggest(environment, 0, 0, 0).forEach(System.out::println);
    }

    @Test
    void generation() {
        environment.generate(-3, -3, -3, 3, 3, 3);
        environment.validateAll();
        environment.debugPrint();
    }
}
