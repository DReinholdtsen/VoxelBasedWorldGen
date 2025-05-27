package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.Biome.Biome;
import org.example.TerrainGeneration.Biome.BiomeGenerator;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Decorator {
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight);

    public static Consumer<Block.Setter> structureToSetter(Structure structure, Point decorationPos) {
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
    public static Consumer<Block.Setter> createTreeSetter(Point decorationPos, int height) {
        return setter -> {

            for (int y = 0; y < height; y++) {
                setter.setBlock(decorationPos.add(0, y, 0), Block.OAK_LOG);
            }
            for (int x = -2; x <= 2; x++) {
                // x coordinate for leaves relative to trunk
                for (int z = -2; z <= 2; z++) {
                    // same thing
                    // check to make sure not overriding trunk
                    int startHeight = height - 3;
                    int maxHeight = height + 1;
                    if (Math.abs(x) == 2 && Math.abs(z) == 2) {
                        // no corners
                        continue;
                    }
                    if (x == 0 && z == 0) {
                        startHeight = height;
                        maxHeight += 1;
                    }
                    if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                        maxHeight -= 1;
                    }
                    for (int y = startHeight; y < maxHeight; y++) {
                        setter.setBlock(decorationPos.add(x, y, z), Block.OAK_LEAVES);
                    }

                }
            }
        };
    }
    public static boolean validTreeLocation(int x, int z) {
        // check n by n before tree location for other trees
        for (int deltaX = -4; deltaX < 5; deltaX++) {
            for (int deltaZ = -4; deltaZ <= 0; deltaZ++) {
                if (deltaZ == 0) {
                    if (deltaX >= 0) {
                        continue;
                    }
                    // dont check, has tree if this is called
                }
                int newX = x + deltaX;
                int newZ = z + deltaZ;
                Decorator decorator = TerrainGenerator.biomeGenerator.getBiome(newX, newZ).getDecorator();
                double value = PointUtils.randomFromCoordinate(TerrainGenerator.seed, newX, newZ);
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
            }
        }
        return true;
    }


}
