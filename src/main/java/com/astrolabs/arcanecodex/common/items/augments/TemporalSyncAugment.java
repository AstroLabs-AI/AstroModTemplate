package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class TemporalSyncAugment extends AugmentItem {
    
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("7f3d82a4-1234-4567-8910-111213141516");
    
    public TemporalSyncAugment(Properties properties) {
        super(properties, "respiratory", 3);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(
            new AttributeModifier(SPEED_MODIFIER_UUID, "Temporal Sync", 0.2, AttributeModifier.Operation.MULTIPLY_BASE)
        );
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_UUID);
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("+20% Movement Speed")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("Temporal field manipulation")
            .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Slows time perception")
            .withStyle(ChatFormatting.DARK_PURPLE));
    }
}