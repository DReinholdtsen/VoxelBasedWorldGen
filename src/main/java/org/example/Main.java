package org.example;

import de.articdive.jnoise.core.api.functions.Interpolation;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex3DVariant;
import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex4DVariant;
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.coordinate.Point;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.UpdateViewDistancePacket;
import org.example.TerrainGeneration.Biome.Decorators.Decorator;
import org.example.TerrainGeneration.PointUtils;
import org.example.TerrainGeneration.TerrainGenerator;

import java.util.Random;
import java.util.function.BiFunction;

public class Main {
    static BlockVec p1;
    static BlockVec p2;
    static BlockVec p3;
    public static void main(String[] args) {
        // Initialization
        System.setProperty("minestom.chunk-view-distance", "3");
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator and add default bright lighting everywhere
        instanceContainer.setGenerator(new TerrainGenerator(100).getGenerator());
        instanceContainer.setChunkSupplier(LightingChunk::new);
        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        // puts player in creative mode
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            player.setGameMode(GameMode.CREATIVE);
            player.sendPacket(new UpdateViewDistancePacket(32));
            player.setFlyingSpeed(1);
            player.getInventory().addItemStack(ItemStack.of(Material.WHITE_WOOL));
            player.getInventory().addItemStack(ItemStack.of(Material.GRAY_WOOL));
            player.getInventory().addItemStack(ItemStack.of(Material.BLACK_WOOL));
            player.getInventory().addItemStack(ItemStack.of(Material.OAK_LOG));
        });

        globalEventHandler.addListener(PlayerBlockPlaceEvent.class, event -> {
            Block block = event.getBlock();

            if (block == Block.WHITE_WOOL) {
                p1 = event.getBlockPosition();
            } else if (block == Block.GRAY_WOOL) {
                p2 = event.getBlockPosition();
            } else if (block == Block.BLACK_WOOL) {
                p3 = event.getBlockPosition();
            }
        });
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            /*
            for (Point point : PointUtils.bezierCurve(p1, p2, p3)) {
                System.out.println(point);
                instanceContainer.setBlock(point, Block.RED_WOOL);
            }*/
            BiFunction<Integer, Double, Double> radiusFunction  = (height, theta) -> {
                return 2 + 12 * Math.pow(Math.sin(2*theta), 10);
            };
            for (Point point : PointUtils.verticalTube(p1, 1, radiusFunction)) {
                instanceContainer.setBlock(point, Block.RED_WOOL);
            }
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}