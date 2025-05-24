package com.astrolabs.arcanecodex.common.registry;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.blocks.QuantumConduitBlock;
import com.astrolabs.arcanecodex.common.blocks.machines.NeuralInterfaceBlock;
import com.astrolabs.arcanecodex.common.blocks.machines.QuantumHarvesterBlock;
import com.astrolabs.arcanecodex.common.blocks.machines.RealityCompilerBlock;
import com.astrolabs.arcanecodex.common.blocks.multiblocks.AugmentationTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<Block> QUANTUM_HARVESTER = BLOCKS.register("quantum_harvester",
        () -> new QuantumHarvesterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .strength(5.0f, 6.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(QuantumHarvesterBlock.ACTIVE) ? 12 : 7)));
    
    public static final RegistryObject<Block> NEURAL_INTERFACE = BLOCKS.register("neural_interface",
        () -> new NeuralInterfaceBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .strength(3.0f, 3.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(NeuralInterfaceBlock.LINKED) ? 15 : 5)));
    
    public static final RegistryObject<Block> QUANTUM_CONDUIT = BLOCKS.register("quantum_conduit",
        () -> new QuantumConduitBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .strength(2.0f, 2.0f)
            .requiresCorrectToolForDrops()
            .noOcclusion()));
    
    public static final RegistryObject<Block> AUGMENTATION_TABLE = BLOCKS.register("augmentation_table",
        () -> new AugmentationTableBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .strength(4.0f, 5.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(AugmentationTableBlock.MULTIBLOCK_FORMED) ? 10 : 5)));
    
    public static final RegistryObject<Block> REALITY_COMPILER = BLOCKS.register("reality_compiler",
        () -> new RealityCompilerBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
            .strength(5.0f, 6.0f)
            .requiresCorrectToolForDrops()
            .lightLevel(state -> state.getValue(RealityCompilerBlock.EXECUTING) ? 15 : 7)));
    
    public static final RegistryObject<Block> TEMPORAL_STABILIZER = BLOCKS.register("temporal_stabilizer",
        () -> new com.astrolabs.arcanecodex.common.blocks.machines.TemporalStabilizerBlock(
            BlockBehaviour.Properties.copy(Blocks.OBSIDIAN)
                .strength(10.0f, 50.0f)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .lightLevel(state -> state.getValue(com.astrolabs.arcanecodex.common.blocks.machines.TemporalStabilizerBlock.ACTIVE) ? 10 : 0)));
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}