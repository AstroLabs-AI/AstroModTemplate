package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.dimensions.properties.DimensionProperties;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;

import java.util.*;

public class DimensionManager {
    private static final Map<ResourceLocation, DimensionProperties> CUSTOM_DIMENSIONS = new HashMap<>();
    private static final Map<ResourceLocation, ResourceKey<Level>> DIMENSION_KEYS = new HashMap<>();
    
    public static class DimensionCreationResult {
        public final boolean success;
        public final ResourceKey<Level> dimensionKey;
        public final String error;
        
        private DimensionCreationResult(boolean success, ResourceKey<Level> dimensionKey, String error) {
            this.success = success;
            this.dimensionKey = dimensionKey;
            this.error = error;
        }
        
        public static DimensionCreationResult success(ResourceKey<Level> key) {
            return new DimensionCreationResult(true, key, null);
        }
        
        public static DimensionCreationResult failure(String error) {
            return new DimensionCreationResult(false, null, error);
        }
    }
    
    public static DimensionCreationResult createDimension(MinecraftServer server, DimensionProperties properties) {
        try {
            String dimensionName = properties.getName();
            ResourceLocation dimensionId = new ResourceLocation(ArcaneCodex.MOD_ID, "custom/" + sanitizeName(dimensionName));
            
            // Check if dimension already exists
            if (CUSTOM_DIMENSIONS.containsKey(dimensionId)) {
                return DimensionCreationResult.failure("Dimension already exists: " + dimensionName);
            }
            
            // Create dimension keys
            ResourceKey<Level> levelKey = ResourceKey.create(Registries.DIMENSION, dimensionId);
            ResourceKey<DimensionType> typeKey = ResourceKey.create(Registries.DIMENSION_TYPE, 
                new ResourceLocation(dimensionId.getNamespace(), dimensionId.getPath() + "_type"));
            ResourceKey<LevelStem> stemKey = ResourceKey.create(Registries.LEVEL_STEM, dimensionId);
            
            // Get registries
            Registry<DimensionType> dimensionTypeRegistry = server.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
            Registry<LevelStem> levelStemRegistry = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
            WritableRegistry<Level> levelRegistry = (WritableRegistry<Level>) server.registryAccess().registryOrThrow(Registries.DIMENSION);
            
            // Create and register dimension type
            DimensionType dimensionType = DimensionFactory.createDimensionType(properties);
            Registry.register(dimensionTypeRegistry, typeKey, dimensionType);
            
            // Create chunk generator
            var chunkGenerator = DimensionFactory.createChunkGenerator(properties, server);
            
            // Create and register level stem
            LevelStem levelStem = new LevelStem(
                Holder.direct(dimensionType),
                chunkGenerator
            );
            Registry.register(levelStemRegistry, stemKey, levelStem);
            
            // Note: In Minecraft 1.20.1, dynamic dimension creation is complex and requires
            // modifying server internals. For now, we'll store the dimension data and
            // require a world restart to actually create the dimension.
            
            // Store dimension info for next world load
            CUSTOM_DIMENSIONS.put(dimensionId, properties);
            DIMENSION_KEYS.put(dimensionId, levelKey);
            
            // The actual dimension will be created on world restart
            
            return DimensionCreationResult.success(levelKey);
            
        } catch (Exception e) {
            // Log error without using private logger
            e.printStackTrace();
            return DimensionCreationResult.failure("Failed to create dimension: " + e.getMessage());
        }
    }
    
    public static boolean deleteDimension(MinecraftServer server, ResourceLocation dimensionId) {
        try {
            ResourceKey<Level> levelKey = DIMENSION_KEYS.get(dimensionId);
            if (levelKey == null) {
                return false;
            }
            
            ServerLevel level = server.getLevel(levelKey);
            if (level == null) {
                return false;
            }
            
            // Teleport all players out
            for (ServerPlayer player : level.players()) {
                player.teleportTo(server.overworld(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            }
            
            // Unload the dimension
            MinecraftForge.EVENT_BUS.post(new LevelEvent.Unload(level));
            server.forgeGetWorldMap().remove(levelKey);
            
            // Remove from tracking
            CUSTOM_DIMENSIONS.remove(dimensionId);
            DIMENSION_KEYS.remove(dimensionId);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static Optional<DimensionProperties> getDimensionProperties(ResourceLocation dimensionId) {
        return Optional.ofNullable(CUSTOM_DIMENSIONS.get(dimensionId));
    }
    
    public static Set<ResourceLocation> getCustomDimensions() {
        return new HashSet<>(CUSTOM_DIMENSIONS.keySet());
    }
    
    public static Optional<ResourceKey<Level>> getDimensionKey(ResourceLocation dimensionId) {
        return Optional.ofNullable(DIMENSION_KEYS.get(dimensionId));
    }
    
    public static void saveCustomDimensions(CompoundTag tag) {
        ListTag dimensionList = new ListTag();
        
        for (Map.Entry<ResourceLocation, DimensionProperties> entry : CUSTOM_DIMENSIONS.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("id", entry.getKey().toString());
            dimTag.put("properties", entry.getValue().save());
            dimensionList.add(dimTag);
        }
        
        tag.put("customDimensions", dimensionList);
    }
    
    public static void loadCustomDimensions(CompoundTag tag, MinecraftServer server) {
        if (!tag.contains("customDimensions")) {
            return;
        }
        
        ListTag dimensionList = tag.getList("customDimensions", Tag.TAG_COMPOUND);
        
        for (int i = 0; i < dimensionList.size(); i++) {
            CompoundTag dimTag = dimensionList.getCompound(i);
            ResourceLocation id = new ResourceLocation(dimTag.getString("id"));
            DimensionProperties properties = DimensionProperties.load(dimTag.getCompound("properties"));
            
            // Recreate the dimension
            createDimension(server, properties);
        }
    }
    
    private static String sanitizeName(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9_]", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");
    }
}