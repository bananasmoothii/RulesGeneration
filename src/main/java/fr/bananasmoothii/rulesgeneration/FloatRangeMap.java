package fr.bananasmoothii.rulesgeneration;

import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is a special type of map or list, I don't really know, that uses ranges as index.
 * For exemple, if I put element A from index 0 to 5 and element B from 4 to 10, then if I call
 * {@link #get(float) get(2f)}, it will return element A. But if I call {@link #get(float) get(4.5f)}, it will return
 * B because this is the element I set the last.
 *
 * This class is only used in {@link SuggestionList} for now. See {@link SuggestionList#choose()}
 * @param <T> the type of all elements that you can {@link #get(float) get} or {@link #put(float, float, Object) put}.
 * @see SuggestionList#choose()
 */
@SuppressWarnings("JavadocReference") // for the choose link above
public class FloatRangeMap<T> implements Iterable<FloatRangeMap.FloatRangeNode<T>> {

    final List<FloatRangeNode<T>> nodes = new ArrayList<>();

    public void put(float from, float to, T value) {
        nodes.add(new FloatRangeNode<>(from, to, value));
    }

    public @Nullable T get(float where) {
        for (FloatRangeNode<T> node : this) {
            if (node.contains(where)) return node.value;
        }
        return null;
    }

    @NotNull
    @Override
    public Iterator<FloatRangeNode<T>> iterator() {
        return new Iterator<FloatRangeNode<T>>() {
            private int i = nodes.size();

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public FloatRangeNode<T> next() {
                i--;
                return nodes.get(i);
            }
        };
    }

    public static class FloatRangeNode<V> {
        float from, to;
        V value;

        /**
         * @param from inclusive
         * @param to exclusive
         */
        public FloatRangeNode(float from, float to, V value) {
            if (to <= from) throw new IllegalArgumentException("to must be greater than from");
            this.from = from;
            this.to = to;
            this.value = value;
        }

        public boolean contains(float number) {
            return from <= number && number < to;
        }
    }
}
