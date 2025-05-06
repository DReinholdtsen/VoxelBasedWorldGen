package org.example.TerrainGeneration.Biome.Decorators;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.NoiseUtils;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class ForestDecorator implements Decorator {
    public double treeThreshold = .25;
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, decorationPos.blockX(), decorationPos.blockZ());
        if (randomVal < treeThreshold) {
            // generate random height tree from 4 to 6
            if (Decorator.validTreeLocation(decorationPos.blockX(), decorationPos.blockZ())) {
                // check for collisions
                unit.fork(Decorator.createTreeSetter(decorationPos, 4 + (int) (randomVal/treeThreshold * 3)));
            }


        }
    }
}
