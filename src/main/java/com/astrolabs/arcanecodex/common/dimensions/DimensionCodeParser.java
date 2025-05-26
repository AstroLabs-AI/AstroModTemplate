package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.common.dimensions.properties.*;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DimensionCodeParser {
    
    // Pattern for dimension property commands
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("^\\s*([A-Z_]+)\\s*:\\s*(.+)$");
    private static final Pattern BIOME_PATTERN = Pattern.compile("^\\s*BIOME\\s*\\{([^}]+)\\}$");
    private static final Pattern RULE_PATTERN = Pattern.compile("^\\s*RULE\\s*\\{([^}]+)\\}$");
    
    public static class ParseResult {
        public final boolean success;
        public final DimensionProperties properties;
        public final List<Component> errors;
        
        private ParseResult(boolean success, DimensionProperties properties, List<Component> errors) {
            this.success = success;
            this.properties = properties;
            this.errors = errors;
        }
        
        public static ParseResult success(DimensionProperties properties) {
            return new ParseResult(true, properties, Collections.emptyList());
        }
        
        public static ParseResult failure(List<Component> errors) {
            return new ParseResult(false, null, errors);
        }
    }
    
    public static ParseResult parse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return ParseResult.failure(List.of(Component.literal("Dimension code cannot be empty")));
        }
        
        List<Component> errors = new ArrayList<>();
        DimensionProperties.Builder builder = DimensionProperties.builder();
        
        // Split code into lines and process each
        String[] lines = code.split("\n");
        int lineNumber = 0;
        
        for (String line : lines) {
            lineNumber++;
            line = line.trim();
            
            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            
            // Check for biome definition
            Matcher biomeMatcher = BIOME_PATTERN.matcher(line);
            if (biomeMatcher.matches()) {
                parseBiome(biomeMatcher.group(1), builder, errors, lineNumber);
                continue;
            }
            
            // Check for rule definition
            Matcher ruleMatcher = RULE_PATTERN.matcher(line);
            if (ruleMatcher.matches()) {
                parseRule(ruleMatcher.group(1), builder, errors, lineNumber);
                continue;
            }
            
            // Check for property definition
            Matcher propertyMatcher = PROPERTY_PATTERN.matcher(line);
            if (propertyMatcher.matches()) {
                String property = propertyMatcher.group(1);
                String value = propertyMatcher.group(2).trim();
                parseProperty(property, value, builder, errors, lineNumber);
                continue;
            }
            
            errors.add(Component.literal("Line " + lineNumber + ": Invalid syntax - " + line));
        }
        
        // Validate required properties
        if (!builder.hasName()) {
            errors.add(Component.literal("Missing required property: NAME"));
        }
        
        if (!errors.isEmpty()) {
            return ParseResult.failure(errors);
        }
        
        return ParseResult.success(builder.build());
    }
    
    private static void parseProperty(String property, String value, DimensionProperties.Builder builder,
                                    List<Component> errors, int lineNumber) {
        try {
            switch (property) {
                case "NAME":
                    builder.name(value);
                    break;
                case "SEED":
                    builder.seed(Long.parseLong(value));
                    break;
                case "GRAVITY":
                    builder.gravity(Float.parseFloat(value));
                    break;
                case "TIME_FLOW":
                    builder.timeFlow(Float.parseFloat(value));
                    break;
                case "LIGHT_LEVEL":
                    builder.lightLevel(Integer.parseInt(value));
                    break;
                case "SKY_COLOR":
                    builder.skyColor(parseColor(value));
                    break;
                case "FOG_COLOR":
                    builder.fogColor(parseColor(value));
                    break;
                case "TERRAIN_TYPE":
                    builder.terrainType(TerrainType.valueOf(value.toUpperCase()));
                    break;
                case "HAS_CEILING":
                    builder.hasCeiling(Boolean.parseBoolean(value));
                    break;
                case "HAS_SKYLIGHT":
                    builder.hasSkylight(Boolean.parseBoolean(value));
                    break;
                case "ENERGY_DENSITY":
                    builder.energyDensity(Float.parseFloat(value));
                    break;
                case "INSTABILITY":
                    builder.instability(Float.parseFloat(value));
                    break;
                default:
                    errors.add(Component.literal("Line " + lineNumber + ": Unknown property - " + property));
            }
        } catch (Exception e) {
            errors.add(Component.literal("Line " + lineNumber + ": Invalid value for " + property + " - " + e.getMessage()));
        }
    }
    
    private static void parseBiome(String biomeData, DimensionProperties.Builder builder,
                                 List<Component> errors, int lineNumber) {
        // Parse biome properties
        Map<String, String> biomeProps = new HashMap<>();
        String[] parts = biomeData.split(",");
        
        for (String part : parts) {
            String[] keyValue = part.trim().split("=");
            if (keyValue.length == 2) {
                biomeProps.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        
        String name = biomeProps.get("name");
        if (name == null) {
            errors.add(Component.literal("Line " + lineNumber + ": Biome missing name"));
            return;
        }
        
        try {
            CustomBiome biome = new CustomBiome(
                name,
                biomeProps.getOrDefault("type", "plains"),
                Float.parseFloat(biomeProps.getOrDefault("temperature", "0.5")),
                Float.parseFloat(biomeProps.getOrDefault("humidity", "0.5")),
                parseColor(biomeProps.getOrDefault("grassColor", "0x88CC88")),
                parseColor(biomeProps.getOrDefault("waterColor", "0x3F76E4"))
            );
            builder.addBiome(biome);
        } catch (Exception e) {
            errors.add(Component.literal("Line " + lineNumber + ": Invalid biome data - " + e.getMessage()));
        }
    }
    
    private static void parseRule(String ruleData, DimensionProperties.Builder builder,
                                List<Component> errors, int lineNumber) {
        String[] parts = ruleData.split(":");
        if (parts.length != 2) {
            errors.add(Component.literal("Line " + lineNumber + ": Invalid rule format"));
            return;
        }
        
        String ruleName = parts[0].trim();
        String ruleValue = parts[1].trim();
        
        try {
            DimensionRule rule = new DimensionRule(ruleName, ruleValue);
            builder.addRule(rule);
        } catch (Exception e) {
            errors.add(Component.literal("Line " + lineNumber + ": Invalid rule - " + e.getMessage()));
        }
    }
    
    private static int parseColor(String colorStr) {
        if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
            return Integer.parseInt(colorStr.substring(2), 16);
        } else if (colorStr.startsWith("#")) {
            return Integer.parseInt(colorStr.substring(1), 16);
        } else {
            return Integer.parseInt(colorStr);
        }
    }
}