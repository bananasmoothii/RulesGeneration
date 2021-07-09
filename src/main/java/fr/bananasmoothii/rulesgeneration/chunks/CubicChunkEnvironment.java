package fr.bananasmoothii.rulesgeneration.chunks;

import fr.bananasmoothii.rulesgeneration.suggestions.SuggestionList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CubicChunkEnvironment implements Iterable<CubicChunkCoords> {

    public final Random random;
    private static final HashMap<Random, CubicChunkEnvironment> instances = new HashMap<>();
    private static int enlargeAtOnce = 10;

    private CubicChunk[][][] cubicChunks;
    private int xOffset, yOffset, zOffset,
                xMin, yMin, zMin, // inclusive
                xMax, yMax, zMax, // inclusive too
                xArraySize, yArraySize, zArraySize;

    public CubicChunkEnvironment() {
        this(new Random());
    }

    public CubicChunkEnvironment(Random random) {
        this(random, 16);
    }

    public CubicChunkEnvironment(Random random, int ySize) {
        this(random, ySize, 50);
    }

    public CubicChunkEnvironment(Random random, int ySize, int size) {
        instances.put(random, this);
        this.random = random;
        xArraySize = size; yArraySize = ySize; zArraySize = size;
        cubicChunks = new CubicChunk[xArraySize][ySize][zArraySize];
        int halfSize = size / 2;
        xOffset = halfSize; yOffset = 0; zOffset = halfSize;
    }

    public static CubicChunkEnvironment withSeed(Random random) {
        return withSeed(random, 16);
    }

    public static CubicChunkEnvironment withSeed(Random random, int ySize) {
        return withSeed(random, ySize, 50);
    }

    public static CubicChunkEnvironment withSeed(Random random, int ySize, int size) {
        @Nullable CubicChunkEnvironment candidate = instances.get(random);
        if (candidate != null) return candidate;
        else return new CubicChunkEnvironment(random, ySize, size);
    }

    public @Nullable CubicChunk get(int x, int y, int z) {
        try {
            return cubicChunks[x + xOffset][y + yOffset][z + zOffset];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void set(@Nullable CubicChunk chunk, int x, int y, int z) {
        ensureCapacityForElement(x, y, z);
        cubicChunks[x + xOffset][y + yOffset][z + zOffset] = chunk;
    }

    public void set(CubicChunkCoords chunkCoords) {
        set(chunkCoords.cubicChunk, chunkCoords.x, chunkCoords.y, chunkCoords.z);
    }

    public void ensureCapacityForElement(int x, int y, int z) {
        if (x < xMin) {
            enlargeX(x - xMin); // = -(xMin - x)
        } else if (x > xMax) {
            enlargeX(x - xMax);
        }
        if (y < yMin) {
            enlargeY(y - yMin); // = -(yMin - y)
        } else if (y > xMax) {
            enlargeY(y - yMax);
        }
        if (z < zMin) {
            enlargeZ(z - zMin); // = -(zMin - z)
        } else if (z > zMax) {
            enlargeZ(z - zMax);
        }
    }

    void enlargeX(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (xMin + addingSpace + xOffset < 0) {
                CubicChunk[][][] old = cubicChunks;
                cubicChunks = new CubicChunk[xArraySize + totalAddingSpace][yArraySize][zArraySize];
                System.arraycopy(old, 0, cubicChunks, totalAddingSpace, xArraySize);
                xArraySize += totalAddingSpace;
                xOffset += totalAddingSpace;
            }
            xMin += addingSpace; // so xMin -= abs(addingSpace)
        } else if (addingSpace > 0) {
            if (xMax + addingSpace + xOffset >= xArraySize) {
                CubicChunk[][][] old = cubicChunks;
                cubicChunks = new CubicChunk[xArraySize + totalAddingSpace][yArraySize][zArraySize];
                System.arraycopy(old, 0, cubicChunks, 0, xArraySize);
                xArraySize += totalAddingSpace;
            }
            xMax += addingSpace;
        }
    }

    void enlargeY(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (yMin + addingSpace + yOffset < 0) {
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
            if (yMax + addingSpace + yOffset >= yArraySize) {
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

    void enlargeZ(final int addingSpace) {
        final int totalAddingSpace = Math.abs(addingSpace) + enlargeAtOnce;
        if (addingSpace < 0) {
            if (zMin + addingSpace + zOffset < 0) {
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
            if (zMax + addingSpace + zOffset >= zArraySize) {
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
    @Override
    public Iterator<CubicChunkCoords> iterator() {
        return new Iterator<CubicChunkCoords>() {
            private int currentX = xMin, currentY = yMin, currentZ = zMin;

            @Override
            @Contract(pure = true)
            public boolean hasNext() {
                return currentZ < zMax && currentY < yMax && currentX < xMax;
            }

            @Override
            public CubicChunkCoords next() {
                if (++currentZ >= zMax) {
                    currentZ = zMin;
                    currentY++;
                }
                if (currentY >= yMax) {
                    currentY = yMin;
                    currentX++;
                }
                if (currentX >= xMax) throw new NoSuchElementException();
                CubicChunk c = get(currentX, currentY, currentZ);
                return new CubicChunkCoords(c, currentX, currentY, currentZ);
            }
        };
    }

    public void generate(int xFrom, int yFrom, int zFrom, int xTo, int yTo, int zTo) {
        if (xTo <= xFrom || yTo <= yFrom || zTo <= zFrom) throw new IllegalArgumentException("To coordinates must be greater than From coordinates");
        int xMiddle = (xFrom + xTo) / 2, yMiddle = (yFrom + yTo) / 2, zMiddle = (zFrom + zTo) / 2;
        regenerate(xMiddle, yMiddle, zMiddle);

        int maxRadius = xTo - xFrom;
        {
            int tempR = yTo - yFrom;
            if (tempR > maxRadius) maxRadius = tempR;
            tempR = zTo - zFrom;
            if (tempR > maxRadius) maxRadius = tempR;
        } // we don't need tempR anymore
        // divide by 2 and round up
        if (maxRadius % 2 == 1) maxRadius++;
        maxRadius >>= 1; // maxRadius = maxRadius/2

        for (int currentRadius = 1; currentRadius <= maxRadius; currentRadius++) {
            int xMinBound = xMiddle - currentRadius, // all inclusive
                xMaxBound = xMiddle + currentRadius,
                yMinBound = yMiddle - currentRadius,
                yMaxBound = yMiddle + currentRadius,
                zMinBound = zMiddle - currentRadius,
                zMaxBound = zMiddle + currentRadius;
            for (int x = xMinBound; x <= xMaxBound; x++) {
                for (int y = yMinBound; y <= yMaxBound; y++) {
                    if (x == xMinBound || x == xMaxBound || y == yMinBound || y == yMaxBound) {
                        for (int z = zMinBound; z <= zMaxBound; z++) {
                            generate(x, y, z);
                        }
                    } else {
                        generate(x, y, zMinBound);
                        generate(x, y, zMaxBound);
                    }
                }
            }
        }
    }

    public void regenerate(int x, int y, int z) {
        CubicChunk[] allAvailable = CubicChunk.allAvailable().toArray(new CubicChunk[0]);
        CubicChunk chunk = allAvailable[random.nextInt(allAvailable.length)];
        set(chunk, x, y, z);
        chunk.rules.apply(this, x, y, z);
    }

    public void generate(int x, int y, int z) {
        if (get(x, y, z) == null) regenerate(x, y, z);
    }

    public void validateAll() {
        for (CubicChunkCoords cubicChunkCoords : this) {
            if (cubicChunkCoords.cubicChunk != null) {
                cubicChunkCoords.cubicChunk.rules.apply(this, cubicChunkCoords.x, cubicChunkCoords.y, cubicChunkCoords.z);
            }
        }
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
