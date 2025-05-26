package com.astrolabs.arcanecodex.common.items.tools;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.capabilities.ConsciousnessCapability;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.dimensions.DimensionStabilityManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DimensionStabilizerItem extends Item {
    private static final int CHARGE_COST = 20;
    private static final float STABILITY_BOOST = 0.1f;
    
    public DimensionStabilizerItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        
        // Check if in custom dimension
        if (!level.dimension().location().getNamespace().equals("arcanecodex") ||
            !level.dimension().location().getPath().startsWith("custom/")) {
            player.displayClientMessage(Component.literal("§cThis item only works in custom dimensions!"), true);
            return InteractionResultHolder.fail(stack);
        }
        
        // Check consciousness and neural charge
        var capOptional = player.getCapability(ModCapabilities.CONSCIOUSNESS);
        if (!capOptional.isPresent()) {
            return InteractionResultHolder.fail(stack);
        }
        
        var consciousness = capOptional.orElse(null);
        long currentCharge = consciousness.getNeuralCharge();
        if (currentCharge < CHARGE_COST) {
            player.displayClientMessage(Component.literal("§cInsufficient Neural Charge! Need " + CHARGE_COST), true);
            return InteractionResultHolder.fail(stack);
        }
        
        // Consume charge
        consciousness.addNeuralCharge(-CHARGE_COST);
        
        // Stabilize dimension
        ServerLevel serverLevel = (ServerLevel) level;
        DimensionStabilityManager manager = DimensionStabilityManager.get(serverLevel.getServer());
        manager.stabilizeDimension(level.dimension().location(), STABILITY_BOOST);
        
        // Effects
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0));
        player.displayClientMessage(Component.literal("§aDimension stabilized! +" + (int)(STABILITY_BOOST * 100) + "% stability"), true);
        
        // Show current stability
        float currentStability = manager.getStability(level.dimension().location());
        player.displayClientMessage(Component.literal("§7Current stability: " + (int)(currentStability * 100) + "%"), true);
        
        // Damage item
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        
        // Cooldown
        player.getCooldowns().addCooldown(this, 200);
        
        return InteractionResultHolder.consume(stack);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7Stabilizes custom dimensions"));
        tooltip.add(Component.literal("§7Cost: §b" + CHARGE_COST + " Neural Charge"));
        tooltip.add(Component.literal("§7Boost: §a+" + (int)(STABILITY_BOOST * 100) + "% stability"));
        
        if (level != null && level.dimension().location().getNamespace().equals("arcanecodex") &&
            level.dimension().location().getPath().startsWith("custom/")) {
            
            // Show current dimension stability if in a custom dimension
            if (level instanceof ServerLevel serverLevel) {
                DimensionStabilityManager manager = DimensionStabilityManager.get(serverLevel.getServer());
                float stability = manager.getStability(level.dimension().location());
                
                ChatFormatting color = stability > 0.7f ? ChatFormatting.GREEN :
                                      stability > 0.3f ? ChatFormatting.YELLOW :
                                      ChatFormatting.RED;
                
                tooltip.add(Component.literal(""));
                tooltip.add(Component.literal("§7Current Dimension Stability: ")
                    .append(Component.literal((int)(stability * 100) + "%").withStyle(color)));
            }
        }
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}