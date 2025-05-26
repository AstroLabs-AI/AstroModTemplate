package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class QuantumCoreAugment extends AugmentItem {
    
    public QuantumCoreAugment(Properties properties) {
        super(properties, "cardiovascular", 4);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Increase max neural charge
        long currentMax = consciousness.getMaxNeuralCharge();
        consciousness.setNeuralCharge(consciousness.getNeuralCharge()); // This will be handled by capability
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        // Decrease max neural charge - handled by capability
        long currentCharge = consciousness.getNeuralCharge();
        if (currentCharge > consciousness.getMaxNeuralCharge()) {
            consciousness.setNeuralCharge(consciousness.getMaxNeuralCharge());
        }
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("+100 Max Neural Charge")
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("+50% Energy Resonance")
            .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Quantum coherence amplification")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}