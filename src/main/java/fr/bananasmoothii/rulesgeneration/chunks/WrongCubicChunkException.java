package fr.bananasmoothii.rulesgeneration.chunks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrongCubicChunkException extends RuntimeException {
    private final @NotNull String present;
    private final CubicChunk[] shouldBe;

    public WrongCubicChunkException(@Nullable CubicChunk present, CubicChunk... shouldBe) {
        this.present = present == null ? "nothing" : present.toString();
        this.shouldBe = shouldBe;
    }

    public WrongCubicChunkException(String message, @Nullable CubicChunk present, CubicChunk... shouldBe) {
        super(message);
        this.present = present == null ? "nothing" : present.toString();
        this.shouldBe = shouldBe;
    }

    @Override
    public String getMessage() {
        StringBuilder expected = new StringBuilder();
        if (shouldBe.length != 0) {
            for (CubicChunk cubicChunk : shouldBe) {
                expected.append(cubicChunk)
                        .append(", ");
            }
            expected.setLength(expected.length() - 2);
        }
        return present + " was found but expected " + expected + '.' + (super.getMessage() == null ? "" : ' ' + super.getMessage());
    }
}
