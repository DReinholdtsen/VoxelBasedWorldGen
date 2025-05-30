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
import java.util.function.Function;

// contains helper methods for procedurally generating trees.
public class TreeDecorators {
    private static final Block logBlock = Block.DARK_OAK_WOOD;
    private static final Block leafBlock = Block.DARK_OAK_LEAVES;

    // generates the large dark oak trees found in forests and plains
    public static Structure generateTree(Point decorationPos, int seed) {
        Structure tree = new Structure();
        // generate trunk
        for (BlockVec point : largeTreeTrunk(decorationPos, seed)) {
            tree.addBlock(point, logBlock);
        }
        for (int i = 0; i < 6; i++) {
            // generate branches, i represents n value of branch
            Set<BlockVec> branch = generateBranch(decorationPos, seed, i);
            int k = 0;
            // add logs and leaves surrounding
            for (BlockVec point : generateBranch(decorationPos, seed, i)) {
                tree.addBlock(point, logBlock);
                if (k % 3 == 0) {
                    tree.addBlocks(PointUtils.ellipsoid(point, new Pos(3.5, 2, 3.5)), leafBlock);
                }
                k++;
            }
        }
        // generate leaf clusters
        // area for valid leaf clusters
        Set<BlockVec> area = PointUtils.ellipsoid(new Pos(0, 23, 0), new Pos(9, 3, 9));
        for (int i = 0; i < 20; i++) {
            // amount of generated leaf clusters
            // 25 = height
            // get random center points for each leaf cluster.
            Point point = randomFromSet(area, (int)(Integer.MAX_VALUE * PointUtils.randomFromCoordinate(seed * 12 + 7 * i, decorationPos.blockX(), decorationPos.blockY())));
            Point size = new Pos(6 * PointUtils.randomFromCoordinate(seed * 17 + i * 91, decorationPos.blockX(), decorationPos.blockZ()) + 2, 3 * PointUtils.randomFromCoordinate(seed * 17 + i * 92, decorationPos.blockX(), decorationPos.blockZ()) + 1, 6 * PointUtils.randomFromCoordinate(seed * 17 + i * 93, decorationPos.blockX(), decorationPos.blockZ()) + 2);
            tree.addBlocks(PointUtils.ellipsoid(point, size), leafBlock);
        }
        return tree;
    }
    // generates the large spruce trees in taigas
    public static Structure generateSpruceTree(Point decorationPos, int seed) {
        Structure tree = new Structure();
        // sets height and center shift for trunk
        Function<Integer, Point> centerShift = n -> spruceCenterShift(decorationPos, seed, n);
        int height = 30 + (int) (PointUtils.randomFromCoordinate(seed + 173, decorationPos.blockX(), decorationPos.blockZ()) * 10);
        // add trunk
        for (BlockVec point : spruceTreeTrunk(decorationPos, seed, centerShift, height)) {
            tree.addBlock(point, Block.SPRUCE_WOOD);
        }
        // add leaves
        for (BlockVec point : spruceLeaves(decorationPos, height, seed)) {
            tree.addBlock(point, Block.SPRUCE_LEAVES);
        }
        return tree;
    }

    // creates tree trunk for oak tree
    public static Set<BlockVec> largeTreeTrunk(Point decorationPos, int seed) {
        BiFunction<Integer, Double, Double> radiusFunction  = (height, theta) -> {
            return Math.pow(1.4, -(height))*2.2 + .6;
        };

        Function<Integer, Point> centerShift = x -> centerShift(decorationPos, seed, x);

        Set<BlockVec> points = PointUtils.verticalTube(new Pos(0, -2, 0), 27, radiusFunction, centerShift);
        return points;



    }

    // returns set of block positions of spruce tree trunk
    public static Set<BlockVec> spruceTreeTrunk(Point decorationPos, int seed, Function<Integer, Point> centerShift, int height) {
        BiFunction<Integer, Double, Double> radiusFunction  = (h, theta) -> {
            return Math.pow(1.4, -(h))*2.2 + 1;
        };


        Set<BlockVec> points = PointUtils.verticalTube(new Pos(0, -2, 0), height, radiusFunction, centerShift);
        return points;



    }

    // returns random value from a set from initial seed
    public static BlockVec randomFromSet(Set<BlockVec> set, int seed) {
        int size = set.size();
        int item = seed % size;
        int i = 0;
        for(BlockVec obj : set)
        {
            if (i == item)
                return obj;
            i++;
        }
        return null;
    }

    // generates a branch for a specific tree given initial seeding and n value (determines rotation)
    public static Set<BlockVec> generateBranch(Point decorationPos, int seed, int n) {
        // determine height and start block (from center)
        int branchHeight = (int)(17 + 8 * PointUtils.randomFromCoordinate(seed * 4 + n >> 2, decorationPos.blockX(), decorationPos.blockZ()));
        Point startBlock = new Pos(0, branchHeight, 0).add(centerShift(decorationPos, seed, branchHeight));
        // determines angle and length of branch to determine outer point
        double theta = 2 * Math.PI * n / 6 + PointUtils.randomFromCoordinate(-37 + n * 5 + seed * 2, decorationPos.blockX(), decorationPos.blockZ());
        double length = 10 * (1 + PointUtils.randomFromCoordinate(-1398 + n + seed * 3, decorationPos.blockX(), decorationPos.blockZ()));
        Point shiftVector = new Pos(length * Math.cos(theta), 0, length * Math.sin(theta));

        // represents the shift in the midpoint to curve branch
        Point shift1 = new Pos((PointUtils.randomFromCoordinate(-11 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) - .5),  (PointUtils.randomFromCoordinate(-14 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) - .5), (PointUtils.randomFromCoordinate(-18 + n * 24798 + seed, decorationPos.blockX(), decorationPos.blockZ()) -.5)).mul(4);
        Set<BlockVec> set = new HashSet<>();
        for (Point point : PointUtils.bezierCurve(startBlock, startBlock.add(shiftVector.mul(.5)).add(shift1.mul(5)), startBlock.add(shiftVector), t -> .6d)) {
            // adds all points in bezier curve to branch
            set.add(new BlockVec(point));
        }
        return set;
    }

    // takes in seeding and a height value and returns an x z point represnting the 2d shift of the trunk at that layer
    public static Point centerShift(Point decorationPos, int seed, int n) {
        double random = PointUtils.randomFromCoordinate(seed * 3 + n >> 2, decorationPos.blockX(), decorationPos.blockZ());
        // start angle (angle at n = 0) determined by seeding.
        double startAngle = 2 * Math.PI * PointUtils.randomFromCoordinate(-16, decorationPos.blockX(), decorationPos.blockZ());
        Point shift = new Pos(1.2*Math.cos(startAngle + n/5d+ random), 0, Math.sin(startAngle + n/5d + random)*1.2);

        return shift;
    }

    // takes in seeding and height and returns an x z point for 2d shift on spruce trunk.
    public static Point spruceCenterShift(Point decorationPos, int seed, int n) {
        double random = PointUtils.randomFromCoordinate(seed * 3 + n >> 2, decorationPos.blockX(), decorationPos.blockZ());
        // start angle (angle at n = 0)
        double startAngle = 2 * Math.PI * PointUtils.randomFromCoordinate(-16, decorationPos.blockX(), decorationPos.blockZ());
        // slight bias in a certain direction, makes leaning effect)
        Point leanDirection = new Pos(Math.cos(startAngle), 0, Math.sin(startAngle));
        Point shift = new Pos(.4*Math.cos(startAngle + n/5d+ random), 0, Math.sin(startAngle + n/5d + random)*.4).add(leanDirection.mul(n / 8d));

        return shift;
    }

    // returns set of block vectors of spruce leaves
    public static Set<BlockVec> spruceLeaves(Point decorationPos, int height, int seed) {
        BiFunction<Integer, Double, Double> radiusFunction = (h, theta) ->  2.5 - (double) ((h) % 3) + (20-h) / 8d;
        Function<Integer, Point> centerShift = n -> spruceCenterShift(decorationPos, seed, n + 20);

        return PointUtils.verticalTube(new Pos(0, height - 21, 0), 21, radiusFunction, centerShift);
    }
}
