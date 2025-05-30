package org.example.TerrainGeneration.Biome;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.instance.block.Block;
import org.example.TerrainGeneration.Biome.Decorators.*;
import org.example.TerrainGeneration.NoiseUtils;

import java.util.HashMap;
import java.util.Map;

// uses overlap of continuous noise to assign an ID to each coordinate, representing a specific biome
public class BiomeGenerator {
    // noise maps for each factor that determines biome.
    JNoise temperature;
    JNoise rainfall;

    // maps from a numerical id to a biome type
    public Map<Integer, Biome> IDtoBiome;

    // initializes biome generator using a specific seed
    public BiomeGenerator(int seed) {
        // creates noise maps for different aspects based on
        this.temperature = NoiseUtils.createNoise(seed * seed, 0.0006);
        this.rainfall = NoiseUtils.createNoise(seed * seed + 1, 0.0006);
        this.IDtoBiome = new HashMap<Integer, Biome>();
        fillBiomeArray();
    }

    // adds biomes to list of biomes, will later use JSON parsing
    private void fillBiomeArray() {
        // initializes all biomes with proper block, decorator, and biome type.
        IDtoBiome.put(Biomes.PLAINS, new Biome(Block.DIRT, new PlainsDecorator(), net.minestom.server.world.biome.Biome.PLAINS));
        IDtoBiome.put(Biomes.DESERT, new Biome(Block.SAND, new DesertDecorator(), net.minestom.server.world.biome.Biome.DESERT));
        IDtoBiome.put(Biomes.FOREST, new Biome(Block.DIRT, new ForestDecorator(), net.minestom.server.world.biome.Biome.DARK_FOREST));
        IDtoBiome.put(Biomes.TAIGA, new Biome(Block.DIRT, new TaigaDecorator(), net.minestom.server.world.biome.Biome.SNOWY_TAIGA));
        IDtoBiome.put(Biomes.SNOWY_TUNDRA, new Biome(Block.SNOW_BLOCK, new TundraDecorator(), net.minestom.server.world.biome.Biome.SNOWY_TAIGA));
        IDtoBiome.put(Biomes.LAKE, new Biome(Block.GRAVEL, new LakeDecorator(), net.minestom.server.world.biome.Biome.OCEAN));
        IDtoBiome.put(Biomes.FROZEN_LAKE, new Biome(Block.GRAVEL, new FrozenLakeDecorator(), net.minestom.server.world.biome.Biome.FROZEN_OCEAN));
        IDtoBiome.put(Biomes.BEACH, new Biome(Block.SAND, new BeachDecorator(), net.minestom.server.world.biome.Biome.BEACH));
    }
    // gets the biome object corresponding to a specific coordinate
    public Biome getBiome(int x, int z) {
        // gets noise value and shifts it to between 0-1
        double temperatureValue = (this.temperature.evaluateNoise(x, z) + 1) / 2d;
        double rainfallValue = (this.rainfall.evaluateNoise(x, z) + 1) / 2d;
        return IDtoBiome.get(valuesToID(temperatureValue, rainfallValue));
    }
    // takes the values representing different aspects of climate and returns the corresponding biome
    public int valuesToID(double temperature, double rainfall) {
        if (temperature < 1d/4) {
            // icy
            if (rainfall < 2d/5) {
                // dry, so ice spikes
                return Biomes.SNOWY_TUNDRA;
            }
            return Biomes.TAIGA;
        }
        if (temperature < 5d/7) {
            // medium temperature, plains/forest
            if (rainfall < 1d/2) {
                return Biomes.PLAINS;
            }
            return Biomes.FOREST;
        }
        else {
            // mountains, snowy peaks, savannah
            return Biomes.DESERT;
        }
    }
}
