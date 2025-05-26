package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.ArcaneCodex;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;

public class ArcaneCodexDimensions {
    
    // Predefined dimensions
    public static final ResourceKey<Level> DATA_STREAM = ResourceKey.create(Registries.DIMENSION,
        new ResourceLocation(ArcaneCodex.MOD_ID, "data_stream"));
    
    public static final ResourceKey<Level> PROBABILITY_GARDEN = ResourceKey.create(Registries.DIMENSION,
        new ResourceLocation(ArcaneCodex.MOD_ID, "probability_garden"));
    
    public static final ResourceKey<Level> TIME_ECHO = ResourceKey.create(Registries.DIMENSION,
        new ResourceLocation(ArcaneCodex.MOD_ID, "time_echo"));
    
    public static final ResourceKey<Level> NULL_SPACE = ResourceKey.create(Registries.DIMENSION,
        new ResourceLocation(ArcaneCodex.MOD_ID, "null_space"));
    
    // Dimension types
    public static final ResourceKey<DimensionType> DATA_STREAM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
        new ResourceLocation(ArcaneCodex.MOD_ID, "data_stream_type"));
    
    public static final ResourceKey<DimensionType> PROBABILITY_GARDEN_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
        new ResourceLocation(ArcaneCodex.MOD_ID, "probability_garden_type"));
    
    public static final ResourceKey<DimensionType> TIME_ECHO_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
        new ResourceLocation(ArcaneCodex.MOD_ID, "time_echo_type"));
    
    public static final ResourceKey<DimensionType> NULL_SPACE_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
        new ResourceLocation(ArcaneCodex.MOD_ID, "null_space_type"));
    
    // Level stems
    public static final ResourceKey<LevelStem> DATA_STREAM_STEM = ResourceKey.create(Registries.LEVEL_STEM,
        new ResourceLocation(ArcaneCodex.MOD_ID, "data_stream"));
    
    public static final ResourceKey<LevelStem> PROBABILITY_GARDEN_STEM = ResourceKey.create(Registries.LEVEL_STEM,
        new ResourceLocation(ArcaneCodex.MOD_ID, "probability_garden"));
    
    public static final ResourceKey<LevelStem> TIME_ECHO_STEM = ResourceKey.create(Registries.LEVEL_STEM,
        new ResourceLocation(ArcaneCodex.MOD_ID, "time_echo"));
    
    public static final ResourceKey<LevelStem> NULL_SPACE_STEM = ResourceKey.create(Registries.LEVEL_STEM,
        new ResourceLocation(ArcaneCodex.MOD_ID, "null_space"));
    
    // Dynamic dimension creation
    public static ResourceKey<Level> createDimensionKey(String name) {
        return ResourceKey.create(Registries.DIMENSION, 
            new ResourceLocation(ArcaneCodex.MOD_ID, "custom/" + sanitizeName(name)));
    }
    
    public static ResourceKey<DimensionType> createDimensionTypeKey(String name) {
        return ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(ArcaneCodex.MOD_ID, "custom/" + sanitizeName(name) + "_type"));
    }
    
    public static ResourceKey<LevelStem> createLevelStemKey(String name) {
        return ResourceKey.create(Registries.LEVEL_STEM,
            new ResourceLocation(ArcaneCodex.MOD_ID, "custom/" + sanitizeName(name)));
    }
    
    private static String sanitizeName(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9_]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");
    }
}