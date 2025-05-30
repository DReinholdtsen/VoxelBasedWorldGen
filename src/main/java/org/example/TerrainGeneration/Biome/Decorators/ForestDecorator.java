package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

// Decorator for forest biome
// Includes dense trees as well as grass and mushrooms
public class ForestDecorator implements Decorator {
    public double treeThreshold = .05;

    // Adds appropriate forest decoration
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        // find decoration position
        Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
        // set top block to grass as it's dirt by default
        unit.modifier().setBlock(decorationPos.add(0, -1, 0), Block.GRASS_BLOCK);
        // determine decoration and place
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, decorationPos.blockX(), decorationPos.blockZ());
        if (randomVal < treeThreshold) {
            // generate random height tree from 4 to 6
            if (Decorator.validDecorationLocation(decorationPos.blockX(), decorationPos.blockZ())) {
                // check for collisions
                unit.fork(Decorator.structureToSetter(TreeDecorators.generateTree(decorationPos, TerrainGenerator.seed), decorationPos));
            }
        } else if (randomVal < .07) {
            // other small surface vegetation decoration.
            unit.modifier().setBlock(decorationPos, Block.TALL_GRASS.withProperty("half", "lower"));
            unit.modifier().setBlock(decorationPos.add(0, 1, 0), Block.TALL_GRASS.withProperty("half", "upper"));
        } else if (randomVal < .1) {
            unit.modifier().setBlock(decorationPos, Block.SHORT_GRASS);
        } else if (randomVal < .11) {
            unit.modifier().setBlock(decorationPos, Block.RED_MUSHROOM);
        } else if (randomVal < .12) {
            unit.modifier().setBlock(decorationPos, Block.BROWN_MUSHROOM);
        }
    }
}
