package org.example.TerrainGeneration.Biome.Decorators;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.function.BiFunction;
import java.util.function.Function;


// decorator for tundra biome, including ice spikes
public class TundraDecorator implements Decorator {

    public TundraDecorator() {
    }
    public double spikeThreshold = 0.005;
    public void addDecoration(GenerationUnit unit, int x, int z, int surfaceHeight) {

        Point bottom = unit.absoluteStart().add(x, surfaceHeight, z);
        double randomVal = PointUtils.randomFromCoordinate(TerrainGenerator.seed, bottom.blockX(), bottom.blockZ());
        if (randomVal > spikeThreshold) {
            // no decoration, return
            return;
        }
        // check for nearby spikes
        if (!Decorator.validTreeLocation(bottom.blockX(), bottom.blockZ())) {
            return;
        }
        unit.fork(Decorator.structureToSetter(generateIceSpike(bottom, TerrainGenerator.seed), bottom));
    }

    public static Structure generateIceSpike(Point decorationPos, int seed) {
        Structure iceSpike = new Structure();
        BiFunction<Integer, Double, Double> radiusFunction = (h, theta) -> {
            return 3.45d - h/5d;
        };
        for (BlockVec point : PointUtils.verticalTube(new Pos(0, -4, 0), 17, radiusFunction)) {
            iceSpike.addBlock(point, Block.PACKED_ICE);
        }
        return iceSpike;
    }
}
