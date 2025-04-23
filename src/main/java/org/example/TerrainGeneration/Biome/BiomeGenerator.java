package org.example.TerrainGeneration.Biome;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import org.example.TerrainGeneration.Biome.Decorators.DesertDecorator;
import org.example.TerrainGeneration.Biome.Decorators.EmptyDecorator;
import org.example.TerrainGeneration.Biome.Decorators.ForestDecorator;
import org.example.TerrainGeneration.Biome.Decorators.PlainsDecorator;
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
        // TODO: add biomes
        IDtoBiome.put(Biomes.PLAINS, new Biome(Block.GRASS_BLOCK, new PlainsDecorator(), net.minestom.server.world.biome.Biome.PLAINS));
        IDtoBiome.put(Biomes.SAVANNAH, new Biome(Block.ORANGE_WOOL, new EmptyDecorator(), net.minestom.server.world.biome.Biome.SAVANNA));
        IDtoBiome.put(Biomes.MOUNTAINS, new Biome(Block.STONE, new EmptyDecorator(), net.minestom.server.world.biome.Biome.STONY_PEAKS));
        IDtoBiome.put(Biomes.DESERT, new Biome(Block.SAND, new DesertDecorator(), net.minestom.server.world.biome.Biome.DESERT));
        IDtoBiome.put(Biomes.SNOWY_PEAKS, new Biome(Block.BLUE_ICE, new EmptyDecorator(), net.minestom.server.world.biome.Biome.FROZEN_PEAKS));
        IDtoBiome.put(Biomes.FOREST, new Biome(Block.MOSS_BLOCK, new ForestDecorator(), net.minestom.server.world.biome.Biome.FOREST));
        IDtoBiome.put(Biomes.TAIGA, new Biome(Block.CYAN_WOOL, new EmptyDecorator(), net.minestom.server.world.biome.Biome.TAIGA));
        IDtoBiome.put(Biomes.SNOWY_TUNDRA, new Biome(Block.SNOW_BLOCK, new EmptyDecorator(), net.minestom.server.world.biome.Biome.SNOWY_TAIGA));
        IDtoBiome.put(Biomes.HILLY_DESERT, new Biome(Block.SAND, new EmptyDecorator(), net.minestom.server.world.biome.Biome.DESERT));
    }
    // gets the biome object corresponding to a specific coordinate
    public Biome getBiome(int x, int z) {
        // gets noise value and shifts it to between 0-1
        double temperatureValue = (this.temperature.evaluateNoise(x, z) + 1) / 2d;
        double rainfallValue = (this.rainfall.evaluateNoise(x, z) + 1) / 2d;
        double ruggednessValue = (this.ruggedness.evaluateNoise(x, z) + 1) / 2d;
        return IDtoBiome.get(valuesToID(temperatureValue, rainfallValue, ruggednessValue));
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
