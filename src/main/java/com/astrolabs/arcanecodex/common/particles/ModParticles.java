package com.astrolabs.arcanecodex.common.particles;

import com.astrolabs.arcanecodex.ArcaneCodex;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = 
        DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<SimpleParticleType> QUANTUM_ENERGY = PARTICLE_TYPES.register("quantum_energy",
        () -> new SimpleParticleType(true));
    
    public static final RegistryObject<SimpleParticleType> NEURAL_SPARK = PARTICLE_TYPES.register("neural_spark",
        () -> new SimpleParticleType(true));
    
    public static final RegistryObject<SimpleParticleType> HOLOGRAPHIC = PARTICLE_TYPES.register("holographic",
        () -> new SimpleParticleType(true));
    
    public static final RegistryObject<SimpleParticleType> REALITY_GLITCH = PARTICLE_TYPES.register("reality_glitch",
        () -> new SimpleParticleType(true));
    
    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}