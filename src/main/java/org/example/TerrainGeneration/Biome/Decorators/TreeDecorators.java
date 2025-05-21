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

    public static Structure generateTree(Point decorationPos, int seed) {
        Structure tree = new Structure();
        for (BlockVec point : largeTreeTrunk(decorationPos, seed)) {
            tree.addBlock(point, Block.OAK_LOG);
        }
        for (int i = 0; i < 6; i++) {
            // generate branches, i represents n value
            Set<BlockVec> branch = generateBranch(decorationPos, seed, i);
            int k = 0;
            // add logs and leaves surrounding
            for (BlockVec point : generateBranch(decorationPos, seed, i)) {
                tree.addBlock(point, Block.OAK_LOG);
                if (k % 3 == 0) {
                    tree.addBlocks(PointUtils.ellipsoid(point, new Pos(3.5, 2, 3.5)), Block.OAK_LEAVES);
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
            Point point = randomFromSet(area);
            Point size = new Pos(6 * PointUtils.randomFromCoordinate(seed * 17 + i * 91, decorationPos.blockX(), decorationPos.blockZ()) + 2, 3 * PointUtils.randomFromCoordinate(seed * 17 + i * 92, decorationPos.blockX(), decorationPos.blockZ()) + 1, 6 * PointUtils.randomFromCoordinate(seed * 17 + i * 93, decorationPos.blockX(), decorationPos.blockZ()) + 2);
            tree.addBlocks(PointUtils.ellipsoid(point, size), Block.OAK_LEAVES);
        }
        return tree;
    }

    public static Set<BlockVec> largeTreeTrunk(Point decorationPos, int seed) {
        BiFunction<Integer, Double, Double> radiusFunction  = (height, theta) -> {
            return Math.pow(1.4, -(height))*2.2 + .6;
        };

        Function<Integer, Point> centerShift = x -> centerShift(decorationPos, seed, x);

        Set<BlockVec> points = PointUtils.verticalTube(new Pos(0, -2, 0), 27, radiusFunction, centerShift);
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
        Point startBlock = new Pos(0, branchHeight, 0).add(centerShift(decorationPos, seed, branchHeight));
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
                for (BlockVec pointInSphere : PointUtils.sphere(point, 3)) {
                    points.add(pointInSphere);
                }
            }
        }
        return points;
    }
}
