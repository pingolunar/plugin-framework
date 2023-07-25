package mine.plugins.lunar.plugin_framework.border;

import lombok.AllArgsConstructor;
import org.joml.Vector2d;

import java.util.List;

import static mine.plugins.lunar.plugin_framework.border.BorderManager.localChunkBorder;
import static mine.plugins.lunar.plugin_framework.border.BorderManager.localChunkLineBorderSize;

@AllArgsConstructor
public enum RectangleBorder {

    DOWN(localChunkBorder.stream().limit(localChunkLineBorderSize).toList(),
            new Vector2d(0, -1)),
    UP(localChunkBorder.stream().skip(localChunkLineBorderSize * 2L).limit(localChunkLineBorderSize).toList(),
            new Vector2d(0, 1)),
    LEFT(localChunkBorder.stream().skip(localChunkLineBorderSize * 3L).toList(),
            new Vector2d(-1, 0)),
    RIGHT(localChunkBorder.stream().skip(localChunkLineBorderSize).limit(localChunkLineBorderSize).toList(),
            new Vector2d(1, 0));

    public final List<Vector2d> localLineBorder;
    public final Vector2d chunkRelativePos;

    public RectangleBorder getOpposite() {
        return switch (this) {
            case DOWN -> UP;
            case UP -> DOWN;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
