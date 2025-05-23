package com.astrolabs.arcanecodex.common.capabilities;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class ConsciousnessCapability implements IConsciousness {
    
    private int consciousnessLevel = 0;
    private long neuralCharge = 0;
    private long maxNeuralCharge = 1000;
    private final Set<ResourceLocation> unlockedResearch = new HashSet<>();
    private final Map<String, ResourceLocation> augmentSlots = new HashMap<>();
    private float synergyBonus = 1.0f;
    
    // Augment slots
    public static final String SLOT_CORTEX = "cortex";
    public static final String SLOT_OPTICS = "optics";
    public static final String SLOT_RESPIRATORY = "respiratory";
    public static final String SLOT_CARDIOVASCULAR = "cardiovascular";
    public static final String SLOT_SKELETAL = "skeletal";
    public static final String SLOT_DERMAL = "dermal";
    public static final String SLOT_NEURAL = "neural";
    
    @Override
    public int getConsciousnessLevel() {
        return consciousnessLevel;
    }
    
    @Override
    public void setConsciousnessLevel(int level) {
        this.consciousnessLevel = Math.max(0, level);
        this.maxNeuralCharge = 1000L * (level + 1);
    }
    
    @Override
    public long getNeuralCharge() {
        return neuralCharge;
    }
    
    @Override
    public void setNeuralCharge(long charge) {
        this.neuralCharge = Math.max(0, Math.min(charge, maxNeuralCharge));
    }
    
    @Override
    public long getMaxNeuralCharge() {
        return maxNeuralCharge;
    }
    
    @Override
    public boolean consumeNeuralCharge(long amount) {
        if (neuralCharge >= amount) {
            neuralCharge -= amount;
            return true;
        }
        return false;
    }
    
    @Override
    public void addNeuralCharge(long amount) {
        setNeuralCharge(neuralCharge + amount);
    }
    
    @Override
    public Set<ResourceLocation> getUnlockedResearch() {
        return Collections.unmodifiableSet(unlockedResearch);
    }
    
    @Override
    public boolean hasResearch(ResourceLocation research) {
        return unlockedResearch.contains(research);
    }
    
    @Override
    public void unlockResearch(ResourceLocation research) {
        unlockedResearch.add(research);
        // Increase consciousness level for major discoveries
        if (unlockedResearch.size() % 5 == 0) {
            setConsciousnessLevel(consciousnessLevel + 1);
        }
    }
    
    @Override
    public Set<ResourceLocation> getInstalledAugments() {
        return new HashSet<>(augmentSlots.values());
    }
    
    @Override
    public boolean hasAugment(ResourceLocation augment) {
        return augmentSlots.containsValue(augment);
    }
    
    @Override
    public void installAugment(ResourceLocation augment, String slot) {
        augmentSlots.put(slot, augment);
        recalculateSynergies();
    }
    
    @Override
    public void removeAugment(String slot) {
        augmentSlots.remove(slot);
        recalculateSynergies();
    }
    
    @Override
    public ResourceLocation getAugmentInSlot(String slot) {
        return augmentSlots.get(slot);
    }
    
    @Override
    public float getSynergyBonus() {
        return synergyBonus;
    }
    
    @Override
    public void recalculateSynergies() {
        synergyBonus = 1.0f;
        
        // Check for specific synergy combinations
        if (hasAugmentCombo(SLOT_OPTICS, SLOT_CORTEX)) {
            synergyBonus += 0.2f; // Predictive targeting
        }
        
        if (hasAugmentCombo(SLOT_NEURAL, SLOT_CARDIOVASCULAR)) {
            synergyBonus += 0.3f; // Energy overflow
        }
        
        if (hasAugmentCombo(SLOT_DERMAL, SLOT_SKELETAL)) {
            synergyBonus += 0.25f; // Adaptive armor
        }
        
        // Bonus for full augmentation
        if (augmentSlots.size() >= 7) {
            synergyBonus += 0.5f;
        }
    }
    
    private boolean hasAugmentCombo(String slot1, String slot2) {
        return augmentSlots.containsKey(slot1) && augmentSlots.containsKey(slot2);
    }
    
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        
        tag.putInt("ConsciousnessLevel", consciousnessLevel);
        tag.putLong("NeuralCharge", neuralCharge);
        tag.putLong("MaxNeuralCharge", maxNeuralCharge);
        tag.putFloat("SynergyBonus", synergyBonus);
        
        // Save research
        ListTag researchList = new ListTag();
        for (ResourceLocation research : unlockedResearch) {
            researchList.add(StringTag.valueOf(research.toString()));
        }
        tag.put("Research", researchList);
        
        // Save augments
        CompoundTag augmentTag = new CompoundTag();
        for (Map.Entry<String, ResourceLocation> entry : augmentSlots.entrySet()) {
            augmentTag.putString(entry.getKey(), entry.getValue().toString());
        }
        tag.put("Augments", augmentTag);
        
        return tag;
    }
    
    @Override
    public void deserializeNBT(CompoundTag tag) {
        consciousnessLevel = tag.getInt("ConsciousnessLevel");
        neuralCharge = tag.getLong("NeuralCharge");
        maxNeuralCharge = tag.getLong("MaxNeuralCharge");
        synergyBonus = tag.getFloat("SynergyBonus");
        
        // Load research
        unlockedResearch.clear();
        ListTag researchList = tag.getList("Research", Tag.TAG_STRING);
        for (int i = 0; i < researchList.size(); i++) {
            unlockedResearch.add(new ResourceLocation(researchList.getString(i)));
        }
        
        // Load augments
        augmentSlots.clear();
        CompoundTag augmentTag = tag.getCompound("Augments");
        for (String slot : augmentTag.getAllKeys()) {
            augmentSlots.put(slot, new ResourceLocation(augmentTag.getString(slot)));
        }
    }
}