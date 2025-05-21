package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import org.example.TerrainGeneration.PointUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.example.TerrainGeneration.PointUtils.rectangularPrism;

public class TreeDecorators {
/*
    public static Consumer<Block.Setter> largeTreeSetter(Point decorationPos, int seed) {

        return setter -> {

            for (int y = 0; y < height; y++) {
                setter.setBlock(decorationPos.add(0, y, 0), Block.OAK_LOG);
            }
            for (int x = -2; x <= 2; x++) {
                // x coordinate for leaves relative to trunk
                for (int z = -2; z <= 2; z++) {
                    // same thing
                    // check to make sure not overriding trunk
                    int startHeight = height - 3;
                    int maxHeight = height + 1;
                    if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                        // no corners
                        continue;
                    }
                    if (x == 0 && z == 0) {
                        startHeight = height;
                        maxHeight += 1;
                    }
                    if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                        maxHeight -= 1;
                    }
                    for (int y = startHeight; y < maxHeight; y++) {
                        setter.setBlock(decorationPos.add(x, y, z), Block.OAK_LEAVES);
                    }

                }
            }
        };
    }*/


    public static Set<BlockVec> largeTreeTrunk(Point decorationPos, int seed) {
        BiFunction<Integer, Double, Double> radiusFunction  = (height, theta) -> {
            return Math.pow(1.4, -height)*2.2 + .6;
        };

        Function<Integer, Point> centerShift = x -> centerShift(decorationPos, seed, x);

        Set<BlockVec> points = PointUtils.verticalTube(decorationPos, 25, radiusFunction, centerShift);
        Set<BlockVec> newPoints = new HashSet<BlockVec>();
        for (int i = 0; i < 6; i++) {
            newPoints.addAll(generateBranch(decorationPos, seed, i));
        }
        points.addAll(newPoints);
        return points;



    }

    public static BlockVec randomFromSet(Set<BlockVec> set) {
        int size = set.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(BlockVec obj : set)
        {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }
    public static Set<BlockVec> generateBranch(Point decorationPos, int seed, int n) {
        int height = 25; // hard coded height
        int branchHeight = (int)(17 + 8 * PointUtils.randomFromCoordinate(seed * 4 + n >> 2, decorationPos.blockX(), decorationPos.blockZ()));
        Point startBlock = decorationPos.add(0, branchHeight, 0).add(centerShift(decorationPos, seed, branchHeight));
        //double theta = 2 * Math.PI * PointUtils.randomFromCoordinate(-35 + n + seed * 3, decorationPos.blockX(), decorationPos.blockZ());
        double theta = 2 * Math.PI * n / 6 + PointUtils.randomFromCoordinate(-37 + n * 5 + seed * 2, decorationPos.blockX(), decorationPos.blockZ());
        double length = 10 * (1 + PointUtils.randomFromCoordinate(-1398 + n + seed * 3, decorationPos.blockX(), decorationPos.blockZ()));
        Point shiftVector = new Pos(length * Math.cos(theta), 0, length * Math.sin(theta));

        Point shift1 = new Pos((PointUtils.randomFromCoordinate(-11 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) - .5),  (PointUtils.randomFromCoordinate(-14 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) - .5), (PointUtils.randomFromCoordinate(-18 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) -.5)).mul(4);
        Set<BlockVec> set = new HashSet<>();
        for (Point point : PointUtils.bezierCurve(startBlock, startBlock.add(shiftVector.mul(.5)).add(shift1.mul(5)), startBlock.add(shiftVector), t -> .6d)) {
            set.add(new BlockVec(point));
        }
        return set;
    }

    public static Point centerShift(Point decorationPos, int seed, int n) {
        double random = PointUtils.randomFromCoordinate(seed * 3 + n >> 2, decorationPos.blockX(), decorationPos.blockZ());
        double startAngle = 2 * Math.PI * PointUtils.randomFromCoordinate(-16, decorationPos.blockX(), decorationPos.blockZ());
        Point shift = new Pos(1.2*Math.cos(startAngle + n/5d+ random), 0, Math.sin(startAngle + n/5d + random)*1.2);

        return shift;
    }

    public static Set<BlockVec> leaves(Point decorationPos, int seed) {
        Set<BlockVec> points = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            Set<BlockVec> branch = generateBranch(decorationPos, seed, i);
            for (BlockVec point : branch) {
                int radius = 3;
                BlockVec cubeOffset = new BlockVec((int) radius + 1, (int) radius + 1, (int) radius + 1);
                double radiusSquared = Math.pow(radius, 2);
                for (BlockVec pointInCube : rectangularPrism(point.add(cubeOffset), point.sub(cubeOffset))) {
                    Point difference = pointInCube.sub(point);
                    double distanceSquared = difference.distanceSquared(Pos.ZERO);
                    if (distanceSquared <= radiusSquared) {
                        // point within distance from curve
                        points.add(pointInCube);
                    }
                }
            }
        }
        return points;
    }
}
