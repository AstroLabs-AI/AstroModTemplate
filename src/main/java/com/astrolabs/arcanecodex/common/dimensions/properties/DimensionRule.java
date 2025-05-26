package com.astrolabs.arcanecodex.common.dimensions.properties;

import net.minecraft.nbt.CompoundTag;

public class DimensionRule {
    private final String name;
    private final String value;
    
    public DimensionRule(String name, String value) {
        this.name = name;
        this.value = value;
        validateRule();
    }
    
    private void validateRule() {
        // Validate known rules
        switch (name.toUpperCase()) {
            case "NO_MOBS":
            case "NO_WEATHER":
            case "ALWAYS_DAY":
            case "ALWAYS_NIGHT":
            case "NO_HUNGER":
            case "INSTANT_RESPAWN":
            case "KEEP_INVENTORY":
                if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("Rule " + name + " must be true or false");
                }
                break;
            case "MOB_SPAWN_RATE":
            case "BLOCK_TICK_SPEED":
            case "RANDOM_TICK_SPEED":
                try {
                    Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Rule " + name + " must be a number");
                }
                break;
            case "ALLOWED_BLOCKS":
            case "FORBIDDEN_BLOCKS":
                // These can be comma-separated lists, no validation needed
                break;
            default:
                // Allow custom rules for future expansion
                break;
        }
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putString("value", value);
        return tag;
    }
    
    public static DimensionRule load(CompoundTag tag) {
        return new DimensionRule(
            tag.getString("name"),
            tag.getString("value")
        );
    }
    
    public String getName() { return name; }
    public String getValue() { return value; }
    
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(value);
    }
    
    public float getFloatValue() {
        return Float.parseFloat(value);
    }
    
    public String[] getListValue() {
        return value.split(",");
    }
}