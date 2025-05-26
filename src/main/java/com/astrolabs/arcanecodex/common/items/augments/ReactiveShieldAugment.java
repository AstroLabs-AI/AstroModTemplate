package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ReactiveShieldAugment extends AugmentItem {
    
    private static final float DAMAGE_REDUCTION = 0.15f;
    private static final int COOLDOWN_TICKS = 100; // 5 seconds
    
    public ReactiveShieldAugment(Properties properties) {
        super(properties, "shoulder", 4);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Effects are handled through event system
        // Could store player UUID for event handling
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        // No persistent effects to remove
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Quantum probability field deflects incoming damage")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• 15% Damage Reduction (5s cooldown)")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Holographic shield projection")
            .withStyle(ChatFormatting.GRAY));
    }
}