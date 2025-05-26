package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class SynapticBoosterAugment extends AugmentItem {
    
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("e7d4f5c3-9a2b-4f8e-a1d6-3c2a5b7e9f0d");
    
    public SynapticBoosterAugment(Properties properties) {
        super(properties, "spine", 3);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Increase attack speed
        player.getAttribute(Attributes.ATTACK_SPEED).addTransientModifier(
            new AttributeModifier(ATTACK_SPEED_UUID, "Synaptic Booster", 0.3, AttributeModifier.Operation.MULTIPLY_BASE)
        );
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.ATTACK_SPEED).removeModifier(ATTACK_SPEED_UUID);
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Accelerates synaptic response times")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• +30% Attack Speed")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• +5 Neural Charge/3s")
            .withStyle(ChatFormatting.GRAY));
    }
}