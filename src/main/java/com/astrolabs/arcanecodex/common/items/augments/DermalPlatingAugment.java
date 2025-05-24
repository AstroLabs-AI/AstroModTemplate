package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public class DermalPlatingAugment extends AugmentItem {
    
    private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("8f3d82a4-5678-4567-8910-121314151617");
    private static final UUID TOUGHNESS_MODIFIER_UUID = UUID.fromString("9f3d82a4-5678-4567-8910-121314151618");
    
    public DermalPlatingAugment(Properties properties) {
        super(properties, "dermal", 2);
    }
    
    @Override
    protected void applyAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.ARMOR).addTransientModifier(
            new AttributeModifier(ARMOR_MODIFIER_UUID, "Dermal Plating", 4.0, AttributeModifier.Operation.ADDITION)
        );
        player.getAttribute(Attributes.ARMOR_TOUGHNESS).addTransientModifier(
            new AttributeModifier(TOUGHNESS_MODIFIER_UUID, "Dermal Toughness", 2.0, AttributeModifier.Operation.ADDITION)
        );
    }
    
    @Override
    protected void removeAugmentEffects(Player player, IConsciousness consciousness) {
        player.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
        player.getAttribute(Attributes.ARMOR_TOUGHNESS).removeModifier(TOUGHNESS_MODIFIER_UUID);
    }
    
    @Override
    protected void addAugmentDescription(List<Component> tooltip) {
        tooltip.add(Component.literal("+4 Armor")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("+2 Armor Toughness")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Nano-reinforced skin")
            .withStyle(ChatFormatting.DARK_GRAY));
    }
}