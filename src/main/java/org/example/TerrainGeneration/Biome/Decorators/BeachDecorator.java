package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

public class BeachDecorator implements Decorator {
    public BeachDecorator() {
    }

    // place appropriate beach decoration
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        // find position for decoration
        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);

        // determine and place decoration based on random value
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, bottom.blockX(), bottom.blockZ());

    }
}
