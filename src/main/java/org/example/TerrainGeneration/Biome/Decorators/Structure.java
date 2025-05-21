package org.example.TerrainGeneration.Biome.Decorators;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.*;
// represents a set of block locations and the corresponding block type
// written by ChatGPT
public class Structure {

    // Map from Block type to set of relative Vec positions
    private final Map<Block, Set<BlockVec>> blockMap = new HashMap<>();

    /**
     * Adds a block of the given type at the given relative position.
     */
    public void addBlock(BlockVec pos, Block block) {
        blockMap.computeIfAbsent(block, k -> new HashSet<>()).add(pos);
    }


    /**
     * Adds a collection of blocks of the given type at the given relative position.
     */
    public void addBlocks(Collection<BlockVec> points, Block block) {
        for (BlockVec pos : points) {
            blockMap.computeIfAbsent(block, k -> new HashSet<>()).add(pos);
        }

    }

    /**
     * Pastes the structure into the world at the given origin.
     */
    public void paste(Instance instance, BlockVec origin) {
        for (Map.Entry<Block, Set<BlockVec>> entry : blockMap.entrySet()) {
            Block block = entry.getKey();
            for (BlockVec relPos : entry.getValue()) {
                BlockVec worldPos = origin.add(relPos);
                instance.setBlock(worldPos, block);
            }
        }
    }

    /**
     * Returns an unmodifiable view of the block map.
     */
    public Map<Block, Set<BlockVec>> getBlockMap() {
        Map<Block, Set<BlockVec>> copy = new HashMap<>();
        for (Map.Entry<Block, Set<BlockVec>> entry : blockMap.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}