package fr.bananasmoothii.rulesgeneration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CubicChunkEnvironment implements Iterable<@Nullable CubicChunk> {

    private final long seed;
    private static final HashMap<Long, CubicChunkEnvironment> instances = new HashMap<>();
    private static int enlargeAtOnce = 10;

    private CubicChunk[][][] cubicChunks;
    private int xOffset, yOffset, zOffset,
                xMin, yMin, zMin, // exclusive
                xMax, yMax, zMax, // inclusive
                xArraySize, yArraySize, zArraySize;

    public CubicChunkEnvironment(long seed) {
        this(seed, 50);
    }

    public CubicChunkEnvironment(long seed, int size) {
        this(seed, size, 16);
    }

    public CubicChunkEnvironment(long seed, int size, int ySize) {
        instances.put(seed, this);
        this.seed = seed;
        xArraySize = size; yArraySize = ySize; zArraySize = size;
        cubicChunks = new CubicChunk[xArraySize][ySize][zArraySize];
        int halfSize = size / 2;
        xOffset = halfSize; yOffset = ySize / 2; zOffset = halfSize;
    }

    public @Nullable CubicChunk get(int x, int y, int z) {
        try {
            return cubicChunks[x + xOffset][y + yOffset][z + zOffset];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void set(CubicChunk chunk, int x, int y, int z) {
        ensureCapacityForElement(x, y, z);
        cubicChunks[x + xOffset][y + yOffset][z + zOffset] = chunk;
    }

    public void ensureCapacityForElement(int x, int y, int z) {
        if (x < xMin) {
            enlargeX(x - xMin); // = -(xMin - x)
        } else if (x > xMax) {
            enlargeX(x - xMax);
        }
        if (y < yMin) {
            enlargeY(y - yMin); // = -(yMin - y)
        } else if (x > xMax) {
            enlargeY(y - yMax);
        }
        if (z < zMin) {
            enlargeZ(z - zMin); // = -(zMin - z)
        } else if (z > zMax) {
            enlargeZ(z - zMax);
        }
    }

    private void enlargeX(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (xMin + addingSpace < 0) {
                CubicChunk[][][] old = cubicChunks;
                cubicChunks = new CubicChunk[xArraySize + totalAddingSpace][yArraySize][zArraySize];
                System.arraycopy(old, 0, cubicChunks, totalAddingSpace, xArraySize);
                xArraySize += totalAddingSpace;
                xOffset += totalAddingSpace;
            }
            xMin += addingSpace; // so xMin -= abs(addingSpace)
        } else if (addingSpace > 0) {
            if (xMax + addingSpace > xArraySize) {
                CubicChunk[][][] old = cubicChunks;
                cubicChunks = new CubicChunk[xArraySize + totalAddingSpace][yArraySize][zArraySize];
                System.arraycopy(old, 0, cubicChunks, 0, xArraySize);
                xArraySize += totalAddingSpace;
            }
            xMax += addingSpace;
        }
    }

    private void enlargeY(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (yMin + addingSpace < 0) {
                for (int x = 0; x < xArraySize; x++) {
                    CubicChunk[][] old = cubicChunks[x];
                    cubicChunks[x] = new CubicChunk[yArraySize + totalAddingSpace][zArraySize];
                    System.arraycopy(old, 0, cubicChunks[x], totalAddingSpace, yArraySize);
                }
                yArraySize += totalAddingSpace;
                yOffset += totalAddingSpace;
            }
            yMin += addingSpace;
        } else if (addingSpace > 0) {
            if (yMax + addingSpace > yArraySize) {
                for (int x = 0; x < xArraySize; x++) {
                    CubicChunk[][] old = cubicChunks[x];
                    cubicChunks[x] = new CubicChunk[yArraySize + totalAddingSpace][zArraySize];
                    System.arraycopy(old, 0, cubicChunks[x], 0, yArraySize);
                }
                yArraySize += totalAddingSpace;
            }
            yMax += addingSpace;
        }
    }

    private void enlargeZ(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (zMin + addingSpace < 0) {
                for (int x = 0; x < xArraySize; x++) {
                    for (int y = 0; y < yArraySize; y++) {
                        CubicChunk[] old = cubicChunks[x][y];
                        cubicChunks[x][y] = new CubicChunk[zArraySize + totalAddingSpace];
                        System.arraycopy(old, 0, cubicChunks[x][y], totalAddingSpace, zArraySize);
                    }
                }
                zArraySize += totalAddingSpace;
                zOffset += totalAddingSpace;
            }
            zMin -= addingSpace;
        } else if (addingSpace > 0) {
            if (zMax + addingSpace > zArraySize) {
                for (int x = 0; x < xArraySize; x++) {
                    for (int y = 0; y < yArraySize; y++) {
                        CubicChunk[] old = cubicChunks[x][y];
                        cubicChunks[x][y] = new CubicChunk[zArraySize + totalAddingSpace];
                        System.arraycopy(old, 0, cubicChunks[x][y], 0, zArraySize);
                    }
                }
                zArraySize += totalAddingSpace;
            }
            zMax += addingSpace;
        }
    }

    /**
     * when the arrays need to be enlarged, it will directly grow for example 10, so it hasn't to copy arrays each time.
     */
    public static void setEnlargeAtOnce(int enlargeAtOnce) {
        if (enlargeAtOnce < 1) throw new IllegalArgumentException("enlargeAtOnce must be >= 1");
        CubicChunkEnvironment.enlargeAtOnce = enlargeAtOnce;
    }

    /**
     * @see #setEnlargeAtOnce(int)
     */
    public static int getEnlargeAtOnce() {
        return enlargeAtOnce;
    }

    @NotNull
    @Override // TODO
    public Iterator<@Nullable CubicChunk> iterator() {
        return new Iterator<@Nullable CubicChunk>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public CubicChunk next() {
                return null;
            }
        };
    }

    public void debugPrint() {
        System.out.println("xMin = " + xMin + " ;  xMax = " + xMax + " ;  yMin = " + yMin + " ;  yMax = " + yMax);
        for (int x = xMin + xOffset; x <= xMax + xOffset; x++) {
            for (int y = yMin + yOffset ; y <= yMax + yOffset; y++) {
                @Nullable CubicChunk chunk = cubicChunks[x][y][zOffset];
                if (chunk != null) {
                    int id = chunk.getId();
                    System.out.print(id < 10 && id >= 0 ? " " + id : String.valueOf(id));
                } else {
                    System.out.print("  ");
                }
                System.out.print(' ');
            }
            System.out.print('\n');
        }
    }
}
