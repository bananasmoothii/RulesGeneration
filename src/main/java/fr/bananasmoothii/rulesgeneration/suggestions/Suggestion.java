package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.CubicChunkCoords;

import java.util.Collection;

public interface Suggestion {
    Collection<CubicChunkCoords> apply();
}
