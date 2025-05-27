package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

public class WaterDecorator implements Decorator {
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {

        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);
        double random = PointUtils.randomFromCoordinate(TerrainGenerator.seed, bottom.blockX(), bottom.blockZ());

        unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(-4), Block.WATER);
        if (random < .1) {
            //generate sea grass with 10% chance
            unit.modifier().setBlock(bottom, Block.SEAGRASS);
        }

    }
}


