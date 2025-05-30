package org.example.TerrainGeneration.Biome;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.registry.DynamicRegistry;
import org.example.TerrainGeneration.Biome.Decorators.Decorator;

// basically a record class representing specific information about a biome
// has a addDecoration method that is called when generated decorations in the biome
public class Biome {
    // fields
    private final Block surfaceBlock;
    private final Decorator decorator;
    private final DynamicRegistry.Key<net.minestom.server.world.biome.Biome> biomeKey;

    // biome constructor initializes fields
    public Biome(Block surfaceBlock, Decorator decorator, DynamicRegistry.Key<net.minestom.server.world.biome.Biome> biomeKey) {
        this.surfaceBlock = surfaceBlock;
        this.decorator = decorator;
        this.biomeKey = biomeKey;
    }
    // returns the surface block
    public Block getSurfaceBlock() {
        return this.surfaceBlock;
    }
    // adds a decorator to the decorator of the biome
    public void addDecoration(GenerationUnit unit, int x, int z, int y) {
        this.decorator.addDecoration(unit, x, z, y);
    }
    // returns decorator of biome
    public Decorator getDecorator() {
        return this.decorator;
    }
    // returns minecraft biome type of biome
    public DynamicRegistry.Key<net.minestom.server.world.biome.Biome> getBiomeKey() {
        return this.biomeKey;
    }
}
