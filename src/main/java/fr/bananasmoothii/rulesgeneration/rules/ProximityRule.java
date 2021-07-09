package fr.bananasmoothii.rulesgeneration.rules;

import fr.bananasmoothii.rulesgeneration.LogicalOperator;
import fr.bananasmoothii.rulesgeneration.chunks.CubicChunk;

public class ProximityRule extends RuleList<RelativeRule> {
    public final int radius;

    public ProximityRule(int radius, CubicChunk... what) {
        this(radius, 1, what);
    }

    public ProximityRule(int radius, int minAmount, CubicChunk... what) {
        super(LogicalOperator.OR, minAmount);
        this.radius = radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    add(new RelativeRule(x, y, z, what));
                }
            }
        }
    }
}
