package com.astrolabs.astroexpansion.common.registry;

import com.astrolabs.astroexpansion.AstroExpansion;
import com.astrolabs.astroexpansion.common.items.DroneItem;
import com.astrolabs.astroexpansion.common.items.StorageDriveItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, AstroExpansion.MODID);
    
    // Raw Ores
    public static final RegistryObject<Item> RAW_TITANIUM = ITEMS.register("raw_titanium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_LITHIUM = ITEMS.register("raw_lithium",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_URANIUM = ITEMS.register("raw_uranium",
        () -> new Item(new Item.Properties()));
    
    // Ingots
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LITHIUM_INGOT = ITEMS.register("lithium_ingot",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> URANIUM_INGOT = ITEMS.register("uranium_ingot",
        () -> new Item(new Item.Properties()));
    
    // Dusts
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LITHIUM_DUST = ITEMS.register("lithium_dust",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> URANIUM_DUST = ITEMS.register("uranium_dust",
        () -> new Item(new Item.Properties()));
    
    // Components
    public static final RegistryObject<Item> CIRCUIT_BOARD = ITEMS.register("circuit_board",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PROCESSOR = ITEMS.register("processor",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ENERGY_CORE = ITEMS.register("energy_core",
        () -> new Item(new Item.Properties()));
    
    // Misc
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
        () -> new Item(new Item.Properties().stacksTo(1)));
    
    // Storage Components
    public static final RegistryObject<Item> STORAGE_DRIVE_1K = ITEMS.register("storage_drive_1k",
        () -> new StorageDriveItem(new Item.Properties(), 1000, "1k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_4K = ITEMS.register("storage_drive_4k",
        () -> new StorageDriveItem(new Item.Properties(), 4000, "4k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_16K = ITEMS.register("storage_drive_16k",
        () -> new StorageDriveItem(new Item.Properties(), 16000, "16k"));
    public static final RegistryObject<Item> STORAGE_DRIVE_64K = ITEMS.register("storage_drive_64k",
        () -> new StorageDriveItem(new Item.Properties(), 64000, "64k"));
    
    public static final RegistryObject<Item> STORAGE_HOUSING = ITEMS.register("storage_housing",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STORAGE_PROCESSOR = ITEMS.register("storage_processor",
        () -> new Item(new Item.Properties()));
    
    // Drones
    public static final RegistryObject<Item> MINING_DRONE = ITEMS.register("mining_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.MINING_DRONE, "Mining"));
    public static final RegistryObject<Item> CONSTRUCTION_DRONE = ITEMS.register("construction_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.CONSTRUCTION_DRONE, "Construction"));
    public static final RegistryObject<Item> FARMING_DRONE = ITEMS.register("farming_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.FARMING_DRONE, "Farming"));
    public static final RegistryObject<Item> COMBAT_DRONE = ITEMS.register("combat_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.COMBAT_DRONE, "Combat"));
    public static final RegistryObject<Item> LOGISTICS_DRONE = ITEMS.register("logistics_drone",
        () -> new DroneItem(new Item.Properties(), ModEntities.LOGISTICS_DRONE, "Logistics"));
    public static final RegistryObject<Item> DRONE_CORE = ITEMS.register("drone_core",
        () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DRONE_SHELL = ITEMS.register("drone_shell",
        () -> new Item(new Item.Properties()));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}