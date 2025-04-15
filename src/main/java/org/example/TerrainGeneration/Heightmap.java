package org.example.TerrainGeneration;

public class Heightmap {
    private final int xSize;
    private final int zSize;
    private int[][] heights;
    public Heightmap(int width, int height) {
        this.xSize = width;
        this.zSize = height;
        this.heights = new int[height][width];
    }

    public void setHeight(int x, int y, int height) {
        heights[y][x] = height;
    }
    public int getHeight(int x, int y) {
        return heights[y][x];
    }

    public int getxSize() {
        return this.xSize;
    }
    public int getzSize() {
        return this.zSize;
    }
}
