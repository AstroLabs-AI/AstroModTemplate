package com.astrolabs.arcanecodex.common.registry;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.client.gui.holographic.ResearchTreeMenu;
import com.astrolabs.arcanecodex.client.gui.RealityCompilerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, ArcaneCodex.MOD_ID);
    
    public static final RegistryObject<MenuType<ResearchTreeMenu>> RESEARCH_TREE = MENU_TYPES.register("research_tree",
        () -> IForgeMenuType.create(ResearchTreeMenu::new));
    
    public static final RegistryObject<MenuType<RealityCompilerMenu>> REALITY_COMPILER = MENU_TYPES.register("reality_compiler",
        () -> IForgeMenuType.create(RealityCompilerMenu::new));
    
    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}