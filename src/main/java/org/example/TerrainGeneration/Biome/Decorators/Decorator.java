package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Decorator {
    public void addDecoration(GenerationUnit unit, int x, int z, Random random, int surfaceHeight);

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


}
