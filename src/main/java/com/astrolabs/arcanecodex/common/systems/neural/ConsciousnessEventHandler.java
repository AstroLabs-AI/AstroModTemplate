package com.astrolabs.arcanecodex.common.systems.neural;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneCodex.MOD_ID)
public class ConsciousnessEventHandler {
    
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide) {
            Player player = event.player;
            
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                // Passive neural charge regeneration based on consciousness level
                if (player.level.getGameTime() % 20 == 0) { // Every second
                    long currentCharge = consciousness.getNeuralCharge();
                    long maxCharge = consciousness.getMaxNeuralCharge();
                    
                    if (currentCharge < maxCharge) {
                        // Base regen + level bonus
                        int regenAmount = 1 + consciousness.getConsciousnessLevel();
                        consciousness.addNeuralCharge(regenAmount);
                    }
                }
                
                // Apply augment effects
                applyAugmentEffects(player, consciousness);
            });
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level.isClientSide) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            
            // Restore consciousness data on death/dimension change
            oldPlayer.reviveCaps();
            oldPlayer.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(oldCap -> {
                newPlayer.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(newCap -> {
                    newCap.deserializeNBT(oldCap.serializeNBT());
                });
            });
            oldPlayer.invalidateCaps();
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            // Sync consciousness data to client
            serverPlayer.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                // TODO: Send sync packet to client
            });
        }
    }
    
    private static void applyAugmentEffects(Player player, IConsciousness consciousness) {
        // Apply continuous effects from augments
        if (consciousness.hasAugment(new net.minecraft.resources.ResourceLocation(ArcaneCodex.MOD_ID, "optic_enhancer"))) {
            // Optic enhancer provides energy sight
            if (player.level.getGameTime() % 10 == 0) {
                // TODO: Highlight nearby energy sources
            }
        }
        
        if (consciousness.hasAugment(new net.minecraft.resources.ResourceLocation(ArcaneCodex.MOD_ID, "cortex_processor"))) {
            // Cortex processor increases neural charge capacity
            // This is handled in the capability itself
        }
    }
}