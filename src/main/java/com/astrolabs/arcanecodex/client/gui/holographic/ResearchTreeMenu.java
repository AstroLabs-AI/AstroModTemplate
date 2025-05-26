package com.astrolabs.arcanecodex.client.gui.holographic;

import com.astrolabs.arcanecodex.common.registry.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ResearchTreeMenu extends AbstractContainerMenu {
    
    public ResearchTreeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory);
    }
    
    public ResearchTreeMenu(int containerId, Inventory playerInventory) {
        super(ModMenuTypes.RESEARCH_TREE.get(), containerId);
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}