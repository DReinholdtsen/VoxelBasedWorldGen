package org.example.TerrainGeneration.Biome;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.registry.DynamicRegistry;
import org.example.TerrainGeneration.Biome.Decorators.Decorator;
import org.example.TerrainGeneration.Heightmap;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;

public class Biome {

    private Block surfaceBlock;
    private Decorator decorator;
    private int seed;
    private DynamicRegistry.Key<net.minestom.server.world.biome.Biome> biomeKey;

    public Biome(Block surfaceBlock, Decorator decorator, DynamicRegistry.Key<net.minestom.server.world.biome.Biome> biomeKey) {
        this.surfaceBlock = surfaceBlock;
        this.seed = TerrainGenerator.seed;
        this.decorator = decorator;
        this.biomeKey = biomeKey;
    }

    public Block getSurfaceBlock() {
        return this.surfaceBlock;
    }

    public void generateUnit(GenerationUnit unit, Heightmap heightmap) {
        Point start = unit.absoluteStart();
        Point size = unit.size();
        for (int z = 0; z < heightmap.getzSize(); z++) {
            for (int x = 0; x < heightmap.getxSize(); x++) {
                Point bottom = start.add(x, 0, z);
                int y = heightmap.getHeight(x, z);
                unit.modifier().fill(bottom, bottom.add(1, y, 1), this.getSurfaceBlock());

                decorator.addDecoration(unit, x, z, y);
            }
        }
    }

    public void addDecoration(GenerationUnit unit, int x, int z, int y) {
        this.decorator.addDecoration(unit, x, z, y);
    }
    public Decorator getDecorator() {
        return this.decorator;
    }
    public DynamicRegistry.Key<net.minestom.server.world.biome.Biome> getBiomeKey() {
        return this.biomeKey;
    }
}
