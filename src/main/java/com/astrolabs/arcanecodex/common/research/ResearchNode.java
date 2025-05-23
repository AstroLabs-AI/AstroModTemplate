package com.astrolabs.arcanecodex.common.research;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ResearchNode {
    
    private final ResourceLocation id;
    private final Component name;
    private final Component description;
    private final ItemStack icon;
    private final int requiredLevel;
    private final int neuralCost;
    private final ResearchCategory category;
    
    // Position in 3D space for holographic display
    private final float x, y, z;
    
    // Prerequisites
    private final List<ResourceLocation> prerequisites = new ArrayList<>();
    
    // Rewards
    private final List<ResourceLocation> unlockedRecipes = new ArrayList<>();
    private final List<ResourceLocation> unlockedAugments = new ArrayList<>();
    private final List<String> unlockedAbilities = new ArrayList<>();
    
    public enum ResearchCategory {
        QUANTUM_CONSCIOUSNESS("quantum_consciousness", 0xFF00FF),
        DIGITAL_TRANSCENDENCE("digital_transcendence", 0x00FFFF),
        REALITY_COMPILATION("reality_compilation", 0xFFFF00),
        TEMPORAL_MECHANICS("temporal_mechanics", 0xFFD700),
        DIMENSIONAL_WEAVING("dimensional_weaving", 0x9932CC);
        
        private final String name;
        private final int color;
        
        ResearchCategory(String name, int color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() { return name; }
        public int getColor() { return color; }
    }
    
    public ResearchNode(ResourceLocation id, Component name, Component description, 
                       ItemStack icon, int requiredLevel, int neuralCost, 
                       ResearchCategory category, float x, float y, float z) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.requiredLevel = requiredLevel;
        this.neuralCost = neuralCost;
        this.category = category;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public ResearchNode addPrerequisite(ResourceLocation prerequisite) {
        prerequisites.add(prerequisite);
        return this;
    }
    
    public ResearchNode addRecipeUnlock(ResourceLocation recipe) {
        unlockedRecipes.add(recipe);
        return this;
    }
    
    public ResearchNode addAugmentUnlock(ResourceLocation augment) {
        unlockedAugments.add(augment);
        return this;
    }
    
    public ResearchNode addAbilityUnlock(String ability) {
        unlockedAbilities.add(ability);
        return this;
    }
    
    public boolean canUnlock(List<ResourceLocation> unlockedResearch, int consciousnessLevel) {
        if (consciousnessLevel < requiredLevel) return false;
        
        for (ResourceLocation prereq : prerequisites) {
            if (!unlockedResearch.contains(prereq)) return false;
        }
        
        return true;
    }
    
    // Getters
    public ResourceLocation getId() { return id; }
    public Component getName() { return name; }
    public Component getDescription() { return description; }
    public ItemStack getIcon() { return icon; }
    public int getRequiredLevel() { return requiredLevel; }
    public int getNeuralCost() { return neuralCost; }
    public ResearchCategory getCategory() { return category; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
    public List<ResourceLocation> getPrerequisites() { return prerequisites; }
    public List<ResourceLocation> getUnlockedRecipes() { return unlockedRecipes; }
    public List<ResourceLocation> getUnlockedAugments() { return unlockedAugments; }
    public List<String> getUnlockedAbilities() { return unlockedAbilities; }
}