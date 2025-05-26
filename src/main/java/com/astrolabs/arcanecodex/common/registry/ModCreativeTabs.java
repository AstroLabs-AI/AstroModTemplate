package com.astrolabs.arcanecodex.common.registry;

import com.astrolabs.arcanecodex.ArcaneCodex;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<CreativeModeTab> ARCANE_CODEX_TAB = CREATIVE_MODE_TABS.register("arcane_codex_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.QUANTUM_SCANNER.get()))
            .title(Component.translatable("creativetab.arcanecodex"))
            .displayItems((parameters, output) -> {
                // Tools
                output.accept(ModItems.QUANTUM_SCANNER.get());
                output.accept(ModItems.NANO_MULTITOOL.get());
                
                // Components
                output.accept(ModItems.MEMORY_FRAGMENT.get());
                output.accept(ModItems.QUANTUM_CORE.get());
                output.accept(ModItems.NEURAL_MATRIX.get());
                
                // Augments
                output.accept(ModItems.CORTEX_PROCESSOR.get());
                output.accept(ModItems.OPTIC_ENHANCER.get());
                
                // RPL Codex
                output.accept(ModItems.RPL_CODEX_BASICS.get());
                output.accept(ModItems.RPL_CODEX_QUANTUM.get());
                output.accept(ModItems.RPL_CODEX_TEMPORAL.get());
                
                // Blocks
                output.accept(ModBlocks.QUANTUM_HARVESTER.get());
                output.accept(ModBlocks.NEURAL_INTERFACE.get());
                output.accept(ModBlocks.QUANTUM_CONDUIT.get());
                output.accept(ModBlocks.AUGMENTATION_TABLE.get());
                output.accept(ModBlocks.REALITY_COMPILER.get());
            })
            .build());
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}