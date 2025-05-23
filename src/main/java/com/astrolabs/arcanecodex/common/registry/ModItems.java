package com.astrolabs.arcanecodex.common.registry;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.items.MemoryFragmentItem;
import com.astrolabs.arcanecodex.common.items.augments.CortexProcessorAugment;
import com.astrolabs.arcanecodex.common.items.augments.OpticEnhancerAugment;
import com.astrolabs.arcanecodex.common.items.tools.NanoMultitool;
import com.astrolabs.arcanecodex.common.items.tools.QuantumScannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<Item> QUANTUM_SCANNER = ITEMS.register("quantum_scanner",
        () -> new QuantumScannerItem(new Item.Properties().stacksTo(1)));
    
    public static final RegistryObject<Item> MEMORY_FRAGMENT = ITEMS.register("memory_fragment",
        () -> new MemoryFragmentItem(new Item.Properties().stacksTo(64)));
    
    // Tools
    public static final RegistryObject<Item> NANO_MULTITOOL = ITEMS.register("nano_multitool",
        () -> new NanoMultitool(new Item.Properties().stacksTo(1).durability(2000)));
    
    public static final RegistryObject<Item> QUANTUM_HARVESTER_ITEM = ITEMS.register("quantum_harvester",
        () -> new BlockItem(ModBlocks.QUANTUM_HARVESTER.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> NEURAL_INTERFACE_ITEM = ITEMS.register("neural_interface",
        () -> new BlockItem(ModBlocks.NEURAL_INTERFACE.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> QUANTUM_CONDUIT_ITEM = ITEMS.register("quantum_conduit",
        () -> new BlockItem(ModBlocks.QUANTUM_CONDUIT.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> AUGMENTATION_TABLE_ITEM = ITEMS.register("augmentation_table",
        () -> new BlockItem(ModBlocks.AUGMENTATION_TABLE.get(), new Item.Properties()));
    
    // Components and materials
    public static final RegistryObject<Item> QUANTUM_CORE = ITEMS.register("quantum_core",
        () -> new Item(new Item.Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    
    public static final RegistryObject<Item> NEURAL_MATRIX = ITEMS.register("neural_matrix",
        () -> new Item(new Item.Properties().stacksTo(16).rarity(net.minecraft.world.item.Rarity.RARE)));
    
    // Augments
    public static final RegistryObject<Item> CORTEX_PROCESSOR = ITEMS.register("cortex_processor",
        () -> new CortexProcessorAugment(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    
    public static final RegistryObject<Item> OPTIC_ENHANCER = ITEMS.register("optic_enhancer",
        () -> new OpticEnhancerAugment(new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}