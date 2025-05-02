package org.example.TerrainGeneration;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PointUtils {
    public static Set<Point> bezierCurve(Point point1, Point point2, Point point3) {
        return bezierCurve(point1, point2, point3, t -> 0d);
    }
    // returns a set of block vecs of a quadratic bezier curve
    // thickness function represents radius of curve at specific t value
    public static Set<Point> bezierCurve(Point point1, Point point2, Point point3, Function<Double, Double> thicknessFunction) {
        Set<Point> curvePoints = new HashSet<Point>();
        for (int i = 0; i < 100; i++) {
            curvePoints.add(bezierPoint((double) i / 100, point1, point2, point3));
        }
        // points after thickness
        Set<Point> points = new HashSet<Point>();
        for (Point point : curvePoints) {

        }
        return points;
    }

    public static Point bezierPoint(double t, Point p0, Point p1, Point p2)
    {
        Point p = new BlockVec(p0.mul(Math.pow(1-t, 2)).add(p1.mul(2 * (1-t) * t)).add(p2.mul(t*t)));
        //System.out.println(p);
        return p;
    }

    public static BlockVec[] rectangularPrism(BlockVec p1, BlockVec p2) {
        int minX = Math.min(p1.blockX(), p2.blockX());
        int minY = Math.min(p1.blockY(), p2.blockY());
        int minZ = Math.min(p1.blockZ(), p2.blockZ());
        int maxX = Math.max(p1.blockX(), p2.blockX());
        int maxY = Math.max(p1.blockY(), p2.blockY());
        int maxZ = Math.max(p1.blockZ(), p2.blockZ());

        BlockVec[] points = new BlockVec[(maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1)];
        int i = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    points[i] = new BlockVec(x, y, z);
                    i++;
                }
            }
        }
    }
}
