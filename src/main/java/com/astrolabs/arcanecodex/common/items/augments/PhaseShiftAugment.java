package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PhaseShiftAugment extends AugmentItem {
    
    public PhaseShiftAugment(Properties properties) {
        super(properties, "skeletal", 3);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        player.setMaxHealth(player.getMaxHealth() + 4.0f);
        player.setHealth(player.getHealth() + 4.0f);
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.setMaxHealth(player.getMaxHealth() - 4.0f);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("+2 Hearts")
            .withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Phase through reality")
            .withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.literal("Damage resistance when phasing")
            .withStyle(ChatFormatting.DARK_RED));
    }
}