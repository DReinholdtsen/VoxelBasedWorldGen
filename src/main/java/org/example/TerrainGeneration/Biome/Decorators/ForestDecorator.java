package org.example.TerrainGeneration.Biome.Decorators;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.NoiseUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class ForestDecorator implements Decorator {
        public void addDecoration(GenerationUnit unit, int x, int z, Random random, int surfaceHeight) {
            double randomVal = random.nextDouble();
            Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
            if (randomVal < .006) {
                // generate random height tree from 4 to 6
                unit.fork(Decorator.createTreeSetter(decorationPos, 4 + (int) (randomVal * 500)));

            }
        }
}
