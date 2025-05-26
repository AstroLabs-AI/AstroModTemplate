package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class NeuralLinkAugment extends AugmentItem {
    
    public NeuralLinkAugment(Properties properties) {
        super(properties, "neural", 2);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Increase max neural charge - handled by capability
        // The actual bonus is applied in ConsciousnessCapability based on installed augments
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
        tooltip.add(Component.literal("+50 Max Neural Charge")
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Allows quantum entanglement")
            .withStyle(ChatFormatting.DARK_AQUA));
    }
}