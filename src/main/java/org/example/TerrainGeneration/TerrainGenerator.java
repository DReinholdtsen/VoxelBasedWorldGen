package org.example.TerrainGeneration;

import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex3DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex4DVariant;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;

import java.util.ArrayList;

// Represents a terrain generator object, with parameters dictating how the terrain is generated
// Contained a generator (Consumer of GenerationUnit) which is added to instance
public class TerrainGenerator {
    // stores key values such as seed and to preserve one noise source
    private final int seed;
    private ArrayList<HeightGenerator> elevationNoises;
    private Generator generator;

    // Initialize a TerrainGenerator object using a seed
    public TerrainGenerator(int seed) {
        // Initializes variables and creates primary noise source for terrain
        this.seed = seed;
        // initializes all height generators
        initializeHeightGenerators();
        //this.noise = createNoise(seed, .01);
        this.generator = unit -> {
            Point start = unit.absoluteStart();
            Point size = unit.size();
            for (int x = 0; x < size.blockX(); x++) {
                for (int z = 0; z < size.blockZ(); z++) {
                    Point bottom = start.add(x, 0, z);
                    int height = 0;
                    for (HeightGenerator heightGenerator : elevationNoises) {
                        height += heightGenerator.getHeight(bottom.blockX(), bottom.blockZ());
                    }

                    // limit height to max height of unit
                    int y = Math.min(height, size.blockY() - start.blockY());
                    unit.modifier().fill(bottom, bottom.add(1, y, 1), Block.MOSS_BLOCK);
                    // System.out.println(bottom + " " + bottom.add(1, y, 1));
                }
            }
        };
    }
    private void initializeHeightGenerators() {
        elevationNoises = new ArrayList<HeightGenerator>();
        elevationNoises.add(new HeightGenerator(seed, .01, noiseValue -> (int) ((noiseValue + 1) * 10) + 1));
        elevationNoises.add(new HeightGenerator(seed+1, .003, noiseValue -> Math.max(0, (int) ((noiseValue - .9) * 1000 + 1))));
    }

    public Generator getGenerator() {
        return this.generator;
    }
}
