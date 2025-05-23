package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AstroExpansion.MODID);
    
    public static final RegistryObject<CreativeModeTab> ASTRO_TAB = CREATIVE_MODE_TABS.register("astro_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT.get()))
            .title(Component.translatable("creativetab.astroexpansion"))
            .displayItems((params, output) -> {
                // Ores
                output.accept(ModBlocks.TITANIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_TITANIUM_ORE.get());
                output.accept(ModBlocks.LITHIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_LITHIUM_ORE.get());
                output.accept(ModBlocks.URANIUM_ORE.get());
                output.accept(ModBlocks.DEEPSLATE_URANIUM_ORE.get());
                
                // Raw Materials
                output.accept(ModItems.RAW_TITANIUM.get());
                output.accept(ModItems.RAW_LITHIUM.get());
                output.accept(ModItems.RAW_URANIUM.get());
                
                // Ingots
                output.accept(ModItems.TITANIUM_INGOT.get());
                output.accept(ModItems.LITHIUM_INGOT.get());
                output.accept(ModItems.URANIUM_INGOT.get());
                
                // Dusts
                output.accept(ModItems.TITANIUM_DUST.get());
                output.accept(ModItems.LITHIUM_DUST.get());
                output.accept(ModItems.URANIUM_DUST.get());
                
                // Blocks
                output.accept(ModBlocks.TITANIUM_BLOCK.get());
                output.accept(ModBlocks.LITHIUM_BLOCK.get());
                output.accept(ModBlocks.URANIUM_BLOCK.get());
                
                // Components
                output.accept(ModItems.CIRCUIT_BOARD.get());
                output.accept(ModItems.PROCESSOR.get());
                output.accept(ModItems.ENERGY_CORE.get());
                
                // Machines
                output.accept(ModBlocks.BASIC_GENERATOR.get());
                output.accept(ModBlocks.MATERIAL_PROCESSOR.get());
                output.accept(ModBlocks.ORE_WASHER.get());
                output.accept(ModBlocks.ENERGY_CONDUIT.get());
                output.accept(ModBlocks.ENERGY_STORAGE.get());
                
                // Storage System
                output.accept(ModBlocks.STORAGE_CORE.get());
                output.accept(ModBlocks.STORAGE_TERMINAL.get());
                output.accept(ModItems.STORAGE_DRIVE_1K.get());
                output.accept(ModItems.STORAGE_DRIVE_4K.get());
                output.accept(ModItems.STORAGE_DRIVE_16K.get());
                output.accept(ModItems.STORAGE_DRIVE_64K.get());
                output.accept(ModItems.STORAGE_HOUSING.get());
                output.accept(ModItems.STORAGE_PROCESSOR.get());
                
                // Drones
                output.accept(ModBlocks.DRONE_DOCK.get());
                output.accept(ModItems.MINING_DRONE.get());
                output.accept(ModItems.DRONE_CORE.get());
                output.accept(ModItems.DRONE_SHELL.get());
                
                // Tools
                output.accept(ModItems.WRENCH.get());
            })
            .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}