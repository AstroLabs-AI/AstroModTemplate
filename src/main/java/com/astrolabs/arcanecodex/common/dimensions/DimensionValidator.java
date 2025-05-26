package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.common.dimensions.properties.DimensionProperties;
import com.astrolabs.arcanecodex.common.dimensions.properties.DimensionRule;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DimensionValidator {
    
    private static final float MIN_GRAVITY = -2.0f;
    private static final float MAX_GRAVITY = 5.0f;
    private static final float MIN_TIME_FLOW = 0.01f;
    private static final float MAX_TIME_FLOW = 10.0f;
    private static final float MAX_INSTABILITY = 1.0f;
    private static final float MAX_ENERGY_DENSITY = 10.0f;
    
    public static class ValidationResult {
        public final boolean valid;
        public final List<Component> warnings;
        public final List<Component> errors;
        public final int complexityScore;
        public final long energyCost;
        
        private ValidationResult(boolean valid, List<Component> warnings, List<Component> errors, 
                               int complexityScore, long energyCost) {
            this.valid = valid;
            this.warnings = warnings;
            this.errors = errors;
            this.complexityScore = complexityScore;
            this.energyCost = energyCost;
        }
    }
    
    public static ValidationResult validate(DimensionProperties properties) {
        List<Component> warnings = new ArrayList<>();
        List<Component> errors = new ArrayList<>();
        int complexityScore = 0;
        
        // Validate gravity
        if (properties.getGravity() < MIN_GRAVITY || properties.getGravity() > MAX_GRAVITY) {
            errors.add(Component.literal("Gravity must be between " + MIN_GRAVITY + " and " + MAX_GRAVITY));
        } else if (Math.abs(properties.getGravity()) < 0.1f) {
            warnings.add(Component.literal("Very low gravity may cause movement issues"));
            complexityScore += 2;
        }
        
        // Validate time flow
        if (properties.getTimeFlow() < MIN_TIME_FLOW || properties.getTimeFlow() > MAX_TIME_FLOW) {
            errors.add(Component.literal("Time flow must be between " + MIN_TIME_FLOW + " and " + MAX_TIME_FLOW));
        } else if (properties.getTimeFlow() > 5.0f || properties.getTimeFlow() < 0.2f) {
            warnings.add(Component.literal("Extreme time flow may cause performance issues"));
            complexityScore += 3;
        }
        
        // Validate instability
        if (properties.getInstability() < 0 || properties.getInstability() > MAX_INSTABILITY) {
            errors.add(Component.literal("Instability must be between 0 and " + MAX_INSTABILITY));
        } else if (properties.getInstability() > 0.7f) {
            warnings.add(Component.literal("High instability will cause frequent reality glitches"));
            complexityScore += 5;
        }
        
        // Validate energy density
        if (properties.getEnergyDensity() < 0 || properties.getEnergyDensity() > MAX_ENERGY_DENSITY) {
            errors.add(Component.literal("Energy density must be between 0 and " + MAX_ENERGY_DENSITY));
        }
        
        // Validate biomes
        if (properties.getBiomes().isEmpty()) {
            warnings.add(Component.literal("No custom biomes defined, default biome will be used"));
        } else {
            complexityScore += properties.getBiomes().size();
        }
        
        // Validate rules
        for (DimensionRule rule : properties.getRules()) {
            complexityScore += validateRule(rule, warnings);
        }
        
        // Calculate terrain complexity
        switch (properties.getTerrainType()) {
            case FRACTAL:
            case QUANTUM:
                complexityScore += 10;
                break;
            case CRYSTALLINE:
            case INVERTED:
                complexityScore += 5;
                break;
            case ISLANDS:
            case GRID:
                complexityScore += 3;
                break;
            default:
                complexityScore += 1;
        }
        
        // Calculate energy cost based on complexity
        long baseCost = 10000L;
        long energyCost = baseCost * (1 + complexityScore);
        
        // Add warnings for high complexity
        if (complexityScore > 20) {
            warnings.add(Component.literal("Very high complexity - dimension creation may take longer"));
        }
        
        boolean valid = errors.isEmpty();
        
        return new ValidationResult(valid, warnings, errors, complexityScore, energyCost);
    }
    
    private static int validateRule(DimensionRule rule, List<Component> warnings) {
        int complexity = 1;
        
        switch (rule.getName().toUpperCase()) {
            case "NO_MOBS":
                if (rule.getBooleanValue()) {
                    complexity = 2;
                }
                break;
            case "ALWAYS_DAY":
            case "ALWAYS_NIGHT":
                if (rule.getBooleanValue()) {
                    warnings.add(Component.literal("Constant time may affect certain game mechanics"));
                    complexity = 3;
                }
                break;
            case "MOB_SPAWN_RATE":
                float rate = rule.getFloatValue();
                if (rate > 5.0f) {
                    warnings.add(Component.literal("High mob spawn rate may cause performance issues"));
                    complexity = 4;
                }
                break;
            case "RANDOM_TICK_SPEED":
                float tickSpeed = rule.getFloatValue();
                if (tickSpeed > 10.0f) {
                    warnings.add(Component.literal("High tick speed may cause lag"));
                    complexity = 5;
                }
                break;
        }
        
        return complexity;
    }
}