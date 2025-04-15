package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.instance.generator.GenerationUnit;

import java.util.Random;

public interface Decorator {
    public void addDecoration(GenerationUnit unit, int x, int z, Random random, int surfaceHeight);
}
