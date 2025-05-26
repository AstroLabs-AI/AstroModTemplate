package com.astrolabs.arcanecodex.common.dimensions.properties;

public enum TerrainType {
    NORMAL("Normal terrain generation"),
    FLAT("Flat world generation"),
    ISLANDS("Floating islands"),
    VOID("Empty void with platforms"),
    FRACTAL("Fractal-based terrain"),
    CRYSTALLINE("Crystal formations"),
    LIQUID("Ocean-based world"),
    INVERTED("Upside-down terrain"),
    GRID("Grid-based structures"),
    QUANTUM("Quantum superposition terrain");
    
    private final String description;
    
    TerrainType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}