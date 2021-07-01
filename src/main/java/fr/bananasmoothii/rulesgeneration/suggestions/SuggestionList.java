package fr.bananasmoothii.rulesgeneration.suggestions;

import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkCoords;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import fr.bananasmoothii.rulesgeneration.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class SuggestionList extends Suggestion implements List<Suggestion> {

    final ArrayList<Suggestion> list;
    public final LogicalOperator logicalOperator;

    public SuggestionList(CubicChunkEnvironment environment, int initialCapacity) {
        super(environment);
        list = new ArrayList<>(initialCapacity);
        logicalOperator = LogicalOperator.OR;
    }

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
                return list.get(ThreadLocalRandom.current().nextInt(list.size())).applyWouldChange();
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

    public void validate() {
        switch (logicalOperator) {
            case OR:
                ArrayList<Runnable> todo = new ArrayList<>();
                for (final Suggestion suggestion : list) {
                    for (CubicChunkCoords change : suggestion.apply()) {
                        if (change.cubicChunk == null) continue;
                        boolean deleteSuggestionIfNeedsOtherSuggestions = list.size() > 1 && ThreadLocalRandom.current().nextBoolean();
                        for (Rule rule : change.cubicChunk.rules) {
                            if (deleteSuggestionIfNeedsOtherSuggestions) {
                                if (!rule.test(environment, change.x, change.y, change.z)) {
                                    todo.add(() -> list.remove(suggestion));
                                }
                            } else {
                                SuggestionList newSuggestions = rule.testAndSuggest(environment, change.x, change.y, change.z);
                                if (newSuggestions != null) {
                                    newSuggestions.validate();
                                    todo.add(() -> list.addAll(newSuggestions));
                                }
                            }
                        }
                        suggestion.undo();
                    }
                }
                for (Runnable todo1 : todo) {
                    todo1.run();
                }
                break;
            case AND:
                // TODO
                break;
        }
    }

    @Override
    public double shouldFollow() {
        if (list.isEmpty()) return 0d;
        double average = 0d;
        for (Suggestion suggestion : list) {
            average += suggestion.shouldFollow();
        }
        return average / list.size();
    }

    public enum LogicalOperator {
        AND, OR
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
        return list.set(index, element);
    }

    @Override
    public boolean add(Suggestion suggestion) {
        return list.add(suggestion);
    }

    @Override
    public void add(int index, Suggestion element) {
        list.add(index, element);
    }

    @Override
    public Suggestion remove(int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Suggestion> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Suggestion> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
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
