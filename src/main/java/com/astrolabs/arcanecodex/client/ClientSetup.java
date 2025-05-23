package com.astrolabs.arcanecodex.client;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.client.particles.HolographicParticle;
import com.astrolabs.arcanecodex.client.particles.NeuralSparkParticle;
import com.astrolabs.arcanecodex.common.particles.ModParticles;
import com.astrolabs.arcanecodex.common.particles.QuantumParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneCodex.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.QUANTUM_ENERGY.get(), QuantumParticle.Factory::new);
        event.registerSpriteSet(ModParticles.NEURAL_SPARK.get(), NeuralSparkParticle.Factory::new);
        event.registerSpriteSet(ModParticles.HOLOGRAPHIC.get(), HolographicParticle.Factory::new);
        // Reality glitch particle uses quantum particle with different behavior
        event.registerSpriteSet(ModParticles.REALITY_GLITCH.get(), QuantumParticle.Factory::new);
    }
}