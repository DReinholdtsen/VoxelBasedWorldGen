package org.example.TerrainGeneration.Biome;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.Biome.Decorators.Decorator;
import org.example.TerrainGeneration.Heightmap;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class Biome {

    private Block surfaceBlock;
    private Decorator decorator;
    private int seed;

    public Biome(Block surfaceBlock) {
        this.surfaceBlock = surfaceBlock;
        this.seed = TerrainGenerator.seed;
    }

    public Block getSurfaceBlock() {
        return this.surfaceBlock;
    }

    public void generateUnit(GenerationUnit unit, Heightmap heightmap) {
        Point start = unit.absoluteStart();
        Point size = unit.size();
        Random random = new Random((long) (Math.pow(start.blockX(), 3) + Math.pow(start.blockZ(), 2) + this.seed));
        for (int z = 0; z < heightmap.getzSize(); z++) {
            for (int x = 0; x < heightmap.getxSize(); x++) {
                Point bottom = start.add(x, 0, z);
                int y = heightmap.getHeight(x, z);
                unit.modifier().fill(bottom, bottom.add(1, y, 1), this.getSurfaceBlock());

                decorator.addDecoration(unit, x, z, random, y);
            }
        }
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
