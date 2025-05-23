package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.common.blocks.multiblocks.AugmentationTableBlock;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class AugmentationTableBlockEntity extends BlockEntity implements MenuProvider {
    
    private final ItemStackHandler inventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    
    private boolean multiblockFormed = false;
    private int animationTicks = 0;
    
    public AugmentationTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AUGMENTATION_TABLE.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, AugmentationTableBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) { // Check every second
            blockEntity.checkMultiblockStructure();
        }
        
        if (blockEntity.multiblockFormed) {
            // Process augmentation recipes
            blockEntity.processAugmentation();
        }
    }
    
    public static void clientTick(Level level, BlockPos pos, BlockState state, AugmentationTableBlockEntity blockEntity) {
        if (blockEntity.multiblockFormed) {
            blockEntity.animationTicks++;
            
            // Spawn holographic particles
            if (level.random.nextFloat() < 0.2f) {
                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2;
                double y = pos.getY() + 1.5;
                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2;
                
                // TODO: Add custom holographic particles
            }
        }
    }
    
    public boolean checkMultiblockStructure() {
        boolean wasFormed = multiblockFormed;
        multiblockFormed = true;
        
        // Check for Neural Matrix blocks at corners
        for (Direction horizontal : Direction.Plane.HORIZONTAL) {
            BlockPos cornerPos = worldPosition.relative(horizontal, 2).relative(horizontal.getClockWise(), 2);
            BlockState cornerState = level.getBlockState(cornerPos);
            
            // TODO: Replace with actual Neural Matrix block check
            if (cornerState.isAir()) {
                multiblockFormed = false;
                break;
            }
        }
        
        // Update block state if formation status changed
        if (wasFormed != multiblockFormed) {
            level.setBlock(worldPosition, getBlockState().setValue(AugmentationTableBlock.MULTIBLOCK_FORMED, multiblockFormed), 3);
            setChanged();
        }
        
        return multiblockFormed;
    }
    
    private void processAugmentation() {
        // Check for augmentation recipes
        // TODO: Implement augmentation crafting logic
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        multiblockFormed = tag.getBoolean("MultiblockFormed");
        animationTicks = tag.getInt("AnimationTicks");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", inventory.serializeNBT());
        tag.putBoolean("MultiblockFormed", multiblockFormed);
        tag.putInt("AnimationTicks", animationTicks);
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcanecodex.augmentation_table");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        // TODO: Create augmentation GUI
        return null;
    }
    
    public ItemStackHandler getInventory() {
        return inventory;
    }
    
    public boolean isMultiblockFormed() {
        return multiblockFormed;
    }
}