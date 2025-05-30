package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

// Decorator for Taiga biome
// Includes snow, spruce trees, and grass
public class TaigaDecorator implements Decorator {
    public double treeThreshold = .003;
    // Adds appropriate decoration to Taiga
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        // find decoration position
        Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
        // set surface to grass
        unit.modifier().setBlock(decorationPos.add(0, -1, 0), Block.GRASS_BLOCK);
        // determine decoration
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, decorationPos.blockX(), decorationPos.blockZ());
        if (randomVal < treeThreshold) {
            // generate random height tree from 4 to 6
            if (Decorator.validDecorationLocation(decorationPos.blockX(), decorationPos.blockZ())) {
                // check for collisions
                unit.fork(Decorator.structureToSetter(TreeDecorators.generateSpruceTree(decorationPos, TerrainGenerator.seed), decorationPos));

            }

        }
        else if (randomVal < .85) {
            // surface is snow
            unit.modifier().setBlock(decorationPos, Block.SNOW);
        } else if (randomVal < .9) {
            // decoration is short grass
            unit.modifier().setBlock(decorationPos, Block.SHORT_GRASS);
        }

    }
}
