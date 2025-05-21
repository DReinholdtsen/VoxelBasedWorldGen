package org.example.TerrainGeneration.Biome.Decorators;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.NoiseUtils;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class PlainsDecorator implements Decorator {
    JNoise flowerNoise = NoiseUtils.createNoise(TerrainGenerator.seed + 2, .05);
    public double treeThreshold = .0004;
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, decorationPos.blockX(), decorationPos.blockZ());
        if (randomVal < treeThreshold) {
            // check for no tree collisions
            // generate random height tree from 4 to 6
            if (Decorator.validTreeLocation(decorationPos.blockX(), decorationPos.blockZ())) {
                unit.fork(Decorator.structureToSetter(TreeDecorators.generateTree(decorationPos, TerrainGenerator.seed), decorationPos));
                //unit.fork(Decorator.createTreeSetter(decorationPos, 4 + (int) (randomVal/treeThreshold * 3)));
            }
        }
        if (randomVal < .03) {
            // 3% chance for tall grass, set both blocks of grass
            unit.modifier().setBlock(decorationPos, Block.TALL_GRASS.withProperty("half", "lower"));
            unit.modifier().setBlock(decorationPos.add(0, 1, 0), Block.TALL_GRASS.withProperty("half", "upper"));
        } else if (randomVal < .1) {
            unit.modifier().setBlock(decorationPos, Block.SHORT_GRASS);

        }
        else if (randomVal < .4) {

        }
        else {
            double noiseValue = flowerNoise.evaluateNoise(decorationPos.blockX(), decorationPos.blockZ());
            if (noiseValue > .9) {
                unit.modifier().setBlock(decorationPos, Block.POPPY);
            }
        }

    }
    public boolean hasTree(double value) {
        return value < .0004;
    }
}
