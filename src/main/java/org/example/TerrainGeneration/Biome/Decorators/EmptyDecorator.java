package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.instance.generator.GenerationUnit;

// represents a placeholder decoration with no decorations
public class EmptyDecorator implements Decorator {
    public EmptyDecorator() {

    }

    // does nothing
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {

    }

}
