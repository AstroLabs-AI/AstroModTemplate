package com.astrolabs.arcanecodex.client.gui;

import com.astrolabs.arcanecodex.common.blockentities.RealityCompilerBlockEntity;
import com.astrolabs.arcanecodex.common.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RealityCompilerMenu extends AbstractContainerMenu {
    
    private final RealityCompilerBlockEntity blockEntity;
    
    public RealityCompilerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, data.readBlockPos());
    }
    
    public RealityCompilerMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(ModMenuTypes.REALITY_COMPILER.get(), containerId);
        
        BlockEntity be = playerInventory.player.level().getBlockEntity(pos);
        if (be instanceof RealityCompilerBlockEntity) {
            this.blockEntity = (RealityCompilerBlockEntity) be;
        } else {
            this.blockEntity = null;
        }
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && 
               player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5, 
                                   blockEntity.getBlockPos().getY() + 0.5, 
                                   blockEntity.getBlockPos().getZ() + 0.5) <= 64.0;
    }
    
    public RealityCompilerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}