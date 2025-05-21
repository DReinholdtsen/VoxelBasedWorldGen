package org.example.TerrainGeneration;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PointUtils {
    public static final Function<Point, Double> EUCLIDEAN = point -> point.distanceSquared(Pos.ZERO);
    public static final Function<Point, Double> CUBE = point -> Math.max(Math.max(point.x(), point.y()), point.z());
    public static Set<Point> bezierCurve(Point point1, Point point2, Point point3) {
        return bezierCurve(point1, point2, point3, t -> 1d, EUCLIDEAN);
    }
    public static Set<Point> bezierCurve(Point point1, Point point2, Point point3, Function<Double, Double> thicknessFunction) {
        return bezierCurve(point1, point2, point3, thicknessFunction, EUCLIDEAN);
    }
    // returns a set of block vecs of a quadratic bezier curve
    // thickness function represents radius of curve at specific t value
    // distanceMethod: represents type of distance, true -> euclidean, false -> manhattan
    public static Set<Point> bezierCurve(Point point1, Point point2, Point point3, Function<Double, Double> thicknessFunction, Function<Point, Double> distanceFunction) {
        Set<Point> points = new HashSet<Point>();
        for (int i = 0; i < 100; i++) {
            double t = (double) i / 100;
            Point curvePoint = bezierPoint(t, point1, point2, point3);
            double radius = thicknessFunction.apply(t);
            BlockVec cubeOffset = new BlockVec((int) radius + 1, (int) radius + 1, (int) radius + 1);
            double radiusSquared = Math.pow(radius, 2);
            // check points in cube with radius from thickness function
            for (BlockVec pointInCube : rectangularPrism(curvePoint.add(cubeOffset), curvePoint.sub(cubeOffset))) {
                Point difference = pointInCube.sub(curvePoint);
                double distance = distanceFunction.apply(difference);
                double distanceSquared = Math.pow(distance, 2);
                if (distanceSquared <= radiusSquared) {
                    // point within distance from curve
                    points.add(pointInCube);
                }
            }
        }
        return points;
    }


    public static Point bezierPoint(double t, Point p0, Point p1, Point p2)
    {
        Point p = new Pos(p0.mul(Math.pow(1-t, 2)).add(p1.mul(2 * (1-t) * t)).add(p2.mul(t*t)));
        //System.out.println(p);
        return p;
    }

    public static BlockVec[] rectangularPrism(Point p1, Point p2) {
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
    // centerShift is a function that returns a point that shifts the center of the shape based on height
    public static Set<BlockVec> verticalTube(Point bottomPos, int height, BiFunction<Integer, Double, Double> radiusFunction, Function<Integer, Point>  centerShift) {
        Set<BlockVec> points = new HashSet<BlockVec>();
        // adjust to center of block
        bottomPos = bottomPos.add(.5, 0, .5);
        for (int currentHeight = 0; currentHeight < height; currentHeight++) {
            Point innerPoint = bottomPos.add(centerShift.apply(currentHeight)).add(0, currentHeight, 0);
            for (int t = 0; t < 125; t++) {
                // get radius from function, plugging in height and theta (radians)
                double theta = (double) t / 125 * Math.TAU;
                double radius = radiusFunction.apply(currentHeight, theta);

                Point outerPoint = innerPoint.add(radius * Math.cos(theta), 0, radius * Math.sin(theta));

                if (!points.contains(outerPoint)) {
                    points.add(new BlockVec(outerPoint.blockX(), outerPoint.blockY(), outerPoint.blockZ()));
                    points.addAll(line(innerPoint, outerPoint));
                }


            }
        }
        return points;
    }
    public static Set<BlockVec> verticalTube(Point bottomPos, int height, BiFunction<Integer, Double, Double> radiusFunction) {
        return verticalTube(bottomPos, height, radiusFunction, h -> Pos.ZERO);
    }

    // Returns a set of all voxels contained on a line between two points
    public static Set<BlockVec> line(Point p1, Point p2) {
        // 1) shift both points up 0.5 so we're shooting through block-centers vertically
        p1 = p1.add(0, 0.5, 0);
        p2 = p2.add(0, 0.5, 0);

        // 2) get starting and ending voxel coords
        int x     = p1.blockX(),  y     = p1.blockY(),  z     = p1.blockZ();
        int endX  = p2.blockX(),  endY  = p2.blockY(),  endZ  = p2.blockZ();

        Set<BlockVec> voxels = new HashSet<>();
        voxels.add(new BlockVec(x, y, z));

        // 3) parametric deltas
        double dx = p2.x() - p1.x();
        double dy = p2.y() - p1.y();
        double dz = p2.z() - p1.z();

        // 4) integer step directions
        int stepX = dx > 0 ?  1 : (dx < 0 ? -1 : 0);
        int stepY = dy > 0 ?  1 : (dy < 0 ? -1 : 0);
        int stepZ = dz > 0 ?  1 : (dz < 0 ? -1 : 0);

        // 5) how far (in t) between each boundary crossing
        final double INF = Double.POSITIVE_INFINITY;
        double tDeltaX = dx != 0 ? 1.0/Math.abs(dx) : INF;
        double tDeltaY = dy != 0 ? 1.0/Math.abs(dy) : INF;
        double tDeltaZ = dz != 0 ? 1.0/Math.abs(dz) : INF;

        // 6) where along t∈[0,1] we hit the first X-boundary, Y-boundary, Z-boundary
        double tMaxX = dx != 0
                ? ((stepX > 0 ? (x + 1) - p1.x() : p1.x() - x) * tDeltaX)
                : INF;
        double tMaxY = dy != 0
                ? ((stepY > 0 ? (y + 1) - p1.y() : p1.y() - y) * tDeltaY)
                : INF;
        double tMaxZ = dz != 0
                ? ((stepZ > 0 ? (z + 1) - p1.z() : p1.z() - z) * tDeltaZ)
                : INF;

        // 7) march!
        while (x != endX || y != endY || z != endZ) {
            // pick the smallest tMax — but prefer X over Y over Z on ties
            if (tMaxX <= tMaxY && tMaxX <= tMaxZ) {
                x += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY <= tMaxZ) {
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }
            voxels.add(new BlockVec(x, y, z));
        }

        return voxels;
    }


    public static double randomFromCoordinate(int seed, int x, int z) {
         long h = seed * 374761393 + x * 668265263 + z * 2246822519L;
         h = (h ^ (h >> 13)) * 1274126177;
         h = h ^ (h >> 16);
         long unsigned = Math.abs(h);
         return (unsigned & 0xFFFFFFFFL) / (double)(1L << 32);
    }

    // returns the L_p norm with specified p value. p = 1 -> Manhattan, p = 2 -> Euclidean, p = inf -> cube
    public static Function<Point, Double> L_P_norm(int p) {
        return point -> Math.pow(Math.abs(Math.pow(point.x(), p)) + Math.abs(Math.pow(point.y(), p)) + Math.abs(Math.pow(point.z(), p)), 1d/p);
    }
}
