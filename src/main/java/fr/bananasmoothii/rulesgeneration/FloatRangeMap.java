package fr.bananasmoothii.rulesgeneration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FloatRangeMap<V> implements Iterable<FloatRangeMap.FloatRangeNode<V>> {

    final List<FloatRangeNode<V>> nodes = new ArrayList<>();

    public void put(float from, float to, V value) {
        nodes.add(new FloatRangeNode<>(from, to, value));
    }

    public @Nullable V get(float where) {
        for (FloatRangeNode<V> node : this) {
            if (node.contains(where)) return node.value;
        }
        return null;
    }

    @NotNull
    @Override
    public Iterator<FloatRangeNode<V>> iterator() {
        return new Iterator<FloatRangeNode<V>>() {
            private int i = nodes.size();

            @Override
            public boolean hasNext() {
                return i > 0;
            }

            @Override
            public FloatRangeNode<V> next() {
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
