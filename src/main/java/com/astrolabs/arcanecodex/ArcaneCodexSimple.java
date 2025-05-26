package com.astrolabs.arcanecodex;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ArcaneCodexSimple.MOD_ID)
public class ArcaneCodexSimple {
    public static final String MOD_ID = "arcanecodex";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArcaneCodexSimple() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Arcane Codex - Development Build Loaded");
    }
}