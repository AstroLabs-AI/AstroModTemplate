package com.astrolabs.arcanecodex.common.items.tools;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class QuantumScannerItem extends Item {
    
    public QuantumScannerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Player player = context.getPlayer();
            BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
            
            if (blockEntity != null) {
                blockEntity.getCapability(ModCapabilities.QUANTUM_ENERGY).ifPresent(energy -> {
                    player.sendSystemMessage(Component.literal("=== Quantum Energy Scan ===").withStyle(ChatFormatting.AQUA));
                    
                    for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
                        long stored = energy.getEnergyStored(type);
                        long max = energy.getMaxEnergyStored(type);
                        float resonance = energy.getResonance(type);
                        
                        if (max > 0) {
                            Component typeComponent = Component.literal(type.getName())
                                .withStyle(style -> style.withColor(type.getColor()));
                            
                            Component energyInfo = Component.literal(
                                String.format(": %d / %d (%.1f%% resonance)", stored, max, resonance * 100)
                            ).withStyle(ChatFormatting.WHITE);
                            
                            player.sendSystemMessage(typeComponent.copy().append(energyInfo));
                        }
                    }
                });
                
                // Also scan for consciousness if it's a player
                if (player != null) {
                    player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                        player.sendSystemMessage(Component.literal("\n=== Consciousness Scan ===").withStyle(ChatFormatting.LIGHT_PURPLE));
                        player.sendSystemMessage(Component.literal("Level: " + consciousness.getConsciousnessLevel()));
                        player.sendSystemMessage(Component.literal("Neural Charge: " + consciousness.getNeuralCharge() + " / " + consciousness.getMaxNeuralCharge()));
                        player.sendSystemMessage(Component.literal("Research Unlocked: " + consciousness.getUnlockedResearch().size()));
                        player.sendSystemMessage(Component.literal("Augments Installed: " + consciousness.getInstalledAugments().size()));
                        player.sendSystemMessage(Component.literal("Synergy Bonus: " + String.format("%.0f%%", (consciousness.getSynergyBonus() - 1) * 100)));
                    });
                }
                
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.arcanecodex.quantum_scanner.tooltip").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Use on machines to scan quantum energy").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("Sneak-use to scan your consciousness").withStyle(ChatFormatting.DARK_GRAY));
    }
}