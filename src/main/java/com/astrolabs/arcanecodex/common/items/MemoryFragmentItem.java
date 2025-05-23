package com.astrolabs.arcanecodex.common.items;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.research.ResearchNode;
import com.astrolabs.arcanecodex.common.research.ResearchTree;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
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
import java.util.Random;

public class MemoryFragmentItem extends Item {
    
    private static final Random RANDOM = new Random();
    
    public MemoryFragmentItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            
            // Generate research hint if not already present
            if (!tag.contains("ResearchHint")) {
                generateResearchHint(stack, player);
            }
            
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                // Grant small amount of neural charge
                consciousness.addNeuralCharge(10 + RANDOM.nextInt(20));
                
                // Show hint
                if (tag.contains("ResearchHint")) {
                    ResourceLocation hintId = new ResourceLocation(tag.getString("ResearchHint"));
                    ResearchNode node = ResearchTree.getNode(hintId);
                    if (node != null) {
                        player.sendSystemMessage(Component.literal("Memory Fragment reveals: ")
                            .withStyle(ChatFormatting.LIGHT_PURPLE)
                            .append(node.getName().copy().withStyle(ChatFormatting.AQUA)));
                        
                        // Small chance to unlock research directly
                        if (RANDOM.nextFloat() < 0.1f && node.canUnlock(
                                consciousness.getUnlockedResearch().stream().toList(), 
                                consciousness.getConsciousnessLevel())) {
                            consciousness.unlockResearch(hintId);
                            player.sendSystemMessage(Component.literal("Breakthrough! Research unlocked!")
                                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
                        }
                    }
                }
                
                stack.shrink(1);
            });
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    private void generateResearchHint(ItemStack stack, Player player) {
        player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
            List<ResearchNode> available = ResearchTree.getAvailableResearch(
                consciousness.getUnlockedResearch().stream().toList(),
                consciousness.getConsciousnessLevel()
            );
            
            if (!available.isEmpty()) {
                ResearchNode hint = available.get(RANDOM.nextInt(available.size()));
                stack.getOrCreateTag().putString("ResearchHint", hint.getId().toString());
                stack.getOrCreateTag().putString("HintName", hint.getName().getString());
            }
        });
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("A fragment of lost knowledge").withStyle(ChatFormatting.GRAY));
        
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("HintName")) {
            tooltip.add(Component.literal("Contains memories of: " + tag.getString("HintName"))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Component.literal("Use to reveal its secrets").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().contains("ResearchHint");
    }
}