package com.astrolabs.arcanecodex.common.capabilities;

import com.astrolabs.arcanecodex.ArcaneCodex;
import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArcaneCodex.MOD_ID)
public class ModCapabilities {
    
    public static final Capability<IQuantumEnergy> QUANTUM_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IConsciousness> CONSCIOUSNESS = CapabilityManager.get(new CapabilityToken<>() {});
    
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IQuantumEnergy.class);
        event.register(IConsciousness.class);
    }
    
    @SubscribeEvent
    public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                new ResourceLocation(ArcaneCodex.MOD_ID, "consciousness"),
                new ConsciousnessProvider()
            );
        }
    }
    
    public static class ConsciousnessProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        
        private final ConsciousnessCapability consciousness = new ConsciousnessCapability();
        private final LazyOptional<IConsciousness> optional = LazyOptional.of(() -> consciousness);
        
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == CONSCIOUSNESS) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }
        
        @Override
        public CompoundTag serializeNBT() {
            return consciousness.serializeNBT();
        }
        
        @Override
        public void deserializeNBT(CompoundTag nbt) {
            consciousness.deserializeNBT(nbt);
        }
    }
    
    public static class QuantumEnergyProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
        
        private final QuantumEnergyStorage energy;
        private final LazyOptional<IQuantumEnergy> optional;
        
        public QuantumEnergyProvider(long maxEnergy) {
            this.energy = new QuantumEnergyStorage(maxEnergy);
            this.optional = LazyOptional.of(() -> energy);
        }
        
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            if (cap == QUANTUM_ENERGY) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }
        
        @Override
        public CompoundTag serializeNBT() {
            return energy.serializeNBT();
        }
        
        @Override
        public void deserializeNBT(CompoundTag nbt) {
            energy.deserializeNBT(nbt);
        }
    }
}