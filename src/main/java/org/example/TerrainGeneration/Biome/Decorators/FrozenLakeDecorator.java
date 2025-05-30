package org.example.TerrainGeneration.Biome.Decorators;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.NoiseUtils;
import org.example.TerrainGeneration.TerrainGenerator;

// Decorator for frozen lake biome
// Like a regular lake but with ice.
public class FrozenLakeDecorator implements Decorator {
    JNoise noise;

    // Adds water above surface as well as ice on surface
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        noise = NoiseUtils.createNoise(TerrainGenerator.seed + 198378949, .1);
        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);
        double random = noise.evaluateNoise(bottom.blockX(), bottom.blockZ());

        unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(-4), Block.WATER);
        if (random < .5) {
            //generate sea grass with 10% chance
            unit.modifier().setBlock(bottom.withY(-5), Block.ICE);
        }

    }
}
