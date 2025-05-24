package com.astrolabs.arcanecodex.common.items.tools;

import com.astrolabs.arcanecodex.common.systems.quantum.QuantumEntanglementManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class QuantumEntangler extends Item {
    
    public QuantumEntangler(Properties properties) {
        super(properties.stacksTo(1));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        
        if (!level.isClientSide && player != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                CompoundTag tag = stack.getOrCreateTag();
                
                if (!tag.contains("EntangledPos")) {
                    // First selection
                    tag.putLong("EntangledPos", pos.asLong());
                    tag.putString("EntangledDim", level.dimension().location().toString());
                    
                    player.sendSystemMessage(Component.literal("First quantum anchor set at " + pos.toShortString())
                        .withStyle(ChatFormatting.AQUA));
                    
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.5f, 2.0f);
                    
                    // Spawn particles
                    spawnEntanglementParticles(level, pos);
                } else {
                    // Second selection - create entanglement
                    BlockPos firstPos = BlockPos.of(tag.getLong("EntangledPos"));
                    String firstDim = tag.getString("EntangledDim");
                    
                    if (firstDim.equals(level.dimension().location().toString()) && !firstPos.equals(pos)) {
                        // Create entanglement
                        UUID pairId = QuantumEntanglementManager.createEntanglement(level, firstPos, pos);
                        
                        player.sendSystemMessage(Component.literal("Quantum entanglement established!")
                            .withStyle(ChatFormatting.GREEN));
                        player.sendSystemMessage(Component.literal("Distance: " + 
                            String.format("%.1f blocks", firstPos.distSqr(pos)))
                            .withStyle(ChatFormatting.GRAY));
                        
                        level.playSound(null, pos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 0.5f, 1.5f);
                        
                        // Clear the tag
                        tag.remove("EntangledPos");
                        tag.remove("EntangledDim");
                        
                        // Spawn connection particles
                        spawnConnectionParticles(level, firstPos, pos);
                    } else {
                        player.sendSystemMessage(Component.literal("Cannot entangle across dimensions or same position!")
                            .withStyle(ChatFormatting.RED));
                    }
                }
                
                return InteractionResult.SUCCESS;
            }
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (player.isShiftKeyDown()) {
            // Clear selection
            stack.getOrCreateTag().remove("EntangledPos");
            stack.getOrCreateTag().remove("EntangledDim");
            
            if (!level.isClientSide) {
                player.sendSystemMessage(Component.literal("Entanglement data cleared")
                    .withStyle(ChatFormatting.YELLOW));
            }
            
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        
        return InteractionResultHolder.pass(stack);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && selected && level.getGameTime() % 20 == 0) {
            CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains("EntangledPos")) {
                BlockPos pos = BlockPos.of(tag.getLong("EntangledPos"));
                
                // Check if the entangled position still exists
                if (level.isLoaded(pos) && level.getBlockEntity(pos) == null) {
                    tag.remove("EntangledPos");
                    tag.remove("EntangledDim");
                }
            }
        }
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("EntangledPos")) {
            BlockPos pos = BlockPos.of(tag.getLong("EntangledPos"));
            tooltip.add(Component.literal("Linked to: " + pos.toShortString())
                .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.literal("Right-click blocks to create quantum entanglement")
                .withStyle(ChatFormatting.GRAY));
        }
        
        tooltip.add(Component.literal("Shift + Right-click to clear")
            .withStyle(ChatFormatting.DARK_GRAY));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains("EntangledPos");
    }
    
    private void spawnEntanglementParticles(Level level, BlockPos pos) {
        if (level.isClientSide) return;
        
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2;
            double y = pos.getY() + 0.5 + (level.random.nextDouble() - 0.5) * 2;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2;
            
            level.addParticle(
                com.astrolabs.arcanecodex.common.particles.ModParticles.QUANTUM_ENERGY.get(),
                x, y, z, 0, 0.1, 0
            );
        }
    }
    
    private void spawnConnectionParticles(Level level, BlockPos pos1, BlockPos pos2) {
        if (level.isClientSide) return;
        
        double distance = Math.sqrt(pos1.distSqr(pos2));
        int particleCount = (int)(distance * 2);
        
        for (int i = 0; i < particleCount; i++) {
            double t = i / (double)particleCount;
            double x = pos1.getX() + 0.5 + (pos2.getX() - pos1.getX()) * t;
            double y = pos1.getY() + 0.5 + (pos2.getY() - pos1.getY()) * t;
            double z = pos1.getZ() + 0.5 + (pos2.getZ() - pos1.getZ()) * t;
            
            level.addParticle(
                com.astrolabs.arcanecodex.common.particles.ModParticles.HOLOGRAPHIC.get(),
                x, y, z, 0, 0, 0
            );
        }
    }
}