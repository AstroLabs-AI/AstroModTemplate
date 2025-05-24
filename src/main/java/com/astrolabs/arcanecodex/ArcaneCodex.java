package com.astrolabs.arcanecodex;

import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.particles.ModParticles;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import com.astrolabs.arcanecodex.common.registry.ModBlocks;
import com.astrolabs.arcanecodex.common.registry.ModCreativeTabs;
import com.astrolabs.arcanecodex.common.registry.ModItems;
import com.astrolabs.arcanecodex.common.registry.ModMenuTypes;
import com.astrolabs.arcanecodex.common.network.ModNetworking;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArcaneCodex.MOD_ID)
public class ArcaneCodex {
    public static final String MOD_ID = "arcanecodex";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArcaneCodex() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModParticles.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        
        modEventBus.addListener(ModCapabilities::register);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        ModNetworking.register();
        
        LOGGER.info("Initializing Arcane Codex - Where quantum physics meets digital sorcery");
    }
}