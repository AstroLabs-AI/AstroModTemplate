package com.astrolabs.arcanecodex.common.dimensions.generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DataStreamChunkGenerator extends ChunkGenerator {
    public static final Codec<DataStreamChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource)
        ).apply(instance, DataStreamChunkGenerator::new)
    );
    
    private static final int PLATFORM_HEIGHT = 64;
    private static final int GRID_SIZE = 32;
    private static final int PLATFORM_SIZE = 12;
    
    public DataStreamChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }
    
    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }
    
    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState random, 
                           BiomeManager biomeManager, StructureManager structureManager, 
                           ChunkAccess chunk, GenerationStep.Carving step) {
        // No carving in data stream dimension
    }
    
    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, 
                           RandomState random, ChunkAccess chunk) {
        // Surface is built during terrain generation
    }
    
    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No mob spawning in data stream
    }
    
    @Override
    public int getGenDepth() {
        return 256;
    }
    
    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, 
                                                       RandomState random, StructureManager structureManager, 
                                                       ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            generateDataPlatforms(chunk);
            return chunk;
        }, executor);
    }
    
    private void generateDataPlatforms(ChunkAccess chunk) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        
        // Calculate grid position
        int gridX = Math.floorDiv(chunkX * 16, GRID_SIZE);
        int gridZ = Math.floorDiv(chunkZ * 16, GRID_SIZE);
        
        // Determine if this chunk contains a platform
        boolean hasPlatform = (gridX % 3 == 0 && gridZ % 3 == 0) || 
                            (gridX % 5 == 1 && gridZ % 5 == 1);
        
        if (hasPlatform) {
            // Calculate platform center in world coordinates
            int platformCenterX = gridX * GRID_SIZE + GRID_SIZE / 2;
            int platformCenterZ = gridZ * GRID_SIZE + GRID_SIZE / 2;
            
            // Generate platform
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int worldX = chunkX * 16 + x;
                    int worldZ = chunkZ * 16 + z;
                    
                    // Check if within platform bounds
                    int dx = Math.abs(worldX - platformCenterX);
                    int dz = Math.abs(worldZ - platformCenterZ);
                    
                    if (dx <= PLATFORM_SIZE / 2 && dz <= PLATFORM_SIZE / 2) {
                        generatePlatformColumn(chunk, mutable, x, z, dx, dz);
                    }
                }
            }
        }
        
        // Add data streams (vertical beams)
        if ((chunkX + chunkZ) % 7 == 0) {
            int streamX = 8 + (chunkX * 5) % 8;
            int streamZ = 8 + (chunkZ * 7) % 8;
            generateDataStream(chunk, mutable, streamX, streamZ);
        }
    }
    
    private void generatePlatformColumn(ChunkAccess chunk, BlockPos.MutableBlockPos mutable, 
                                      int x, int z, int dx, int dz) {
        // Base platform
        for (int y = PLATFORM_HEIGHT - 3; y <= PLATFORM_HEIGHT; y++) {
            mutable.set(x, y, z);
            BlockState block = getPlatformBlock(y - PLATFORM_HEIGHT + 3, dx, dz);
            chunk.setBlockState(mutable, block, false);
        }
        
        // Edge decoration
        if ((dx == PLATFORM_SIZE / 2 || dz == PLATFORM_SIZE / 2) && 
            !((dx == PLATFORM_SIZE / 2) && (dz == PLATFORM_SIZE / 2))) {
            mutable.set(x, PLATFORM_HEIGHT + 1, z);
            chunk.setBlockState(mutable, Blocks.CYAN_STAINED_GLASS.defaultBlockState(), false);
        }
    }
    
    private BlockState getPlatformBlock(int layer, int dx, int dz) {
        return switch (layer) {
            case 0 -> Blocks.BLACK_CONCRETE.defaultBlockState();
            case 1 -> Blocks.GRAY_CONCRETE.defaultBlockState();
            case 2 -> ((dx + dz) % 2 == 0) ? 
                Blocks.LIGHT_BLUE_CONCRETE.defaultBlockState() : 
                Blocks.CYAN_CONCRETE.defaultBlockState();
            case 3 -> Blocks.SEA_LANTERN.defaultBlockState();
            default -> Blocks.AIR.defaultBlockState();
        };
    }
    
    private void generateDataStream(ChunkAccess chunk, BlockPos.MutableBlockPos mutable, int x, int z) {
        // Vertical beam of "data"
        for (int y = 0; y < 256; y += 4) {
            mutable.set(x, y, z);
            chunk.setBlockState(mutable, Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(), false);
            
            if (y % 16 == 0) {
                mutable.set(x, y + 1, z);
                chunk.setBlockState(mutable, Blocks.GLOWSTONE.defaultBlockState(), false);
            }
        }
    }
    
    @Override
    public int getSeaLevel() {
        return -63;
    }
    
    @Override
    public int getMinY() {
        return 0;
    }
    
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type, LevelHeightAccessor level, RandomState random) {
        return PLATFORM_HEIGHT + 1;
    }
    
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState random) {
        BlockState[] states = new BlockState[256];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(0, states);
    }
    
    @Override
    public void addDebugScreenInfo(List<String> info, RandomState random, BlockPos pos) {
        info.add("Data Stream Generator");
        info.add("Platform Grid: " + GRID_SIZE + "x" + GRID_SIZE);
    }
}