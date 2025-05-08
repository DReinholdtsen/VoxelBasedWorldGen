package org.example.TerrainGeneration;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
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
        return curvePoints;
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
        return points;
    }

    // returns a set of points (roughly cylindrical) from bottom pos up by height
    // radiusFunction represents the radius at a specific height (arg 1) and theta (arg 2)
    public static Set<BlockVec> verticalTube(Point bottomPos, int height, BiFunction<Integer, Double, Double> radiusFunction) {
        Set<BlockVec> points = new HashSet<BlockVec>();
        for (int currentHeight = 0; currentHeight < height; currentHeight++) {
            for (int t = 0; t < 2500; t++) {
                // get radius from function, plugging in height and theta (radians)
                double theta = (double) t / 2500 * Math.TAU;
                double radius = radiusFunction.apply(currentHeight, theta);
                Point outerPoint = bottomPos.add(radius * Math.cos(theta), currentHeight, radius * Math.sin(theta));
                points.addAll(line(bottomPos.add(0, currentHeight, 0), outerPoint));

            }
        }
        return points;
    }

    public static double randomFromCoordinate(int seed, int x, int z) {
         long h = seed * 374761393 + x * 668265263 + z * 2246822519L;
         h = (h ^ (h >> 13)) * 1274126177;
         h = h ^ (h >> 16);
         long unsigned = Math.abs(h);
         return (unsigned & 0xFFFFFFFFL) / (double)(1L << 32);
    }

    // get all points on a line between two points
    public static Set<BlockVec> line(Point p1, Point p2) {
        Set<BlockVec> voxels = new HashSet<>();

        int x1 = p1.blockX(), y1 = p1.blockY(), z1 = p1.blockZ();
        int x2 = p2.blockX(), y2 = p2.blockY(), z2 = p2.blockZ();

        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);
        int xs = Integer.compare(x2, x1), ys = Integer.compare(y2, y1), zs = Integer.compare(z2, z1);

        int p1x = 2 * dy - dx;
        int p1y = 2 * dz - dy;
        int p1z = 2 * dx - dz;

        int x = x1, y = y1, z = z1;

        int max = Math.max(dx, Math.max(dy, dz));

        for (int i = 0; i <= max; i++) {
            voxels.add(new BlockVec(x, y, z));

            if (p1x >= 0) {
                y += ys;
                p1x -= 2 * dx;
            }
            if (p1y >= 0) {
                z += zs;
                p1y -= 2 * dy;
            }
            if (p1z >= 0) {
                x += xs;
                p1z -= 2 * dz;
            }

            p1x += 2 * dy;
            p1y += 2 * dz;
            p1z += 2 * dx;
        }
        /*
        BlockVec[] points = new BlockVec[voxels.size()];
        int i = 0;
        for (BlockVec voxel : voxels) {
            points[i] = voxel;
            i++;
        }
         */
        return voxels;
    }

}
