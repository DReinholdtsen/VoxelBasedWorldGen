package org.example.TerrainGeneration.Biome.Decorators;

import de.articdive.jnoise.pipeline.JNoise;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.NoiseUtils;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PlainsDecorator implements Decorator {
    Set<Pair<JNoise, Block>> flowerNoises = new HashSet<>();
    public double treeThreshold = .0004;

    public PlainsDecorator() {
        super();
        int seed = TerrainGenerator.seed + 824;
        addFlowerNoise(seed, Block.POPPY);
        addFlowerNoise(seed + 1, Block.DANDELION);
        addFlowerNoise(seed + 2, Block.ALLIUM);
        addFlowerNoise(seed + 3, Block.CORNFLOWER);
        addFlowerNoise(seed + 4, Block.AZURE_BLUET);
        addFlowerNoise(seed + 5, Block.RED_TULIP);
        addFlowerNoise(seed + 6, Block.PINK_TULIP);
        addFlowerNoise(seed + 7, Block.WHITE_TULIP);
        addFlowerNoise(seed + 8, Block.PINK_TULIP);
    }
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
        else if (randomVal < .8) {

        }
        else {
            for (Pair<JNoise, Block> pair : flowerNoises) {
                JNoise noise = pair.first();
                Block flowerType = pair.second();
                double noiseValue = noise.evaluateNoise(decorationPos.blockX(), decorationPos.blockZ());
                if (noiseValue > .95) {
                    unit.modifier().setBlock(decorationPos, flowerType);
                }
            }

        }

    }
    public boolean hasTree(double value) {
        return value < .0004;
    }

    private void addFlowerNoise(int seed, Block flowerType) {
        flowerNoises.add(Pair.of(NoiseUtils.createNoise(seed, .02), flowerType));
    }
}
