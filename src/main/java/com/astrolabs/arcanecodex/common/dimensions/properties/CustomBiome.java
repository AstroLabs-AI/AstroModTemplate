package com.astrolabs.arcanecodex.common.dimensions.properties;

import net.minecraft.nbt.CompoundTag;

public class CustomBiome {
    private final String name;
    private final String type;
    private final float temperature;
    private final float humidity;
    private final int grassColor;
    private final int waterColor;
    
    public CustomBiome(String name, String type, float temperature, float humidity, int grassColor, int waterColor) {
        this.name = name;
        this.type = type;
        this.temperature = temperature;
        this.humidity = humidity;
        this.grassColor = grassColor;
        this.waterColor = waterColor;
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("type", type);
        tag.putFloat("temperature", temperature);
        tag.putFloat("humidity", humidity);
        tag.putInt("grassColor", grassColor);
        tag.putInt("waterColor", waterColor);
        return tag;
    }
    
    public static CustomBiome load(CompoundTag tag) {
        return new CustomBiome(
            tag.getString("name"),
            tag.getString("type"),
            tag.getFloat("temperature"),
            tag.getFloat("humidity"),
            tag.getInt("grassColor"),
            tag.getInt("waterColor")
        );
    }
    
    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public float getTemperature() { return temperature; }
    public float getHumidity() { return humidity; }
    public int getGrassColor() { return grassColor; }
    public int getWaterColor() { return waterColor; }
}