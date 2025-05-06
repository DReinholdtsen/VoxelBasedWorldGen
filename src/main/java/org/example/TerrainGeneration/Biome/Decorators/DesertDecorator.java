package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class DesertDecorator implements Decorator {

    public DesertDecorator() {
    }

    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {

        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, bottom.blockX(), bottom.blockZ());
        if (randomVal > .01) {
            // no decoration, return
            return;
        }
        int decorationHeight = (int) (randomVal * 300) + 1;
        unit.modifier().fill(bottom, bottom.add(1, decorationHeight, 1), Block.CACTUS);
    }

}
