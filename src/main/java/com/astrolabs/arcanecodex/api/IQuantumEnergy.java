package com.astrolabs.arcanecodex.api;

import net.minecraft.core.Direction;

public interface IQuantumEnergy {
    
    enum EnergyType {
        COHERENT_LIGHT("coherent_light", 0x00FFFF),
        QUANTUM_FOAM("quantum_foam", 0xFF00FF),
        NEURAL_CHARGE("neural_charge", 0x9932CC),
        TEMPORAL_FLUX("temporal_flux", 0xFFD700),
        DARK_CURRENT("dark_current", 0x4B0082),
        SYNTHESIS_WAVE("synthesis_wave", 0x00FF00);
        
        private final String name;
        private final int color;
        
        EnergyType(String name, int color) {
            this.name = name;
            this.color = color;
        }
        
        public String getName() { return name; }
        public int getColor() { return color; }
    }
    
    long insertEnergy(EnergyType type, long amount, boolean simulate);
    
    long extractEnergy(EnergyType type, long maxExtract, boolean simulate);
    
    long getEnergyStored(EnergyType type);
    
    long getMaxEnergyStored(EnergyType type);
    
    boolean canExtract(Direction side, EnergyType type);
    
    boolean canReceive(Direction side, EnergyType type);
    
    default float getResonance(EnergyType type) {
        return 1.0f;
    }
    
    default void onEnergyChanged(EnergyType type, long oldAmount, long newAmount) {}
}