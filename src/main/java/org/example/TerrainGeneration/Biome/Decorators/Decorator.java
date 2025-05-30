package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

// Interface that can be called at a specific position to place an implemented decoration
// Also contains helper methods for decorations
public interface Decorator {
    void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight);

    static Consumer<Block.Setter> structureToSetter(Structure structure, Point decorationPos) {
        return setter -> {
            Map<Block, Set<BlockVec>> blockSetMap = structure.getBlockMap();
            for (Block block : blockSetMap.keySet()) {
                Set<BlockVec> points = blockSetMap.get(block);
                for (Point point : points) {
                    setter.setBlock(decorationPos.add(point), block);
                }
            }
        };
    }

    // determines whether a location is a valid location for a decoration (other decoration nearby or not)
    static boolean validDecorationLocation(int x, int z) {
        // check all n by n square around point for other decorations
        for (int deltaX = -4; deltaX < 5; deltaX++) {
            for (int deltaZ = -4; deltaZ <= 0; deltaZ++) {
                if (deltaZ == 0) {
                    if (deltaX >= 0) {
                        continue;
                    }
                    // dont check, has decoration if this is called
                }
                int newX = x + deltaX;
                int newZ = z + deltaZ;
                // get decorator and simulate random value
                Decorator decorator = TerrainGenerator.biomeGenerator.getBiome(newX, newZ).getDecorator();
                double value = PointUtils.randomFromCoordinate(TerrainGenerator.seed, newX, newZ);
                // check if any biomes with large decorators would have generated at point
                // if so, return false
                if (decorator instanceof PlainsDecorator) {
                    if (value < ((PlainsDecorator) decorator).treeThreshold) {
                        return false;
                    }
                } else if (decorator instanceof TaigaDecorator) {
                    if (value < ((TaigaDecorator) decorator).treeThreshold) {
                        return false;
                    }
                } else if (decorator instanceof ForestDecorator) {
                    if (value < ((ForestDecorator) decorator).treeThreshold) {
                        return false;
                    }
                }
                else if (decorator instanceof TundraDecorator) {
                    if (value < ((TundraDecorator) decorator).spikeThreshold) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


}
