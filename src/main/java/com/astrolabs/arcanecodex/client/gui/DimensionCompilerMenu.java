package com.astrolabs.arcanecodex.client.gui;

import com.astrolabs.arcanecodex.common.blockentities.DimensionCompilerCoreBlockEntity;
import com.astrolabs.arcanecodex.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DimensionCompilerMenu extends AbstractContainerMenu {
    public final DimensionCompilerCoreBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;
    
    // Client constructor
    public DimensionCompilerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(data.readBlockPos()));
    }
    
    // Server constructor
    public DimensionCompilerMenu(int containerId, Inventory playerInventory, BlockEntity blockEntity) {
        super(ModMenuTypes.DIMENSION_COMPILER.get(), containerId);
        
        if (blockEntity instanceof DimensionCompilerCoreBlockEntity dimensionCompiler) {
            this.blockEntity = dimensionCompiler;
        } else {
            throw new IllegalStateException("Invalid block entity type for DimensionCompilerMenu");
        }
        
        this.levelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY; // No item slots in this container
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, this.blockEntity.getBlockState().getBlock());
    }
}