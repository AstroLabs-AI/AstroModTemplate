package com.astrolabs.arcanecodex.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ArcaneCodexConfig {
    public static ForgeConfigSpec CLIENT_CONFIG;
    public static ForgeConfigSpec COMMON_CONFIG;
    
    public static ForgeConfigSpec.IntValue QUANTUM_HARVESTER_ENERGY_RATE;
    public static ForgeConfigSpec.IntValue QUANTUM_CONDUIT_CAPACITY;
    public static ForgeConfigSpec.IntValue NEURAL_INTERFACE_CHARGE_COST;
    public static ForgeConfigSpec.IntValue DIMENSION_STABILITY_DECAY_RATE;
    public static ForgeConfigSpec.IntValue MAX_DIMENSIONS_PER_PLAYER;
    
    public static ForgeConfigSpec.BooleanValue ENABLE_PARTICLE_EFFECTS;
    public static ForgeConfigSpec.BooleanValue ENABLE_RESEARCH_ANIMATIONS;
    public static ForgeConfigSpec.IntValue PARTICLE_DENSITY;
    
    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        
        COMMON_BUILDER.comment("General settings").push("general");
        
        QUANTUM_HARVESTER_ENERGY_RATE = COMMON_BUILDER
                .comment("Energy generation rate for Quantum Harvester (per tick)")
                .defineInRange("quantumHarvesterRate", 10, 1, 1000);
                
        QUANTUM_CONDUIT_CAPACITY = COMMON_BUILDER
                .comment("Energy capacity of Quantum Conduits")
                .defineInRange("quantumConduitCapacity", 10000, 1000, 100000);
                
        NEURAL_INTERFACE_CHARGE_COST = COMMON_BUILDER
                .comment("Neural charge cost per consciousness expansion")
                .defineInRange("neuralInterfaceCost", 100, 10, 1000);
                
        DIMENSION_STABILITY_DECAY_RATE = COMMON_BUILDER
                .comment("Base stability decay rate for custom dimensions")
                .defineInRange("dimensionStabilityDecay", 1, 0, 100);
                
        MAX_DIMENSIONS_PER_PLAYER = COMMON_BUILDER
                .comment("Maximum number of dimensions per player")
                .defineInRange("maxDimensionsPerPlayer", 5, 1, 50);
                
        COMMON_BUILDER.pop();
        
        CLIENT_BUILDER.comment("Client settings").push("client");
        
        ENABLE_PARTICLE_EFFECTS = CLIENT_BUILDER
                .comment("Enable particle effects")
                .define("enableParticles", true);
                
        ENABLE_RESEARCH_ANIMATIONS = CLIENT_BUILDER
                .comment("Enable 3D research tree animations")
                .define("enableResearchAnimations", true);
                
        PARTICLE_DENSITY = CLIENT_BUILDER
                .comment("Particle effect density (1-10)")
                .defineInRange("particleDensity", 5, 1, 10);
                
        CLIENT_BUILDER.pop();
        
        COMMON_CONFIG = COMMON_BUILDER.build();
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
    
    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
    }
}