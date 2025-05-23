package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.blockentities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AstroExpansion.MODID);
    
    public static final RegistryObject<BlockEntityType<BasicGeneratorBlockEntity>> BASIC_GENERATOR =
        BLOCK_ENTITIES.register("basic_generator",
            () -> BlockEntityType.Builder.of(BasicGeneratorBlockEntity::new,
                ModBlocks.BASIC_GENERATOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<MaterialProcessorBlockEntity>> MATERIAL_PROCESSOR =
        BLOCK_ENTITIES.register("material_processor",
            () -> BlockEntityType.Builder.of(MaterialProcessorBlockEntity::new,
                ModBlocks.MATERIAL_PROCESSOR.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<OreWasherBlockEntity>> ORE_WASHER =
        BLOCK_ENTITIES.register("ore_washer",
            () -> BlockEntityType.Builder.of(OreWasherBlockEntity::new,
                ModBlocks.ORE_WASHER.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<EnergyConduitBlockEntity>> ENERGY_CONDUIT =
        BLOCK_ENTITIES.register("energy_conduit",
            () -> BlockEntityType.Builder.of(EnergyConduitBlockEntity::new,
                ModBlocks.ENERGY_CONDUIT.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<EnergyStorageBlockEntity>> ENERGY_STORAGE =
        BLOCK_ENTITIES.register("energy_storage",
            () -> BlockEntityType.Builder.of(EnergyStorageBlockEntity::new,
                ModBlocks.ENERGY_STORAGE.get()).build(null));
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}