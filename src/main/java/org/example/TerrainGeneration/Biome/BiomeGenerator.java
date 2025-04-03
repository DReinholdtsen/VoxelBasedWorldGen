package org.example.TerrainGeneration.Biome;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.instance.block.Block;
import org.example.TerrainGeneration.NoiseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// uses continuous noise to assign an ID to each coordinate, representing a specific biome
public class BiomeGenerator {

    JNoise noise;
    JNoise temperature;
    JNoise rainfall;
    JNoise ruggedness;

    // TODO: Put all biomes into json format and read from file
    Map<Integer, Biome> IDtoBiome;

    // initializes biome generator using a specific seed
    public BiomeGenerator(int seed) {
        this.noise = NoiseUtils.createNoise(seed, 0.0006);
        // creates noise maps for different aspects based on
        this.temperature = NoiseUtils.createNoise(seed * seed, 0.0006);
        this.rainfall = NoiseUtils.createNoise(seed * seed + 1, 0.0006);
        this.ruggedness = NoiseUtils.createNoise(seed * seed + 2, 0.0006);
        this.IDtoBiome = new HashMap<Integer, Biome>();
        fillBiomeArray();
    }

    // adds biomes to list of biomes, will later use JSON parsing
    private void fillBiomeArray() {
        IDtoBiome.put(Biomes.PLAINS, new Biome(Block.LIME_WOOL));
        IDtoBiome.put(Biomes.SAVANNAH, new Biome(Block.ORANGE_WOOL));
        IDtoBiome.put(Biomes.MOUNTAINS, new Biome(Block.GRAY_WOOL));
        IDtoBiome.put(Biomes.DESERT, new Biome(Block.YELLOW_WOOL));
        IDtoBiome.put(Biomes.SNOWY_PEAKS, new Biome(Block.WHITE_WOOL));
        IDtoBiome.put(Biomes.FOREST, new Biome(Block.GREEN_WOOL));
        IDtoBiome.put(Biomes.TAIGA, new Biome(Block.CYAN_WOOL));
        IDtoBiome.put(Biomes.SNOWY_TUNDRA, new Biome(Block.PINK_WOOL));
        IDtoBiome.put(Biomes.HILLY_DESERT, new Biome(Block.YELLOW_WOOL));
    }
    // gets the biome object corresponding to a specific coordinate
    public Biome getBiome(int x, int z) {
        // gets noise value and shifts it to between 0-1
        double temperatureValue = (this.temperature.evaluateNoise(x, z) + 1) / 2d;
        double rainfallValue = (this.rainfall.evaluateNoise(x, z) + 1) / 2d;
        double ruggednessValue = (this.ruggedness.evaluateNoise(x, z) + 1) / 2d;
        return IDtoBiome.get(valuesToID(temperatureValue, rainfallValue, ruggednessValue));
    }
    // takes a double value from 0 to 1 and returns an ID based on which range it falls into
    public int rangeToID(double value) {
        if (value < .2) {
            return 0;
        }
        if (value < .4) {
            return 1;
        }
        if (value < .6) {
            return 2;
        }
        if (value < .8) {
            return 3;
        }
        return 4;
    }

    // takes the values representing different aspects of climate and returns the corresponding biome
    public int valuesToID(double temperature, double rainfall, double ruggednessValue) {
        if (temperature < 1d/4) {
            // snowy
            if (rainfall < 2d/5) {
                return Biomes.SNOWY_TUNDRA;
            }
            return Biomes.TAIGA;
        }
        if (temperature < 5d/7) {
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
