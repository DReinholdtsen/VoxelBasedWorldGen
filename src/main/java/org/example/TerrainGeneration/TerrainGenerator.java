package org.example.TerrainGeneration;

import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex3DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex4DVariant;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;
import org.example.TerrainGeneration.Biome.Biome;
import org.example.TerrainGeneration.Biome.BiomeGenerator;

import java.util.ArrayList;

// Represents a terrain generator object, with parameters dictating how the terrain is generated
// Contained a generator (Consumer of GenerationUnit) which is added to instance
public class TerrainGenerator {
    // stores key values such as seed and to preserve one noise source
    private final int seed;
    private ArrayList<HeightGenerator> elevationNoises;
    private Generator generator;
    private BiomeGenerator biomeGenerator;
    // Initialize a TerrainGenerator object using a seed
    public TerrainGenerator(int seed) {
        // Initializes variables and creates primary noise source for terrain
        this.seed = seed;
        // initializes all height generators
        initializeHeightGenerators();
        // creates biome generator
        this.biomeGenerator = new BiomeGenerator(seed * seed);
        this.generator = unit -> {
            Point start = unit.absoluteStart();
            Point size = unit.size();
            for (int x = 0; x < size.blockX(); x++) {
                for (int z = 0; z < size.blockZ(); z++) {
                    Point bottom = start.add(x, 0, z);
                    double doubleHeight = 0;
                    // adds height from all generators
                    for (HeightGenerator heightGenerator : elevationNoises) {
                        doubleHeight += heightGenerator.getHeight(bottom.blockX(), bottom.blockZ());
                    }
                    int height = (int) doubleHeight;
                    Biome biome = biomeGenerator.getBiome(bottom.blockX(), bottom.blockZ());
                    // limit height to max height of unit
                    int y = Math.min(height, size.blockY() - start.blockY());
                    unit.modifier().fill(bottom, bottom.add(1, y, 1), biome.getSurfaceBlock());
                    // System.out.println(bottom + " " + bottom.add(1, y, 1));
                }
            }
        };
    }
    private void initializeHeightGenerators() {
        elevationNoises = new ArrayList<HeightGenerator>();
        HeightGenerator base = new HeightGenerator(seed, .01, noiseValue -> ((noiseValue + 1) * 10) + 1);

        HeightGenerator smallHills = smallHillsGenerator();
        HeightGenerator mountains = mountainsGenerator();
        elevationNoises.add(base);
        elevationNoises.add(smallHills);
        elevationNoises.add(mountains);
    }

    public Generator getGenerator() {
        return this.generator;
    }

    // creates height generator for small hills
    public HeightGenerator smallHillsGenerator() {
        return new HeightGenerator(seed+1, .001, noiseValue -> {
            // creates a threshhold of above .9 (1/20)
            double val = Math.max(0, noiseValue - .85);
            // if value is 0 or close, ignore
            if (val <= .00000001) {
                return 0d;
            }
            val = Math.pow(val, 2);
            val = val * 5000;
            return val;
        });
    }

    // creates height generator for large mountains
    public HeightGenerator mountainsGenerator() {
        return new HeightGenerator(seed+2, .0002, noiseValue -> {
            // creates a threshhold of above .9 (1/20)
            double val = Math.max(0, noiseValue - .9);
            // if value is 0 or close, ignore
            if (val <= .00000001) {
                return 0d;
            }
            val = Math.pow(val, 2);
            val = val * 42000;
            return val;
        });
    }
}
