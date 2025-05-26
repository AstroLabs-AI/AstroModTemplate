package com.astrolabs.arcanecodex.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import java.util.Set;

public interface IConsciousness {
    
    int getConsciousnessLevel();
    
    void setConsciousnessLevel(int level);
    
    long getNeuralCharge();
    
    void setNeuralCharge(long charge);
    
    long getMaxNeuralCharge();
    
    boolean consumeNeuralCharge(long amount);
    
    void addNeuralCharge(long amount);
    
    Set<ResourceLocation> getUnlockedResearch();
    
    boolean hasResearch(ResourceLocation research);
    
    void unlockResearch(ResourceLocation research);
    
    Set<ResourceLocation> getInstalledAugments();
    
    boolean hasAugment(ResourceLocation augment);
    
    void installAugment(ResourceLocation augment, String slot);
    
    void removeAugment(String slot);
    
    ResourceLocation getAugmentInSlot(String slot);
    
    float getSynergyBonus();
    
    void recalculateSynergies();
    
    CompoundTag serializeNBT();
    
    void deserializeNBT(CompoundTag nbt);
}