package fr.bananasmoothii.rulesgeneration.chunks;

import fr.bananasmoothii.rulesgeneration.LogicalOperator;
import fr.bananasmoothii.rulesgeneration.rules.Rule;
import fr.bananasmoothii.rulesgeneration.rules.RuleList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.*;

/**
 * A CubicChunk is the base bloc of generation. It is 16x16x16 minecraft blocs.
 * The world (should) generate only with pre-built CubicChunk.
 */
public class CubicChunk implements Iterable<BlockData> {

    private final BlockData[][][] blockDatas;
    private final int id;
    public final RuleList<Rule> rules = new RuleList<>(LogicalOperator.AND);
    /** 0 = common (default), positive = very common, negative = rare. Please keep values in the range [-100; 100] */
    public final @Range(from = -100, to = 100) float rarity;

    private static final TreeMap<Integer, CubicChunk> instances = new TreeMap<>();

    public static final CubicChunk AIR_CHUNK;

    static {
        //noinspection ConstantConditions
        if (Bukkit.getServer() != null) {
            AIR_CHUNK = new CubicChunk(0, 50, true);
        } else {
            // here it means the class was called too early or we are in test mode
            AIR_CHUNK = new CubicChunk(0, 50, false);
        }
    }

    /**
     * New instance using {@link #nextId()}
     */
    public CubicChunk() {
        this(nextId(), 0f, true);
    }

    /**
     * @param rarity 0 = common (default), positive = very common, negative = rare. Please keep values in the range [-100; 100]
     */
    public CubicChunk(@Range(from = -100, to = 100) float rarity) {
        this(nextId(), rarity, true);
    }

    /**
     * @return The ID that the next instance will have
     */
    public static int nextId() {
        return instances.lastKey() + 1;
    }

    protected CubicChunk(int id, float rarity, boolean fillWithAir) {
        if (instances.containsKey(id))
            throw new IllegalArgumentException("That id is already taken");
        this.id = id;
        this.rarity = rarity;
        blockDatas = new BlockData[16][16][16];
        instances.put(id, this);
        if (fillWithAir)
            fill(Material.AIR);
    }

    /**
     * Fills all 4096 minecraft blocs (16x16x16) with {@link Bukkit#createBlockData(Material)}
     * @param material the {@link Material} to fill with
     */
    public void fill(Material material) {
        for (byte x = 0; x < 16; x++) {
            blockDatas[x] = new BlockData[16][16];
            for (byte y = 0; y < 16; y++) {
                blockDatas[x][y] = new BlockData[16];
                Arrays.fill(blockDatas[x][y], Bukkit.createBlockData(material));
            }
        }
    }

    public @Nullable static CubicChunk getInstance(int id) {
        return instances.get(id);
    }

    public @NotNull static CubicChunk getInstanceOrNew(int id) {
        return getInstanceOrNew(id, 0f);
    }

    public @NotNull static CubicChunk getInstanceOrNew(int id, float rarity) {
        CubicChunk candidate = instances.get(id);
        if (candidate != null) return candidate;
        return new CubicChunk(id, rarity, true);
    }

    /**
     * @return all instances of {@link CubicChunk}
     */
    public static Collection<CubicChunk> allAvailable() {
        return Collections.unmodifiableCollection(instances.values());
    }

    /**
     * @throws IndexOutOfBoundsException if the coordinates are not valid
     */
    public static void checkIndex(int x, int y, int z) {
        if (x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15)
            throw new IndexOutOfBoundsException("indexes must be be between 0 and 15");
    }

    public void set(BlockData blockData, int x, int y, int z) {
        checkIndex(x, y, z);
        blockDatas[x][y][z] = blockData;
    }

    public void set(Material material, int x, int y, int z) {
        set(Bukkit.createBlockData(material), x, y, z);
    }

    public BlockData get(int x, int y, int z) {
        checkIndex(x, y, z);
        return blockDatas[x][y][z];
    }

    public int getId() {
        return id;
    }

    /**
     * Iterates over all 4096 blocs
     */
    @NotNull
    @Override
    public Iterator<@NotNull BlockData> iterator() {
        return new Iterator<BlockData>() {
            private int x, y, z;

            @Override
            public boolean hasNext() {
                return x < 16 && y < 16 && z < 16;
            }

            @Override
            public @NotNull BlockData next() {
                if (z == 15) {
                    y++;
                    z = 0;
                }
                if (y == 15) {
                    x++;
                    y = 0;
                }
                return blockDatas[x][y][z++];
            }
        };
    }

    @Override
    public String toString() {
        return "CubicChunk{" +
                "id=" + id +
                ", rarity=" + rarity +
                '}';
    }
}
