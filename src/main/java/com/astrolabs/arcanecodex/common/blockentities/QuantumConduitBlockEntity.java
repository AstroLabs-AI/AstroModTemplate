package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class QuantumConduitBlockEntity extends BlockEntity {
    
    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(10000);
    private final LazyOptional<IQuantumEnergy> energyOptional = LazyOptional.of(() -> energyStorage);
    private final Map<Direction, LazyOptional<IQuantumEnergy>> connectedCaches = new HashMap<>();
    
    private static final long TRANSFER_RATE = 1000; // per tick
    
    public QuantumConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_CONDUIT.get(), pos, state);
        
        // Set conduit to not store energy long-term
        for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
            energyStorage.setMaxEnergy(type, 1000); // Small buffer
        }
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumConduitBlockEntity blockEntity) {
        // Clear invalid cache entries
        blockEntity.connectedCaches.entrySet().removeIf(entry -> !entry.getValue().isPresent());
        
        // Balance energy between all connected sides
        blockEntity.balanceEnergy();
    }
    
    private void balanceEnergy() {
        for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
            long totalEnergy = energyStorage.getEnergyStored(type);
            Map<Direction, Long> connectedEnergy = new HashMap<>();
            int connectedCount = 1; // Include self
            
            // Gather energy levels from all connected blocks
            for (Direction direction : Direction.values()) {
                LazyOptional<IQuantumEnergy> connected = getConnectedEnergy(direction);
                if (connected.isPresent()) {
                    connected.ifPresent(energy -> {
                        if (energy.canReceive(direction.getOpposite(), type) || energy.canExtract(direction.getOpposite(), type)) {
                            long stored = energy.getEnergyStored(type);
                            connectedEnergy.put(direction, stored);
                            totalEnergy += stored;
                            connectedCount++;
                        }
                    });
                }
            }
            
            if (connectedCount > 1) {
                // Calculate average energy per connection
                long averageEnergy = totalEnergy / connectedCount;
                
                // Transfer energy to balance levels
                for (Map.Entry<Direction, Long> entry : connectedEnergy.entrySet()) {
                    Direction direction = entry.getKey();
                    long currentEnergy = entry.getValue();
                    long difference = averageEnergy - currentEnergy;
                    
                    if (difference > 0) {
                        // Transfer to neighbor
                        transferToNeighbor(direction, type, Math.min(difference, TRANSFER_RATE));
                    } else if (difference < 0) {
                        // Pull from neighbor
                        pullFromNeighbor(direction, type, Math.min(-difference, TRANSFER_RATE));
                    }
                }
            }
        }
    }
    
    private void transferToNeighbor(Direction direction, IQuantumEnergy.EnergyType type, long amount) {
        LazyOptional<IQuantumEnergy> connected = getConnectedEnergy(direction);
        connected.ifPresent(energy -> {
            if (energy.canReceive(direction.getOpposite(), type)) {
                long extracted = energyStorage.extractEnergy(type, amount, true);
                if (extracted > 0) {
                    long accepted = energy.insertEnergy(type, extracted, false);
                    if (accepted > 0) {
                        energyStorage.extractEnergy(type, accepted, false);
                    }
                }
            }
        });
    }
    
    private void pullFromNeighbor(Direction direction, IQuantumEnergy.EnergyType type, long amount) {
        LazyOptional<IQuantumEnergy> connected = getConnectedEnergy(direction);
        connected.ifPresent(energy -> {
            if (energy.canExtract(direction.getOpposite(), type)) {
                long canAccept = energyStorage.insertEnergy(type, amount, true);
                if (canAccept > 0) {
                    long extracted = energy.extractEnergy(type, canAccept, false);
                    if (extracted > 0) {
                        energyStorage.insertEnergy(type, extracted, false);
                    }
                }
            }
        });
    }
    
    private LazyOptional<IQuantumEnergy> getConnectedEnergy(Direction direction) {
        return connectedCaches.computeIfAbsent(direction, dir -> {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor != null) {
                return neighbor.getCapability(ModCapabilities.QUANTUM_ENERGY, dir.getOpposite());
            }
            return LazyOptional.empty();
        });
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("Energy"));
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.QUANTUM_ENERGY) {
            return energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
        connectedCaches.values().forEach(LazyOptional::invalidate);
        connectedCaches.clear();
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        connectedCaches.clear();
    }
}