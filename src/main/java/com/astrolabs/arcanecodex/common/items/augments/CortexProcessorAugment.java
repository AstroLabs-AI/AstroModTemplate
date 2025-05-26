package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class CortexProcessorAugment extends AugmentItem {
    
    public CortexProcessorAugment(Properties properties) {
        super(properties, "cortex", 1);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Increase max neural charge by 50%
        long currentMax = consciousness.getMaxNeuralCharge();
        consciousness.setNeuralCharge(consciousness.getNeuralCharge()); // This will recalculate max based on level
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        // Recalculate max neural charge
        consciousness.setNeuralCharge(consciousness.getNeuralCharge());
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Enhanced cognitive processing")
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("• +50% Max Neural Charge")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• +25% Research Speed")
            .withStyle(ChatFormatting.GRAY));
    }
}