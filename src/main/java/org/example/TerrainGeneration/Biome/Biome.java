package org.example.TerrainGeneration.Biome;

import net.minestom.server.instance.block.Block;

public class Biome {

    private Block surfaceBlock;

    public Biome(Block surfaceBlock) {
        this.surfaceBlock = surfaceBlock;
    }

    public Block getSurfaceBlock() {
        return this.surfaceBlock;
    }
}
