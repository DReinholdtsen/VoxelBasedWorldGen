package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;


public class TaigaDecorator implements Decorator {
    public double treeThreshold = .003;
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {
        Point decorationPos = unit.absoluteStart().add(x, surfaceHeight, z);
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, decorationPos.blockX(), decorationPos.blockZ());
        if (randomVal < treeThreshold) {
            // generate random height tree from 4 to 6
            if (Decorator.validTreeLocation(decorationPos.blockX(), decorationPos.blockZ())) {
                // check for collisions
                unit.fork(Decorator.structureToSetter(TreeDecorators.generateSpruceTree(decorationPos, TerrainGenerator.seed), decorationPos));

                //unit.fork(Decorator.createTreeSetter(decorationPos, 4 + (int) (randomVal/treeThreshold * 3)));
            }

        }
        else if (randomVal < .85) {
            unit.modifier().setBlock(decorationPos, Block.SNOW);
        }

    }
}
