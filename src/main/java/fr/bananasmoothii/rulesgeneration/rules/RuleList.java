package fr.bananasmoothii.rulesgeneration.rules;

import fr.bananasmoothii.rulesgeneration.LogicalOperator;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunkEnvironment;
import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * list of rules, with a {@link LogicalOperator} to know how to proceed.
 * @param <R> the class extending {@link Rule}, most of the time you will just need RuleList<{@linkplain Rule}>
 * @see SuggestionList
 */
public class RuleList<R extends Rule> extends Rule implements List<R> {

    final List<R> list;
    public final LogicalOperator logicalOperator;
    public final @Range(from = 1, to = Integer.MAX_VALUE) int minAmount;

    /**
     * New instance with {@link LogicalOperator#AND}
     * @see #RuleList(LogicalOperator) constructor with a logical operator
     */
    public RuleList() {
        this(LogicalOperator.AND);
    }

    /**
     * @param minAmount the minimum amount of validated rules from the list to make {@link #test(CubicChunkEnvironment, int, int, int)}
     *                  return {@code true} and {@link #testAndSuggest(CubicChunkEnvironment, int, int, int)} return
     *                  {@code null}. This automatically sets the {@link LogicalOperator} to {@link LogicalOperator#OR OR}
     *                  because {@link LogicalOperator#AND AND} requires every rule in the list to be valid.
     * @see #RuleList(LogicalOperator) constructor with minAmount = 1 and where you can set a logical operator
     */
    public RuleList(@Range(from = 1, to = Integer.MAX_VALUE) int minAmount) {
        this.logicalOperator = LogicalOperator.OR;
        //noinspection ConstantConditions
        if (minAmount <= 0) throw new IllegalArgumentException("min amount needs to be 1 or above");
        this.minAmount = minAmount;
        list = new ArrayList<>();
    }

    /**
     * Constructor where you can choose your {@link LogicalOperator}. {@code minAmount} is set to 1
     * @param logicalOperator The logical operator used in tests
     * @see #RuleList(int) constructor to set a minimum amount of validated rules
     */
    public RuleList(LogicalOperator logicalOperator) {
        this.logicalOperator = logicalOperator;
        this.minAmount = 1;
        list = new ArrayList<>();
    }

    @Override
    public @Nullable SuggestionList testAndSuggest(CubicChunkEnvironment environment, int x, int y, int z) {
        SuggestionList suggestions = new SuggestionList(environment, logicalOperator);
        // shouldn't need to validate suggestions here as they are built based on the rules
        switch (logicalOperator) {
            case AND:
                // test everything
                for (R rule : list) {
                    SuggestionList newSuggestions = rule.testAndSuggest(environment, x, y, z);
                    if (newSuggestions != null) suggestions.addAll(newSuggestions);
                }
                return suggestions.isEmpty() ? null : suggestions;
            case OR:
                // first test if everything is ok, continue with suggestions only after
                RuleList<R> wrongRules = new RuleList<>(LogicalOperator.OR);
                for (R rule : list) {
                    if (!rule.test(environment, x, y, z)) wrongRules.add(rule);
                }
                if (list.size() - wrongRules.size() >= minAmount) return null;
                // there are wrong rules, we need to make suggestions.
                // first, randomly pick some rules (we pick "minAmount" of them)
                RuleList<R> rulesIWantToValidate = new RuleList<>(LogicalOperator.OR);
                for (int i = 0; i < minAmount; i++) {
                    if (wrongRules.size() == 0) break;
                    int pick = environment.random.nextInt(wrongRules.size());
                    rulesIWantToValidate.add(wrongRules.get(pick));
                    wrongRules.remove(pick);
                }
                // then add every suggestions
                for (R rule : rulesIWantToValidate) {
                    SuggestionList newSuggestions = rule.testAndSuggest(environment, x, y, z);
                    if (newSuggestions != null) suggestions.addAll(newSuggestions);
                }
                return suggestions;
            default:
                throw new IllegalStateException("Unexpected value: " + logicalOperator);
        }
    }

    @Override
    public boolean test(CubicChunkEnvironment environment, int x, int y, int z) {
        switch (logicalOperator) {
            case AND:
                for (R rule : list) {
                    if (!rule.test(environment, x, y, z)) return false;
                }
                return true;
            case OR:
                int validatedRules = 0;
                for (R rule : list) {
                    if (rule.test(environment, x, y, z)) {
                        if (validatedRules > minAmount) return true;
                        validatedRules++;
                    }
                }
                return false;

            default:
                throw new IllegalStateException("Unexpected value: " + logicalOperator);
        }
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
    public Iterator<R> iterator() {
        return list.iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return list.toArray();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(R rule) {
        return list.add(rule);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends R> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends R> c) {
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

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public R get(int index) {
        return list.get(index);
    }

    @Override
    public R set(int index, R element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, R element) {
        list.add(index, element);
    }

    @Override
    public R remove(int index) {
        return list.remove(index);
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
    public ListIterator<R> listIterator() {
        return list.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<R> listIterator(int index) {
        return list.listIterator(index);
    }

    @NotNull
    @Override
    public List<R> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
