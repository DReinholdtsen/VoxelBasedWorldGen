package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.Random;

public class DesertDecorator implements Decorator {

    public DesertDecorator() {
    }

    public void addDecoration(GenerationUnit unit, int x, int z, Random random, int surfaceHeight) {
        if (random.nextDouble() > .01) {
            // no decoration, return
            return;
        }
        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);
        int decorationHeight = random.nextInt(3) + 1;
        unit.modifier().fill(bottom, bottom.add(1, decorationHeight, 1), Block.CACTUS);
    }

}
