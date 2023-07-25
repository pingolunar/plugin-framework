package mine.plugins.lunar.plugin_framework.border;

import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BorderManager {

    public static Double[] getLineBorder(double min, double max, double spacing) {
        var xAmount = (int) Math.floor((max-min) / spacing) + 1;
        return IntStream.range(0, xAmount).mapToObj(i -> min+i*spacing).toArray(Double[]::new);
    }

    public static final int chunkSize = 16;

    static final List<Vector2d> localChunkBorder =
            BorderManager.getRectangleBorder(0, chunkSize, 0, chunkSize, 1);

    static final int localChunkLineBorderSize = localChunkBorder.size() / 4;

    /**
     * Starts from local (0, 0)
     */
    public static List<Vector2d> getRectangleBorder(double xMin, double xMax, double zMin, double zMax,
                                                    double spacing) {

        var x = getLineBorder(xMin, xMax, spacing);
        var z = getLineBorder(zMin, zMax, spacing);

        var borderLocations = new ArrayList<Vector2d>(2*(x.length-1)+2*(z.length-1));

        borderLocations.addAll(IntStream.range(0, x.length-1)
                .mapToObj(i -> new Vector2d(x[i], z[0])).toList());
        borderLocations.addAll(IntStream.range(0, z.length-1)
                .mapToObj(i -> new Vector2d(x[x.length-1], z[i])).toList());
        borderLocations.addAll(IntStream.iterate(x.length-1, i -> i - 1).limit(x.length-1)
                .mapToObj(i -> new Vector2d(x[i], z[z.length-1])).toList());
        borderLocations.addAll(IntStream.iterate(z.length-1, i -> i - 1).limit(z.length-1)
                .mapToObj(i -> new Vector2d(x[0], z[i])).toList());


        return borderLocations;
    }

    /**
     * Starts from 0 degrees
     */
    public static List<Vector2d> getCircleBorder(double radius, int particleAmount) {
        var borderLocations = new ArrayList<Vector2d>(particleAmount);

        var angleSpacing = 2 * Math.PI / particleAmount;

        IntStream.range(0, particleAmount).forEach(i -> {
            var particleAngle = angleSpacing * i;
            borderLocations.add(new Vector2d(Math.cos(particleAngle) * radius, Math.sin(particleAngle) * radius));
        });

        return borderLocations;
    }
}
