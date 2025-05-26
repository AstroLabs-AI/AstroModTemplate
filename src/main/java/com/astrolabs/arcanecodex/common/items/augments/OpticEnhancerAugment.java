package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class OpticEnhancerAugment extends AugmentItem {
    
    public OpticEnhancerAugment(Properties properties) {
        super(properties, "optics", 2);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Grant permanent night vision
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 
            Integer.MAX_VALUE, 0, false, false));
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Quantum-enhanced vision")
            .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("• Permanent Night Vision")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• See energy signatures")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• +Synergy: Cortex = Predictive Targeting")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}