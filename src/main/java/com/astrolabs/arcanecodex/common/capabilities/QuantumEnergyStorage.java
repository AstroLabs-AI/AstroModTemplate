package com.astrolabs.arcanecodex.common.capabilities;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.EnumMap;
import java.util.Map;

public class QuantumEnergyStorage implements IQuantumEnergy, INBTSerializable<CompoundTag> {
    
    private final Map<EnergyType, Long> energyStored = new EnumMap<>(EnergyType.class);
    private final Map<EnergyType, Long> maxEnergy = new EnumMap<>(EnergyType.class);
    private final Map<EnergyType, Float> resonance = new EnumMap<>(EnergyType.class);
    private final long defaultMaxEnergy;
    private boolean canExtract = true;
    private boolean canReceive = true;
    
    public QuantumEnergyStorage(long defaultMaxEnergy) {
        this.defaultMaxEnergy = defaultMaxEnergy;
        for (EnergyType type : EnergyType.values()) {
            energyStored.put(type, 0L);
            maxEnergy.put(type, defaultMaxEnergy);
            resonance.put(type, 1.0f);
        }
    }
    
    @Override
    public long insertEnergy(EnergyType type, long amount, boolean simulate) {
        if (!canReceive || amount <= 0) return 0;
        
        long stored = energyStored.get(type);
        long max = maxEnergy.get(type);
        long toStore = Math.min(amount, max - stored);
        
        if (!simulate && toStore > 0) {
            long oldAmount = stored;
            energyStored.put(type, stored + toStore);
            onEnergyChanged(type, oldAmount, stored + toStore);
            updateResonance(type);
        }
        
        return toStore;
    }
    
    @Override
    public long extractEnergy(EnergyType type, long maxExtract, boolean simulate) {
        if (!canExtract || maxExtract <= 0) return 0;
        
        long stored = energyStored.get(type);
        long toExtract = Math.min(maxExtract, stored);
        
        if (!simulate && toExtract > 0) {
            long oldAmount = stored;
            energyStored.put(type, stored - toExtract);
            onEnergyChanged(type, oldAmount, stored - toExtract);
            updateResonance(type);
        }
        
        return toExtract;
    }
    
    @Override
    public long getEnergyStored(EnergyType type) {
        return energyStored.get(type);
    }
    
    @Override
    public long getMaxEnergyStored(EnergyType type) {
        return maxEnergy.get(type);
    }
    
    @Override
    public boolean canExtract(Direction side, EnergyType type) {
        return canExtract;
    }
    
    @Override
    public boolean canReceive(Direction side, EnergyType type) {
        return canReceive;
    }
    
    @Override
    public float getResonance(EnergyType type) {
        return resonance.get(type);
    }
    
    private void updateResonance(EnergyType type) {
        // Living energy behavior - resonance changes based on usage patterns
        float currentResonance = resonance.get(type);
        long stored = energyStored.get(type);
        long max = maxEnergy.get(type);
        
        // Calculate new resonance based on fill ratio and neighboring energy types
        float fillRatio = (float) stored / max;
        float targetResonance = 0.5f + (fillRatio * 0.5f);
        
        // Check for resonance cascades with other energy types
        for (EnergyType otherType : EnergyType.values()) {
            if (otherType != type && energyStored.get(otherType) > 0) {
                targetResonance += 0.1f;
            }
        }
        
        // Smoothly adjust resonance
        float newResonance = currentResonance + (targetResonance - currentResonance) * 0.1f;
        resonance.put(type, Math.min(2.0f, newResonance));
    }
    
    public void setCanExtract(boolean canExtract) {
        this.canExtract = canExtract;
    }
    
    public void setCanReceive(boolean canReceive) {
        this.canReceive = canReceive;
    }
    
    public void setMaxEnergy(EnergyType type, long maxEnergy) {
        this.maxEnergy.put(type, maxEnergy);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag energyTag = new CompoundTag();
        CompoundTag maxTag = new CompoundTag();
        CompoundTag resonanceTag = new CompoundTag();
        
        for (EnergyType type : EnergyType.values()) {
            energyTag.putLong(type.getName(), energyStored.get(type));
            maxTag.putLong(type.getName(), maxEnergy.get(type));
            resonanceTag.putFloat(type.getName(), resonance.get(type));
        }
        
        tag.put("Energy", energyTag);
        tag.put("MaxEnergy", maxTag);
        tag.put("Resonance", resonanceTag);
        tag.putBoolean("CanExtract", canExtract);
        tag.putBoolean("CanReceive", canReceive);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        CompoundTag energyTag = tag.getCompound("Energy");
        CompoundTag maxTag = tag.getCompound("MaxEnergy");
        CompoundTag resonanceTag = tag.getCompound("Resonance");
        
        for (EnergyType type : EnergyType.values()) {
            energyStored.put(type, energyTag.getLong(type.getName()));
            maxEnergy.put(type, maxTag.contains(type.getName()) ? 
                maxTag.getLong(type.getName()) : defaultMaxEnergy);
            resonance.put(type, resonanceTag.contains(type.getName()) ? 
                resonanceTag.getFloat(type.getName()) : 1.0f);
        }
        
        canExtract = tag.getBoolean("CanExtract");
        canReceive = tag.getBoolean("CanReceive");
    }
}