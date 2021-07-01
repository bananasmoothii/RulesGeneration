package fr.bananasmoothii.rulesgeneration.chunks;

import fr.bananasmoothii.rulesgeneration.rules.Rule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CubicChunk implements Iterable<BlockData> {

    private final BlockData[][][] blockDatas;
    private final int id;
    private int invertedGenerationChance;
    public final List<Rule> rules = new ArrayList<>();
    /** 0 = common (default), positive = very common, negative = rare */
    public final double rarity;

    private static final HashMap<Integer, CubicChunk> instances = new HashMap<>();

    public static final CubicChunk AIR_CHUNK = new CubicChunk(0);

    public CubicChunk(int id) {
        this(id, 0d);
    }

    public CubicChunk(int id, double rarity) {
        if (instances.containsKey(id))
            throw new IllegalArgumentException("That id is already taken");
        instances.put(id, this);
        this.id = id;
        this.rarity = rarity;
        blockDatas = new BlockData[16][16][16];
        fill(Material.AIR);
    }

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
        CubicChunk candidate = instances.get(id);
        if (candidate != null) return candidate;
        return new CubicChunk(id);
    }

    public static Collection<CubicChunk> allAvailable() {
        return Collections.unmodifiableCollection(instances.values());
    }

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

    public int getInvertedGenerationChance() {
        return invertedGenerationChance;
    }

    public @NotNull CubicChunk setInvertedGenerationChance(int invertedGenerationChance) {
        this.invertedGenerationChance = invertedGenerationChance;
        return this;
    }
}
