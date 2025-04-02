package org.example.TerrainGeneration;

import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex3DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex4DVariant;
import de.articdive.jnoise.pipeline.JNoise;

public class NoiseUtils {
    // creates a JNoise object (fastSimplex) based on a given seed and scale
    public static JNoise createNoise(int seed, double scale) {
        JNoise noise = JNoise.newBuilder()
                .fastSimplex(seed, Simplex2DVariant.CLASSIC, Simplex3DVariant.CLASSIC, Simplex4DVariant.CLASSIC)
                .scale(scale) // Low frequency for smooth terrain
                .build();
        return noise;
    }
}
