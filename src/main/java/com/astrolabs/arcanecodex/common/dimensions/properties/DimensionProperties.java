package com.astrolabs.arcanecodex.common.dimensions.properties;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class DimensionProperties {
    private final String name;
    private final long seed;
    private final float gravity;
    private final float timeFlow;
    private final int lightLevel;
    private final int skyColor;
    private final int fogColor;
    private final TerrainType terrainType;
    private final boolean hasCeiling;
    private final boolean hasSkylight;
    private final float energyDensity;
    private final float instability;
    private final List<CustomBiome> biomes;
    private final List<DimensionRule> rules;
    
    private DimensionProperties(Builder builder) {
        this.name = builder.name;
        this.seed = builder.seed;
        this.gravity = builder.gravity;
        this.timeFlow = builder.timeFlow;
        this.lightLevel = builder.lightLevel;
        this.skyColor = builder.skyColor;
        this.fogColor = builder.fogColor;
        this.terrainType = builder.terrainType;
        this.hasCeiling = builder.hasCeiling;
        this.hasSkylight = builder.hasSkylight;
        this.energyDensity = builder.energyDensity;
        this.instability = builder.instability;
        this.biomes = new ArrayList<>(builder.biomes);
        this.rules = new ArrayList<>(builder.rules);
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putLong("seed", seed);
        tag.putFloat("gravity", gravity);
        tag.putFloat("timeFlow", timeFlow);
        tag.putInt("lightLevel", lightLevel);
        tag.putInt("skyColor", skyColor);
        tag.putInt("fogColor", fogColor);
        tag.putString("terrainType", terrainType.name());
        tag.putBoolean("hasCeiling", hasCeiling);
        tag.putBoolean("hasSkylight", hasSkylight);
        tag.putFloat("energyDensity", energyDensity);
        tag.putFloat("instability", instability);
        
        ListTag biomeList = new ListTag();
        for (CustomBiome biome : biomes) {
            biomeList.add(biome.save());
        }
        tag.put("biomes", biomeList);
        
        ListTag ruleList = new ListTag();
        for (DimensionRule rule : rules) {
            ruleList.add(rule.save());
        }
        tag.put("rules", ruleList);
        
        return tag;
    }
    
    public static DimensionProperties load(CompoundTag tag) {
        Builder builder = builder()
            .name(tag.getString("name"))
            .seed(tag.getLong("seed"))
            .gravity(tag.getFloat("gravity"))
            .timeFlow(tag.getFloat("timeFlow"))
            .lightLevel(tag.getInt("lightLevel"))
            .skyColor(tag.getInt("skyColor"))
            .fogColor(tag.getInt("fogColor"))
            .terrainType(TerrainType.valueOf(tag.getString("terrainType")))
            .hasCeiling(tag.getBoolean("hasCeiling"))
            .hasSkylight(tag.getBoolean("hasSkylight"))
            .energyDensity(tag.getFloat("energyDensity"))
            .instability(tag.getFloat("instability"));
        
        ListTag biomeList = tag.getList("biomes", Tag.TAG_COMPOUND);
        for (int i = 0; i < biomeList.size(); i++) {
            builder.addBiome(CustomBiome.load(biomeList.getCompound(i)));
        }
        
        ListTag ruleList = tag.getList("rules", Tag.TAG_COMPOUND);
        for (int i = 0; i < ruleList.size(); i++) {
            builder.addRule(DimensionRule.load(ruleList.getCompound(i)));
        }
        
        return builder.build();
    }
    
    // Getters
    public String getName() { return name; }
    public long getSeed() { return seed; }
    public float getGravity() { return gravity; }
    public float getTimeFlow() { return timeFlow; }
    public int getLightLevel() { return lightLevel; }
    public int getSkyColor() { return skyColor; }
    public int getFogColor() { return fogColor; }
    public TerrainType getTerrainType() { return terrainType; }
    public boolean hasCeiling() { return hasCeiling; }
    public boolean hasSkylight() { return hasSkylight; }
    public float getEnergyDensity() { return energyDensity; }
    public float getInstability() { return instability; }
    public List<CustomBiome> getBiomes() { return new ArrayList<>(biomes); }
    public List<DimensionRule> getRules() { return new ArrayList<>(rules); }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name = "Unknown Dimension";
        private long seed = System.currentTimeMillis();
        private float gravity = 1.0f;
        private float timeFlow = 1.0f;
        private int lightLevel = 0;
        private int skyColor = 0x78A7FF;
        private int fogColor = 0xC0D8FF;
        private TerrainType terrainType = TerrainType.NORMAL;
        private boolean hasCeiling = false;
        private boolean hasSkylight = true;
        private float energyDensity = 1.0f;
        private float instability = 0.0f;
        private final List<CustomBiome> biomes = new ArrayList<>();
        private final List<DimensionRule> rules = new ArrayList<>();
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }
        
        public Builder gravity(float gravity) {
            this.gravity = gravity;
            return this;
        }
        
        public Builder timeFlow(float timeFlow) {
            this.timeFlow = timeFlow;
            return this;
        }
        
        public Builder lightLevel(int lightLevel) {
            this.lightLevel = lightLevel;
            return this;
        }
        
        public Builder skyColor(int skyColor) {
            this.skyColor = skyColor;
            return this;
        }
        
        public Builder fogColor(int fogColor) {
            this.fogColor = fogColor;
            return this;
        }
        
        public Builder terrainType(TerrainType terrainType) {
            this.terrainType = terrainType;
            return this;
        }
        
        public Builder hasCeiling(boolean hasCeiling) {
            this.hasCeiling = hasCeiling;
            return this;
        }
        
        public Builder hasSkylight(boolean hasSkylight) {
            this.hasSkylight = hasSkylight;
            return this;
        }
        
        public Builder energyDensity(float energyDensity) {
            this.energyDensity = energyDensity;
            return this;
        }
        
        public Builder instability(float instability) {
            this.instability = instability;
            return this;
        }
        
        public Builder addBiome(CustomBiome biome) {
            this.biomes.add(biome);
            return this;
        }
        
        public Builder addRule(DimensionRule rule) {
            this.rules.add(rule);
            return this;
        }
        
        public boolean hasName() {
            return name != null && !name.isEmpty() && !name.equals("Unknown Dimension");
        }
        
        public DimensionProperties build() {
            return new DimensionProperties(this);
        }
    }
}