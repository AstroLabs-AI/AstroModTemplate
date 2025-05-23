package com.astrolabs.arcanecodex.common.items.tools;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NanoMultitool extends DiggerItem {
    
    private static final Tier NANO_TIER = new Tier() {
        @Override
        public int getUses() {
            return 2000;
        }
        
        @Override
        public float getSpeed() {
            return 8.0F;
        }
        
        @Override
        public float getAttackDamageBonus() {
            return 3.0F;
        }
        
        @Override
        public int getLevel() {
            return 3;
        }
        
        @Override
        public int getEnchantmentValue() {
            return 15;
        }
        
        @Override
        public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
            return net.minecraft.world.item.crafting.Ingredient.of(Items.DIAMOND);
        }
    };
    
    public NanoMultitool(Properties properties) {
        super(5.0F, -2.8F, NANO_TIER, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }
    
    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        // Can mine anything a pickaxe, axe, or shovel can mine
        return TierSortingRegistry.isCorrectTierForDrops(getTier(), state) &&
               (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || 
                state.is(BlockTags.MINEABLE_WITH_AXE) || 
                state.is(BlockTags.MINEABLE_WITH_SHOVEL));
    }
    
    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        float speed = super.getDestroySpeed(stack, state);
        
        // Check if any material tag matches
        if (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            speed = getTier().getSpeed();
        }
        
        // Bonus speed based on consciousness level
        if (stack.getOrCreateTag().contains("BoundPlayer")) {
            Player player = getOwner(stack);
            if (player != null) {
                player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                    speed *= (1.0F + consciousness.getConsciousnessLevel() * 0.1F);
                });
            }
        }
        
        return speed;
    }
    
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level.isClientSide && attacker instanceof Player player) {
            // Drain neural charge on hit for bonus damage
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                if (consciousness.consumeNeuralCharge(50)) {
                    target.hurt(player.level.damageSources().magic(), 2.0F);
                    // Add visual effect
                    player.level.broadcastEntityEvent(target, (byte) 20);
                }
            });
        }
        return super.hurtEnemy(stack, target, attacker);
    }
    
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            // Small chance to not consume durability based on consciousness
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                float chance = consciousness.getConsciousnessLevel() * 0.05F;
                if (level.random.nextFloat() < chance) {
                    stack.setDamageValue(stack.getDamageValue() - 1);
                }
            });
        }
        return super.mineBlock(stack, level, state, pos, entity);
    }
    
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        // Bind to player
        stack.getOrCreateTag().putUUID("BoundPlayer", player.getUUID());
    }
    
    private Player getOwner(ItemStack stack) {
        if (stack.getOrCreateTag().contains("BoundPlayer")) {
            Level level = net.minecraft.client.Minecraft.getInstance().level;
            if (level != null) {
                return level.getPlayerByUUID(stack.getOrCreateTag().getUUID("BoundPlayer"));
            }
        }
        return null;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Adaptive nano-technology tool").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Functions as pickaxe, axe, and shovel").withStyle(ChatFormatting.DARK_GRAY));
        
        if (stack.getOrCreateTag().contains("BoundPlayer") && level != null) {
            Player owner = getOwner(stack);
            if (owner != null) {
                tooltip.add(Component.literal("Bound to: " + owner.getName().getString()).withStyle(ChatFormatting.AQUA));
                owner.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                    tooltip.add(Component.literal("Consciousness Bonus: +" + (consciousness.getConsciousnessLevel() * 10) + "%").withStyle(ChatFormatting.LIGHT_PURPLE));
                });
            }
        }
    }
}