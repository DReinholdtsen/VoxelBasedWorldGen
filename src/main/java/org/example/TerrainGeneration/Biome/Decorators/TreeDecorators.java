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
            return Math.pow(1.4, -height)*2.2 + .8;
        };
        double startAngle = 2 * Math.PI * PointUtils.randomFromCoordinate(-16, decorationPos.blockX(), decorationPos.blockZ());
        Function<Integer, Point> centerShift = x -> {
            double random = PointUtils.randomFromCoordinate(x, decorationPos.blockX(), decorationPos.blockZ());
            Point shift = new Pos(1.2*Math.sin(startAngle + x/5d+ random), 0, Math.sin(startAngle + x/5d + random)*1.2);
            return shift;
        };

        Set<BlockVec> points = PointUtils.verticalTube(decorationPos, 25, radiusFunction, centerShift);
        Set<BlockVec> newPoints = new HashSet<BlockVec>();
        for (int i = 0; i < 6; i++) {
            BlockVec startBlock = randomFromSet(points);

            Point shift1 = new Pos((PointUtils.randomFromCoordinate(-11 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) - .5) * 50,  (PointUtils.randomFromCoordinate(-14 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) - .5) * 50, (PointUtils.randomFromCoordinate(-18 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) -.5) * 50);
            Point shift2 = new Pos((PointUtils.randomFromCoordinate(-10 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) -.5) * 50,  (PointUtils.randomFromCoordinate(-9 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) - .5) * 50, (PointUtils.randomFromCoordinate(-8 + i * 24798, decorationPos.blockX(), decorationPos.blockZ()) - .5) * 50);
            for (Point branchPoint: PointUtils.bezierCurve(startBlock, startBlock.add(shift1), startBlock.add(shift2), t -> 1.5d)) {
                newPoints.add(new BlockVec(branchPoint));
            }
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

}
