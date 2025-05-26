package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class NeuralResonatorAugment extends AugmentItem {
    
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("f8c3d2e1-7a5b-4c9d-8e2f-1a3b5d7c9e0f");
    private static final UUID LUCK_UUID = UUID.fromString("a9b8c7d6-5e4f-3a2b-1c9d-8e7f6a5b4c3d");
    
    public NeuralResonatorAugment(Properties properties) {
        super(properties, "heart", 5);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Increase movement speed based on neural charge level
        float chargeRatio = (float) consciousness.getNeuralCharge() / consciousness.getMaxNeuralCharge();
        float speedBonus = chargeRatio * 0.2f; // Up to 20% speed boost
        
        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(
            new AttributeModifier(MOVEMENT_SPEED_UUID, "Neural Resonator", speedBonus, AttributeModifier.Operation.MULTIPLY_BASE)
        );
        
        // Also add luck based on consciousness level
        float luckBonus = consciousness.getConsciousnessLevel() * 0.5f;
        player.getAttribute(Attributes.LUCK).addTransientModifier(
            new AttributeModifier(LUCK_UUID, "Neural Resonator Luck", luckBonus, AttributeModifier.Operation.ADDITION)
        );
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(MOVEMENT_SPEED_UUID);
        player.getAttribute(Attributes.LUCK).removeModifier(LUCK_UUID);
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Synchronizes heartbeat with quantum field fluctuations")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• Movement Speed scales with Neural Charge")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• +0.5 Luck per Consciousness Level")
            .withStyle(ChatFormatting.GRAY));
    }
}