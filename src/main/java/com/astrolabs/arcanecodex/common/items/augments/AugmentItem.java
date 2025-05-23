package com.astrolabs.arcanecodex.common.items.augments;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AugmentItem extends Item {
    
    private final String slot;
    private final int requiredLevel;
    
    public AugmentItem(Properties properties, String slot, int requiredLevel) {
        super(properties.stacksTo(1));
        this.slot = slot;
        this.requiredLevel = requiredLevel;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                if (consciousness.getConsciousnessLevel() >= requiredLevel) {
                    ResourceLocation augmentId = new ResourceLocation(stack.getItem().getDescriptionId());
                    
                    // Check if slot is already occupied
                    ResourceLocation existing = consciousness.getAugmentInSlot(slot);
                    if (existing != null) {
                        player.sendSystemMessage(Component.literal("Augment slot already occupied!")
                            .withStyle(ChatFormatting.RED));
                        return;
                    }
                    
                    // Install augment
                    consciousness.installAugment(augmentId, slot);
                    applyAugmentEffects(player, consciousness);
                    
                    player.sendSystemMessage(Component.literal("Augment installed successfully!")
                        .withStyle(ChatFormatting.GREEN));
                    player.sendSystemMessage(Component.literal("Synergy Bonus: " + 
                        String.format("%.0f%%", (consciousness.getSynergyBonus() - 1) * 100))
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                    
                    stack.shrink(1);
                } else {
                    player.sendSystemMessage(Component.literal("Requires Consciousness Level " + requiredLevel)
                        .withStyle(ChatFormatting.RED));
                }
            });
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    protected abstract void applyAugmentEffects(Player player, IConsciousness consciousness);
    
    protected abstract void removeAugmentEffects(Player player, IConsciousness consciousness);
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Augment Slot: " + getSlotDisplayName())
            .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Required Level: " + requiredLevel)
            .withStyle(ChatFormatting.GRAY));
        
        addAugmentDescription(tooltip);
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Right-click to install")
            .withStyle(ChatFormatting.DARK_GRAY));
    }
    
    protected abstract void addAugmentDescription(List<Component> tooltip);
    
    protected String getSlotDisplayName() {
        return switch (slot) {
            case "cortex" -> "Cortex";
            case "optics" -> "Optics";
            case "respiratory" -> "Respiratory";
            case "cardiovascular" -> "Cardiovascular";
            case "skeletal" -> "Skeletal";
            case "dermal" -> "Dermal";
            case "neural" -> "Neural";
            default -> "Unknown";
        };
    }
    
    public String getSlot() {
        return slot;
    }
    
    public int getRequiredLevel() {
        return requiredLevel;
    }
}