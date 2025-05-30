package org.example.TerrainGeneration;


import de.articdive.jnoise.pipeline.JNoise;

import net.minestom.server.coordinate.Point;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;

import org.example.TerrainGeneration.Biome.Biome;
import org.example.TerrainGeneration.Biome.BiomeGenerator;
import org.example.TerrainGeneration.Biome.Biomes;
import org.example.TerrainGeneration.Biome.Decorators.FrozenLakeDecorator;
import org.example.TerrainGeneration.Biome.Decorators.WaterDecorator;

import java.util.ArrayList;
import java.util.Random;

// Represents a terrain generator object, with parameters dictating how the terrain is generated
// Contained a generator (Consumer of GenerationUnit) which is added to instance
public class TerrainGenerator {
    // stores key values such as seed and to preserve one noise source
    public static int seed;
    private ArrayList<HeightGenerator> elevationNoises;
    private Generator generator;
    public static BiomeGenerator biomeGenerator;
    public static HeightGenerator hilliness;
    public static JNoise caveNoise;
    // Initialize a TerrainGenerator object using a seed
    public TerrainGenerator(int seed) {
        // Initializes variables and creates primary noise source for terrain
        TerrainGenerator.seed = seed;
        // initializes all height generators
        initializeHeightGenerators();
        // creates biome generator
        biomeGenerator = new BiomeGenerator(seed * seed);
        caveNoise = NoiseUtils.createNoise(seed * 5 + 17985, .025);
        hilliness = hillinessGenerator();
        this.generator = unit -> {
            Point start = unit.absoluteStart();
            Point size = unit.size();
            Random random = new Random((long) (Math.pow(start.blockX(), 2) + Math.pow(start.blockZ(), 2)));
            for (int x = 0; x < size.blockX(); x++) {
                for (int z = 0; z < size.blockZ(); z++) {
                    Heightmap unitHeightmap = new Heightmap(size.blockX(), size.blockZ());
                    Point bottom = start.add(x, 0, z);
                    double doubleHeight = 0;
                    double hillinessValue = hilliness.getHeight(bottom.blockX(), bottom.blockZ());
                    // adds height from all generators

                    for (HeightGenerator heightGenerator : elevationNoises) {
                        double stepHeight = heightGenerator.getHeight(bottom.blockX(), bottom.blockZ());
                        doubleHeight += stepHeight;
                    }
                    doubleHeight *= hillinessValue;
                    int height = (int) doubleHeight;
                    height += 60;
                    Biome biome = biomeGenerator.getBiome(bottom.blockX(), bottom.blockZ());
                    if (height < 60) {
                        if (biome.getSurfaceBlock() == Block.SNOW_BLOCK || biome.getBiomeKey() == net.minestom.server.world.biome.Biome.SNOWY_TAIGA) {
                            // frozen lake
                            biome = biomeGenerator.IDtoBiome.get(Biomes.FROZEN_LAKE);
                        } else {
                            biome = biomeGenerator.IDtoBiome.get(Biomes.LAKE);
                        }


                    }

                    // limit height to max height of unit
                    // set in game biome
                    height = Math.min(height, size.blockY() - start.blockY());

                    // set blocks, including cave air blocks
                    int highestRealHeight = 0;
                    int distanceFromSurface = height;
                    for (int currentHeight = 0; currentHeight < height; currentHeight++) {
                        // find cave noise result at current location, determine if it should be air
                        double caveNoiseResult = caveNoise.evaluateNoise(bottom.blockX(), currentHeight, bottom.blockZ());
                        // otherwise set it to appropriate block
                        if (caveNoiseResult < .4 + currentHeight / 240d) {
                            if (distanceFromSurface > 4) {
                                unit.modifier().setBlock(bottom.add(0, currentHeight, 0), Block.STONE);
                            } else {
                                unit.modifier().setBlock(bottom.add(0, currentHeight, 0), biome.getSurfaceBlock());
                            }

                            highestRealHeight = currentHeight;
                        } else {
                            //System.out.println(caveNoiseResult);
                        }
                        distanceFromSurface -= 1;
                    }

                    // set biome
                    for (int currentHeight = 0; currentHeight <= Math.max(height + 2, 66); currentHeight++) {

                        unit.modifier().setBiome(bottom.add(0, currentHeight, 0), biome.getBiomeKey());
                    }
                    if (highestRealHeight > 0 && (height - highestRealHeight < 5 || biome.getDecorator() instanceof FrozenLakeDecorator || biome.getDecorator() instanceof WaterDecorator)) {
                        biome.addDecoration(unit, x, z, highestRealHeight + 1);
                    } else if (highestRealHeight <= 0) {
                        biome.addDecoration(unit, x, z, 1);
                    }

                }
            }
        };
    }
    public void initializeHeightGenerators() {
        elevationNoises = new ArrayList<HeightGenerator>();
        HeightGenerator base = new HeightGenerator(seed, .01, noiseValue -> ((noiseValue + 1) * 10));
        elevationNoises.add(new HeightGenerator(seed + 1, .005, noiseValue -> ((noiseValue + 1) * 5)));
        elevationNoises.add(new HeightGenerator(seed + 2, .0025, noiseValue -> ((noiseValue + 1) * 2.5)));
        elevationNoises.add(new HeightGenerator(seed + 3, .00125, noiseValue -> ((noiseValue + 1) * 1.25)));
        elevationNoises.add(new HeightGenerator(seed + 4, .0006, noiseValue -> ((noiseValue + 1) * .6)));
        elevationNoises.add(new HeightGenerator(seed + 5, .0003, noiseValue -> ((noiseValue + 1) * .3)));

        elevationNoises.add(new HeightGenerator(seed + 7, .01, noiseValue -> -noiseValue * 6));

        HeightGenerator smallHills = smallHillsGenerator();
        HeightGenerator mountains = mountainsGenerator();
        HeightGenerator negativeGenerator = negativeGenerator();
        elevationNoises.add(base);
        elevationNoises.add(smallHills);
        elevationNoises.add(mountains);
        elevationNoises.add(negativeGenerator);
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

    // creates negative height generator for valleys and lakes specifically
    public HeightGenerator negativeGenerator() {
        return new HeightGenerator(seed+192, .003, noiseValue -> {
            // creates a threshhold of above .9 (1/20)
            double val = Math.max(0, (noiseValue + 1) / 2d - .6);
            // if value is 0 or close, ignore
            if (val <= .00000001) {
                return 0d;
            }
            val = Math.pow(val, 1.6);
            val = val * -250;
            if (val < 0) {
                //System.out.println(val);
            }
            return val;
        });
    }

    // creates a "height" generator that returns a value from .5 to 1 representing the hilliness of a region
    public HeightGenerator hillinessGenerator() {
        return new HeightGenerator(seed + 193, .00008, noiseValue -> {
            return Math.pow(noiseValue + 1 / 2d, 3) * .75 + .75;
        });
    }
    // returns the seed of the given world
    public int getSeed() {
        return seed;
    }


}
