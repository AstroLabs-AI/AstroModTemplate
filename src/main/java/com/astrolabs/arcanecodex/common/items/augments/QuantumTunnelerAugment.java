package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class QuantumTunnelerAugment extends AugmentItem {
    
    private static final int PHASE_COST = 50;
    private static final int MAX_DISTANCE = 7;
    
    public QuantumTunnelerAugment(Properties properties) {
        super(properties, "hand", 4);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Effects handled through use action
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        // No persistent effects
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Quantum probability allows brief phasing")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• Right-click: Phase through walls")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Cost: 50 Neural Charge")
            .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("• Range: 7 blocks")
            .withStyle(ChatFormatting.GRAY));
    }
}