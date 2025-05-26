package com.astrolabs.arcanecodex.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RPLCodexItem extends Item {
    
    private final String codexType;
    
    public RPLCodexItem(Properties properties, String codexType) {
        super(properties);
        this.codexType = codexType;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!level.isClientSide) {
            // Create a written book with RPL code
            ItemStack book = new ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK);
            CompoundTag bookTag = book.getOrCreateTag();
            
            bookTag.putString("author", "Quantum Architect");
            bookTag.putString("title", "RPL Codex: " + codexType);
            bookTag.putBoolean("resolved", true);
            
            ListTag pages = new ListTag();
            
            switch (codexType) {
                case "basics" -> {
                    pages.add(StringTag.valueOf(Component.Serializer.toJson(
                        Component.literal("// Reality Programming\n// Basic Commands\n\n")
                            .append("reality.manifest({\n")
                            .append("  type: \"gravitational_anomaly\",\n")
                            .append("  strength: 9.8,\n")
                            .append("  duration: 100\n")
                            .append("})")
                    )));
                    
                    pages.add(StringTag.valueOf(Component.Serializer.toJson(
                        Component.literal("// Phase Shifting\n\n")
                            .append("player.augment(\"phase_shift\", {\n")
                            .append("  density: 0.1,\n")
                            .append("  duration: 200\n")
                            .append("})")
                    )));
                }
                
                case "quantum" -> {
                    pages.add(StringTag.valueOf(Component.Serializer.toJson(
                        Component.literal("// Quantum Mechanics\n\n")
                            .append("quantum.measure(\"entities\")\n\n")
                            .append("quantum.measure(\"probability\")")
                    )));
                    
                    pages.add(StringTag.valueOf(Component.Serializer.toJson(
                        Component.literal("// Energy Cascade\n\n")
                            .append("energy.cascade({\n")
                            .append("  type: \"quantum_foam\",\n")
                            .append("  amount: 1000,\n")
                            .append("  radius: 5\n")
                            .append("})")
                    )));
                }
                
                case "temporal" -> {
                    pages.add(StringTag.valueOf(Component.Serializer.toJson(
                        Component.literal("// Time Manipulation\n\n")
                            .append("time.dilate({\n")
                            .append("  factor: 2.0,\n")
                            .append("  radius: 5,\n")
                            .append("  duration: 200\n")
                            .append("})")
                    )));
                }
            }
            
            bookTag.put("pages", pages);
            book.setTag(bookTag);
            
            // Give the book to the player
            if (!player.getInventory().add(book)) {
                player.drop(book, false);
            }
            
            player.sendSystemMessage(Component.literal("RPL Codex materialized!").withStyle(ChatFormatting.AQUA));
            
            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }
        
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Contains Reality Programming Language examples").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Type: " + codexType).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Use to create a written book with code").withStyle(ChatFormatting.DARK_GRAY));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}