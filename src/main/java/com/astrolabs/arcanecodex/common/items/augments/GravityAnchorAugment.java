package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class GravityAnchorAugment extends AugmentItem {
    
    private static final float FALL_DAMAGE_REDUCTION = 0.75f;
    
    public GravityAnchorAugment(Properties properties) {
        super(properties, "feet", 2);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Effects are handled through tick and event system
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        // No persistent effects to remove
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("Manipulates local gravity field")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("• 10% Slower Falling")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• 75% Fall Damage Reduction")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("• Consumes Neural Charge on impact")
            .withStyle(ChatFormatting.GOLD));
    }
    
    // Gravity reduction could be handled in a tick event handler
    public void applyGravityReduction(Player player) {
        if (!player.onGround() && player.getDeltaMovement().y < 0) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, motion.y * 0.9, motion.z);
        }
    }
}