package com.astrolabs.arcanecodex.common.registry;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.common.blockentities.AugmentationTableBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.NeuralInterfaceBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.QuantumConduitBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.QuantumHarvesterBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.RealityCompilerBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.DimensionCompilerCoreBlockEntity;
import com.astrolabs.arcanecodex.common.blockentities.DimensionalRiftBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<BlockEntityType<QuantumHarvesterBlockEntity>> QUANTUM_HARVESTER =
        BLOCK_ENTITIES.register("quantum_harvester", () ->
            BlockEntityType.Builder.of(QuantumHarvesterBlockEntity::new, ModBlocks.QUANTUM_HARVESTER.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<NeuralInterfaceBlockEntity>> NEURAL_INTERFACE =
        BLOCK_ENTITIES.register("neural_interface", () ->
            BlockEntityType.Builder.of(NeuralInterfaceBlockEntity::new, ModBlocks.NEURAL_INTERFACE.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<QuantumConduitBlockEntity>> QUANTUM_CONDUIT =
        BLOCK_ENTITIES.register("quantum_conduit", () ->
            BlockEntityType.Builder.of(QuantumConduitBlockEntity::new, ModBlocks.QUANTUM_CONDUIT.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<AugmentationTableBlockEntity>> AUGMENTATION_TABLE =
        BLOCK_ENTITIES.register("augmentation_table", () ->
            BlockEntityType.Builder.of(AugmentationTableBlockEntity::new, ModBlocks.AUGMENTATION_TABLE.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<RealityCompilerBlockEntity>> REALITY_COMPILER =
        BLOCK_ENTITIES.register("reality_compiler", () ->
            BlockEntityType.Builder.of(RealityCompilerBlockEntity::new, ModBlocks.REALITY_COMPILER.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<com.astrolabs.arcanecodex.common.blockentities.TemporalStabilizerBlockEntity>> TEMPORAL_STABILIZER =
        BLOCK_ENTITIES.register("temporal_stabilizer", () ->
            BlockEntityType.Builder.of(com.astrolabs.arcanecodex.common.blockentities.TemporalStabilizerBlockEntity::new, 
                ModBlocks.TEMPORAL_STABILIZER.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<DimensionCompilerCoreBlockEntity>> DIMENSION_COMPILER_CORE =
        BLOCK_ENTITIES.register("dimension_compiler_core", () ->
            BlockEntityType.Builder.of(DimensionCompilerCoreBlockEntity::new, ModBlocks.DIMENSION_COMPILER_CORE.get())
                .build(null));
    
    public static final RegistryObject<BlockEntityType<DimensionalRiftBlockEntity>> DIMENSIONAL_RIFT =
        BLOCK_ENTITIES.register("dimensional_rift", () ->
            BlockEntityType.Builder.of(DimensionalRiftBlockEntity::new, ModBlocks.DIMENSIONAL_RIFT.get())
                .build(null));
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}