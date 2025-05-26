package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.common.dimensions.properties.DimensionProperties;
import com.astrolabs.arcanecodex.common.dimensions.properties.TerrainType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class DimensionFactory {
    
    public static DimensionType createDimensionType(DimensionProperties properties) {
        return new DimensionType(
            OptionalLong.empty(), // Fixed time if specified
            properties.hasSkylight(),
            properties.hasCeiling(),
            false, // ultrawarm
            true, // natural
            1.0, // coordinate scale
            true, // bed works
            false, // respawn anchor works
            0, // min Y
            256, // height
            256, // logical height
            BlockTags.INFINIBURN_OVERWORLD,
            BuiltinDimensionTypes.OVERWORLD_EFFECTS,
            properties.getLightLevel() > 0 ? properties.getLightLevel() / 15.0f : 0.0f,
            new DimensionType.MonsterSettings(false, false, ConstantInt.of(0), 0)
        );
    }
    
    public static NoiseGeneratorSettings createNoiseSettings(DimensionProperties properties) {
        NoiseSettings noiseSettings = NoiseSettings.create(
            0, // min Y
            256, // height
            1, // noiseSizeHorizontal
            2  // noiseSizeVertical
        );
        
        return new NoiseGeneratorSettings(
            noiseSettings,
            Blocks.STONE.defaultBlockState(),
            Blocks.WATER.defaultBlockState(),
            createNoiseRouter(properties),
            createSurfaceRules(properties),
            List.of(), // spawn target
            63, // sea level
            false, // disable mob generation
            false, // aquifers enabled
            false, // ore veins enabled
            false  // legacy random source
        );
    }
    
    private static NoiseRouter createNoiseRouter(DimensionProperties properties) {
        // Create noise router based on terrain type
        return switch (properties.getTerrainType()) {
            case FLAT -> createFlatNoiseRouter();
            case ISLANDS -> createIslandsNoiseRouter();
            case VOID -> createVoidNoiseRouter();
            case FRACTAL -> createFractalNoiseRouter();
            case CRYSTALLINE -> createCrystallineNoiseRouter();
            default -> createDefaultNoiseRouter();
        };
    }
    
    private static NoiseRouter createDefaultNoiseRouter() {
        // Simplified noise router - in a real implementation this would be much more complex
        DensityFunction zero = DensityFunctions.zero();
        
        return new NoiseRouter(
            zero, // barrier noise
            zero, // fluid level floodedness noise
            zero, // fluid level spread noise
            zero, // lava noise
            zero, // temperature
            zero, // vegetation
            zero, // continents
            zero, // erosion
            zero, // depth
            zero, // ridges
            zero, // initial density without jaggedness
            zero, // final density
            zero, // vein toggle
            zero, // vein ridged
            zero  // vein gap
        );
    }
    
    private static NoiseRouter createFlatNoiseRouter() {
        // Flat world generation
        DensityFunction flatDensity = DensityFunctions.constant(0.0);
        
        return new NoiseRouter(
            flatDensity, flatDensity, flatDensity, flatDensity,
            flatDensity, flatDensity, flatDensity, flatDensity,
            flatDensity, flatDensity, flatDensity, flatDensity,
            flatDensity, flatDensity, flatDensity
        );
    }
    
    private static NoiseRouter createIslandsNoiseRouter() {
        // Floating islands generation
        DensityFunction islandDensity = DensityFunctions.zero(); // Simplified
        
        return new NoiseRouter(
            islandDensity, islandDensity, islandDensity, islandDensity,
            islandDensity, islandDensity, islandDensity, islandDensity,
            islandDensity, islandDensity, islandDensity, islandDensity,
            islandDensity, islandDensity, islandDensity
        );
    }
    
    private static NoiseRouter createVoidNoiseRouter() {
        // Empty void
        DensityFunction voidDensity = DensityFunctions.constant(-1.0);
        
        return new NoiseRouter(
            voidDensity, voidDensity, voidDensity, voidDensity,
            voidDensity, voidDensity, voidDensity, voidDensity,
            voidDensity, voidDensity, voidDensity, voidDensity,
            voidDensity, voidDensity, voidDensity
        );
    }
    
    private static NoiseRouter createFractalNoiseRouter() {
        // Fractal terrain
        DensityFunction fractalDensity = DensityFunctions.zero(); // Simplified
        
        return new NoiseRouter(
            fractalDensity, fractalDensity, fractalDensity, fractalDensity,
            fractalDensity, fractalDensity, fractalDensity, fractalDensity,
            fractalDensity, fractalDensity, fractalDensity, fractalDensity,
            fractalDensity, fractalDensity, fractalDensity
        );
    }
    
    private static NoiseRouter createCrystallineNoiseRouter() {
        // Crystal formations
        DensityFunction crystalDensity = DensityFunctions.zero(); // Simplified
        
        return new NoiseRouter(
            crystalDensity, crystalDensity, crystalDensity, crystalDensity,
            crystalDensity, crystalDensity, crystalDensity, crystalDensity,
            crystalDensity, crystalDensity, crystalDensity, crystalDensity,
            crystalDensity, crystalDensity, crystalDensity
        );
    }
    
    private static SurfaceRules.RuleSource createSurfaceRules(DimensionProperties properties) {
        // Create surface rules based on terrain type
        return switch (properties.getTerrainType()) {
            case CRYSTALLINE -> SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.AMETHYST_BLOCK.defaultBlockState()))
            );
            case LIQUID -> SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.WATER.defaultBlockState()))
            );
            case GRID -> SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.IRON_BLOCK.defaultBlockState()))
            );
            default -> SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR,
                    SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState()))
            );
        };
    }
    
    public static Biome createBiome(DimensionProperties properties) {
        BiomeGenerationSettings.Builder generationBuilder = new BiomeGenerationSettings.Builder(
            null,  // PlacedFeature registry
            null   // ConfiguredCarver registry
        );
        
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.5f)
            .downfall(0.5f)
            .specialEffects(new BiomeSpecialEffects.Builder()
                .waterColor(0x3F76E4)
                .waterFogColor(0x050533)
                .fogColor(properties.getFogColor())
                .skyColor(properties.getSkyColor())
                .build())
            .mobSpawnSettings(spawnBuilder.build())
            .generationSettings(generationBuilder.build())
            .build();
    }
    
    public static ChunkGenerator createChunkGenerator(DimensionProperties properties, MinecraftServer server) {
        HolderGetter<Biome> biomeRegistry = server.registryAccess().lookupOrThrow(Registries.BIOME);
        HolderGetter<DensityFunction> densityRegistry = server.registryAccess().lookupOrThrow(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseRegistry = server.registryAccess().lookupOrThrow(Registries.NOISE);
        
        NoiseGeneratorSettings noiseSettings = createNoiseSettings(properties);
        
        // Create biome source
        BiomeSource biomeSource = new FixedBiomeSource(
            biomeRegistry.getOrThrow(Biomes.PLAINS) // Simplified - should use custom biome
        );
        
        return new NoiseBasedChunkGenerator(
            biomeSource,
            Holder.direct(noiseSettings)
        );
    }
}