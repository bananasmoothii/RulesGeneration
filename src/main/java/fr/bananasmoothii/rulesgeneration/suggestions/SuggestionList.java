package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.FloatRangeMap;
import fr.bananasmoothii.rulesgeneration.LogicalOperator;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import fr.bananasmoothii.rulesgeneration.rules.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * List of {@link Suggestion}
 * @see fr.bananasmoothii.rulesgeneration.rules.RuleList
 */
public class SuggestionList extends Suggestion implements List<Suggestion> {

    final List<Suggestion> list;
    public final LogicalOperator logicalOperator;

    public SuggestionList(CubicChunkEnvironment environment) {
        this(environment, LogicalOperator.OR);
    }

    public SuggestionList(CubicChunkEnvironment environment, LogicalOperator logicalOperator) {
        super(environment);
        list = new ArrayList<>();
        this.logicalOperator = logicalOperator;
    }

    @Override
    public CubicChunkCoords[] applyWouldChange() {
        if (list.isEmpty()) return new CubicChunkCoords[0];
        switch (logicalOperator) {
            case OR:
                if (chosenSuggestion == null) choose();
                return chosenSuggestion.applyWouldChange();
            case AND:
                CubicChunkCoords[][] applyWouldChange1 = new CubicChunkCoords[list.size()][];
                int totalSize = 0;
                for (int i = 0; i < list.size(); i++) {
                    applyWouldChange1[i] = list.get(i).applyWouldChange();
                    totalSize += applyWouldChange1[i].length;
                }
                CubicChunkCoords[] result = new CubicChunkCoords[totalSize];
                int i = 0;
                for (CubicChunkCoords[] change1 : applyWouldChange1) {
                    for (CubicChunkCoords change2 : change1) {
                        result[i] = change2;
                        i++;
                    }
                }
                return result;
            default:
                throw new IllegalStateException("Unexpected value: " + logicalOperator);
        }
    }

    @Nullable Suggestion chosenSuggestion;

    /**
     * This is useful only when {@link #logicalOperator} is {@link LogicalOperator#OR OR}. It picks one suggestion based
     * on {@link Suggestion#shouldFollow()}. The result, {@link #chosenSuggestion}, will be {@code null} if
     * {@link #logicalOperator} is not {@link LogicalOperator#OR OR}.
     *
     * This uses a {@link FloatRangeMap} where all node size are the value of {@link Suggestion#shouldFollow()} + 100.
     * So for exemple, If element A has a size of 3, B 8 and C 4, the map will look like {@code AAABBBBBBBBCCCC}. We can
     * see here that if you pick a random element, there are more chances to get B.
     */
    void choose() {
        if (logicalOperator == LogicalOperator.OR) {
            float lastRangeTo = 0f;
            FloatRangeMap<Suggestion> map = new FloatRangeMap<>();
            for (Suggestion suggestion : list) {
                @Range(from = 0, to = 200) float nodeSize = suggestion.shouldFollow() + 100;
                map.put(lastRangeTo, lastRangeTo + nodeSize, suggestion);
                lastRangeTo += nodeSize;
            }
            chosenSuggestion = map.get(environment.random.nextInt((int) (lastRangeTo * 1000)) / 1000f);
        }
    }

    /**
     * Be sure everything that will be changed if that suggestion is applied is valid according to the rules of each
     * {@link CubicChunk} changed
     */
    public void validate() {
        ArrayList<Runnable> todo = new ArrayList<>();
        switch (logicalOperator) {
            case OR:
                if (chosenSuggestion == null) choose();
                for (CubicChunkCoords change : chosenSuggestion.apply()) {
                    if (change.cubicChunk == null) continue;
                    for (Rule rule : change.cubicChunk.rules) {
                        // if the suggestion isn't valid according to all rules, should it just remove that suggestion...
                        if (list.size() > 1 && (Thread.currentThread().getStackTrace().length > 100 || environment.random.nextBoolean())) {
                            if (!rule.test(environment, change.x, change.y, change.z)) {
                                todo.add(() -> list.remove(chosenSuggestion));
                            }
                        // ...or try to add other suggestions so the rule is valid
                        } else {
                            SuggestionList newSuggestions = rule.testAndSuggest(environment, change.x, change.y, change.z);
                            if (newSuggestions != null) {
                                //newSuggestions.validate();
                                todo.add(() -> list.addAll(newSuggestions));
                            }
                        }
                    }
                }
                chosenSuggestion.undo();
                break;
            case AND:
                ArrayList<CubicChunkCoords> changes = new ArrayList<>();
                for (Suggestion suggestion : list) {
                    changes.addAll(Arrays.asList(suggestion.apply()));
                    todo.add(suggestion::undo);
                }
                for (CubicChunkCoords change : changes) {
                    if (change.cubicChunk == null) continue;
                    for (Rule rule : change.cubicChunk.rules) {
                        SuggestionList newSuggestions = rule.testAndSuggest(environment, change.x, change.y, change.z);
                        if (newSuggestions != null) {
                            //newSuggestions.validate();
                            list.addAll(newSuggestions);
                        }
                    }
                }
                break;
        }
        for (int i = todo.size() - 1; i >= 0; i--) {
            todo.get(i).run();
        }
    }

    /**
     * @return the average of all elements {@link Suggestion#shouldFollow()} result.
     */
    @Override
    public float shouldFollow() {
        if (list.isEmpty()) return 0f;
        float average = 0f;
        for (Suggestion suggestion : list) {
            average += suggestion.shouldFollow();
        }
        if (average == 0f) return 0f;
        return average / list.size();
    }


    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return list.toArray();
    }

    @SuppressWarnings({"SuspiciousToArrayCall", "NullableProblems"})
    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public Suggestion get(int index) {
        return list.get(index);
    }

    @Override
    public Suggestion set(int index, Suggestion element) {
        chosenSuggestion = null;
        return list.set(index, element);
    }

    @Override
    public boolean add(Suggestion suggestion) {
        chosenSuggestion = null;
        return list.add(suggestion);
    }

    @Override
    public void add(int index, Suggestion element) {
        chosenSuggestion = null;
        list.add(index, element);
    }

    @Override
    public Suggestion remove(int index) {
        chosenSuggestion = null;
        return list.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        chosenSuggestion = null;
        return list.remove(o);
    }

    @Override
    public void clear() {
        chosenSuggestion = null;
        list.clear();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Suggestion> c) {
        chosenSuggestion = null;
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Suggestion> c) {
        chosenSuggestion = null;
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        chosenSuggestion = null;
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        chosenSuggestion = null;
        return list.retainAll(c);
    }

    @NotNull
    @Override
    public ListIterator<Suggestion> listIterator(int index) {
        return list.listIterator(index);
    }

    @NotNull
    @Override
    public ListIterator<Suggestion> listIterator() {
        return list.listIterator();
    }

    @NotNull
    @Override
    public Iterator<Suggestion> iterator() {
        return list.iterator();
    }

    @NotNull
    @Override
    public List<Suggestion> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }
}
