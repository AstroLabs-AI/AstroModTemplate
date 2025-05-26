package com.astrolabs.arcanecodex.common.research;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class ResearchTree {
    
    private static final Map<ResourceLocation, ResearchNode> RESEARCH_NODES = new HashMap<>();
    private static final Map<ResearchNode.ResearchCategory, List<ResearchNode>> NODES_BY_CATEGORY = new HashMap<>();
    
    // Research IDs
    public static final ResourceLocation DIGITAL_AWAKENING = new ResourceLocation(ArcaneCodex.MOD_ID, "digital_awakening");
    public static final ResourceLocation QUANTUM_BASICS = new ResourceLocation(ArcaneCodex.MOD_ID, "quantum_basics");
    public static final ResourceLocation NEURAL_EXPANSION = new ResourceLocation(ArcaneCodex.MOD_ID, "neural_expansion");
    public static final ResourceLocation ENERGY_MANIPULATION = new ResourceLocation(ArcaneCodex.MOD_ID, "energy_manipulation");
    public static final ResourceLocation AUGMENT_INTEGRATION = new ResourceLocation(ArcaneCodex.MOD_ID, "augment_integration");
    public static final ResourceLocation REALITY_CODING = new ResourceLocation(ArcaneCodex.MOD_ID, "reality_coding");
    
    static {
        initializeResearchTree();
    }
    
    private static void initializeResearchTree() {
        // Tier 0: Starting Research
        registerNode(new ResearchNode(
            DIGITAL_AWAKENING,
            Component.literal("Digital Awakening"),
            Component.literal("Your first step into quantum consciousness"),
            new ItemStack(ModItems.QUANTUM_SCANNER.get()),
            0, 0,
            ResearchNode.ResearchCategory.QUANTUM_CONSCIOUSNESS,
            0, 0, 0
        ).addRecipeUnlock(new ResourceLocation(ArcaneCodex.MOD_ID, "quantum_scanner")));
        
        // Tier 1: Basic Research
        registerNode(new ResearchNode(
            QUANTUM_BASICS,
            Component.literal("Quantum Fundamentals"),
            Component.literal("Understanding the nature of quantum energy"),
            new ItemStack(ModItems.QUANTUM_CORE.get()),
            1, 100,
            ResearchNode.ResearchCategory.QUANTUM_CONSCIOUSNESS,
            -2, 0, 1
        ).addPrerequisite(DIGITAL_AWAKENING)
         .addRecipeUnlock(new ResourceLocation(ArcaneCodex.MOD_ID, "quantum_harvester")));
        
        registerNode(new ResearchNode(
            NEURAL_EXPANSION,
            Component.literal("Neural Expansion"),
            Component.literal("Expand your consciousness capacity"),
            new ItemStack(ModItems.NEURAL_MATRIX.get()),
            1, 150,
            ResearchNode.ResearchCategory.DIGITAL_TRANSCENDENCE,
            2, 0, 1
        ).addPrerequisite(DIGITAL_AWAKENING)
         .addRecipeUnlock(new ResourceLocation(ArcaneCodex.MOD_ID, "neural_interface")));
        
        // Tier 2: Advanced Research
        registerNode(new ResearchNode(
            ENERGY_MANIPULATION,
            Component.literal("Energy Manipulation"),
            Component.literal("Control the flow of quantum energies"),
            new ItemStack(Items.ENDER_EYE),
            2, 300,
            ResearchNode.ResearchCategory.REALITY_COMPILATION,
            0, 1, 2
        ).addPrerequisite(QUANTUM_BASICS)
         .addPrerequisite(NEURAL_EXPANSION)
         .addAbilityUnlock("energy_sight"));
        
        registerNode(new ResearchNode(
            AUGMENT_INTEGRATION,
            Component.literal("Augment Integration"),
            Component.literal("Merge technology with biology"),
            new ItemStack(ModItems.CORTEX_PROCESSOR.get()),
            2, 250,
            ResearchNode.ResearchCategory.DIGITAL_TRANSCENDENCE,
            3, 0, 2
        ).addPrerequisite(NEURAL_EXPANSION)
         .addAugmentUnlock(new ResourceLocation(ArcaneCodex.MOD_ID, "neural_augment")));
        
        // Tier 3: Reality Programming
        registerNode(new ResearchNode(
            REALITY_CODING,
            Component.literal("Reality Programming"),
            Component.literal("Write code that reshapes the world"),
            new ItemStack(Items.COMMAND_BLOCK),
            3, 500,
            ResearchNode.ResearchCategory.REALITY_COMPILATION,
            0, 2, 3
        ).addPrerequisite(ENERGY_MANIPULATION)
         .addAbilityUnlock("reality_programming_language"));
    }
    
    private static void registerNode(ResearchNode node) {
        RESEARCH_NODES.put(node.getId(), node);
        NODES_BY_CATEGORY.computeIfAbsent(node.getCategory(), k -> new ArrayList<>()).add(node);
    }
    
    public static ResearchNode getNode(ResourceLocation id) {
        return RESEARCH_NODES.get(id);
    }
    
    public static Collection<ResearchNode> getAllNodes() {
        return RESEARCH_NODES.values();
    }
    
    public static List<ResearchNode> getNodesByCategory(ResearchNode.ResearchCategory category) {
        return NODES_BY_CATEGORY.getOrDefault(category, Collections.emptyList());
    }
    
    public static List<ResearchNode> getAvailableResearch(List<ResourceLocation> unlockedResearch, int consciousnessLevel) {
        List<ResearchNode> available = new ArrayList<>();
        
        for (ResearchNode node : RESEARCH_NODES.values()) {
            if (!unlockedResearch.contains(node.getId()) && node.canUnlock(unlockedResearch, consciousnessLevel)) {
                available.add(node);
            }
        }
        
        return available;
    }
    
    public static List<SynapticLink> getSynapticLinks(List<ResourceLocation> unlockedResearch) {
        List<SynapticLink> links = new ArrayList<>();
        
        for (ResearchNode node : RESEARCH_NODES.values()) {
            for (ResourceLocation prereq : node.getPrerequisites()) {
                ResearchNode prereqNode = RESEARCH_NODES.get(prereq);
                if (prereqNode != null) {
                    boolean active = unlockedResearch.contains(prereq) && unlockedResearch.contains(node.getId());
                    links.add(new SynapticLink(prereqNode, node, active));
                }
            }
        }
        
        return links;
    }
    
    public static class SynapticLink {
        public final ResearchNode from;
        public final ResearchNode to;
        public final boolean active;
        
        public SynapticLink(ResearchNode from, ResearchNode to, boolean active) {
            this.from = from;
            this.to = to;
            this.active = active;
        }
    }
}