package org.example.TerrainGeneration.Biome;

import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.instance.block.Block;
import org.example.TerrainGeneration.NoiseUtils;

import java.util.ArrayList;

// uses continuous noise to assign an ID to each coordinate, representing a specific biome
public class BiomeGenerator {

    JNoise noise;

    // TODO: Put all biomes into json format and read from file
    ArrayList<Biome> IDtoBiome;
    public BiomeGenerator(int seed) {
        this.noise = NoiseUtils.createNoise(seed, 0.0006);
        this.IDtoBiome = new ArrayList<Biome>();
        fillBiomeArray();
    }

    private void fillBiomeArray() {
        IDtoBiome.add(new Biome(Block.WHITE_WOOL));
        IDtoBiome.add(new Biome(Block.RED_WOOL));
        IDtoBiome.add(new Biome(Block.GRAY_WOOL));
        IDtoBiome.add(new Biome(Block.BLUE_WOOL));
        IDtoBiome.add(new Biome(Block.BLACK_WOOL));
    }
    // gets the biome object corresponding to a specific coordinate
    public Biome getBiome(int x, int z) {
        // gets noise value and shifts it to between 0-1
        double value = (this.noise.evaluateNoise(x, z) + 1) / 2d;
        return IDtoBiome.get(rangeToID(value));
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
}
