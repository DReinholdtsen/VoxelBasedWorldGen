package org.example.TerrainGeneration;

import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex3DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex4DVariant;
import de.articdive.jnoise.pipeline.JNoise;

import java.util.function.Function;

// Represents a unique mapping of coordinates to a block height value using noise
public class HeightGenerator {
    private final JNoise noise;
    private final Function<Double, Double> heightFunction;
    // Initializes a HeightGenerator
    // Seed: controls the seed used for the noise generator
    // Scale: controls the "frequency" of the noise
    // heightFunction: maps a value from the noise (-1 to 1), to an integer representing block height
    public HeightGenerator(int seed, double scale, Function<Double, Double> heightFunction) {
        this.noise = NoiseUtils.createNoise(seed, scale);
        this.heightFunction = heightFunction;
    }


    // returns the height of this generator at the specified coordinates
    public double getHeight(int x, int z) {
        return this.heightFunction.apply(this.noise.evaluateNoise(x, z));
    }
}
